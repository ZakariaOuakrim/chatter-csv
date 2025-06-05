package com.zakaria.projectmanagement.controllers;

import com.zakaria.projectmanagement.MainController;
import com.zakaria.projectmanagement.services.EmailService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

import javax.mail.MessagingException;

public class RFIController {
    @FXML private TextField projectNameField;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea overviewArea;
    @FXML private ComboBox<String> costVariationComboBox;
    @FXML private ComboBox<String> timeChangeComboBox;
    @FXML private TextArea requestArea;
    @FXML private TextField requestingPartyField;

    private MainController mainController;

    @FXML
    public void initialize() {
        // Initialize dropdown values
        costVariationComboBox.setItems(FXCollections.observableArrayList(
                "No change", "Cost Increase", "Cost Decrease"
        ));

        timeChangeComboBox.setItems(FXCollections.observableArrayList(
                "No change", "Increase in time", "Decrease in time"
        ));

        // Set default date to today
        deadlinePicker.setValue(LocalDate.now());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleBackButton() {
        mainController.showHomeScene();
    }

    @FXML
    private void handleGenerateButton() {
        if (validateForm()) {
            try {
                // Ask user for output directory
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Output Directory");
                File outputDir = directoryChooser.showDialog(projectNameField.getScene().getWindow());
                
                if (outputDir != null) {
                    // Generate the RFI document
                    File generatedFile = generateRFIDocument(outputDir);
                    
                    // Send email with attachment
                    try {
                        String subject = "New RFI Document: " + projectNameField.getText();
                        String body = "Please find attached the RFI document for project: " + projectNameField.getText() + 
                                     "\n\nRequesting Party: " + requestingPartyField.getText() +
                                     "\nDeadline: " + deadlinePicker.getValue().toString();
                        
                        EmailService.sendEmailWithAttachment(generatedFile, subject, body);
                        
                        // Add email sent confirmation to success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("RFI Generated");
                        alert.setHeaderText("RFI Document Created Successfully");
                        alert.setContentText("The RFI document has been saved to:\n" + 
                                             generatedFile.getAbsolutePath() + 
                                             "\n\nAn email with the document has been sent to adilmoukhlik@gmail.com");
                        alert.showAndWait();
                    } catch (MessagingException e) {
                        // Email failed but file was generated
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("RFI Generated - Email Failed");
                        alert.setHeaderText("RFI Document Created Successfully");
                        alert.setContentText("The RFI document has been saved to:\n" + 
                                             generatedFile.getAbsolutePath() + 
                                             "\n\nHowever, sending the email failed: " + e.getMessage());
                        alert.showAndWait();
                        e.printStackTrace();
                    }
                    
                    // Return to home screen
                    mainController.showHomeScene();
                }
            } catch (Exception e) {
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Generate RFI");
                alert.setContentText("An error occurred: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }
    
    private File generateRFIDocument(File outputDir) throws IOException {
        // Get template file
        InputStream templateStream = getClass().getResourceAsStream("/com/zakaria/projectmanagement/input/RFI.xlsx");
        if (templateStream == null) {
            throw new FileNotFoundException("RFI template file not found");
        }
        
        // Create a unique filename with project name and timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String projectName = projectNameField.getText().replaceAll("[^a-zA-Z0-9]", "_");
        String outputFileName = "RFI_" + projectName + "_" + timestamp + ".xlsx";
        File outputFile = new File(outputDir, outputFileName);
        
        // Load the Excel workbook
        XSSFWorkbook workbook = new XSSFWorkbook(templateStream);
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data goes in first sheet
        
        // Fill in the form fields
        // Project Name -> A4
        getOrCreateCell(sheet, 3, 0).setCellValue(projectNameField.getText());
        
        // Response Deadline -> H4
        getOrCreateCell(sheet, 3, 7).setCellValue(deadlinePicker.getValue().toString());
        
        // Overview -> A6
        getOrCreateCell(sheet, 5, 0).setCellValue(overviewArea.getText());
        
        // Cost Variation checkboxes
        String costVariation = costVariationComboBox.getValue();
        // Create a checkmark style
        XSSFCellStyle checkmarkStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Wingdings 2");
        font.setCharSet(XSSFFont.DEFAULT_CHARSET);
        checkmarkStyle.setFont(font);
        
        // Set the appropriate checkbox based on selection
        if ("No change".equals(costVariation)) {
            Cell cell = getOrCreateCell(sheet, 7, 0); // A8
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        } else if ("Cost Increase".equals(costVariation)) {
             Cell cell = getOrCreateCell(sheet, 8, 0); // A9
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        } else if ("Cost Decrease".equals(costVariation)) {
            Cell cell = getOrCreateCell(sheet, 9, 0); // A10
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        }
        
        // Time Change checkboxes
        String timeChange = timeChangeComboBox.getValue();
        if ("No change".equals(timeChange)) {
            Cell cell = getOrCreateCell(sheet, 7, 4); // E8
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        } else if ("Increase in time".equals(timeChange)) {
            Cell cell = getOrCreateCell(sheet, 8, 4); // E9
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        } else if ("Decrease in time".equals(timeChange)) {
            Cell cell = getOrCreateCell(sheet, 9, 4); // E10
            cell.setCellValue("R"); // Wingdings 2 checkmark
            cell.setCellStyle(checkmarkStyle);
        }
        
        // Request/Clarification Required -> A13
        getOrCreateCell(sheet, 12, 0).setCellValue(requestArea.getText());
        
        // Requesting Party -> A20
        getOrCreateCell(sheet, 19, 0).setCellValue(requestingPartyField.getText());
        
        // Write the workbook to the output file
        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
        templateStream.close();
        
        return outputFile;
    }
    
    // Helper method to get or create a cell at the specified row and column
    private Cell getOrCreateCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        
        return cell;
    }

    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        // Check required fields
        if (isNullOrEmpty(projectNameField.getText())) {
            errorMessage.append("Please enter a Project Name.\n");
        }

        if (deadlinePicker.getValue() == null) {
            errorMessage.append("Please select a Deadline for Response.\n");
        }

        if (isNullOrEmpty(overviewArea.getText())) {
            errorMessage.append("Please provide a Short Overview of the RFI.\n");
        }

        if (costVariationComboBox.getValue() == null) {
            errorMessage.append("Please select a Cost Variation option.\n");
        }

        if (timeChangeComboBox.getValue() == null) {
            errorMessage.append("Please select a Change in Time option.\n");
        }

        if (isNullOrEmpty(requestArea.getText())) {
            errorMessage.append("Please describe the Request/Clarification Required.\n");
        }

        if (isNullOrEmpty(requestingPartyField.getText())) {
            errorMessage.append("Please enter the Requesting Party.\n");
        }

        // If there are validation errors, show them
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Validation Error");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty() || text.trim().matches("^(hahaha|ssss)$");
    }
}