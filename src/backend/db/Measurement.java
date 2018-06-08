package backend.db;

import java.util.Arrays;
import java.util.List;

public class Measurement {
    private Clazz clazz;
    private double[] features;

    Measurement(Clazz clazz, double[] features) {
        this.clazz = clazz;
        this.features = features;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public double getFeatureById(int id) {
        return features[id];
    }
}
