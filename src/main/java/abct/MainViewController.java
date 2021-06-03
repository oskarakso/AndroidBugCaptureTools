package abct;

import abct.Utils.DuplicateFileReturn;
import abct.Utils.MainViewControllerTools;
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
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static abct.Utils.GlobalTools.*;
import static abct.adb_tools.LogCapture.*;
import static abct.adb_tools.getDevices.*;


public class MainViewController extends AbstractController implements Initializable {

    protected Map<String, String> devices;
    private static final String scrcpyLocationStoredPath = "src/main/resources/text-content/ScrCpyLocation.txt";
    private static final String destinationFolderTxtPath = "src/main/resources/text-content/destinationFolderLocation.txt";
    private static String scrcpyLocation = getTextFromFile(scrcpyLocationStoredPath);
    private String logCaptureOutputFormatDefaultSelection;
    private String logCaptureLevelFilterDefaultSelection;
    public boolean isStarted = false;
    public static ArrayList<String> installationLogs = new ArrayList<>();
    //True - Install apk + install button, False - Update apk + update button
    public boolean isInstallMode = true;

    @FXML
    private Text focus_loser;
    @FXML
    private Text installUpdateApkTextField;
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

    //Destination path
    @FXML
    //if empty - will be catched before running anything that involves this field (or at least should be catched :P)
    public TextField destinationFolderPath;
    @FXML //checked - create new folder with date | unchecked - use destination folder
    public CheckBox destinationFolderDate;
    @FXML //checked - use default name | unchecked: File name -> (if empty) - log_date
    public CheckBox destinationFileDefault;

    //Log Capture
    @FXML
    private ComboBox<String> logCaptureOutputFormat;
    @FXML
    private ComboBox<String> logCaptureLevelFilter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: save and read checkboxes under destination folder + change all to config.json
        addRegexps();
        //Read and set last destination folder selection
        destinationFolderPath.setText(getTextFromFile(destinationFolderTxtPath));
        alignmentDoinger(destinationFolderPath);
        //Read and set log formats/filters
        logCaptureOutputFormat.setItems(getOutputFormat());
        logCaptureOutputFormat.getSelectionModel().selectFirst();
        logCaptureOutputFormatDefaultSelection = logCaptureOutputFormat.getValue();
        logCaptureLevelFilter.setItems(getOutputFilter());
        logCaptureLevelFilter.getSelectionModel().selectFirst();
        logCaptureLevelFilterDefaultSelection = logCaptureLevelFilter.getValue();
    }

    // -- LOG CAPTURE -- \\

    //TODO: package filter: Selected package (checkbox)

    @FXML
    public void dumpLogs() throws IOException {
        if (isEligibleToStart(true, true)) {
            new Thread(() -> {
                LogCapture lc = new LogCapture(this);
                try {
                    lc.dumpLogs();
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showPopup(e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    private void cleanLogs() {
        if (isEligibleToStart(true, false)) {
            new Thread(() -> {
                LogCapture lc = new LogCapture(this);
                lc.cleanLogs();
            }).start();
        }
    }


    // -- PACKAGE MANAGER -- \\

    //todo: think about better inheritance or smth to make MVC smaller as it's getting too big rn
    @FXML
    // Works as expected but locks app as it's executing - using threads ect. leads to problems with resizing - BUT LOOKS FINE THO!
    private void updatePackageList() throws IOException {
        if (!isDeviceSelected()) {
            Platform.runLater(() -> this.packageListComboBox.hide());
            loseFocus();
            return;
        }

        if (!isEligibleToStart(true, false)) {
            this.packageListComboBox.getItems().clear();
            Platform.runLater(() -> this.packageListComboBox.hide());
            loseFocus();
            return;
        }

        PackageManager pm = new PackageManager(this);
        this.packageListComboBox.getItems().setAll(pm.getDevicePackages());
        this.packageListComboBox.setVisibleRowCount(Math.min(this.packageListComboBox.getItems().size(), 8));
    }

    @FXML
    private void closeApk() {
        if (isEligibleToStart(true, false)) {
            new Thread(() -> {
                PackageManager pm = new PackageManager(this);
                pm.closeApk();
            }).start();
        }
    }

    @FXML
    private void uninstallApk() {
        if (isEligibleToStart(true, false)) {
            new Thread(() -> {
                PackageManager pm = new PackageManager(this);
                try {
                    pm.uninstallApp();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showPopup(e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    private void openApk() {
        if (isEligibleToStart(true, false)) {
            new Thread(() -> {
                PackageManager pm = new PackageManager(this);
                try {
                    pm.openApk();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showPopup(e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    private void clearApkData() {
        if (isEligibleToStart(true, false)) {
            new Thread(() -> {
                PackageManager pm = new PackageManager(this);
                try {
                    pm.clearAppData();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showPopup(e.getMessage()));
                }
            }).start();
        }
    }

    // -- INSTALL / UPDATE APK SECTION -- \\
    @FXML
    private void showInstallLog() {
        showLogsPopup();
    }

    @FXML
    private void changeInstallMode() {
        changeInstallMode(isInstallMode);
    }

    @FXML
    private void apkInstall() {
        String apkPath = apkPickerTextBox.getText();

        if (null == getDevice()) {
            showPopup("No device selected!");
        } else {
            if (isEligibleToStart(true, false)) {
                if (null == apkPath || apkPath.equals("")) {
                    showPopup("Missing apk file location!");
                } else if (!apkPath.endsWith(".apk") && !apkPath.endsWith(".apex")) {
                    showPopup("Unsupported file type selected!");
                } else {
                    InstallApk installApk = new InstallApk(this);
                    Thread t1 = new Thread(installApk);
                    t1.start();
                }
            }
        }
    }

    // -- DEVICE SECTION -- \\

    @FXML
    public void refreshDevicesPress() {
        try {
            devices = getDevicesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(devices);
        UpdateDevicesList();
    }

    @FXML
    private void scrcpyLaunch() {
        if (isEligibleToStart(true, false)) {
            String pathStr = getScrcpyLocation();
            Path path = Paths.get(pathStr);
            if (!Files.isReadable(path) || pathStr.equals("")) {
                showPopup("Missing scrcpy exe \n file location!");
            } else {
                Scrcpy scrcpy = new Scrcpy(this);
                scrcpy.runScrcpy();
            }
        }
    }

    @FXML
    private void scrcpyLocationSelector() {
        scrcpyLocation = fileSelector("exe");
        saveValueInTxt(scrcpyLocationStoredPath, scrcpyLocation);
    }

    // -- UTILITIES -- \\

    public Boolean isEligibleToStart(Boolean checkDevice, Boolean checkDestinationFolder) {
        MainViewControllerTools mvct = new MainViewControllerTools(this);
        return mvct.isEligibleToStart(checkDevice, checkDestinationFolder);
    }

    @FXML
    private void loseFocus() {
        focus_loser.requestFocus();
    }

    public void showPopup(String text) {
        PopUpController popUpController = new PopUpController();
        popUpController.showPopupWindow(text);
    }

    public static DuplicateFileReturn showPopupDuplicateFile(String fileName) {
        PopUpDuplicateFileController pdfc = new PopUpDuplicateFileController();
        pdfc.showPopupWindow(fileName);
        return new DuplicateFileReturn(pdfc.getFileNameString(), pdfc.getIsCanceled());
    }

    public void showLogsPopup() {
        PopUpLogsController popUpLogsController = new PopUpLogsController();
        popUpLogsController.showPopupWindow(installationLogs);
        loseFocus();
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
        String toReturn;
        if (null == combo_box1.getValue() || combo_box1.isDisabled() || combo_box1.getValue().contains("No device")) {
            return null;
        } else {
            toReturn = combo_box1.getValue().split("-", 2)[0];
            toReturn = toReturn.replaceAll("\\s+", "");
            return toReturn;
        }
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

    @SuppressWarnings("RedundantIfStatement")
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

    @SuppressWarnings("RedundantIfStatement")
    public Boolean isPackageSelected() {
        String packageEditorValue = packageListComboBox.getEditor().textProperty().getValue();
        if (null != packageEditorValue && !"".equals(packageEditorValue)) {
            return true;
        } else {
            return false;
        }
    }

    public void checkAdb() {
        if (!checkAdbStatus()) {
            showPopup("NO ADB DETECTED");
            Platform.exit();
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

    // -- ADDITIONAL BACK/FRONTEND CODE -- \\

    public void changeInstallMode(Boolean b) {
        if (b) {
            installUpdateApkTextField.setText("Update apk");
            apkInstall.setText("Update");
            isInstallMode = false;
        } else {
            installUpdateApkTextField.setText("Install apk");
            apkInstall.setText("Install");
            isInstallMode = true;
        }
    }

    public void UpdateDevicesList() {
        ObservableList<String> deviceList = FXCollections.observableArrayList();

        for (Map.Entry<String, String> entry : devices.entrySet()) {
            deviceList.add(entry.getKey() + " - " + entry.getValue());
        }

        if (deviceList.size() > 0) {
            String selectedDevice = combo_box1.getSelectionModel().getSelectedItem();
            combo_box1.setItems(deviceList);
            if (deviceList.contains(selectedDevice)) {
                combo_box1.getSelectionModel().select(selectedDevice);
            } else {
                combo_box1.getSelectionModel().selectFirst();
            }
            combo_box1.setDisable(false);
        } else {
            combo_box1.getSelectionModel().clearSelection();
            combo_box1.getItems().clear();
            combo_box1.getItems().add("No devices available");
            combo_box1.getSelectionModel().selectFirst();
            combo_box1.setDisable(true);
        }
    }

    public static String getDestinationFolderTxtPath() {
        return destinationFolderTxtPath;
    }

    private void addRegexps() {
        setRegexpForFileFolder(logCaptureFileName);
    }

    public void setAs(String s) {
        switch (s) {
            case "new" -> {
                apkInstall.setDisable(false);
//                apkInstallResult.setAlignment(Pos.CENTER);
                apkInstallResult.setText("status");
                apkInstallResult.setStyle("-fx-text-fill: white;");
            }
            case "start" -> {
                apkInstall.setDisable(true);
                apkInstallResult.setStyle("-fx-text-fill: white;");
                apkInstallResult.setText("Installing...");
            }
            case "fail" -> {
                apkInstall.setDisable(false);
                apkInstallResult.setText("Failed!");
                apkInstallResult.setStyle("-fx-text-fill: red;");
            }
            case "pass" -> {
                apkInstall.setDisable(false);
                apkInstallResult.setText("Success!");
                apkInstallResult.setStyle("-fx-text-fill: green;");
            }
        }
    }
}