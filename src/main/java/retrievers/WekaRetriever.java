package retrievers;
import controllers.ClassifierController;
import exception.ClassifierException;
import model.AcumeFile;
import model.WekaClassifier;
import model.ResultOfClassifier;
import utilities.ACUME;
import weka.attributeSelection.BestFirst;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.ArrayList;
import java.util.List;

public class WekaRetriever {
    private final String projName;
    private final int lenOfHalfRelease;

    /**
     * Constructor for the WekaRetriever class.
     *
     * Initializes a new instance of WekaRetriever with the specified project name and the length of half-release.
     *
     * @param projName the name of the project for which data retrieval operations will be performed.
     * @param lenOfHalfRelease the length (in an appropriate unit) of the half-release, used to calculate partial or segmented release periods.
     */

    public WekaRetriever(String projName, int lenOfHalfRelease) {
    this.projName = projName;
    this.lenOfHalfRelease = lenOfHalfRelease;
    }


    public List<ResultOfClassifier> computeAllClassifier() throws ClassifierException {
        List<ResultOfClassifier> allResultsOfClassifiers = new ArrayList<>();

        // Iterate through each release (halved for snoring), starting from 1 because the first release is skipped.
        // In the context of ML for software engineering, evaluating the classifier on the first training set might underestimate the prediction of buggy classes.
        for (int i = 1; i <= lenOfHalfRelease; i++) {
            try {
                // Load training and testing datasets for the current half-release
                Instances trainingSetInstance = loadInstances("TRAINING-SET", i);
                Instances testingSetInstance = loadInstances("TESTING-SET", i);

                // Set the class index for both datasets, indicating the attribute to be predicted
                setClassIndex(trainingSetInstance);
                setClassIndex(testingSetInstance);

                // Generate a list of all classifiers based on the attribute statistics of the class attribute
                List<WekaClassifier> wekaClassifiers = generateAllClassifiers(trainingSetInstance.attributeStats(trainingSetInstance.numAttributes() - 1));

                // Evaluate each classifier
                for (WekaClassifier wekaClassifier : wekaClassifiers) {
                    // Initialize the evaluator with the testing set
                    Evaluation evaluator = new Evaluation(testingSetInstance);
                    Classifier classifier = wekaClassifier.getClassifier();

                    // Train the classifier with the training set
                    classifier.buildClassifier(trainingSetInstance);

                    // Evaluate the classifier with the testing set
                    evaluator.evaluateModel(classifier, testingSetInstance);

                    // Store the results of the evaluation
                    ResultOfClassifier resultOfClassifier = new ResultOfClassifier(i, wekaClassifier, evaluator);

                    // Calculate and set the training percentage
                    resultOfClassifier.setTrainingPercent(100.0 * trainingSetInstance.numInstances() / (trainingSetInstance.numInstances() + testingSetInstance.numInstances()));

                    // Add the results to the list of all classifier results
                    allResultsOfClassifiers.add(resultOfClassifier);

                    //ACUME

                    String size = "SIZE";
                    String isBuggy = "IS_BUGGY";

                    List<AcumeFile> acumeUtilsList = new ArrayList<>();

                    int sizeIndex = testingSetInstance.attribute(size).index();
                    int isBuggyIndex = testingSetInstance.attribute(isBuggy).index();

                    int trueClassifierIndex = testingSetInstance.classAttribute().indexOfValue("YES");

                    if(trueClassifierIndex != -1){
                        for (int j = 0; j < testingSetInstance.numInstances(); j++) {
                            int sizeValue = (int) testingSetInstance.instance(j).value(sizeIndex);
                            int valueIndex = (int) testingSetInstance.instance(j).value(isBuggyIndex);
                            String buggyness =  testingSetInstance.attribute(isBuggyIndex).value(valueIndex);
                            double[] distribution = classifier.distributionForInstance(testingSetInstance.instance(j));
                            AcumeFile acumeUtils = new AcumeFile(i, sizeValue, distribution[trueClassifierIndex], buggyness);
                            acumeUtilsList.add(acumeUtils);
                        }
                    }
                    ACUME.createFileForAcume(projName,wekaClassifier,i,  acumeUtilsList);


                }
            } catch (Exception e) {
                // Handle any exceptions that occur during the process
                throw new ClassifierException("Error during building or evaluation of the classifier:"+e);
            }
        }
        // Return the list of all classifier results
        return allResultsOfClassifiers;
    }

    private List<WekaClassifier> generateAllClassifiers(AttributeStats isBuggyAttributeStats) {

        //The three used classifier
        NaiveBayes naiveBayesClassifier = new NaiveBayes();
        RandomForest randomForestClassifier = new RandomForest();
        IBk ibkClassifier = new IBk();

        //Get first feature selection
        List<AttributeSelection> featureSelectionFilters = getFeatureSelectionFilters();

        //Then sampling filters
        int isBuggySize = isBuggyAttributeStats.nominalCounts[1];
        int isNotBuggySize = isBuggyAttributeStats.nominalCounts[0];
        List<Filter> samplingFilters = getSamplingFilters(isBuggySize, isNotBuggySize);

        //Now we can compute all combination with filters and ML algorithm
        ClassifierController classifierController = new ClassifierController();

        // NO FEATURE SELECTION, NO SAMPLING, NO COST SENSITIVE
        classifierController.addBasicClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier);

        // ONLY FEATURE SELECTION
        classifierController.addFeatureSelectionClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier, featureSelectionFilters);

        // ONLY SAMPLING
        classifierController.addSamplingClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier, samplingFilters);

        // ONLY COST SENSITIVE
        classifierController.addCostSensitiveClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier);

        // FEATURE SELECTION AND SAMPLING
        classifierController.addFeatureSelectionAndSamplingClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier, featureSelectionFilters, samplingFilters);

        // FEATURE SELECTION AND COST SENSITIVE
        classifierController.addFeatureSelectionAndCostSensitiveClassifiers(naiveBayesClassifier, randomForestClassifier, ibkClassifier, featureSelectionFilters);

        //Retrieve the computed results
        return classifierController.getCustomClassifiersList();
    }

    private Instances loadInstances(String setType, int index) throws Exception {
        return new DataSource("output/arff/" + projName + "/" + setType + "/" + setType + index + ".arff").getDataSet();
    }

    private void setClassIndex(Instances instances) {
        instances.setClassIndex(instances.numAttributes() - 1);
    }

    private List<AttributeSelection> getFeatureSelectionFilters() {
        BestFirst bestFirst = new BestFirst();
        bestFirst.setDirection(new SelectedTag(2, BestFirst.TAGS_SELECTION));
        AttributeSelection attributeSelection = new AttributeSelection();
        attributeSelection.setSearch(bestFirst);
        return List.of(attributeSelection);
    }

    private static List<Filter> getSamplingFilters(int majorityClassSize, int minorityClassSize) {
        double percentStandardOversampling = ((100.0*majorityClassSize)/(majorityClassSize + minorityClassSize))*2;
        double percentSMOTE;
        if(minorityClassSize==0 || minorityClassSize > majorityClassSize){
            percentSMOTE = 0;
        }else{
            percentSMOTE = (100.0*(majorityClassSize-minorityClassSize))/minorityClassSize;
        }
        return setSamplingFilter(percentSMOTE,percentStandardOversampling);
    }
     private static List<Filter> setSamplingFilter(double percentSMOTE, double percentStandardOversampling){
         List<Filter> filterList = new ArrayList<>();
         Resample resample = new Resample();
         resample.setBiasToUniformClass(1.0);
         resample.setSampleSizePercent(percentStandardOversampling);
         filterList.add(resample);
         SpreadSubsample spreadSubsample = new SpreadSubsample();
         spreadSubsample.setDistributionSpread(1.0);
         filterList.add(spreadSubsample);
         SMOTE smote = new SMOTE();
         smote.setClassValue("1");
         smote.setPercentage(percentSMOTE);
         filterList.add(smote);
         return filterList;
     }

}
