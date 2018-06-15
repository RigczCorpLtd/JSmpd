package backend.fisher;

import backend.db.Clazz;
import backend.db.Database;
import backend.db.Sample;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.HashMap;
import java.util.Map;

public class Fisher {
    private int[] combinationOfFeatures;
    private Database database;
    private Map<Clazz, RealMatrix> avgMatrices = new HashMap<>();
    private Map<Clazz, RealMatrix> covarianceMatrices = new HashMap<>();
    private int numberOfFeaturesChosen;
    private double value;


    public Fisher(int[] combinationOfFeatures, Database database) {
        this.combinationOfFeatures = combinationOfFeatures;
        this.database = database;
        this.numberOfFeaturesChosen = combinationOfFeatures.length;
    }

    private void method() {
        computeAvgMatrices();
        computeCovarianceMatrices();
        computeFisher();
    }

    private void computeAvgMatrices() {
        for (Clazz clazz : database.getClazzes()) {
            RealMatrix avgMatrix = avgMatrices.put(clazz, MatrixUtils.createRealMatrix(numberOfFeaturesChosen, 1));
            for (Sample sample : clazz.getSamples()) {
                for (int i = 0; i < numberOfFeaturesChosen - 1; i++) {
                    avgMatrix.setEntry(i, 1, avgMatrix.getEntry(i, 1) + sample.getFeatureById(combinationOfFeatures[i]));
                }
            }
            avgMatrix.scalarMultiply(1/clazz.getNumberOfMeasurements());
        }
    }

    private void computeCovarianceMatrices() {
        for (Clazz clazz : database.getClazzes()) {
            RealMatrix covarianceMatrix = initCovarianceMatrixForClass(clazz);
            covarianceMatrix = covarianceMatrix.multiply(covarianceMatrix.transpose()).scalarMultiply(1 / clazz.getNumberOfMeasurements());
            covarianceMatrices.put(clazz, covarianceMatrix);
        }
    }

    private RealMatrix initCovarianceMatrixForClass(Clazz clazz) {
        RealMatrix covarianceMatrix = avgMatrices.put(clazz, MatrixUtils.createRealMatrix(numberOfFeaturesChosen, clazz.getNumberOfMeasurements()));
        for (int j = 0; j < clazz.getNumberOfMeasurements(); j++) {
            for (int i = 0; i < numberOfFeaturesChosen - 1; i++) {
                covarianceMatrix.setEntry(i, j,
                        clazz.getSamples().get(j).getFeatureById(combinationOfFeatures[i]) - avgMatrices.get(clazz).getEntry(i, 1));
            }
        }
        return covarianceMatrix;
    }

    private void computeFisher() {
        RealMatrix sumOfAllCovarianceMats = covarianceMatrices.entrySet().stream()
                .map(Map.Entry::getValue)
                .reduce(MatrixUtils.createRealMatrix(numberOfFeaturesChosen, numberOfFeaturesChosen), RealMatrix::add);
        double determant = new LUDecomposition(sumOfAllCovarianceMats).getDeterminant();
       // avgMatrices.entrySet().stream().
    }
}
