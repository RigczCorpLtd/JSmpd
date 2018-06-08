package frontend;

/**
 * Created by Dawid on 08.06.2018 at 01:29.
 */
public enum Classifier {
    NN(true, "NN"),
    kNN(false, "k-NN"),
    NM(true, "MM"),
    kNM(false, "k-NM");

    private final boolean normal;
    private String name;

    Classifier(boolean normal, String name) {
        this.normal = normal;
        this.name = name;
    }

    public boolean isNormal() {
        return normal;
    }

    public String toString() {
        return name;
    }
}
