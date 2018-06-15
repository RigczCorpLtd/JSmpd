package backend.classfier;

import backend.db.Database;
import backend.db.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static backend.classfier.NearestNeighborhood.getNearestClassName;

/**
 * Created by Dawid on 15.06.2018 at 00:35.
 */
public class ClassfierEngine {
    private final Long trainingPart;
    private final Long k;
    private final List<Sample> measurements;
    private List<Sample> trainingSamples;
    private List<Sample> samplesToClassify;

    public ClassfierEngine(Database database, Long trainingPart, Integer k) {
        this.trainingPart = trainingPart;
        this.k = Long.valueOf(k);
        measurements = database.getMeasurements();
        trainingSamples = new ArrayList<>();
    }


    public double nearestNeighborhood() {
        prepareTrainingSamples();
        prepareTestSamples();

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

        for (int i = 0; i <= trainingPartSize; i++) {
            int randomIndex = rand.nextInt(copiedList.size());
            Sample randomElement = copiedList.get(randomIndex);
            copiedList.remove(randomIndex);
            trainingSamples.add(randomElement);
        }
    }
}
