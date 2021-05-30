package abct.adb_tools;

import abct.MainViewController;
import abct.Utils.GlobalTools;

public class InstallationLogs {
    //TODO: fix too many new lines added at start
    public static String createFailedInstallLog(String line, String device){

        return GlobalTools.getTimeDate() +
                " | " +
                device +
                " | " +
                line;
    }
}