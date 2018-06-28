package backend.validator;

import backend.classfier.AbstractClassfier;
import backend.classfier.ClassfierResult;
import backend.classfier.NearestMean;
import backend.classfier.NearestNeighborhood;
import backend.db.Database;
import backend.db.Sample;
import frontend.Classifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Crossvalidation {
    private final Database database;
    private double sampleSizePercentage;

    public Crossvalidation(Database database, double sampleSizePercentage) {
        this.database = database;
        this.sampleSizePercentage = sampleSizePercentage;
    }

    public Result compute(long k) {
        double NN = 0;
        double kNN = 0;
        double MM = 0;
        double kNM = 0;
        double sliceFraction = sampleSizePercentage / 100d;
        int numberOfMeasurments = database.getMeasurements().size();
        int sliceSize = (int) Math.floor(numberOfMeasurments * sliceFraction);
        List<Sample> measurements = database.getMeasurements();
        List<List<Sample>> slices = getSlices(numberOfMeasurments, sliceSize);
        for (List<Sample> slice : slices) {
            List<Sample> analyzedSample = new ArrayList<>(measurements);
            analyzedSample.removeAll(slice);
            NN+= nearestNeighborhood(slice, analyzedSample, 1L);
            kNN+= nearestNeighborhood(slice, analyzedSample, k);
            MM+= nearestMean(slice, analyzedSample, 1L, database.getNumberOfFeatures());
        }
        return new Result(NN/ slices.size(), kNN / slices.size(), MM / slices.size());
    }

    private List<List<Sample>> getSlices(int numberOfMeasurments, int sliceSize) {
        List<List<Sample>> slices = new ArrayList<>();
        List<Sample> cuurentSlice = new ArrayList<>();
        for (int i = 0; i < numberOfMeasurments; i++) {
            if (numberOfMeasurments % sliceSize == 0) {
                cuurentSlice = new ArrayList<>();
                slices.add(cuurentSlice);
            }
            cuurentSlice.add(database.getMeasurements().get(i));
        }
        return slices;
    }

    public double nearestMean(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k, int featureCount) {
        NearestMean nearestMean = new NearestMean(trainingSamples, samplesToClassify, k, featureCount);
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
