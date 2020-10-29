package abct;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


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

        primaryStage.show();

        //letting user move app by dragging background
        layout.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        layout.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        myControllerHandle.packageListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String val = myControllerHandle.packageListComboBox.getValue();
            if(null!=val && !"".equals(val)){
            myControllerHandle.packageSectionDisable(false);
            }else{
            myControllerHandle.packageSectionDisable(true);
            }

        });


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

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
