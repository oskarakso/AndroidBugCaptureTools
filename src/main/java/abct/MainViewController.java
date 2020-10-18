package abct;

import abct.adb_tools.InstallApk;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static abct.adb_tools.getDevices.getDevicesList;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class MainViewController extends AbstractController implements Initializable {
    boolean isStarted = false;
    protected Map<String, String> devices;


    @FXML
    private Text focus_loser;
    @FXML
    public TextField folderPickerTextBox;
    @FXML
    public TextField apkPickerTextBox;
    @FXML
    public TextField apkInstallResult;
    @FXML
    private Button folderPickerButton;
    @FXML
    private Button start_stop;
    @FXML
    private Button minimize;
    @FXML
    public Button apkInstall;
    @FXML
    private Button close_app;
    @FXML
    public ComboBox<String> combo_box1;

    @FXML
    private void apkInstall() {
        if(null == combo_box1.getValue() || combo_box1.isDisabled() || combo_box1.getValue().contains("No device")){
                PopUpController popUpController = new PopUpController();
                popUpController.showPopupWindow("Missing device");
        } else if (null == apkPickerTextBox.getText() || apkPickerTextBox.getText().equals("")){
                PopUpController popUpController = new PopUpController();
                popUpController.showPopupWindow("Missing apk file location");
        } else {
        InstallApk installApk = new InstallApk(this);
        Thread t1 = new Thread(installApk);
        t1.start();
        }
    }

    @FXML
    private void openFolderSelector() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(main.getPrimaryStage());
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        try {
            folderPickerTextBox.setText(selectedDirectory.toString());
        } catch (Exception ignored) {
        }
        alignmentDoinger(folderPickerTextBox);
    }

    @FXML
    private void openFileSelector() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("APK", "*.apk"));
        File selectedDirectory = fileChooser.showOpenDialog(main.getPrimaryStage());
        try {
            apkPickerTextBox.setText(selectedDirectory.toString());
        } catch (Exception ignored) {
        }
        alignmentDoinger(apkPickerTextBox);
    }

    @FXML
    private void folderPickerTextBoxUnHover() {
        focus_loser.requestFocus();
        alignmentDoinger(folderPickerTextBox);
    }

    @FXML
    private void folderPickerTextBoxClick() {
        alignmentDoinger(folderPickerTextBox);
    }

    @FXML
    private void minimizeApp() {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void closeApp() {
        //TODO: ADD BLOCKER IF INSTALL IN PROGRESS
        if (isStarted) {
            PopUpController popUpController = new PopUpController();
            popUpController.showPopupWindow("SERVICE IS RUNNING\n" +
                    "PLEASE STOP BEFORE CLOSING");
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void start_stop_press(ActionEvent event) {
        if (!isStarted) {
            this.isStarted = true;
            start_stop.setStyle("-fx-background-color: #c2000d;");
            start_stop.setText("STOP");
        } else {
            this.isStarted = false;
            start_stop.setStyle("-fx-background-color: #00b312;");
            start_stop.setText("START");
        }
    }

    @FXML
    private void refreshDevicesPress(ActionEvent event) throws IOException {
        devices = getDevicesList();
        UpdateDevicesList();
    }

    @FXML
    private void apkPickerTextBoxUnHover() {
        focus_loser.requestFocus();
        alignmentDoinger(apkPickerTextBox);
    }

    @FXML
    private void apkPickerTextBoxClick() {
        alignmentDoinger(apkPickerTextBox);
    }

    public void UpdateDevicesList() {
        // If user disconnect selected device and refresh list
        // there is an empty field but on second click it's not.
        // No idea wtf if going on and how to fix it, spent too much time already on this :)
        ObservableList<String> choiceBox = FXCollections.observableArrayList();
        for (Map.Entry<String, String> entry : devices.entrySet()) {
            choiceBox.add(entry.getKey() + " - " + entry.getValue());
        }
        if (choiceBox.size() == 0) {
            combo_box1.getSelectionModel().clearSelection();
            combo_box1.setItems(null);
            combo_box1.valueProperty().setValue("");
            combo_box1.valueProperty().set("No devices available");
            combo_box1.setDisable(true);
        } else if (combo_box1.getSelectionModel().getSelectedIndex() == -1) {
            combo_box1.setItems(choiceBox);
            combo_box1.getSelectionModel().clearSelection();
            combo_box1.valueProperty().set("No device selected");
            combo_box1.setDisable(false);
        } else {
            combo_box1.setItems(choiceBox);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void alignmentDoinger(TextField field) {
        if ("".equals(field.getText())) {
            field.setAlignment(Pos.CENTER);
            if (field.focusedProperty().getValue()) {
                field.setAlignment(Pos.CENTER_LEFT);
            }
        } else
            field.setAlignment(Pos.CENTER_LEFT);
    }

    public String getApkPath() {
        return apkPickerTextBox.getText();
    }
    public String getDevice() {
        return combo_box1.getValue().split("-", 2)[0];
    }

}