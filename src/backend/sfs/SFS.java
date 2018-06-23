package backend.sfs;

import backend.db.Database;
import backend.fisher.Fisher;

import java.util.*;

public class SFS {
    private final int dimension;
    private List<Integer> result;
    private Database database;
    private Set<Integer> featuresLeft;

    public SFS(Database database, int dimension) {
        this.database = database;
        this.dimension = dimension;
        featuresLeft = new HashSet<>(database.getFeatureIds());
        this.result = new ArrayList<>();
    }

    public void compute() {
        while (result.size() != dimension) {
            double bestValue = Double.MIN_VALUE;
            int bestFeature = -1;
            for (Integer feature : featuresLeft) {
                List<Integer> workingC = new ArrayList<>(result);
                workingC.add(feature);
                double fisher = Fisher.compute(database, workingC);
                if (fisher > bestValue) {
                    bestValue = fisher;
                    bestFeature = feature;
                }
            }
            result.add(bestFeature);
            featuresLeft.remove(bestFeature);
        }
    }

    public List<Integer> getResult() {
        return result;
    }
}
