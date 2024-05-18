package controllers;

import model.WekaClassifier;
import weka.attributeSelection.BestFirst;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.util.ArrayList;
import java.util.List;

public class ClassifierController {
    /**
     * The ClassifierController class is responsible for managing and configuring various classifiers with different
     * preprocessing techniques, including basic classifiers, feature selection, sampling, and cost-sensitive learning.
     * It provides methods to add classifiers with various configurations to a custom list for later evaluation or use.
     * <p>
     * The class offers the following functionality:
     * <p>
     * 1. **Basic Classifiers**:
     *    - Add classifiers without any feature selection or sampling.
     * <p>
     * 2. **Feature Selection**:
     *    - Add classifiers with feature selection filters applied to them.
     * <p>
     * 3. **Sampling**:
     *    - Add classifiers with sampling filters applied to them.
     * <p>
     * 4. **Cost-Sensitive Learning**:
     *    - Add classifiers configured to be cost-sensitive, with an adjustable cost matrix.
     * <p>
     * 5. **Combined Feature Selection and Sampling**:
     *    - Add classifiers with both feature selection and sampling filters applied.
     * <p>
     * 6. **Combined Feature Selection and Cost-Sensitive Learning**:
     *    - Add classifiers with both feature selection filters and cost-sensitive configuration.
     *
     **/

    public List<WekaClassifier> wekaClassifiersList;
    public static final String NO_SELECTION = "NoSelection";
    public static final String NO_SAMPLING = "NoSampling";

    public ClassifierController() {
        this.wekaClassifiersList = new ArrayList<>();
    }

    public void addBasicClassifiers(Classifier naiveBayesClassifier, Classifier randomForestClassifier, Classifier ibkClassifier) {
        this.wekaClassifiersList.add(new WekaClassifier(naiveBayesClassifier, naiveBayesClassifier.getClass().getSimpleName() , NO_SELECTION, null, NO_SAMPLING, false));
        this.wekaClassifiersList.add(new WekaClassifier(randomForestClassifier, randomForestClassifier.getClass().getSimpleName(), NO_SELECTION, null, NO_SAMPLING, false));
        this.wekaClassifiersList.add(new WekaClassifier(ibkClassifier, ibkClassifier.getClass().getSimpleName(), NO_SELECTION, null, NO_SAMPLING, false));
    }

    public void addFeatureSelectionClassifiers(Classifier naiveBayesClassifier, Classifier randomForestClassifier, Classifier ibkClassifier, List<AttributeSelection> featureSelectionFilters) {
        for (AttributeSelection featureSelectionFilter : featureSelectionFilters) {
            setFeatureSelectionClassifier(naiveBayesClassifier, featureSelectionFilter);
            setFeatureSelectionClassifier(randomForestClassifier,featureSelectionFilter);
            setFeatureSelectionClassifier(ibkClassifier, featureSelectionFilter);
        }
    }
    private void setFeatureSelectionClassifier(Classifier classifier, AttributeSelection featureSelectionFilter){
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(classifier);
        filteredClassifier.setFilter(featureSelectionFilter);

        this.wekaClassifiersList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(), featureSelectionFilter.getSearch().getClass().getSimpleName(), ((BestFirst)featureSelectionFilter.getSearch()).getDirection().getSelectedTag().getReadable(), NO_SAMPLING, false));
    }

    public void addSamplingClassifiers(NaiveBayes naiveBayesClassifier, RandomForest randomForestClassifier, IBk ibkClassifier, List<Filter> samplingFilters) {
        for (Filter samplingFilter : samplingFilters) {
            setSamplingClassifier(naiveBayesClassifier,samplingFilter);
            setSamplingClassifier(randomForestClassifier, samplingFilter);
            setSamplingClassifier(ibkClassifier, samplingFilter);
        }
    }

    private void  setSamplingClassifier(Classifier classifier, Filter samplingFilter){
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(classifier);
        filteredClassifier.setFilter(samplingFilter);

        wekaClassifiersList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(),NO_SELECTION, null, samplingFilter.getClass().getSimpleName(), false));
    }

    public void addCostSensitiveClassifiers(NaiveBayes naiveBayesClassifier, RandomForest randomForestClassifier, IBk ibkClassifier) {
        List<CostSensitiveClassifier> costSensitiveFilters = getCostSensitiveFilters();
        for (CostSensitiveClassifier costSensitiveClassifier : costSensitiveFilters) {
         setCostSensitiveClassifier(costSensitiveClassifier, naiveBayesClassifier);
         setCostSensitiveClassifier(costSensitiveClassifier,randomForestClassifier);
         setCostSensitiveClassifier(costSensitiveClassifier,ibkClassifier);
        }
    }

    private List<CostSensitiveClassifier> getCostSensitiveFilters() {
        CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
        costSensitiveClassifier.setMinimizeExpectedCost(false);
        CostMatrix costMatrix = createCostMatrix();
        costSensitiveClassifier.setCostMatrix(costMatrix);
        return new ArrayList<>(List.of(costSensitiveClassifier));
    }

    private void setCostSensitiveClassifier(CostSensitiveClassifier costSensitiveClassifier, Classifier classifier){
        costSensitiveClassifier.setClassifier(classifier);
        wekaClassifiersList.add(new WekaClassifier(costSensitiveClassifier, classifier.getClass().getSimpleName(),NO_SELECTION, null, NO_SAMPLING, true));
    }

    /**
     * Creates a cost matrix for cost-sensitive learning with predefined costs.
     * <p>
     * In the context of bug prediction in software engineering, a cost matrix is used
     * to assign different costs to different types of classification errors, such as
     * false positives (FP) and false negatives (FN). These costs reflect the relative
     * importance of each type of error.
     * <p>
     * In this method, a 2x2 cost matrix is created with predefined costs for FP and FN.
     * The value of 1.0 is assigned to the cell representing the cost of a false positive,
     * indicating that the cost of misclassified a non-buggy class as buggy is 1.0.
     * The value of 10.0 is assigned to the cell representing the cost of a false negative,
     * indicating that the cost of misclassified a buggy class as non-buggy is 10.0.
     * <p>
     * The ratio of the cost of false positives to false negatives (FP/FN) is set to approximately
     * 10:1, based on the context of bug prediction in software engineering. However, this value
     * can be adjusted depending on the specific requirements and priorities of the problem domain.
     **/
    private CostMatrix createCostMatrix() {
        weka.classifiers.CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 1, 1.0);
        costMatrix.setCell(1, 0, 10.0);
        return costMatrix;
    }

    public void addFeatureSelectionAndSamplingClassifiers(NaiveBayes naiveBayesClassifier, RandomForest randomForestClassifier, IBk ibkClassifier, List<AttributeSelection> featureSelectionFilters, List<Filter> samplingFilters) {
        for (AttributeSelection featureSelectionFilter : featureSelectionFilters) {
            for (Filter samplingFilter : samplingFilters) {
                setFeatureSelectionAndSamplingFilters(naiveBayesClassifier, samplingFilter, featureSelectionFilter);
                setFeatureSelectionAndSamplingFilters(randomForestClassifier,samplingFilter,featureSelectionFilter);
                setFeatureSelectionAndSamplingFilters(ibkClassifier,samplingFilter,featureSelectionFilter);
            }
        }
    }
    private void setFeatureSelectionAndSamplingFilters(Classifier classifier, Filter samplingFilter, AttributeSelection featureSelectionFilter){
        FilteredClassifier innerClassifier = new FilteredClassifier();
        innerClassifier.setClassifier(classifier);
        innerClassifier.setFilter(samplingFilter);

        FilteredClassifier externalClassifier = new FilteredClassifier();
        externalClassifier.setFilter(featureSelectionFilter);
        externalClassifier.setClassifier(innerClassifier);

        wekaClassifiersList.add(new WekaClassifier(externalClassifier, classifier.getClass().getSimpleName(), featureSelectionFilter.getSearch().getClass().getSimpleName(), ((BestFirst)featureSelectionFilter.getSearch()).getDirection().getSelectedTag().getReadable(), samplingFilter.getClass().getSimpleName(), false));
    }

    public void addFeatureSelectionAndCostSensitiveClassifiers(NaiveBayes naiveBayesClassifier, RandomForest randomForestClassifier, IBk ibkClassifier, List<AttributeSelection> featureSelectionFilters) {
            List<CostSensitiveClassifier> costSensitiveFilters = getCostSensitiveFilters();
            for(CostSensitiveClassifier costSensitiveClassifier: costSensitiveFilters){
                for (AttributeSelection featureSelectionFilter : featureSelectionFilters) {
                    setFeatureSelectionAndCostSensitiveClassifiers(costSensitiveClassifier, featureSelectionFilter,naiveBayesClassifier);
                    setFeatureSelectionAndCostSensitiveClassifiers(costSensitiveClassifier, featureSelectionFilter,randomForestClassifier);
                    setFeatureSelectionAndCostSensitiveClassifiers(costSensitiveClassifier, featureSelectionFilter, ibkClassifier);
                }
            }
    }

    private void setFeatureSelectionAndCostSensitiveClassifiers(CostSensitiveClassifier costSensitiveClassifier, AttributeSelection featureSelectionFilter, Classifier classifier){
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(featureSelectionFilter);
        costSensitiveClassifier.setClassifier(classifier);
        filteredClassifier.setClassifier(costSensitiveClassifier);

        wekaClassifiersList.add(new WekaClassifier(filteredClassifier, classifier.getClass().getSimpleName(), featureSelectionFilter.getSearch().getClass().getSimpleName(), ((BestFirst)featureSelectionFilter.getSearch()).getDirection().getSelectedTag().getReadable(), NO_SAMPLING, true));
    }
    public List<WekaClassifier> getCustomClassifiersList() {
        return wekaClassifiersList;
    }
}
