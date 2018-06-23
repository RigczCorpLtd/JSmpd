package backend.classfier;

import backend.db.Database;
import backend.db.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static backend.classfier.NearestMean.getNearestMeanClassName;
import static backend.classfier.NearestNeighborhood.getNearestClassName;

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
        Long correctClassify = 0L;
        for (Sample sampleToClassify : samplesToClassify) {
            String nearestClassName = getNearestMeanClassName(trainingSamples, sampleToClassify, k, featureCount);

            if (nearestClassName.equals(sampleToClassify.getClazz().getName())) {
                correctClassify = correctClassify + 1;
            }
        }

        return ((double) correctClassify / samplesToClassify.size() * 100);
    }

    public double nearestNeighborhood() {
        Long correctClassify = 0L;
        for (Sample sampleToClassify : samplesToClassify) {
            String nearestClassName = getNearestClassName(trainingSamples, sampleToClassify, k);

            if (nearestClassName.equals(sampleToClassify.getClazz().getName())) {
                correctClassify = correctClassify + 1;
            }
        }

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
}
