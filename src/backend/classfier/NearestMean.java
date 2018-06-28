package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class NearestMean extends AbstractClassfier {

    private final List<Sample> trainingSample;
    private final List<Sample> samplesToClassify;
    private int featureCount;
    private final Map<Clazz, Double[]> classMean;

    public NearestMean(List<Sample> trainingSample, List<Sample> samplesToClassify, int featureCount) {
        this.trainingSample = trainingSample;
        this.samplesToClassify = samplesToClassify;
        this.featureCount = featureCount;
        classMean = new HashMap<>();
    }

    @Override
    public List<ClassfierResult> classify() {
        Map<Clazz, List<Sample>> trainingSamples = groupByClass();
        calculateClassMeans(trainingSamples);

        return samplesToClassify.stream().map(this::getNearestClassName).collect(Collectors.toList());
    }

    private ClassfierResult getNearestClassName(Sample sampleToClassify) {
        Map<Clazz, Double> classDistances = classMean.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateDistance(stream(e.getValue()).mapToDouble(Double::valueOf).toArray(), sampleToClassify)
                ));
        Clazz classWithTheLowestDistance = getClassWithTheLowestDistance(classDistances);
        return new ClassfierResult(sampleToClassify, classWithTheLowestDistance);
    }

    private Clazz getClassWithTheLowestDistance(Map<Clazz, Double> classDistances) {
        return classDistances.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
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
