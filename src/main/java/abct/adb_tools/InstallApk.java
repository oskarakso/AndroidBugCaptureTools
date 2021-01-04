package abct.adb_tools;

import abct.MainViewController;
import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static abct.Utils.GlobalTools.combineInputStreams;

public class InstallApk implements Runnable {
    MainViewController mainViewController;
    String line = "xx";

    public String getLine() {
        return this.line;
    }

    public InstallApk(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void install() throws InterruptedException, IOException {
        String path = mainViewController.getApkPath();
        //System.out.println("path - " + path);
        String deviceID = mainViewController.getDevice();
        //System.out.println("DevID - " + deviceID);
        if (path == null || deviceID == null) {
            setAs("fail");
            return;
        }

        //adb -s deviceID install "path"
        String command = "adb -s " + deviceID + "install \"" + path + "\"";
        //System.out.println(command);

        setAs("start");
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor(3, TimeUnit.MINUTES);

        InputStream inStream = process.getInputStream();
        InputStream erStream = process.getErrorStream();

        ArrayList<String> processOutput = combineInputStreams(inStream, erStream);
        processOutput.forEach((n) -> {
            if (n.toLowerCase().contains("success")) {
                setAs("pass");
            } else if (n.toLowerCase().contains("waiting for device") || n.toLowerCase().contains(" failed ") || n.toLowerCase().contains("failure") || n.toLowerCase().contains("doesn't end") || n.toLowerCase().contains("error: ")) {
                setAs("fail");
                mainViewController.addLog(InstallationLogs.createFailedInstallLog(n, mainViewController.getDeviceIdName()));
            }
        });
    }

    @Override
    public void run() {
        System.out.println("Starting new thread");
        try {
            install();
        } catch (InterruptedException | IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Dropped new thread due to exception");
            setAs("fail");
            return;
        }
        System.out.println("Dropped new thread");
    }

    public void setAs(String s) {
        switch (s) {
            case "start" -> {
                mainViewController.apkInstall.setDisable(true);
                mainViewController.apkInstallResult.setAlignment(Pos.CENTER_LEFT);
                mainViewController.apkInstallResult.setText("Installing...");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: white;");
            }
            case "fail" -> {
                mainViewController.apkInstall.setDisable(false);
                mainViewController.apkInstallResult.setAlignment(Pos.CENTER);
                mainViewController.apkInstallResult.setText("Failed!");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: red;");
            }
            case "pass" -> {
                mainViewController.apkInstall.setDisable(false);
                mainViewController.apkInstallResult.setAlignment(Pos.CENTER);
                mainViewController.apkInstallResult.setText("Success!");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: green;");
            }
        }
    }
}