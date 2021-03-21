package abct.adb_tools;

import abct.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.List;

import static abct.Utils.GlobalTools.*;

public class PackageManager extends MainViewController implements Runnable {
    MainViewController mainViewController;

    @Override
    public void run() {
    }

    public PackageManager(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void openApk() throws Exception {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        String line = "";
        //adb -s deviceID shell monkey -p com.package.name -c android.intent.category.LAUNCHER 1
        String command = "adb -s " + deviceID + " shell monkey -p " + packageName + "  -c android.intent.category.LAUNCHER 1";

        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(executeCmdAdb(command).getInputStream()));
        while (brCleanUp.readLine() != null) {
            try {
                if ((line = brCleanUp.readLine()) == null) break;
            } catch (IOException ignored) {
            }
            if (!line.equals("")) {
                if (line.contains("monkey aborted")) {
                    throw new Exception("Failed to open: \n " + packageName);
                }
            }
        }
    }

    //not returning anything on failed (for ex. if package doesn't exist)
    public void closeApk() {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        //adb -s LGM20010e5eecb shell am force-stop
        String command = "adb -s " + deviceID + " shell am force-stop " + packageName;
        executeCmdAdb(command);
    }

    //Only checking if returns Success - if not, I read it as failed
    public void uninstallApp() throws Exception {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        String command = "adb -s " + deviceID + " uninstall " + packageName;
        String line = "";

        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(executeCmdAdb(command).getInputStream()));
        while ((line = brCleanUp.readLine()) != null) {
            if (line.contains("Success")) {
                System.out.println("Successfully uninstalled - " + packageName);
            } else throw new Exception("Failed to uninstall: \n " + packageName + "\n " + line);
        }
    }

    //Only checking if returns Success - if not, I read it as failed
    public void clearAppData() throws Exception {
        String deviceID = mainViewController.getDevice();
        String packageName = mainViewController.getPackageName();
        String command = "adb -s " + deviceID + " shell pm clear " + packageName;
        String line = "";

        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(executeCmdAdb(command).getInputStream()));
        while ((line = brCleanUp.readLine()) != null) {
            if (line.contains("Success")) {
                System.out.println("Successfully cleared data of - " + packageName);
            } else throw new Exception("Failed to clear data of: \n" + packageName);
        }
    }


    public ObservableList<String> getDevicePackages() throws IOException {
        //adb shell pm list packages -3
        String deviceID = mainViewController.getDevice();
        String option = "3"; //show only 3rd party packages, showing system ones might lead to some "accidents :)"
        String command = "adb -s " + deviceID + " shell pm list packages -" + option;

        Process process = executeCmdAdb(command);
        List<String> processOutput = combineInputStreams(process.getInputStream(), process.getErrorStream());

        ObservableList<String> toReturn = FXCollections.observableArrayList();
        //Trimming lines and deleting empty ones (occurred on 6.0.1)
        processOutput.forEach((n) -> {
            String s1 = n.substring(n.indexOf(":") + 1);
            if (!s1.equals("")) {
                toReturn.add(s1);
            }
        });
        return toReturn;
    }
}
