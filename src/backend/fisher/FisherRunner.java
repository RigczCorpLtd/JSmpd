package backend.fisher;

import backend.db.Database;
import org.apache.commons.math3.util.Combinations;

import java.util.Arrays;

public class FisherRunner {
    private Database database;
    private int dimension;
    private Result result;

    public FisherRunner(Database database, int dimension) {
        this.database = database;
        this.dimension = dimension;
    }

    public void run() {
        Combinations combinations = new Combinations(database.getNumberOfFeatures(), dimension);
        int[] bestCombination = null;
        double bestValue = Integer.MIN_VALUE;
        for (int[] combination : combinations) {
            Fisher fisher = new Fisher(database, combination);
            fisher.run();
            if (fisher.getValue() > bestValue) {
                bestCombination = combination;
                bestValue = fisher.getValue();
            }
        }
        result = new Result(bestValue, bestCombination);
    }

    public Result getResult() {
        return result;
    }

    public static class Result {
        public final double value;
        public final int[] combination;

        public Result(double value, int[] combination) {
            this.value = value;
            this.combination = combination;
        }

        @Override
        public String toString() {
            return "Best combination: " + Arrays.toString(combination) + " with value: " + value;
        }
    }
}
