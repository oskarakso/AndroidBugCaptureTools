package abct.Utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;


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

    public static String lookForDuplicate(String typeOfFile, String path, String fileName) throws Exception {
        File tmpDir = new File(path);
        if (tmpDir.exists()) {
            DuplicateFileReturn duplicateFileReturn = MainViewControllerTools.handleDuplicate(fileName);
            if (duplicateFileReturn.getIsCanceled()) {
                throw new Exception("File rename canceled(!)");
            }
            if (!duplicateFileReturn.getFileName().equals("")) {
               return duplicateFileReturn.getFileName();
            } else {
                switch (typeOfFile) {
                    case "logcat":
                        return getDefaultLogcatName();
                    case "video":
                        return getDefaultMovieName();
                    case "screenshot":
                        return getDefaultScreenshotName();
                }
            }
        }
        return fileName; //shouldn't be reached
    }

    public static String getDefaultLogcatName() {
        return "logcat_" + getTimeDateForPath();
    }

    public static String getDefaultMovieName() {
        return "recording_" + getTimeDateForPath();
    }

    public static String getDefaultScreenshotName() {
        return "screenshot_" + getTimeDateForPath();
    }

    public static TextField setRegexpForFileFolder(TextField textField) {
        final Pattern pattern = Pattern.compile("^$|[^\\\\/?%*:|\"<>.]+$");
        TextFormatter<?> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change; // allow this change to happen
            } else {
                return null; // prevent change
            }
        });
        textField.setTextFormatter(formatter);
        return textField;
    }

    public static String getTextFromFile(String pathTxt) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(pathTxt)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void saveValueInTxt(String path, String value) {
        try {
            PrintWriter out = new PrintWriter(path);
            out.write(value);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getTimeDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now).toString();
    }

    public static String getTimeDateForPath() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
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
