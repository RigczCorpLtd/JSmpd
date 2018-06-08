package backend.db;

public class Sample {
    private Clazz clazz;
    private double[] features;

    Sample(Clazz clazz, double[] features) {
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
