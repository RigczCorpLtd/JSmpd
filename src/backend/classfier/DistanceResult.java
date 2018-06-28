package backend.classfier;

import backend.db.Clazz;

/**
 * Created by Dawid on 15.06.2018 at 00:55.
 */
public class DistanceResult {
    private Clazz clazz;
    private double distance;

    public Clazz getClazz() {
        return clazz;
    }

    public double getDistance() {
        return distance;
    }


    public DistanceResult(Clazz clazz, double distance) {
        this.clazz = clazz;
        this.distance = distance;
    }
}
