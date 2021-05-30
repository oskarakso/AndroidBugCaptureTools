package abct;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static abct.Utils.GlobalTools.setRegexpForFileFolder;

public class PopUpDuplicateFileController extends AbstractController implements Initializable {
    String textToShow = "File already exists\nPlease change file name or the file will be overwritten";
    private static String fileNameString;
    private static Boolean isCanceled = false;

    @FXML
    private Label textContent;
    @FXML
    private Button cancelButton; //returnCancel
    @FXML
    private Button saveButton; //returnSave
    @FXML
    private TextField fileNameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setRegexpForFileFolder(fileNameField);
        textContent.setText(textToShow);
        cancelButton.setTooltip(new Tooltip("Cancel and don't save anything"));
        saveButton.setTooltip(new Tooltip("Save using file name from the field above"));
        cancelButton.setOnAction((event) -> {
            isCanceled = true;
            closeStage();
        });
        saveButton.setOnAction((event) -> {
            isCanceled = false;
            closeStage();
        });
    }

    public void showPopupWindow(String fileNameStringIn) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUpDuplicateFile.fxml"));
        Stage stage = new Stage(StageStyle.UNDECORATED);
        try {
            stage.setScene(new Scene(loader.load())); //calls initialize() above
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (main != null) {
            stage.initOwner(main.getPrimaryStage()); //remove to make popup an independent window
        }
        PopUpDuplicateFileController controller = loader.getController();
        controller.initData(fileNameStringIn);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOpacity(0.85);
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        centerStage(stage);

        stage.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        fileNameString = fileNameField.getText();
        stage.close();
    }

    void initData(String fileNameString) {
        fileNameField.setText(fileNameString);
    }

    public String getFileNameString() {
        return fileNameString;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }
}

