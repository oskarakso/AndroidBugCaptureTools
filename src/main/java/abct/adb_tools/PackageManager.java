package abct.adb_tools;

import abct.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static abct.Utils.GlobalTools.combineInputStreams;

public class PackageManager extends MainViewController implements Runnable {
    MainViewController mainViewController;

    @Override
    public void run() {
    }

    public PackageManager(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void openApk() {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        //adb | shell monkey -p com.package.name -c android.intent.category.LAUNCHER 1
        String command = "adb -s " + deviceID + " shell monkey -p " + packageName + "  -c android.intent.category.LAUNCHER 1";
        String line = "";
        InputStream inStream = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inStream = process.getInputStream();
        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while (true) {
            try {
                if ((line = brCleanUp.readLine()) == null) break;
            } catch (IOException ignored) {
            }
            if (!line.equals("")) {
                if (line.contains("monkey aborted")) {
                    System.out.println("monkey aborted");
                    //setAs("fail");
                    return;
                }
            }
        }
        System.out.println("pass");
        //setAs("pass");
    }

    public void closeApk() {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        //adb -s LGM20010e5eecb shell am force-stop
        String command = "adb -s " + deviceID + " shell am force-stop " + packageName;
        String line = "";
        InputStream inStream = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void uninstallApp() {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        String command = "adb -s " + deviceID + " uninstall " + packageName;
        String line = "";
        InputStream inStream = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inStream = process.getInputStream();
        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while (true) {
            try {
                if ((line = brCleanUp.readLine()) == null) break;
            } catch (IOException ignored) {
            }
            if (!line.equals("")) {
                if (line.contains("Success")) {
                    System.out.println("Success");
                    //setAs("fail");
                    return;
                }
            }
        }
        System.out.println("fail");
        //setAs("fail");
    }

    public void clearAppData() {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        String command = "adb -s " + deviceID + " shell pm clear " + packageName;
        String line = "";
        InputStream inStream = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inStream = process.getInputStream();
        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while (true) {
            try {
                if ((line = brCleanUp.readLine()) == null) break;
            } catch (IOException ignored) {
            }
            if (!line.equals("")) {
                if (line.contains("Success")) {
                    System.out.println("Success");
                    //setAs("fail");
                    return;
                }
            }
        }
        System.out.println("fail");
        //setAs("fail");
    }


    public ObservableList<String> getDevicePackages() {
        //adb shell pm list packages -3
        String deviceID = mainViewController.getDevice();
        String option = "3";
        String command = "adb -s " + deviceID + " shell pm list packages -" + option;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inStream = process.getInputStream();
        InputStream erStream = process.getErrorStream();
        List<String> processOutput = null;

        try {
            processOutput = combineInputStreams(inStream, erStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> toReturn = FXCollections.observableArrayList();

        processOutput.forEach((n) -> {
            String s1 = n.substring(n.indexOf(":") + 1);
            s1.trim();
            toReturn.add(s1);
        });

        return toReturn;
    }

    // TODO:
    // - Animated or just status as text on button for 3sec (after executing -> pass/fail)
}
