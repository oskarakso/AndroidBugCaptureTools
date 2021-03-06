package abct.adb_tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class getDevices {
    public static HashMap<String, String> getDevicesList() throws IOException {
        HashMap<String, String> DeviceNameID = new HashMap<>();
        String line;
        InputStream inStream;

        Process process = Runtime.getRuntime().exec("adb devices");
        inStream = process.getInputStream();
        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while ((line = brCleanUp.readLine()) != null) {
            if (!line.equals("")) {
                if (line.contains("\tdevice")) {
                    line = line.split("\t")[0];
                    DeviceNameID.put(line, getName(line));
                }
            }
        }
        return DeviceNameID;
    }

    private static String getName(String id) throws IOException {
        //>adb -s id shell getprop ro.product.model
        String line;
        String name = null;
        InputStream inStream;
        String command = "adb -s " + id + " shell getprop ro.product.model";

        Process process = Runtime.getRuntime().exec(command);
        inStream = process.getInputStream();
        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while ((line = brCleanUp.readLine()) != null) {
            if (!line.equals("")) {
                name = line;
            }
        }
        return name;
    }

    public static boolean isAnyDeviceConnected() throws IOException {
        String line;
        StringBuilder output = new StringBuilder();
        InputStream inStream;
        String command = "adb devices";
        Process process = Runtime.getRuntime().exec(command);
        inStream = process.getInputStream();

        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while ((line = brCleanUp.readLine()) != null) {
            if (!line.equals("")) {
                output.append(line);
            }
        }

        return !output.toString().equals("List of devices attached");
    }

    public static Boolean isDeviceConnected(String id) throws IOException {
        String line;
        StringBuilder output = new StringBuilder();
        InputStream inStream;
        String command = "adb devices";
        Process process = Runtime.getRuntime().exec(command);
        inStream = process.getInputStream();

        BufferedReader brCleanUp = new BufferedReader(
                new InputStreamReader(inStream));
        while ((line = brCleanUp.readLine()) != null) {
            if (!line.equals("")) {
                output.append(line);
            }
        }
        try {
            return output.toString().contains(id);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}

