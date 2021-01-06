package abct.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GlobalTools {

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

    public static ArrayList<String> combineInputStreams(InputStream in1, InputStream in2) throws IOException {
        ArrayList <String> toReturn = new ArrayList<>();

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
