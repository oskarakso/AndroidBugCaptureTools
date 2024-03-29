package abct;

import abct.Utils.GlobalTools;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import static abct.MainViewController.getDestinationFolderTxtPath;


public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    static MainViewController myControllerHandle;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainView.fxml"));
        MainViewController mainViewController = new MainViewController();

        mainViewController.setMainApp(this);
        Parent layout = loader.load();

        myControllerHandle = (MainViewController) loader.getController();

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bug Capture Tools");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.getScene().setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("ABCT");
        primaryStage.show();

        mainViewController.checkAdb();

        //letting user move app by dragging background
        layout.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        layout.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });


        ///LISTENERS SECTION
        myControllerHandle.destinationFolderPath.textProperty().addListener(observable ->
                GlobalTools.saveValueInTxt(getDestinationFolderTxtPath(), myControllerHandle.destinationFolderPath.getText()));

        // PACKAGE MANAGER
        myControllerHandle.packageListComboBox.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                packageSectionCheck();
            }
        });

        myControllerHandle.combo_box1.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            try {
                if (!oldValue.equals(newValue) && newValue != null) {
                    packageSectionCheck();
                    resetInstallState();
                } //bug: Not working if there are two devices and one get disconnected - status is not cleared
            } catch (NullPointerException ignored) {
            }
        });

        //alignment after change (by focus) on text-fields
        myControllerHandle.destinationFolderPath.focusedProperty().addListener((obs, oldVal, newVal) ->
                myControllerHandle.alignmentDoinger(myControllerHandle.destinationFolderPath));

        myControllerHandle.logCaptureFileName.focusedProperty().addListener((obs, oldVal, newVal) ->
                myControllerHandle.alignmentDoinger(myControllerHandle.logCaptureFileName));

        myControllerHandle.apkPickerTextBox.textProperty().addListener((obs, oldVal, newVal) -> {
            myControllerHandle.alignmentDoinger(myControllerHandle.apkPickerTextBox);
            resetInstallState();
        });

        ///EXIT PLATFORM SECTION
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                System.out.println(myControllerHandle.isStarted);
                if (myControllerHandle.isStarted || myControllerHandle.apkInstall.isDisabled()) {
                    myControllerHandle.showPopup("SERVICE IS RUNNING\n" +
                            "PLEASE STOP BEFORE CLOSING!");
                } else {
                    Platform.exit();
                }
            }
        });
    }

    public void packageSectionCheck() {
        if (myControllerHandle.isDeviceSelected() && myControllerHandle.isPackageSelected())
            myControllerHandle.packageSectionDisable(false);
        else
            myControllerHandle.packageSectionDisable(true);
    }

    public void resetInstallState() {
        myControllerHandle.setAs("new");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
