package abct.adb_tools;

import abct.MainViewController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class InstallApk extends MainViewController implements Runnable {
    MainViewController mainViewController;

    public InstallApk(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void install() throws InterruptedException, IOException {
        String path = mainViewController.getApkPath();
        System.out.println("path - " + path);
        String deviceID = mainViewController.getDevice();
        System.out.println("DevID - " + deviceID);
        if (path == null || deviceID == null) {
            setAs("fail");
            return;
        }
        String line = "";
        InputStream inStream = null;
        Process process = null;
        //adb -s deviceID install "path"
        String command = "adb -s " + deviceID + "install \"" + path + "\"";
        //System.out.println(command);

        setAs("start");

            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            inStream = process.getInputStream();


        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while (true) {
            try {
                if ((line = brCleanUp.readLine()) == null) break;
            } catch (IOException ignored) {
            }
            if (!line.equals("")) {
                if (line.toLowerCase().contains("success")) {
                    setAs("pass");
                    return;
                }
            }
        }
        setAs("fail");
    }

    @Override
    public void run() {
        System.out.println("Starting new thread");
        try {
            install();
        } catch (InterruptedException | IOException |NullPointerException e) {
            e.printStackTrace();
            System.out.println("Dropped new thread due to exception");
            setAs("fail");
            return;
        }
        System.out.println("Dropped new thread");

    }

    public void setAs(String s){
        switch (s) {
            case "start" -> {
                mainViewController.apkInstall.setDisable(true);
                mainViewController.apkInstallResult.setText("Installing...");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: white;");
            }
            case "fail" -> {
                mainViewController.apkInstall.setDisable(false);
                mainViewController.apkInstallResult.setText("Failed!");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: red;");
            }
            case "pass" -> {
                mainViewController.apkInstall.setDisable(false);
                mainViewController.apkInstallResult.setText("Success!");
                mainViewController.apkInstallResult.setStyle("-fx-text-fill: green;");
            }
        }
    }
}
