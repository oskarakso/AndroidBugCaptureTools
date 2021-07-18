package abct.adb_tools;

import abct.MainViewController;
import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static abct.Utils.GlobalTools.combineInputStreams;

public class InstallApk implements Runnable {
    MainViewController mainViewController;

    public InstallApk(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void install() throws InterruptedException, IOException {
        String path = mainViewController.getApkPath();
        String deviceID = mainViewController.getDevice();
        String deviceIdName = mainViewController.getDeviceIdName();
        String installMode = getInstallMode();
        AtomicBoolean additionalLogs = new AtomicBoolean(false);
        if (path == null || deviceID == null) {
            mainViewController.setAs("fail");
            mainViewController.addLog("Something was null lol");
            return;
        }
        //adb -s deviceID install "path"
        String command = "adb -s " + deviceID + installMode + path + "\"";

        if (mainViewController.isInstallMode) {
            mainViewController.setAs("start");
        } else mainViewController.setAs("start_update");

        Process process = Runtime.getRuntime().exec(command);
        process.waitFor(3, TimeUnit.MINUTES);

        ArrayList<String> processOutput = combineInputStreams(process.getInputStream(), process.getErrorStream());
        processOutput.forEach((n) -> {
            if (n.toLowerCase().contains("success")) {
                mainViewController.setAs("pass");
                mainViewController.addLog(InstallationLogs.createSuccessLog(deviceIdName, path, installMode));
            } else if (isFailedOnStart(n)) {
                mainViewController.setAs("fail");
                mainViewController.addLog(InstallationLogs.createFailedInstallLog(n, deviceIdName));
                additionalLogs.set(true);
            } else if (additionalLogs.get() && !(n.contains("com.android.server.pm"))) {
                //com.android.server.pm - is just debug info that isn't necessary in this case
                mainViewController.addLog(n);
            }
        });
        //As there won't be any more logs from this installation - logs are separated by an empty row
        mainViewController.addLog("");
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
            mainViewController.setAs("fail");
            return;
        }
        System.out.println("Dropped new thread");
    }

    private String getInstallMode() {
        if (mainViewController.isInstallMode) {
            return " install \"";
        } else {
            return " install -r \"";
        }
    }
}