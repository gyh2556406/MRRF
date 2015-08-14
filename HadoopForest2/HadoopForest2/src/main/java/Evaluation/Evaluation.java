package Evaluation;
import Dataset.DataSet;
import RandomForest.Classifier;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daq
 */
public class Evaluation {

    private String clsName;
    private DataSet dataset;
    private double accMean;
    private double accStd;
    private double rmseMean;
    private double rmseStd;

    public Evaluation() {
    }

    public Evaluation(DataSet dataset, String clsName) {
        this.dataset = dataset;
        this.clsName = clsName;
    }
    public ArrayList<Object> devideDataSet(){
        ArrayList<Object> devideResult = new ArrayList<Object>();

        int fold = 3;

        Random random = new Random(2015);
        int[] permutation = new int[10000];
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = i;
        }
        for (int i = 0; i < 10 * permutation.length; i++) {
            int repInd = random.nextInt(permutation.length);
            int ind = i % permutation.length;

            int tmp = permutation[ind];
            permutation[ind] = permutation[repInd];
            permutation[repInd] = tmp;
        }

        int[] perm = new int[dataset.getNumInstnaces()];
        int ind = 0;
        for (int i = 0; i < permutation.length; i++) {
            if (permutation[i] < dataset.getNumInstnaces()) {
                perm[ind++] = permutation[i];
            }
        }

        int share = dataset.getNumInstnaces() / fold;

        boolean[] isCategory = dataset.getIsCategory();
        double[][] features = dataset.getFeatures();
        double[] labels = dataset.getLabels();

        for (int f = 0; f < fold; f++) {
            //System.out.println("No." + (f + 1) + "  time for runing");

            int numTest = f < fold - 1 ? share : dataset.getNumInstnaces() - (fold - 1) * share;
            double[][] trainFeatures = new double[dataset.getNumInstnaces() - numTest][dataset.getNumAttributes()];
            double[] trainLabels = new double[dataset.getNumInstnaces() - numTest];
            double[][] testFeatures = new double[numTest][dataset.getNumAttributes()];
            double[] testLabels = new double[numTest];
            //System.out.println("We get " + (dataset.getNumInstnaces() - numTest) + "examples for training");
            //System.out.println("We get " + numTest + "examples for testing");

            int indTrain = 0, indTest = 0;
            for (int j = 0; j < dataset.getNumInstnaces(); j++) {
                if ((f < fold - 1 && (j < f * share || j >= (f + 1) * share)) || (f == fold - 1 && j < f * share)) {
                    System.arraycopy(features[perm[j]], 0, trainFeatures[indTrain], 0, dataset.getNumAttributes());
                    trainLabels[indTrain] = labels[perm[j]];
                    indTrain++;
                } else {
                    System.arraycopy(features[perm[j]], 0, testFeatures[indTest], 0, dataset.getNumAttributes());
                    testLabels[indTest] = labels[perm[j]];
                    indTest++;
                }
            }
            devideResult.add(trainFeatures);
            devideResult.add(trainLabels);
            devideResult.add(testFeatures);
            devideResult.add(testLabels);
            devideResult.add(isCategory);

        }
            return devideResult;
    }
    public void crossValidation() {
        int fold = 3;

        Random random = new Random(2015);
        int[] permutation = new int[10000];
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = i;
        }
        for (int i = 0; i < 10 * permutation.length; i++) {
            int repInd = random.nextInt(permutation.length);
            int ind = i % permutation.length;

            int tmp = permutation[ind];
            permutation[ind] = permutation[repInd];
            permutation[repInd] = tmp;
        }

        int[] perm = new int[dataset.getNumInstnaces()];
        int ind = 0;
        for (int i = 0; i < permutation.length; i++) {
            if (permutation[i] < dataset.getNumInstnaces()) {
                perm[ind++] = permutation[i];
            }
        }

        int share = dataset.getNumInstnaces() / fold;

        boolean[] isCategory = dataset.getIsCategory();
        double[][] features = dataset.getFeatures();
        double[] labels = dataset.getLabels();

        boolean isClassification = isCategory[isCategory.length - 1];

        double[] measures = new double[fold];
        for (int f = 0; f < fold; f++) {
            try {
                System.out.println("No."+(f+1) + "  time for runing");

                int numTest = f < fold - 1 ? share : dataset.getNumInstnaces() - (fold - 1) * share;
                double[][] trainFeatures = new double[dataset.getNumInstnaces() - numTest][dataset.getNumAttributes()];
                double[] trainLabels = new double[dataset.getNumInstnaces() - numTest];
                double[][] testFeatures = new double[numTest][dataset.getNumAttributes()];
                double[] testLabels = new double[numTest];
                System.out.println("We get "+(dataset.getNumInstnaces() - numTest) + "examples for training");
                System.out.println("We get "+numTest + "examples for testing");

                int indTrain = 0, indTest = 0;
                for (int j = 0; j < dataset.getNumInstnaces(); j++) {
                    if ((f < fold - 1 && (j < f * share || j >= (f + 1) * share)) || (f == fold - 1 && j < f * share)) {
                        System.arraycopy(features[perm[j]], 0, trainFeatures[indTrain], 0, dataset.getNumAttributes());
                        trainLabels[indTrain] = labels[perm[j]];
                        indTrain++;
                    } else {
                        System.arraycopy(features[perm[j]], 0, testFeatures[indTest], 0, dataset.getNumAttributes());
                        testLabels[indTest] = labels[perm[j]];
                        indTest++;
                    }
                }

                Classifier c = (Classifier) Class.forName(clsName).newInstance();
                c.train(isCategory, trainFeatures, trainLabels);
                System.out.println("Items for prediction:\t" +testLabels.length );
                double error = 0;
                for (int j = 0; j < testLabels.length; j++) {
                    double prediction = c.predict(testFeatures[j]);

                    if (isClassification) {
                        if (prediction != testLabels[j]) {
                            error = error + 1;
                        }
                    } else {
                        error = error + (prediction - testLabels[j]) * (prediction - testLabels[j]);
                    }
                }
                if (isClassification) {
                    measures[f] = 1 - error / testLabels.length;//accuracy = 1 - error
                } else {
                    measures[f] = Math.sqrt(error / testLabels.length);
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double[] mean_std = mean_std(measures);
        if (isClassification) {
            accMean = mean_std[0];
            accStd = mean_std[1];
        } else {
            rmseMean = mean_std[0];
            rmseStd = mean_std[1];
        }
    }

    public double[] mean_std(double[] x) {
        double[] ms = new double[2];
        int N = x.length;

        ms[0] = 0;
        for (int i = 0; i < x.length; i++) {
            ms[0] += x[i];
        }
        ms[0] /= N;

        ms[1] = 0;
        for (int i = 0; i < x.length; i++) {
            ms[1] += (x[i] - ms[0]) * (x[i] - ms[0]);
        }
        ms[1] /= (N - 1);

        return ms;
    }

    public double getAccMean() {
        return accMean;
    }

    public double getAccStd() {
        return accStd;
    }

    public double getRmseMean() {
        return rmseMean;
    }

    public double getRmseStd() {
        return rmseStd;
    }
}
