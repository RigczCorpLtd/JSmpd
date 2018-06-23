package frontend;

import backend.classfier.ClassfierEngine;
import backend.db.Database;
import backend.fisher.FisherRunner;
import backend.sfs.SFS;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


public class Controller {
    @FXML
    private ImageView preprocessingTopImageView;
    @FXML
    private ImageView preprocessingBottomImageView;
    @FXML
    private ComboBox<Integer> featureNumberComboBox;
    @FXML
    private RadioButton fisherRadioButton;
    @FXML
    private RadioButton SFSRadioButton;
    @FXML
    private TextArea featureSelectionOutput;
    @FXML
    private ComboBox classifiersComboBox;
    @FXML
    private ComboBox kComboBox;
    @FXML
    private TextField trainingPart;
    @FXML
    private TextArea classfiersOutput;

    private ToggleGroup featureToggleGroup;
    private Database database;


    @FXML
    public void initialize() {
        featureToggleGroup = new ToggleGroup();
        initFisher();
        initClassifiers();

    }

    private void initClassifiers() {
        classifiersComboBox.getItems().setAll(Classifier.values());
        classifiersComboBox.getSelectionModel().select(0);
        kComboBox.setDisable(true);
    }

    private void initFisher() {
        featureToggleGroup.getToggles().addAll(fisherRadioButton, SFSRadioButton);
        fisherRadioButton.fire();
    }


    @FXML
    public void onPreprocessingSelectFolder() {

    }

    @FXML
    public void onFeaturesSelectionOpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                initDatabase(file);
                initFeatureNumberComboBox();
            } catch (IOException e) {
            }
        }
    }

    private void initFeatureNumberComboBox() {
        int size = database.getFeatureIds().size();
        List<Integer> kRange = IntStream.rangeClosed(1, size)
                .boxed().collect(toList());
        featureNumberComboBox.getItems().setAll(kRange);
        featureNumberComboBox.getSelectionModel().select(0);
    }

    @FXML
    public void onFeaturesSelectionSaveFile() {

    }

    @FXML
    public void onFeatureSelectionCompute() {
        if (fisherRadioButton.isSelected()) {
            FisherRunner fisherRunner = new FisherRunner(database, featureNumberComboBox.getSelectionModel().getSelectedItem());
            fisherRunner.run();
            featureSelectionOutput.appendText(fisherRunner.getResult().toString() + "\n");
        }
        if (SFSRadioButton.isSelected()) {
            SFS sfs = new SFS(database, featureNumberComboBox.getSelectionModel().getSelectedItem());
            sfs.compute();
            featureSelectionOutput.appendText(sfs.getResult().stream().map(p -> p.toString()).collect(Collectors.joining(",")));
        }
    }

    @FXML
    public void onClassfiersOpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                initDatabase(file);
                initKComboBox();
            } catch (IOException e) {
            }
        }
    }

    private void initDatabase(File file) throws IOException {
        database = new Database(file);

    }

    private void initKComboBox() {
        int size = database.getMeasurements().size();
        List<Integer> kRange = IntStream.rangeClosed(1, size - 1)
                .boxed().collect(toList());
        kComboBox.getItems().setAll(kRange);
        kComboBox.getSelectionModel().select(0);
    }

    @FXML
    public void onClassifiersSaveFile() {

    }

    @FXML
    public void onClassifiersTrain() {

    }

    @FXML
    public void onClassifiersExecute() {
        ClassfierEngine classfierEngine = new ClassfierEngine(database, Long.valueOf(trainingPart.getText()), (Integer) kComboBox.getSelectionModel().getSelectedItem());
        classfiersOutput.setText("Dobrze zaklasyfikowano: " + classfierEngine.nearestNeighborhood() + "%");
    }

    @FXML
    public void onClassifiersChange() {
        Classifier classifier = (Classifier) classifiersComboBox.getSelectionModel().getSelectedItem();

        kComboBox.setDisable(classifier.isNormal());

        if (classifier.isNormal()) {
            kComboBox.getSelectionModel().select(0);
        }
    }
}
