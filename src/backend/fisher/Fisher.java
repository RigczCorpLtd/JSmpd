package backend.fisher;

import backend.db.Clazz;
import backend.db.Database;
import backend.db.Sample;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Fisher {
    private int[] combinationOfFeatures;
    private Database database;
    private Map<Clazz, RealMatrix> avgMatrices = new HashMap<>();
    private Map<Clazz, RealMatrix> covarianceMatrices = new HashMap<>();
    private int numberOfFeaturesChosen;
    private double value;


    public Fisher(Database database, int[] combinationOfFeatures) {
        this.combinationOfFeatures = combinationOfFeatures;
        this.database = database;
        this.numberOfFeaturesChosen = combinationOfFeatures.length;
    }

    public void run() {
        if (database.getClazzes().size() != 2) {
            throw new RuntimeException("Cannot compute fisher for any other number of classes than 2");
        }
        computeAvgMatrices();
        computeCovarianceMatrices();
        computeFisher();
    }

    private void computeAvgMatrices() {
        for (Clazz clazz : database.getClazzes()) {
            RealMatrix avgMatrix = MatrixUtils.createRealMatrix(numberOfFeaturesChosen, 1);
            for (Sample sample : clazz.getSamples()) {
                for (int i = 0; i < numberOfFeaturesChosen; i++) {
                    avgMatrix.setEntry(i, 0, avgMatrix.getEntry(i, 0) + sample.getFeatureById(combinationOfFeatures[i]));
                }
            }
            avgMatrices.put(clazz,avgMatrix.scalarMultiply(1D/clazz.getNumberOfMeasurements()));
        }
    }

    private void computeCovarianceMatrices() {
        for (Clazz clazz : database.getClazzes()) {
            RealMatrix covarianceMatrix = initCovarianceMatrixForClass(clazz);
            covarianceMatrix = covarianceMatrix.multiply(covarianceMatrix.transpose()).scalarMultiply(1D/ clazz.getNumberOfMeasurements());
            covarianceMatrices.put(clazz, covarianceMatrix);
        }
    }

    private RealMatrix initCovarianceMatrixForClass(Clazz clazz) {
        RealMatrix covarianceMatrix = MatrixUtils.createRealMatrix(numberOfFeaturesChosen, clazz.getNumberOfMeasurements());
        covarianceMatrices.put(clazz, covarianceMatrix);
        for (int j = 0; j < clazz.getNumberOfMeasurements(); j++) {
            for (int i = 0; i < numberOfFeaturesChosen; i++) {
                covarianceMatrix.setEntry(i, j,
                        clazz.getSamples().get(j).getFeatureById(combinationOfFeatures[i]) - avgMatrices.get(clazz).getEntry(i, 0));
            }
        }
        return covarianceMatrix;
    }

    private void computeFisher() {
        RealMatrix sumOfAllCovarianceMats = covarianceMatrices.entrySet().stream()
                .map(Map.Entry::getValue)
                .reduce(MatrixUtils.createRealMatrix(numberOfFeaturesChosen, numberOfFeaturesChosen), RealMatrix::add);
        double determent = new LUDecomposition(sumOfAllCovarianceMats).getDeterminant();
        Iterator<Map.Entry<Clazz, RealMatrix>> iterator = avgMatrices.entrySet().iterator();
        double distance = new EuclideanDistance().compute(
                iterator.next().getValue().getColumn(0),
                iterator.next().getValue().getColumn(0));
        value = distance/determent;

    }

    public double getValue() {
        return value;
    }
}
