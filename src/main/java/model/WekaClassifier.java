package model;


import weka.classifiers.Classifier;

public class WekaClassifier {
    private final Classifier classifier;
    private final String featureSelectionFilterName;
    private final String samplingFilterName;
    private final String classifierName;
    private final boolean isCostSensitive;

    /**
     * Constructor for the WekaClassifier class.
     * <p>
     * Initializes a new instance of WekaClassifier with the specified parameters.
     *
     * @param classifier The underlying classifier model.
     * @param classifierName The name of the classifier.
     * @param featureSelectionFilterName The name of the feature selection filter applied.
     * @param bestFirstDirection The direction of search used in BestFirst feature selection (if applicable).
     * @param samplingFilterName The name of the sampling filter applied.
     * @param isCostSensitive Indicates whether the classifier is configured for cost-sensitive learning.
     */
    public WekaClassifier(Classifier classifier, String classifierName, String featureSelectionFilterName, String bestFirstDirection, String samplingFilterName, boolean isCostSensitive) {
        this.classifier = classifier;
        switch (samplingFilterName) {
            case "Resample" -> this.samplingFilterName = "OverSampl";
            case "SpreadSubsample" -> this.samplingFilterName = "UnderSampl";
            case "SMOTE" -> this.samplingFilterName = "SMOTE";
            default -> this.samplingFilterName = samplingFilterName;
        }
        if (featureSelectionFilterName.equals("BestFirst")) {
            this.featureSelectionFilterName = featureSelectionFilterName + "(" + bestFirstDirection + ")";
        } else {
            this.featureSelectionFilterName = featureSelectionFilterName;
        }
//        if (featureSelectionFilterName.equals("BestFirst")) {
//            this.featureSelectionFilterName = "true";
//        } else {
//            this.featureSelectionFilterName = "false";
//        }
        this.isCostSensitive = isCostSensitive;
        this.classifierName = classifierName;
    }


    public Classifier getClassifier() {
        return classifier;
    }

    public String getClassifierName() {
        return classifierName;
    }

    public String getFeatureSelectionFilterName() {
        return featureSelectionFilterName;
    }

    public String getSamplingFilterName() {
        return samplingFilterName;
    }

    public boolean isCostSensitive() {
        return isCostSensitive;
    }
}
