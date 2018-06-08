package backend.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clazz {

    private String name;

    private List<Measurement> measurements = new ArrayList<>();

    public Clazz(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clazz clazz = (Clazz) o;
        return Objects.equals(name, clazz.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public int getNumberOfMeasurements() {
        return measurements.size();
    }

    public String getName() {
        return name;
    }
}
