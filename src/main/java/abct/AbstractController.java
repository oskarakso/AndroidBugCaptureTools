package abct;

import javafx.beans.value.ChangeListener;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AbstractController {
    //Prevents user from clicking other stages
    public static Main main;
    private static String recentPackageDevice = null;

    public void setMainApp(Main main) {
        //This gives an warning in IDE but don't edit - other code here caused problems before
        this.main = main;
    }

    String folderSelector() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(main.getPrimaryStage());
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return selectedDirectory.toString();
    }

    String fileSelector(String fileType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        switch (fileType) {
            case "apk" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("APK / APEX", "*.apk", "*.apex"));
            case "exe" -> fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE", "*.exe"));
        }
        File selectedFile = fileChooser.showOpenDialog(main.getPrimaryStage());
        return selectedFile.toString();
    }

    Boolean checkAdbStatus() {
        String command = "adb version";
        Process process = null;

        //code bellow gives warning in IDE that it's never used but it is - lol
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            //If not found will throw exception
            e.printStackTrace();
            return false;
        }
        //but if adb exists - no matter what it returns - it will go here and boom works
        return true;
    }

    public void centerStage(Stage stage) {
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            stage.setX(main.getPrimaryStage().getX() + main.getPrimaryStage().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            stage.setY(main.getPrimaryStage().getY() + main.getPrimaryStage().getHeight() / 2 - stageHeight / 2);
        };

        stage.widthProperty().addListener(widthListener);
        stage.heightProperty().addListener(heightListener);

        stage.setOnShown(e -> {
            stage.widthProperty().removeListener(widthListener);
            stage.heightProperty().removeListener(heightListener);
        });
    }

    String recentPackageDevice() {
        return recentPackageDevice;
    }

    void recentPackageDevice(String device) {
        recentPackageDevice = device;
    }


}
