package abct;

import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static abct.MainViewController.installationLogs;
import static abct.Utils.GlobalTools.arrayToString;

public class PopUpLogsController extends AbstractController implements Initializable {
    private static ArrayList<String> installationLogsText = new ArrayList<String>();

    @FXML
    private TextArea logsTextArea;
    @FXML
    private Button returnButton;

    @FXML
    void returnAction(ActionEvent event) {
        closeStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        returnButton.setOnAction((event) -> {
            closeStage();
        });
        logsTextArea.setText(arrayToString(installationLogsText));
        logsTextArea.selectPositionCaret(logsTextArea.getLength()); //Auto-scroll to newest logs
        logsTextArea.deselect();
    }

    public void showPopupWindow(ArrayList<String> logs) {
        installationLogsText = logs;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUpLogs.fxml"));
        Stage popupLogsStage = new Stage(StageStyle.UNDECORATED);
        try {
            popupLogsStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (main != null) {
            popupLogsStage.initOwner(main.getPrimaryStage());
        }
        setUpPopUpStage(popupLogsStage);
    }

    private void closeStage() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cleanLogs(){
        installationLogs.clear();
        installationLogsText.clear();
        logsTextArea.setText("Cleaned!");
    }
}