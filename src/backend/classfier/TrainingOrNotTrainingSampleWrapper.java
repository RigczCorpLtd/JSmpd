package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;

/**
 * Created by Dawid on 28.06.2018 at 21:25.
 */
public class TrainingOrNotTrainingSampleWrapper extends Sample {
    private boolean training;
    private Clazz clazzAfterClassification;

    public TrainingOrNotTrainingSampleWrapper(Sample sample, boolean training) {
        super(sample.getClazz(), sample.getFeatures(), sample.getId());
        this.training = training;

        if (training) {
            clazzAfterClassification = sample.getClazz();
        }
    }

    public boolean isTraining() {
        return training;
    }

    public boolean isNotTrainingSample() {
        return !isTraining();
    }

    public Clazz getClazzAfterClassification() {
        return clazzAfterClassification;
    }

    public void setClazzAfterClassification(Clazz clazzAfterClassification) {
        this.clazzAfterClassification = clazzAfterClassification;
    }
}
