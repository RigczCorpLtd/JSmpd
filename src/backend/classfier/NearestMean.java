package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class NearestMean {

    private final List<Sample> trainingSample;
    private final Sample sampleToClassify;
    private final Long k;
    private int featureCount;
    private final Map<Clazz, Double[]> classMean;

    public NearestMean(List<Sample> trainingSample, Sample sampleToClassify, Long k, int featureCount) {
        this.trainingSample = trainingSample;
        this.sampleToClassify = sampleToClassify;
        this.k = k;
        this.featureCount = featureCount;
        classMean = new HashMap<>();
    }

    public static String getNearestMeanClassName(List<Sample> trainingSample, Sample sampleToClassify, Long k, int featureCount) {
        return new NearestMean(trainingSample, sampleToClassify, k, featureCount).getNearestClassName();
    }

    private String getNearestClassName() {
        Map<Clazz, List<Sample>> classSamples = groupByClass();
        calculateClassMeans(classSamples);
        Map<Clazz, Double> classDistances = classMean.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateDistance(stream(e.getValue()).mapToDouble(Double::valueOf).toArray())
                ));
        return getClassWithTheLowestDistance(classDistances);
    }

    private String getClassWithTheLowestDistance(Map<Clazz, Double> classDistances) {
        return classDistances.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey().getName();
    }

    private double calculateDistance(double[] trainSampleVector) {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        return euclideanDistance.compute(trainSampleVector, sampleToClassify.getFeatures());
    }

    private void calculateClassMeans(Map<Clazz, List<Sample>> classSamples) {
        classSamples.entrySet().forEach(this::calculateMean);
    }

    private void calculateMean(Map.Entry<Clazz, List<Sample>> clazzEntry) {
        List<Sample> samples = clazzEntry.getValue();
        List<Double> means = new ArrayList<>();
        for (int featureId = 0; featureId < featureCount; featureId++) {
            final int id = featureId;
            samples.stream().mapToDouble(s -> s.getFeatureById(id)).average().ifPresent(means::add);
        }

        classMean.putIfAbsent(clazzEntry.getKey(), means.toArray(new Double[means.size()]));
    }

    private Map<Clazz, List<Sample>> groupByClass() {
        return trainingSample.stream().collect(Collectors.groupingBy(Sample::getClazz));
    }
}
