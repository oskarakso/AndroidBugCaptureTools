package abct.adb_tools;

import abct.MainViewController;
import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static abct.Utils.GlobalTools.combineInputStreams;

public class InstallApk implements Runnable {
    MainViewController mainViewController;

    public InstallApk(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void install() throws InterruptedException, IOException {
        String path = mainViewController.getApkPath();
        String deviceID = mainViewController.getDevice();
        if (path == null || deviceID == null) {
            setAs("fail");
            return;
        }
        //adb -s deviceID install "path"
        String command = "adb -s " + deviceID + " install \"" + path + "\"";

        //TODO: I think it needs some deep investigation if it couldn't be optimized or refactored tbh...
        setAs("start");
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor(3, TimeUnit.MINUTES);

        ArrayList<String> processOutput = combineInputStreams(process.getInputStream(), process.getErrorStream());
        processOutput.forEach((n) -> {
            if (n.toLowerCase().contains("success")) {
                setAs("pass");
            } else if (isFailedOnStart(n)) {
                setAs("fail");
                mainViewController.addLog(InstallationLogs.createFailedInstallLog(n, mainViewController.getDeviceIdName()));
            }
        });
    }

    private Boolean isFailedOnStart(String n) {
        return (n.toLowerCase().contains("waiting for device") || n.toLowerCase().contains(" failed ") || n.toLowerCase().contains("failure") || n.toLowerCase().contains("doesn't end") || n.toLowerCase().contains("error: "));
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