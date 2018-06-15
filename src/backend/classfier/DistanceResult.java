package backend.classfier;

/**
 * Created by Dawid on 15.06.2018 at 00:55.
 */
public class DistanceResult {
    public String getClassName() {
        return className;
    }

    public double getDistance() {
        return distance;
    }

    String className;
    double distance;

    public DistanceResult(String className, double distance) {
        this.className = className;
        this.distance = distance;
    }
}
