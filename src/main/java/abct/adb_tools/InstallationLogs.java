package abct.adb_tools;

import abct.MainViewController;
import abct.Utils.GlobalTools;

public class InstallationLogs {

    public static String createFailedInstallLog(String line, String device) {
        return GlobalTools.getTimeDate() +
                " | " +
                device +
                " | " +
                line;
    }

    public static String createSuccessLog(String device, String path, String installMode) {
        return GlobalTools.getTimeDate() + " | " + device + " | Successful " + decodeInstallMode(installMode) + "of " + path;
    }

    private static String decodeInstallMode(String installMode) {
        if (!installMode.contains("-r")) {
            return "install ";
        }
        return "update ";
    }

}
