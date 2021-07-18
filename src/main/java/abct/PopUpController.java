package abct;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PopUpController extends AbstractController implements Initializable {
    private static String text;

    @FXML
    private Label textContent;
    @FXML
    private Button returnButton;

    @FXML
    void returnAction(ActionEvent event) {
        closeStage();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textContent.setText(text);
        returnButton.setOnAction((event) -> {
            closeStage();
        });
    }

    public void showPopupWindow(String textToShow) {
        text = textToShow;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUp.fxml"));
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

}

