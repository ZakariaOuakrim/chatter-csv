module com.zakaria.projectmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.compiler;
    requires org.apache.poi.ooxml;

    opens com.zakaria.projectmanagement to javafx.fxml;
    opens com.zakaria.projectmanagement.controllers to javafx.fxml;

    exports com.zakaria.projectmanagement;
    exports com.zakaria.projectmanagement.controllers;
}