module AndroidBugCaptureTools {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens abct to javafx.fxml, javafx.controls, javafx.graphics;
    exports abct to javafx.graphics, javafx.fxml, javafx.controls;
}