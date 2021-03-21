package abct.adb_tools;

import abct.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static abct.Utils.GlobalTools.executeCmdAdb;
import static abct.Utils.GlobalTools.getTimeDate;

public class LogCapture extends MainViewController {
    MainViewController mvc;

    private String getName() {
        if ("".equals(mvc.logCaptureFileName.getText())) {
            return getTimeDate();
        } else
            return mvc.logCaptureFileName.getText();
    }

    public LogCapture(MainViewController mainViewController) {
        this.mvc = mainViewController;
    }

    public void dumpLogs() {
        String deviceID = mvc.getDevice();
        String fileName = getName();
        System.out.println("Output format: " + outputFormatSelected());
        System.out.println("Output filter: " + outputFilterSelected());
        //todo: whole func

        //adb -s DEVID -v (logCaptureOutputFormat - value logic) -d
        //adb -s DEVID logcat -v (log output format) *:(log output filter)
        //adb logcat "Control log output format" "dump" "log buffers" > filename.txt


    }

    public void cleanLogs() {
        String deviceID = mvc.getDevice();
        // adb -s DEVID logcat -c
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
            return colonCutter(val);
    }

    private String outputFilterSelected() {
        String val = mvc.getLogCaptureLevelFilter().getValue();
        if (val.equals(mvc.getLogCaptureLevelFilterDefaultSelection()))
            return "";
        else
            return colonCutter(val);
    }

    private String colonCutter(String str){
        return str.split(":", 2)[0];
    }

}
