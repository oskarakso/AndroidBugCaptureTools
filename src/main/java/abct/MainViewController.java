package abct;

import abct.adb_tools.InstallApk;
import abct.adb_tools.PackageManager;
import abct.scrcpy.Scrcpy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static abct.adb_tools.getDevices.getDevicesList;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class MainViewController extends AbstractController implements Initializable {


    public boolean isStarted = false;
    protected Map<String, String> devices;
    private String scrcpyLocation = null;

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
    public ComboBox<String> packageListComboBox;
    @FXML
    private Button uninstallApk;
    @FXML
    private Button apkClearData;
    @FXML
    private Button startApk;
    @FXML
    private Button closeApk;

    @FXML
    private void closeApk() {
        new Thread(() -> {
            PackageManager pm = new PackageManager(this);
            pm.closeApk();
        }).start();
    }

    @FXML
    private void uninstallApk() {
        new Thread(() -> {
            PackageManager pm = new PackageManager(this);
            pm.uninstallApp();
        }).start();
    }

    @FXML
    private void openApk() {
        new Thread(() -> {
            PackageManager pm = new PackageManager(this);
            pm.openApk();
        }).start();
    }

    @FXML
    private void clearApkData() {
        new Thread(() -> {
            PackageManager pm = new PackageManager(this);
            pm.clearAppData();
        }).start();
    }

    /*
    @FXML
    private void x(){
    }
     TODO:
     - disable package list if no device selected
     - disable buttons underneath
     - Click on list -> Refresh (or if slow or smth, add button)
     - Click on button -> If succesfull -> Set text as "Done!" wait for few sec (3) -> set back to default text (get it before - pass as arg.) - and maybe implement in install status field
     - think about adding run button to run app "adb shell am start -p package.name 1"
     */

    @FXML
    private void apkInstall() throws IOException {
        if (null == getDevice()) {
            showPopup("No device selected!");
        } else if (null == apkPickerTextBox.getText() || apkPickerTextBox.getText().equals("")) {
            showPopup("Missing apk file location!");
        } else {
            InstallApk installApk = new InstallApk(this);
            Thread t1 = new Thread(installApk);
            t1.start();
        }
    }

    @FXML
    private void scrcpyLaunch() {
        if (null == getDevice()) {
            showPopup("No device selected!");
        } else if (null == getScrcpyLocation()) {
            showPopup("Missing scrcpy exe \n file location!");
        } else {
            Scrcpy scrcpy = new Scrcpy(this);
            scrcpy.runScrcpy();
        }
    }

    @FXML
    private void scrcpyLocationSelector() {
        this.scrcpyLocation = fileSelector("exe");
    }

    @FXML
    private void loseFocus(){
        focus_loser.requestFocus();
    }

    protected void showPopup(String text) {
        PopUpController popUpController = new PopUpController();
        popUpController.showPopupWindow(text);
    }

    private String folderSelector() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(main.getPrimaryStage());
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return selectedDirectory.toString();
    }

    private String fileSelector(String fileType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        switch (fileType) {
            case "apk" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("APK", "*.apk"));
            case "exe" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE", "*.exe"));
        }
        File selectedFile = fileChooser.showOpenDialog(main.getPrimaryStage());
        return selectedFile.toString();
    }


    @FXML
    private void openFolderSelector() {
        folderPickerTextBox.setText(folderSelector());
        alignmentDoinger(folderPickerTextBox);
    }

    @FXML
    private void openFileSelector() {
        apkPickerTextBox.setText(fileSelector("apk"));
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
        if (isStarted || apkInstall.isDisabled()) {
            showPopup("SERVICE IS RUNNING\n" +
                    "PLEASE STOP BEFORE CLOSING!");
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

    public String getPackageName() {
        return packageListComboBox.getValue();
    }

    public String getDevice() {
        if (null == combo_box1.getValue() || combo_box1.isDisabled() || combo_box1.getValue().contains("No device")) {
            return null;
        } else
            return combo_box1.getValue().split("-", 2)[0];
    }

    public String getScrcpyLocation() {
        return scrcpyLocation;
    }

    public void packageSectionDisable(Boolean state){
         uninstallApk.setDisable(state);
         apkClearData.setDisable(state);
         startApk.setDisable(state);
         closeApk.setDisable(state);
    }
}
