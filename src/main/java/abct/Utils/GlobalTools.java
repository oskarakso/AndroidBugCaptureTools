package abct.Utils;

import abct.MainViewController;
import javafx.beans.DefaultProperty;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GlobalTools {

    public static Process executeCmdAdb(String command) {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    public static String getTimeDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now).toString();
    }

    public static String arrayToString(ArrayList<String> input) {
        StringBuilder sb = new StringBuilder();
        for (String s : input) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    //some adb
    public static ArrayList<String> combineInputStreams(InputStream in1, InputStream in2) throws IOException {
        ArrayList<String> toReturn = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in1));
        String line = null;
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            toReturn.add(line);
        }
        reader = new BufferedReader(new InputStreamReader(in2));
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            toReturn.add(line);
        }
        return toReturn;
    }


}
