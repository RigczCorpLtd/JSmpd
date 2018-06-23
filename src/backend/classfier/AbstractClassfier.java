package backend.classfier;

import backend.db.Sample;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 * Created by Dawid on 24.06.2018 at 00:32.
 */
public class AbstractClassfier {
    protected Sample sampleToClassify;

    public AbstractClassfier(Sample sampleToClassify) {
        this.sampleToClassify = sampleToClassify;
    }

    protected DistanceResult calculateDistance(Sample trainingSample) {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double distance = euclideanDistance.compute(trainingSample.getFeatures(), sampleToClassify.getFeatures());
        return new DistanceResult(trainingSample.getClazz().getName(), distance);
    }
}
