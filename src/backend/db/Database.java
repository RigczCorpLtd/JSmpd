package backend.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class Database {


    private List<Clazz> clazzes = new ArrayList<>();
    private List<Integer> featureIds = new ArrayList<>();
    private List<Sample> allSamples;
    private int numberOfFeatures;

    public Database(File file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String firstLine = reader.readLine();
            processHeader(firstLine);
            String line;

            int id = 0;
            while ((line = reader.readLine()) != null) {
                processLine(line, id);
                id = id + 1;
            }
        }

    }

    private void processHeader(String header) throws IOException {
        numberOfFeatures = Integer.parseInt(header.substring(0, header.indexOf(',')));
        StringTokenizer tokenizer = new StringTokenizer(header.substring(header.indexOf(',')), ",");
        while (tokenizer.hasMoreTokens()) {
            featureIds.add(Integer.parseInt(tokenizer.nextToken().trim()));
        }
    }

    private void processLine(String line, int id) {
        String className = line.substring(0, line.indexOf(','));
        int classNamePos;
        if ((classNamePos = className.indexOf(' ') ) != -1) {
            className = className.substring(0, classNamePos);
        }
        Clazz clazz = getOrCreateClazz(className);
        StringTokenizer tokenizer = new StringTokenizer(line.substring(line.indexOf(',')), ",");
        double[] features = new double[numberOfFeatures];
        for (int i = 0; i < numberOfFeatures; i++) {
            features[i] = Double.parseDouble(tokenizer.nextToken());
        }
        clazz.getSamples().add(new Sample(clazz, features, id));
    }

    private Clazz getOrCreateClazz(String clazzName) {
        Optional<Clazz> clazzOptional = clazzes.stream().filter(clazz -> clazz.getName().equals(clazzName)).findAny();
        return clazzOptional.orElseGet(() -> {
            Clazz clazz = new Clazz(clazzName);
            clazzes.add(clazz);
            return clazz;
        });
    }

    public List<Clazz> getClazzes() {
        return clazzes;
    }

    public List<Integer> getFeatureIds() {
        return featureIds;
    }

    public List<Sample> getMeasurements() {
        if (allSamples == null) {
            allSamples = new ArrayList<>(clazzes.get(0).getSamples());
            for (int i = 1; i < clazzes.size(); i++) {
                allSamples.addAll(clazzes.get(i).getSamples());
            }
        }
        return allSamples;
    }

    public int getNumberOfFeatures() {
        return numberOfFeatures;
    }
}
