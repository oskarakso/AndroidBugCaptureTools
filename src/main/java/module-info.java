module AndroidBugCaptureTools {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;

    opens abct to javafx.fxml, javafx.base, javafx.controls, javafx.graphics;
    exports abct to javafx.graphics, javafx.base, javafx.fxml, javafx.controls;
}