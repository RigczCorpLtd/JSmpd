package backend.validator;

import backend.classfier.AbstractClassfier;
import backend.classfier.ClassfierResult;
import backend.classfier.NearestMean;
import backend.classfier.NearestNeighborhood;
import backend.db.Database;
import backend.db.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bootstrap {
    private final Database database;
    private final int sampleSize;
    private final int numberOfIterations;
    private List<Sample> trainingSample;

    public Bootstrap(Database database, int sampleSize, int numberOfIterations, List<Sample> trainingSample) {
        this.database = database;
        this.sampleSize = sampleSize;
        this.numberOfIterations = numberOfIterations;
        this.trainingSample = trainingSample;
    }

    public Result compute(long k) {
        double NN = 0;
        double kNN = 0;
        double MM = 0;
        double kNM = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            List<Sample> samples = getRandomSamples();
            NN += nearestNeighborhood(trainingSample, samples, 1L);
            kNN += nearestNeighborhood(trainingSample, samples, k);
            MM += nearestMean(trainingSample, samples,  database.getNumberOfFeatures());
        }
        return new Result(NN/ numberOfIterations, kNN / numberOfIterations, MM / numberOfIterations);
    }

    public double nearestMean(List<Sample> trainingSamples, List<Sample> samplesToClassify, int featureCount) {
        NearestMean nearestMean = new NearestMean(trainingSamples, samplesToClassify, featureCount);
        return getCorrectlyClassifyPercentage(nearestMean, samplesToClassify);
    }

    public double nearestNeighborhood(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k) {
        NearestNeighborhood nearestNeighborhood = new NearestNeighborhood(trainingSamples, samplesToClassify, k);
        return getCorrectlyClassifyPercentage(nearestNeighborhood, samplesToClassify);
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

        for (int i = 0; i < sampleSize; i++) {
            int randomIndex = rand.nextInt(copiedList.size());
            Sample randomElement = copiedList.get(randomIndex);
            copiedList.remove(randomIndex);
            trainingSamples.add(randomElement);
        }

        return trainingSamples;
    }

    public static class Result {
        public final double NNavg;
        public final double kNNavg;
        public final double MMavg;

        public Result(double NNavg, double kNNavg, double MMavg) {
            this.NNavg = NNavg;
            this.kNNavg = kNNavg;
            this.MMavg = MMavg;
        }
    }
}
