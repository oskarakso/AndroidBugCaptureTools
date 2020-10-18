package abct;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainView.fxml"));
        MainViewController mainViewController = new MainViewController();
        mainViewController.setMainApp(this);
        Parent layout = loader.load();

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


        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                /* TODO: Doesn't work, always reads FALSE :(
                if (mainViewController.isStarted()) {
                    mainViewController.showPopupWindow();
                } else {
                Platform.exit();
                } */
            }
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
