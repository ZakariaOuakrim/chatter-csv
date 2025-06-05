package com.zakaria.projecmanagementchatbotui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatBotApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatBotApplication.class.getResource("fxml/chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader. load(), 800, 600);
        stage.setTitle("Project Management ChatBot");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}