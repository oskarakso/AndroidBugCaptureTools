package abct.scrcpy;

import abct.MainViewController;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

public class Scrcpy extends MainViewController {
    MainViewController mainViewController;

    private static String pathTxt = "src/main/resources/text-content/ScrCpyLocation.txt";

    public Scrcpy(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void runScrcpy() {
        try {
            System.out.println(mainViewController.getScrcpyLocation());
            System.out.println(" -s " + mainViewController.getDevice());
            Process process = new ProcessBuilder(mainViewController.getScrcpyLocation(), "-s " + mainViewController.getDevice()).start();
        } catch (IOException e) {
            String ex = e.toString();
            String s1 = ex.substring(ex.indexOf(".exe") + 6);
            s1 = s1.trim();
            showPopup("Error while starting scrcpy: " + s1);
            e.printStackTrace();
        }
    }

    public static void saveLocation(String path) {
        try {
            PrintWriter out = new PrintWriter(pathTxt);
            out.write(path);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getLocation() {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(pathTxt)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}


