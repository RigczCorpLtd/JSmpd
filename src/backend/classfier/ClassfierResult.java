package backend.classfier;

import backend.db.Clazz;
import backend.db.Sample;

/**
 * Created by Dawid on 28.06.2018 at 18:21.
 */
public class ClassfierResult extends Sample {
    private Clazz resultClass;

    public ClassfierResult(Sample sample, Clazz resultClass) {
        super(sample.getClazz(), sample.getFeatures(), sample.getId());
        this.resultClass = resultClass;
    }

    public boolean isClassfieCorrectly() {
        return resultClass.equals(getClazz());
    }
}
