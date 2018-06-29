package backend.validator;

import backend.classfier.AbstractClassfier;
import backend.classfier.ClassfierResult;
import backend.classfier.KNM;
import backend.classfier.NearestMean;
import backend.classfier.NearestNeighborhood;
import backend.db.Database;
import backend.db.Sample;
import com.google.common.collect.Lists;
import frontend.Classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crossvalidation {
    private final Database database;
    private List<List<Sample>> slices = null;

    public Crossvalidation(Database database, int sliceCount) {
        this.database = database;
        slices = getSlices(database.getMeasurements().size(), sliceCount);

    }

    public double compute(long k, Classifier classifier) {
        double sum = 0;
        List<Sample> measurements = database.getMeasurements();

        for (List<Sample> slice : slices) {
            List<Sample> analyzedSample = new ArrayList<>(measurements);
            analyzedSample.removeAll(slice);
            switch (classifier) {
                case NM:
                    sum += nearestMean(slice, analyzedSample,  database.getNumberOfFeatures());
                    break;
                case NN:
                    sum += nearestNeighborhood(slice, analyzedSample, 1L);
                    break;
                case kNM:
                    sum += kNearestMean(slice, analyzedSample, k , database.getNumberOfFeatures());
                    break;
                case kNN:
                    sum += nearestNeighborhood(slice, analyzedSample, k);
                    break;
            }
        }
        return sum/ slices.size();
    }

    private List<List<Sample>> getSlices(int numberOfMeasurments, int sliceCount) {
        List<Sample> shuffled = new ArrayList<>(database.getMeasurements());
        Collections.shuffle(shuffled);
        return Lists.partition(shuffled, numberOfMeasurments / sliceCount);
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
}
