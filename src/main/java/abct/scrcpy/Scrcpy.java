package abct.scrcpy;

import abct.MainViewController;
import java.io.IOException;

public class Scrcpy extends MainViewController {
    MainViewController mainViewController;

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

}


