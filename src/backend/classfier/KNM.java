package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

/**
 * Created by Dawid on 28.06.2018 at 19:49.
 */
public class KNM extends AbstractClassfier {
    public static final double EPS = 0.00001;
    private final Long k;
    private final List<TrainingOrNotTrainingSampleWrapper> training;
    private final List<TrainingOrNotTrainingSampleWrapper> testing;
    private int featureCount;
    private final List<TrainingOrNotTrainingSampleWrapper> allSamples;
    private List<Double[]> centralPoints;

    public KNM(List<Sample> trainingSamples, List<Sample> samplesToClassify, Long k, int featureCount) {
        this.k = k;
        this.featureCount = featureCount;
        this.training = mapToTrainingOrNotTrainingSample(trainingSamples, true);
        this.testing = mapToTrainingOrNotTrainingSample(samplesToClassify, false);
        this.allSamples = Stream.concat(training.stream(), testing.stream())
                .collect(toList());
    }

    private List<TrainingOrNotTrainingSampleWrapper> mapToTrainingOrNotTrainingSample(List<Sample> trainingSamples, boolean b) {
        return trainingSamples.stream().map(sample -> new TrainingOrNotTrainingSampleWrapper(sample, b)).collect(toList());
    }

    @Override
    public List<ClassfierResult> classify() {
        randCentralPoints();
        List<Double[]> newCentralPoints = centralPoints;
        Map<Double[], List<TrainingOrNotTrainingSampleWrapper>> groupedSamples;
        do {
            this.centralPoints = newCentralPoints;
            groupedSamples = allSamples.stream().collect(groupingBy(this::group));
            newCentralPoints = groupedSamples.entrySet().stream().map(this::toNewCentralPoints).collect(toList());
        } while (centerPointsChangePosition(newCentralPoints));

        groupedSamples.entrySet().forEach(this::recognizeClassAndMarkSamplesToClassify);

        return allSamples.stream()
                .filter(TrainingOrNotTrainingSampleWrapper::isNotTrainingSample)
                .map(sample -> new ClassfierResult(sample, sample.getClazzAfterClassification()))
                .collect(toList());
    }

    private void recognizeClassAndMarkSamplesToClassify(Map.Entry<Double[], List<TrainingOrNotTrainingSampleWrapper>> entry) {
        List<TrainingOrNotTrainingSampleWrapper> samples = entry.getValue();
        Clazz clazz = recognizeClass(samples);

        samples.stream().filter(TrainingOrNotTrainingSampleWrapper::isNotTrainingSample).forEach(sample -> sample.setClazzAfterClassification(clazz));
    }

    private Clazz recognizeClass(List<TrainingOrNotTrainingSampleWrapper> samples) {
        List<TrainingOrNotTrainingSampleWrapper> training = samples.stream().filter(TrainingOrNotTrainingSampleWrapper::isTraining).collect(toList());
        Map<Clazz, Long> classCount = training.stream().collect(groupingBy(Sample::getClazz, counting()));
        return classCount.entrySet().stream().max(comparing(Map.Entry::getValue)).get().getKey();
    }

    private boolean centerPointsChangePosition(List<Double[]> newCentralPoints) {
        for (int i = 0; i < newCentralPoints.size(); i++) {
            Double[] newCentralPoint = newCentralPoints.get(i);
            Double[] oldCentralPoint = centralPoints.get(i);

            for (int j = 0; j < newCentralPoint.length; j++) {
                if (!Precision.equals(newCentralPoint[j], oldCentralPoint[j], EPS)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Double[] toNewCentralPoints(Map.Entry<Double[], List<TrainingOrNotTrainingSampleWrapper>> entry) {
        List<TrainingOrNotTrainingSampleWrapper> samples = entry.getValue();
        List<Double> means = new ArrayList<>();
        for (int featureId = 0; featureId < featureCount; featureId++) {
            final int id = featureId;
            samples.stream().mapToDouble(s -> s.getFeatureById(id)).average().ifPresent(means::add);
        }

        return toObject(means.stream().mapToDouble(Double::doubleValue).toArray());
    }

    private Double[] group(TrainingOrNotTrainingSampleWrapper sample) {
        Map<Double[], Double> centralPointDistance = centralPoints.stream().collect(toCentralPointDistanceMap(sample));
        return centralPointDistance.entrySet().stream().min(comparing(Map.Entry::getValue)).get().getKey();
    }

    private Collector<Double[], ?, Map<Double[], Double>> toCentralPointDistanceMap(TrainingOrNotTrainingSampleWrapper sample) {
        return toMap(centralPoint -> centralPoint, centralPoint -> calculateDistance(toPrimitive(centralPoint), sample));
    }

    private void randCentralPoints() {
        Random rand = new Random();
        List<Double[]> centralPoints = new ArrayList<>();
        List<Sample> copy = new ArrayList<>();
        copy.addAll(training);

        for (int i = 0; i < k; i++) {
            int randomIndex = rand.nextInt(copy.size());
            Sample randomElement = copy.get(randomIndex);
            copy.remove(randomIndex);
            centralPoints.add(toObject(randomElement.getFeatures()));
        }

        this.centralPoints = centralPoints;
    }
}
