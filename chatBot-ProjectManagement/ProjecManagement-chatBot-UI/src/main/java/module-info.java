module com.zakaria.projecmanagementchatbotui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.zakaria.projecmanagementchatbotui to javafx.fxml;
    opens com.zakaria.projecmanagementchatbotui.controllers to javafx.fxml;
    exports com.zakaria.projecmanagementchatbotui;
    exports com.zakaria.projecmanagementchatbotui.controllers;
}