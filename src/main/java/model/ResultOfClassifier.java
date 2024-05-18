package model;

import weka.classifiers.evaluation.Evaluation;

public class ResultOfClassifier {
    private final int walkForwardIteration;
    private final String classifierName;
    private final boolean hasFeatureSelection;
    private final boolean hasSampling;
    private final WekaClassifier wekaClassifier;
    private final boolean hasCostSensitive;
    private double trainingPercent;
    private final double precision;
    private final double recall;
    private final double areaUnderROC;
    private final double kappa;
    private final double truePositives;
    private final double falsePositives;
    private final double trueNegatives;
    private final double falseNegatives;

    /**
     * Constructor for the ResultOfClassifier class.
     *
     * Initializes a new instance of ResultOfClassifier with the specified parameters.
     *
     * @param walkForwardIteration The iteration number of the walk-forward validation process.
     * @param wekaClassifier The WekaClassifier object representing the classifier used.
     * @param evaluation The Evaluation object containing the evaluation metrics of the classifier.
     */

    public ResultOfClassifier(int walkForwardIteration, WekaClassifier wekaClassifier, Evaluation evaluation) {
        this.walkForwardIteration = walkForwardIteration;
        this.wekaClassifier = wekaClassifier;
        this.classifierName = wekaClassifier.getClassifierName();
        this.hasFeatureSelection = (!wekaClassifier.getFeatureSelectionFilterName().equals("NoSelection"));
        this.hasSampling = (!wekaClassifier.getSamplingFilterName().equals("NoSampling"));
        this.hasCostSensitive = wekaClassifier.isCostSensitive();

        trainingPercent = 0.0;
        truePositives = evaluation.numTruePositives(0);
        falsePositives = evaluation.numFalsePositives(0);
        trueNegatives = evaluation.numTrueNegatives(0);
        falseNegatives = evaluation.numFalseNegatives(0);
        if(truePositives == 0.0 && falsePositives == 0.0){
            precision = Double.NaN;
        } else{
            precision = evaluation.precision(0);
        }
        if(truePositives == 0.0 && falseNegatives == 0.0){
            recall = Double.NaN;
        } else{
            recall = evaluation.recall(0);
        }
        areaUnderROC = evaluation.areaUnderROC(0);
        kappa = evaluation.kappa();
    }

    public void setTrainingPercent(double trainingPercent) {
        this.trainingPercent = trainingPercent;
    }

    public double getTrainingPercent() {
        return trainingPercent;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getAreaUnderROC() {
        return areaUnderROC;
    }

    public double getKappa() {
        return kappa;
    }

    public double getTruePositives() {
        return truePositives;
    }

    public double getFalsePositives() {
        return falsePositives;
    }

    public double getTrueNegatives() {
        return trueNegatives;
    }

    public double getFalseNegatives() {
        return falseNegatives;
    }

    public int getWalkForwardIteration() {
        return walkForwardIteration;
    }

    public String getClassifierName() {
        return classifierName;
    }

    public boolean hasFeatureSelection() {
        return hasFeatureSelection;
    }

    public boolean hasSampling() {
        return hasSampling;
    }

    public WekaClassifier getCustomClassifier() {
        return wekaClassifier;
    }

    public boolean hasCostSensitive() {
        return hasCostSensitive;
    }
}
