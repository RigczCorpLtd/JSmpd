package backend.classfier;

import backend.db.Database;
import backend.db.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Dawid on 15.06.2018 at 00:35.
 */
public class ClassfierEngine {
    private final Long trainingPart;
    private final int featureCount;
    private Long k;
    private final List<Sample> measurements;
    private List<Sample> trainingSamples;
    private List<Sample> samplesToClassify;

    public ClassfierEngine(Database database, Long trainingPart, Integer k) {
        this.trainingPart = trainingPart;
        this.k = Long.valueOf(k);
        measurements = database.getMeasurements();
        trainingSamples = new ArrayList<>();
        featureCount = database.getFeatureIds().size();
    }

    public void prepareSamples() {
        prepareTrainingSamples();
        prepareTestSamples();
    }

    public double nearestMean() {
        NearestMean nearestMean = new NearestMean(trainingSamples, samplesToClassify, featureCount);
        return getCorrectlyClassifyPercentage(nearestMean);
    }

    public double kNearestMean() {
        KNM knm = new KNM(trainingSamples, samplesToClassify, k, featureCount);
        return getCorrectlyClassifyPercentage(knm);
    }


    public double nearestNeighborhood() {
        NearestNeighborhood nearestNeighborhood = new NearestNeighborhood(trainingSamples, samplesToClassify, k);
        return getCorrectlyClassifyPercentage(nearestNeighborhood);
    }

    private double getCorrectlyClassifyPercentage(AbstractClassfier abstractClassfier) {
        long correctClassify = abstractClassfier.classify().stream().filter(ClassfierResult::isClassfieCorrectly).count();
        return ((double) correctClassify / samplesToClassify.size() * 100);
    }

    private void prepareTestSamples() {
        samplesToClassify = new ArrayList<>();
        samplesToClassify.addAll(measurements);
        samplesToClassify.removeAll(trainingSamples);
    }

    private void prepareTrainingSamples() {
        Random rand = new Random();
        List<Sample> copiedList = new ArrayList<>();
        copiedList.addAll(measurements);


        int trainingPartSize = (int) (measurements.size() * (trainingPart / 100d));

        for (int i = 0; i < trainingPartSize; i++) {
            int randomIndex = rand.nextInt(copiedList.size());
            Sample randomElement = copiedList.get(randomIndex);
            copiedList.remove(randomIndex);
            trainingSamples.add(randomElement);
        }
    }

    public void setK(Long k) {
        this.k = k;
    }

    public Long getK() {
        return k;
    }

    public List<Sample> getTrainingSamples() {
        return trainingSamples;
    }
}
