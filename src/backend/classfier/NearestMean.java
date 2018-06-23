package backend.classfier;

import backend.db.Sample;

import java.util.List;

public class NearestMean {

    private final List<Sample> trainingSample;
    private final Sample sampleToClassify;
    private final Long k;

    public NearestMean(List<Sample> trainingSample, Sample sampleToClassify, Long k) {
        this.trainingSample = trainingSample;
        this.sampleToClassify = sampleToClassify;
        this.k = k;
    }

    public static String getNearestMeanClassName(List<Sample> trainingSample, Sample sampleToClassify, Long k) {
        return new NearestMean(trainingSample, sampleToClassify, k).getNearestClassName();
    }

    private String getNearestClassName() {
        return null;
    }
}
