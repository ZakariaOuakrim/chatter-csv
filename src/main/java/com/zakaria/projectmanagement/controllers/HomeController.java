package com.zakaria.projectmanagement.controllers;

import com.zakaria.projectmanagement.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class HomeController {

    @FXML private VBox root;
    @FXML private Button rfcButton;
    @FXML private Button rfiButton;

    private MainController mainController;

    @FXML
    public void initialize() {
        // You can add any initialization code here if needed
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setupButtonActions();
    }

    private void setupButtonActions() {
        rfcButton.setOnAction(event -> mainController.showRFCScene());
        rfiButton.setOnAction(event -> mainController.showRFIScene());
    }
}