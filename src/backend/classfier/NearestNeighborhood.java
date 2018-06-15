package backend.classfier;

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
public class NearestNeighborhood {

    private final List<Sample> trainingSamples;
    private final Sample sampleToClassify;
    private final Long k;

    public NearestNeighborhood(List<Sample> trainingSamples, Sample sampleToClassify, Long k) {
        this.trainingSamples = trainingSamples;
        this.sampleToClassify = sampleToClassify;
        this.k = k;
    }

    public static String getNearestClassName(List<Sample> trainingSample, Sample sampleToClassify, Long k) {
        return new NearestNeighborhood(trainingSample, sampleToClassify, k).getNearestClassName();
    }

    private String getNearestClassName() {
        List<DistanceResult> distanceResult = trainingSamples.stream().map(this::calculateDistance).collect(toList());
        return getTheBestResult(distanceResult);
    }

    private String getTheBestResult(List<DistanceResult> distanceResult) {
        Map<String, Long> classNameCountResult = distanceResult.stream()
                .sorted(comparing(result -> result.distance))
                .limit(k)
                .collect(groupingBy(DistanceResult::getClassName, Collectors.counting()));
        return getTheBestResult(classNameCountResult);
    }

    private String getTheBestResult(Map<String, Long> classNameCountResult) {
        return classNameCountResult.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    private DistanceResult calculateDistance(Sample trainingSample) {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double distance = euclideanDistance.compute(trainingSample.getFeatures(), sampleToClassify.getFeatures());
        return new DistanceResult(trainingSample.getClazz().getName(), distance);
    }
}
