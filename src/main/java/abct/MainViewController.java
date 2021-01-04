package abct;

import abct.adb_tools.InstallApk;
import abct.adb_tools.LogCapture;
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
import java.util.*;

import static abct.adb_tools.LogCapture.*;
import static abct.adb_tools.getDevices.getDevicesList;


public class MainViewController extends AbstractController implements Initializable {

    public boolean isStarted = false;
    protected Map<String, String> devices;
    private String scrcpyLocation = null;

    @FXML
    private Text focus_loser;
    @FXML
    public TextField destinationFolderPath;
    @FXML
    public TextField apkPickerTextBox;
    @FXML
    public TextField logCaptureFileName;
    @FXML
    public TextField apkInstallResult;
    @FXML
    private Button start_stop;
    @FXML
    private Button minimize;
    @FXML
    public Button apkInstall;
    @FXML
    public Button logsClean;
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
    //Log Capture
    @FXML
    private ComboBox<String> logCaptureOutputFormat;
    @FXML
    private ComboBox<String> logCaptureLevelFilter;

    public static ArrayList<String> installationLogs = new ArrayList<String>();


    private String logCaptureOutputFormatDefaultSelection;
    private String logCaptureLevelFilterDefaultSelection;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Read and set scrcpy location
        this.scrcpyLocation = Scrcpy.getLocation();
        //Read and set log formats/filters
        logCaptureOutputFormat.setItems(getOutputFormat());
        logCaptureOutputFormat.getSelectionModel().selectFirst();
        logCaptureOutputFormatDefaultSelection = logCaptureOutputFormat.getValue();
        logCaptureLevelFilter.setItems(getOutputFilter());
        logCaptureLevelFilter.getSelectionModel().selectFirst();
        logCaptureLevelFilterDefaultSelection = logCaptureLevelFilter.getValue();
    }

    @FXML
    public void dumpLogs() {
        new Thread(() -> {
            LogCapture lc = new LogCapture(this);
            lc.dumpLogs();
        }).start();
    }

    @FXML
    private void updatePackageList() {
        //TODO: UPDATE PACKAGE LIST ECT.
    }

    @FXML
    private void cleanLogs() {
        new Thread(() -> {
            LogCapture lc = new LogCapture(this);
            lc.cleanLogs();
        }).start();

    }

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

     TODO:
     - Click on list -> Refresh (or if slow or smth, add button)
     - Click on button -> If succesfull -> Set text as "Done!" wait for few sec (3) -> set back to default text (get it before - pass as arg.) - and maybe implement in install status field
     */

    @FXML
    private void showInstallLog() {
        showLogsPopup();
    }

    @FXML
    private void apkInstall() throws InterruptedException {
        if (null == getDevice()) {
            showPopup("No device selected!");
        } else if (null == apkPickerTextBox.getText() || apkPickerTextBox.getText().equals("")) {
            //TODO: Check if file is .apk or .apex
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
        Scrcpy.saveLocation(this.scrcpyLocation);
    }

    @FXML
    private void loseFocus() {
        focus_loser.requestFocus();
    }

    public void showPopup(String text) {
        PopUpController popUpController = new PopUpController();
        popUpController.showPopupWindow(text);
    }

    public void showLogsPopup() {
        // installationLogs.add("11:11:11-12-32-23  DUPA: FATALNY EXCEPTION W MUZGU EHEHEHEH");
        // installationLogs.add("12:10:18-13-12-03  DUPAS: ALALALALLALALLALALLALLALALLALALALLALALALALLALA");
        // installationLogs.add("13:14:10-17-22-43  DUPAL: FATALNY EXCEPTION W MUZGU EHEHEHEH");
        PopUpLogsController popUpLogsController = new PopUpLogsController();
        popUpLogsController.showPopupWindow(installationLogs);
        loseFocus();
        //ADD PASSING THE HASHMAP XD
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
        destinationFolderPath.setText(folderSelector());
        alignmentDoinger(destinationFolderPath);
    }

    @FXML
    private void openFileSelector() {
        apkPickerTextBox.setText(fileSelector("apk"));
        alignmentDoinger(apkPickerTextBox);
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


    public void UpdateDevicesList() {
        // If user disconnect selected device and refresh list
        // there is an empty field but on second click it's not.
        // No idea wtf if going on and how to fix it, I already spent too much time on this :)
        ObservableList<String> choiceBox = FXCollections.observableArrayList();
        for (Map.Entry<String, String> entry : devices.entrySet()) {
            choiceBox.add(entry.getKey() + " - " + entry.getValue());
        }
        if (choiceBox.size() == 0) {
            combo_box1.valueProperty().setValue("");
            combo_box1.getSelectionModel().clearSelection();
            combo_box1.setItems(null);
            combo_box1.setDisable(true);
            combo_box1.valueProperty().set("No devices available");
        } else if (combo_box1.getSelectionModel().getSelectedIndex() == -1) {
            combo_box1.setItems(choiceBox);
            combo_box1.getSelectionModel().clearSelection();
            combo_box1.valueProperty().set("No device selected");
            combo_box1.setDisable(false);
        } else {
            combo_box1.setItems(choiceBox);
        }
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

    public void packageSectionDisable(Boolean state) {
        uninstallApk.setDisable(state);
        apkClearData.setDisable(state);
        startApk.setDisable(state);
        closeApk.setDisable(state);
    }

    public Boolean isDeviceSelected() {
        if (null == combo_box1.getValue()) {
            return false;
        }
        if ("No devices available".equals(combo_box1.getValue())) {
            return false;
        }
        if ("No device selected".equals(combo_box1.getValue())) {
            return false;
        } else
            return true;
    }

    public Boolean isPackageSelected() {
        String val = packageListComboBox.getEditor().textProperty().getValue();
        if (null != val && !"".equals(val)) {
            return true;
        } else {
            return false;
        }
    }

    public ComboBox<String> getLogCaptureOutputFormat() {
        return logCaptureOutputFormat;
    }

    public ComboBox<String> getLogCaptureLevelFilter() {
        return logCaptureLevelFilter;
    }

    public String getLogCaptureOutputFormatDefaultSelection() {
        return logCaptureOutputFormatDefaultSelection;
    }

    public String getLogCaptureLevelFilterDefaultSelection() {
        return logCaptureLevelFilterDefaultSelection;
    }

    public String getDeviceIdName() {
        return combo_box1.getValue();
    }

    public void addLog(String log) {
        installationLogs.add(log);
    }
}