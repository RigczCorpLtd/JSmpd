package frontend;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;


public class Controller {
    @FXML
    private ImageView preprocessingTopImageView;
    @FXML
    private ImageView preprocessingBottomImageView;
    @FXML
    private ComboBox<Classifier> featureNumberComboBox;
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


    @FXML
    public void initialize() {
        featureToggleGroup = new ToggleGroup();
        initFisher();
        initClassifiers();

    }

    private void initClassifiers() {
        classifiersComboBox.getItems().setAll(Classifier.values());
        classifiersComboBox.getSelectionModel().select(0);
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

    }

    @FXML
    public void onFeaturesSelectionSaveFile() {

    }

    @FXML
    public void onFeatureSelectionCompute() {

    }

    @FXML
    public void onClassfiersOpenFile() {

    }

    @FXML
    public void onClassifiersSaveFile() {

    }

    @FXML
    public void onClassifiersTrain() {

    }

    @FXML
    public void onClassifiersExecute() {

    }
}
