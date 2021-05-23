package abct.Utils;

import abct.MainViewController;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static abct.Utils.GlobalTools.getTimeDateForPath;
import static abct.adb_tools.getDevices.isDeviceConnected;

public class MainViewControllerTools extends MainViewController {
    MainViewController mvct;

    public MainViewControllerTools(MainViewController mainViewController) {
        this.mvct = mainViewController;
    }

    public static DuplicateFileReturn handleDuplicate(String fileName) {
        AtomicReference<DuplicateFileReturn> toReturnAtomic = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            toReturnAtomic.set(showPopupDuplicateFile(fileName));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturnAtomic.get();
    }

    //WARNING: If destination path does not exist it will be created(!!!)
    //will (or at least it should ;)) throw an exception in case of missing permissions
    public String getDestinationPath() {
        String destinationPath = mvct.destinationFolderPath.getText();
        boolean isFolderDate = mvct.destinationFolderDate.isSelected();

        if (isFolderDate) {
            destinationPath = destinationPath + "\\" + getTimeDateForPath();
            System.out.println("Destination path: " + destinationPath);
            return destinationPath;
        }
        System.out.println("Destination path: " + destinationPath);
        return destinationPath;
    }

    public Boolean isEligibleToStart(Boolean checkDevice, Boolean checkDestinationFolder) {
        boolean isDeviceOk = false;
        boolean isDestinationFolderOk = false;
        StringBuilder logsToShow = new StringBuilder();

        if (checkDevice) {
            if (!mvct.isDeviceSelected()) {
                logsToShow.append("No device selected\n");
            } else {
                try {
                    if (!isDeviceConnected(mvct.getDevice())) {
                        logsToShow.append("Selected device has been disconnected\n");
                        mvct.refreshDevicesPress();
                    } else {
                        isDeviceOk = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logsToShow.append("Selected device has been disconnected\n");
                }
            }
        }

        if (checkDestinationFolder) {
            if (mvct.destinationFolderPath != null && !mvct.destinationFolderPath.getText().equals("")) {
                Path path = Paths.get(mvct.destinationFolderPath.getText());
                if (Files.isWritable(path)) {
                    isDestinationFolderOk = true;
                } else {
                    logsToShow.append("Unreachable destination folder path");
                }
            } else {
                logsToShow.append("Missing destination folder path");
            }
        }

        //if only device check called
        if (checkDevice && !checkDestinationFolder) {
            if (!isDeviceOk) {
                mvct.showPopup(logsToShow.toString());
                return false;
            } else {
                return true;
            }
        }

        //if only folder check called
        if (!checkDevice && checkDestinationFolder) {
            if (!isDestinationFolderOk) {
                mvct.showPopup(logsToShow.toString());
                return false;
            } else {
                return true;
            }
        }

        //if both folder and device check called
        //noinspection ConstantConditions
        if (checkDevice && checkDestinationFolder) {
            if (isDeviceOk && isDestinationFolderOk) {
                return true;
            } else {
                mvct.showPopup(logsToShow.toString());
                return false;
            }
        }
        return false;
    }
}
