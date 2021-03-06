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
    private Stage stage = null;
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

    //todo: Add dynamic text resize if out of label area (Utils.computeClippedText)
    //todo: refactor
    public void showPopupWindow(String textToShow) {
        text = textToShow;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/PopUp.fxml"));

        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();
            this.setStage(popupStage);
            if (main != null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.setScene(scene);
            centerStage(popupStage);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setTitle("ERROR");
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setOpacity(0.85);
            popupStage.getScene().setFill(Color.TRANSPARENT);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.showAndWait();
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

