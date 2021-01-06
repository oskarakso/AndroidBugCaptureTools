package abct.adb_tools;

import abct.MainViewController;
import abct.Utils.GlobalTools;

public class InstallationLogs {

    public static String createFailedInstallLog(String line, String device){

        return GlobalTools.getTimeDate() +
                " | " +
                device +
                " | " +
                line +
                "\n";
    }
}