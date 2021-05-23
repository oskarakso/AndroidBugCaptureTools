package abct;

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

import static abct.Utils.GlobalTools.arrayToString;

public class PopUpLogsController extends AbstractController implements Initializable {
    private Stage stage = null;
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
    }
    //todo: refactor
    public void showPopupWindow(ArrayList<String> logs) {
        installationLogsText = logs;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/PopUpLogs.fxml"));
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupLogsStage = new Stage();
            this.setStage(popupLogsStage);
            if (main != null) {
                popupLogsStage.initOwner(main.getPrimaryStage());
            }
            popupLogsStage.setScene(scene);
            centerStage(popupLogsStage);
            popupLogsStage.initModality(Modality.WINDOW_MODAL);
            popupLogsStage.setTitle("ERROR");
            popupLogsStage.initStyle(StageStyle.UNDECORATED);
            popupLogsStage.setOpacity(0.85);
            popupLogsStage.getScene().setFill(Color.TRANSPARENT);
            popupLogsStage.initStyle(StageStyle.TRANSPARENT);
            popupLogsStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = new Stage();
    }

    private void closeStage() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }
}