package backend.validator;

import backend.classfier.AbstractClassfier;
import backend.classfier.ClassfierResult;
import backend.classfier.KNM;
import backend.classfier.NearestMean;
import backend.classfier.NearestNeighborhood;
import backend.db.Database;
import backend.db.Sample;
import frontend.Classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bootstrap {
    private final Database database;
    private final int trainingSampleSize;
    private final int numberOfIterations;

    List<List<Sample>> trainigSamples = new ArrayList<>();
    List<List<Sample>> currents = new ArrayList<>();

    public Bootstrap(Database database, int trainingSampleSize, int numberOfIterations) {
        this.database = database;
        this.trainingSampleSize = trainingSampleSize;
        this.numberOfIterations = numberOfIterations;

        for (int i = 0; i < numberOfIterations; i++) {
            List<Sample> trainingSample = getRandomSamples();
            List<Sample> current = new ArrayList<>(database.getMeasurements());
            current.removeAll(trainingSample);
            currents.add(current);
            trainigSamples.add(trainingSample);
        }
    }

    public double compute(long k, Classifier classifier) {
        double sum = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            switch (classifier) {
                case NM:
                    sum += nearestMean(trainigSamples.get(i), currents.get(i),  database.getNumberOfFeatures());
                    break;
                case NN:
                    sum += nearestNeighborhood(trainigSamples.get(i), currents.get(i), 1L);
                    break;
                case kNM:
                    sum += kNearestMean(trainigSamples.get(i), currents.get(i), k , database.getNumberOfFeatures());
                    break;
                case kNN:
                    sum += nearestNeighborhood(trainigSamples.get(i), currents.get(i), k);
                    break;
            }

        }
        return sum/numberOfIterations;
    }

    public double nearestMean(List<Sample> trainingSamples, List<Sample> samplesToClassify, int featureCount) {
        NearestMean nearestMean = new NearestMean(trainingSamples, samplesToClassify, featureCount);
        return getCorrectlyClassifyPercentage(nearestMean, samplesToClassify);
    }

    public double nearestNeighborhood(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k) {
        NearestNeighborhood nearestNeighborhood = new NearestNeighborhood(trainingSamples, samplesToClassify, k);
        return getCorrectlyClassifyPercentage(nearestNeighborhood, samplesToClassify);
    }

    public double kNearestMean(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k, int featureCount) {
        KNM knm = new KNM(trainingSamples, samplesToClassify, k, featureCount);
        return getCorrectlyClassifyPercentage(knm, samplesToClassify);
    }

    private double getCorrectlyClassifyPercentage(AbstractClassfier abstractClassfier, List<Sample> samplesToClassify) {
        long correctClassify = abstractClassfier.classify().stream().filter(ClassfierResult::isClassfieCorrectly).count();
        return ((double) correctClassify / samplesToClassify.size() * 100);
    }


    private List<Sample> getRandomSamples() {
        List<Sample> trainingSamples = new ArrayList<>();
        Random rand = new Random();
        List<Sample> copiedList = new ArrayList<>();
        copiedList.addAll(database.getMeasurements());

        for (int i = 0; i < trainingSampleSize; i++) {
            int randomIndex = rand.nextInt(copiedList.size());
            Sample randomElement = copiedList.get(randomIndex);
            copiedList.remove(randomIndex);
            trainingSamples.add(randomElement);
        }

        return trainingSamples;
    }
}
