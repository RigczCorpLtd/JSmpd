package backend.classfier;

import backend.db.Sample;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.List;

/**
 * Created by Dawid on 24.06.2018 at 00:32.
 */
public abstract class AbstractClassfier {

    public abstract List<ClassfierResult> classify();

    protected double calculateDistance(double[] trainSampleVector, Sample sampleToClassify) {
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        return euclideanDistance.compute(trainSampleVector, sampleToClassify.getFeatures());
    }
}
