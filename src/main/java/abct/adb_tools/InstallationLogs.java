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
//MainViewController.installationLogsMain
//todo:
// Additional: Change phones list: Phone = object of a Device class, device name, device id
// getters setters and all that crap