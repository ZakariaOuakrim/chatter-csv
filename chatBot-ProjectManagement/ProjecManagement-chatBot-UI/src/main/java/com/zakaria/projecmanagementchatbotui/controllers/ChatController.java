package com.zakaria.projecmanagementchatbotui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.CompletableFuture;

public class ChatController {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox chatBox;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        // Set up auto-scrolling
        chatBox.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue(1.0));

        // Add event listener for Enter key
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // Add welcome message
        addBotMessage("Hello! I'm your Project Management Assistant. Ask me questions about your project data.");
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;

        // Add user message to chat
        addUserMessage(message);

        // Clear input field
        messageField.clear();

        // Show typing indicator
        HBox typingBox = new HBox();
        typingBox.setAlignment(Pos.CENTER_LEFT);
        typingBox.setPadding(new Insets(5, 10, 5, 10));
        Text typingText = new Text("Analyzing data...");
        TextFlow typingFlow = new TextFlow(typingText);
        typingFlow.getStyleClass().add("bot-message");
        typingBox.getChildren().add(typingFlow);
        Platform.runLater(() -> chatBox.getChildren().add(typingBox));

        // Process message in background
        CompletableFuture.runAsync(() -> {
            String response = callPythonScript(message);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                // Remove typing indicator
                chatBox.getChildren().remove(typingBox);

                // Add bot response
                addBotMessage(response);
            });
        });
    }

    private void addUserMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.getStyleClass().add("user-message");
        textFlow.setPadding(new Insets(10));

        messageBox.getChildren().add(textFlow);
        chatBox.getChildren().add(messageBox);
    }

    private void addBotMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.getStyleClass().add("bot-message");
        textFlow.setPadding(new Insets(10));

        messageBox.getChildren().add(textFlow);
        chatBox.getChildren().add(messageBox);
    }

    private String callPythonScript(String question) {
        try {
            // Get the absolute path to the Python script
            String pythonScriptPath = System.getProperty("user.dir") + "/../openrouter_deepseek.py";

            // Set up process to run Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python",
                    pythonScriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Write question to process stdin
            OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
            writer.write(question + "\n");
            writer.write("exit\n"); // Send exit command to terminate the Python script's input loop
            writer.flush();
            writer.close(); // Close the stream to signal end of input

            // Read response from process stdout
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            boolean isAnswer = false;

            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line); // Debug output

                if (line.contains("Answer:")) {
                    isAnswer = true;
                    continue;
                }

                if (isAnswer) {
                    response.append(line).append("\n");
                }
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            System.out.println("Python process exited with code: " + exitCode);

            if (response.length() > 0) {
                return response.toString();
            } else {
                return "Sorry, I couldn't process your question. Check the console for details.";
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}