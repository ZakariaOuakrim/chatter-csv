package com.zakaria.projectmanagement;

import com.zakaria.projectmanagement.controllers.HomeController;
import com.zakaria.projectmanagement.controllers.RFCController;
import com.zakaria.projectmanagement.controllers.RFIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainController extends Application {

    private Stage primaryStage;
    private Scene homeScene;
    private Scene rfcScene;
    private Scene rfiScene;
    
    // Store dimensions for home screen
    private double homeWidth;
    private double homeHeight;
    private boolean isMaximized = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("RFC/RFI Generator");

        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        // Set home screen size to 80% of screen size
        homeWidth = screenBounds.getWidth() * 0.8;
        homeHeight = screenBounds.getHeight() * 0.8;

        // Set minimum size
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        // Load all scenes
        loadHomeScene();
        loadRFCScene();
        loadRFIScene();

        // Set the initial scene
        showHomeScene();
        
        // Show the stage
        primaryStage.show();
    }

    private void loadHomeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/zakaria/projectmanagement/fxml/homeScene.fxml"));
        Parent root = loader.load();

        HomeController homeController = loader.getController();
        homeController.setMainController(this);

        homeScene = new Scene(root);
        homeScene.getStylesheets().add(getClass().getResource("/com/zakaria/projectmanagement/css/style.css").toExternalForm());
    }

    private void loadRFCScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/zakaria/projectmanagement/fxml/rfcScene.fxml"));
        Parent root = loader.load();

        RFCController rfcController = loader.getController();
        rfcController.setMainController(this);

        rfcScene = new Scene(root);
        rfcScene.getStylesheets().add(getClass().getResource("/com/zakaria/projectmanagement/css/style.css").toExternalForm());
    }

    private void loadRFIScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/zakaria/projectmanagement/fxml/rfiScene.fxml"));
        Parent root = loader.load();

        RFIController rfiController = loader.getController();
        rfiController.setMainController(this);

        rfiScene = new Scene(root);
        rfiScene.getStylesheets().add(getClass().getResource("/com/zakaria/projectmanagement/css/style.css").toExternalForm());
    }

    public void showHomeScene() {
        // If currently maximized, restore to normal size
        if (isMaximized) {
            primaryStage.setMaximized(false);
            isMaximized = false;
        }
        
        // Set home screen size
        primaryStage.setWidth(homeWidth);
        primaryStage.setHeight(homeHeight);
        
        // Center the window on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - homeWidth) / 2);
        primaryStage.setY((screenBounds.getHeight() - homeHeight) / 2);
        
        // Disable maximizing for home scene
        primaryStage.setResizable(true);
        primaryStage.setMaximized(false);
        primaryStage.setMaxHeight(homeHeight);
        primaryStage.setMaxWidth(homeWidth);



        // Set the scene
        primaryStage.setScene(homeScene);
    }

    public void showRFCScene() {
        // Enable resizing and remove max size constraints
        primaryStage.setResizable(true);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        
        // Set the scene
        primaryStage.setScene(rfcScene);
        
        // Maximize the window
        primaryStage.setMaximized(true);
        isMaximized = true;
    }

    public void showRFIScene() {
        // Enable resizing and remove max size constraints
        primaryStage.setResizable(true);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        
        // Set the scene
        primaryStage.setScene(rfiScene);
        
        // Maximize the window
        primaryStage.setMaximized(true);
        isMaximized = true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}