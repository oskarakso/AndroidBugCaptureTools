package abct.adb_tools;

import abct.MainViewController;
import abct.Utils.MainViewControllerTools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

import static abct.Utils.GlobalTools.*;

public class LogCapture extends MainViewController {
    MainViewController mvc;

    private String getName() {
        if ("".equals(mvc.logCaptureFileName.getText()) || mvc.destinationFileDefault.isSelected()) {
            return getDefaultLogcatName();
        } else
            return mvc.logCaptureFileName.getText();
    }

    public LogCapture(MainViewController mainViewController) {
        this.mvc = mainViewController;
    }

    public void dumpLogs() throws IOException {
        MainViewControllerTools mvct = new MainViewControllerTools(mvc);
        boolean useDefaultFileName = mvc.destinationFileDefault.isSelected();
        String destinationPath = mvct.getDestinationPath(); //Returns destination path
        String deviceID = mvc.getDevice();
        String fileName = getName();
        String outputFormat = outputFormatSelected();
        String outputFilter = outputFilterSelected();

        if (useDefaultFileName) {
            fileName = getDefaultLogcatName();
        }

        String command = "adb -s " + deviceID + " logcat " + outputFormat + outputFilter + " -d";
        String outputPath = destinationPath + "\\" + fileName + ".txt"; //Create pre-final path to check if it exists

        try {
            fileName = lookForDuplicate("logcat", outputPath, fileName); //if it does, it just wont change
        } catch (Exception e) { //but if user click "cancel" - exception will cause return - no file or folder should be created
            e.printStackTrace();
            return;
        }
        outputPath = destinationPath + "\\" + fileName + ".txt"; //final path for the logcat output
        Files.createDirectories(Paths.get(destinationPath)); //If path does not exist it will be created

        Process process = executeCmdAdb(command);
        InputStream in = new BufferedInputStream(process.getInputStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath)); //throws to main

        int cnt;
        byte[] buffer = new byte[1024];
        while ((cnt = in.read(buffer)) != -1) {
            out.write(buffer, 0, cnt);
        }

        in.close();
        out.close();
    }

    //adb -s DEVID -v (logCaptureOutputFormat - value logic) -d
    //adb -s DEVID logcat -v (log output format) *:(log output filter)
    //adb logcat "Control log output format" "dump" "log buffers" > filename.txt


    public void cleanLogs() {
        String deviceID = mvc.getDevice();
        String command = "adb -s " + deviceID + " logcat -c";
        executeCmdAdb(command);
    }

    public static ObservableList<String> getListFromTxt(String path) {
        File txtFile = new File(path);
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        Scanner sc = null;
        try {
            sc = new Scanner(txtFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        while (Objects.requireNonNull(sc).hasNextLine()) {
            toReturn.add(sc.nextLine());
        }
        return toReturn;
    }

    public static ObservableList<String> getOutputFormat() {
        return getListFromTxt("src/main/resources/text-content/LogFormat.txt");
    }

    public static ObservableList<String> getOutputFilter() {
        return getListFromTxt("src/main/resources/text-content/LogFilter.txt");
    }

    private String outputFormatSelected() {
        String val = mvc.getLogCaptureOutputFormat().getValue();
        if (val.equals(mvc.getLogCaptureOutputFormatDefaultSelection()))
            return "";
        else
            return " -v " + colonCutter(val);
    }

    private String outputFilterSelected() {
        String val = mvc.getLogCaptureLevelFilter().getValue();
        if (val.equals(mvc.getLogCaptureLevelFilterDefaultSelection()))
            return "";
        else
            return " *:" + colonCutter(val);
    }

    private String colonCutter(String str) {
        return str.split(":", 2)[0];
    }

}
