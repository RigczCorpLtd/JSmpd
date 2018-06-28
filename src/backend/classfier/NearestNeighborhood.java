package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by Dawid on 15.06.2018 at 00:23.
 */
public class NearestNeighborhood extends AbstractClassfier {

    private final List<Sample> trainingSamples;
    private List<Sample> samplesToClassify;
    private final Long k;

    public NearestNeighborhood(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k) {
        this.trainingSamples = trainingSamples;
        this.samplesToClassify = samplesToClassify;
        this.k = k;
    }


    @Override
    public List<ClassfierResult> classify() {
        return samplesToClassify.stream().map(this::getNearestClassName).collect(Collectors.toList());
    }

    private ClassfierResult getNearestClassName(Sample sampleToClassify) {
        List<DistanceResult> distanceResult = trainingSamples.stream()
                .map(trainingSample -> calculateDistance(trainingSample, sampleToClassify))
                .collect(toList());
        Clazz theBestResult = getTheBestResult(distanceResult);
        return new ClassfierResult(sampleToClassify, theBestResult);
    }

    private Clazz getTheBestResult(List<DistanceResult> distanceResult) {
        Map<Clazz, Long> classNameCountResult = distanceResult.stream()
                .sorted(comparing(DistanceResult::getDistance))
                .limit(k)
                .collect(groupingBy(DistanceResult::getClazz, Collectors.counting()));
        return getTheBestResult(classNameCountResult);
    }

    private Clazz getTheBestResult(Map<Clazz, Long> classNameCountResult) {
        return classNameCountResult.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    private DistanceResult calculateDistance(Sample trainingSample, Sample sampleToClassify) {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double distance = euclideanDistance.compute(trainingSample.getFeatures(), sampleToClassify.getFeatures());
        return new DistanceResult(trainingSample.getClazz(), distance);
    }
}
