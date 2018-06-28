package backend.db;

import java.util.Objects;

public class Sample {
    private Clazz clazz;

    public double[] getFeatures() {
        return features;
    }

    private double[] features;
    private int id;

    public Sample(Clazz clazz, double[] features, int id) {
        this.clazz = clazz;
        this.features = features;
        this.id = id;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public double getFeatureById(int id) {
        return features[id];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sample)) return false;
        Sample sample = (Sample) o;
        return id == sample.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }
}
