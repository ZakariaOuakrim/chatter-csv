package com.zakaria.projectmanagement.controllers;

import com.zakaria.projectmanagement.MainController;
import com.zakaria.projectmanagement.services.EmailService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.ChangeListener;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;

public class RFCController {
    @FXML private ComboBox<String> rfcTypeComboBox;
    @FXML private ComboBox<String> areaComboBox;
    @FXML private TextField projectCodeField;
    @FXML private ComboBox<String> cityComboBox;
    @FXML private TextField initiatorField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField titleField;
    @FXML private TextArea initialScopeArea;
    @FXML private TextArea backgroundArea;
    @FXML private TextArea proposedChangeArea;

    // Impact areas
    @FXML private ComboBox<String> designImpactComboBox;
    @FXML private TextField designImpactField;
    @FXML private ComboBox<String> permitImpactComboBox;
    @FXML private TextField permitImpactField;
    // Add other impact areas similarly

    @FXML private TextArea attachmentsArea;

    private MainController mainController;

    @FXML
    public void initialize() {
        // Initialize dropdown values
        rfcTypeComboBox.setItems(FXCollections.observableArrayList(
                "STANDARDS EXCEPTION REQUEST",
                "PROJECT SCOPE CHANGE REQUEST",
                "PROCEDURE / CONTRACTUAL CHANGE REQUEST"
        ));

        areaComboBox.setItems(FXCollections.observableArrayList("Project", "Programme"));

        cityComboBox.setItems(FXCollections.observableArrayList(
                "Aubervilliers", "Genth", "Madrid", "Zurich", "Geneva"
        ));

        // Initialize Yes/No dropdowns for impact areas
        String[] yesNoOptions = {"No", "Yes"};
        designImpactComboBox.setItems(FXCollections.observableArrayList(yesNoOptions));
        permitImpactComboBox.setItems(FXCollections.observableArrayList(yesNoOptions));
        // Set default values
        designImpactComboBox.setValue("No");
        permitImpactComboBox.setValue("No");

        // Add listeners to enable/disable impact detail fields
        setupImpactFieldListeners();
    }

    private void setupImpactFieldListeners() {
        // Design impact
        designImpactComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            designImpactField.setDisable(!"Yes".equals(newVal));
            if ("No".equals(newVal)) {
                designImpactField.clear();
            }
        });

        // Permit impact
        permitImpactComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            permitImpactField.setDisable(!"Yes".equals(newVal));
            if ("No".equals(newVal)) {
                permitImpactField.clear();
            }
        });

        // Add listeners for other impact areas similarly
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
                File outputDir = directoryChooser.showDialog(titleField.getScene().getWindow());
                
                if (outputDir != null) {
                    // Generate the RFC document
                    File generatedFile = generateRFCDocument(outputDir);
                    
                    // Send email with attachment
                    try {
                        String subject = "New RFC Document: " + titleField.getText();
                        String body = "Please find attached the RFC document for project code: " + projectCodeField.getText() + 
                                     "\n\nInitiator: " + initiatorField.getText() +
                                     "\nTitle: " + titleField.getText();
                        
                        EmailService.sendEmailWithAttachment(generatedFile, subject, body);
                        
                        // Add email sent confirmation to success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("RFC Generated");
                        alert.setHeaderText("RFC Document Created Successfully");
                        alert.setContentText("The RFC document has been saved to:\n" + 
                                             generatedFile.getAbsolutePath() + 
                                             "\n\nAn email with the document has been sent to adilmoukhlik@gmail.com");
                        alert.showAndWait();
                    } catch (MessagingException e) {
                        // Email failed but file was generated
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("RFC Generated - Email Failed");
                        alert.setHeaderText("RFC Document Created Successfully");
                        alert.setContentText("The RFC document has been saved to:\n" + 
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
                alert.setHeaderText("Failed to Generate RFC");
                alert.setContentText("An error occurred: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    private File generateRFCDocument(File outputDir) throws IOException {
        // Get template file
        InputStream templateStream = getClass().getResourceAsStream("/com/zakaria/projectmanagement/input/RFC.xlsx");
        if (templateStream == null) {
            throw new FileNotFoundException("RFC template file not found");
        }
        
        // Create a unique filename with project code and timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String projectCode = projectCodeField.getText().replaceAll("[^a-zA-Z0-9]", "_");
        String outputFileName = "RFC_" + projectCode + "_" + timestamp + ".xlsx";
        File outputFile = new File(outputDir, outputFileName);
        
        // Load the Excel workbook
        XSSFWorkbook workbook = new XSSFWorkbook(templateStream);
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data goes in first sheet
        
        // Set RFC Type and color the appropriate cell
        String rfcType = rfcTypeComboBox.getValue();
        if (rfcType != null) {
            // Create a light green cell style
            XSSFCellStyle lightGreenStyle = workbook.createCellStyle();
            lightGreenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            lightGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Apply style to the appropriate cell based on RFC type
            if ("STANDARDS EXCEPTION REQUEST".equals(rfcType)) {
                Cell cell = getOrCreateCell(sheet, 2, 2); // C3
                cell.setCellStyle(lightGreenStyle);
            } else if ("PROJECT SCOPE CHANGE REQUEST".equals(rfcType)) {
                Cell cell = getOrCreateCell(sheet, 3, 2); // C4
                cell.setCellStyle(lightGreenStyle);
            } else if ("PROCEDURE / CONTRACTUAL CHANGE REQUEST".equals(rfcType)) {
                Cell cell = getOrCreateCell(sheet, 4, 2); // C5
                cell.setCellStyle(lightGreenStyle);
            }
        }
        
        // Fill in the form fields
        // Project code
        getOrCreateCell(sheet, 11, 1).setCellValue(projectCodeField.getText()); // B12
        
        // Area of application
        getOrCreateCell(sheet, 8, 1).setCellValue(areaComboBox.getValue()); // B9
        
        // City
        getOrCreateCell(sheet, 11, 4).setCellValue(cityComboBox.getValue()); // E12
        
        // Today's date
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        getOrCreateCell(sheet, 13, 1).setCellValue(today); // B14
        
        // Initiator
        getOrCreateCell(sheet, 13, 4).setCellValue(initiatorField.getText()); // E14
        
        // Contact email
        getOrCreateCell(sheet, 15, 1).setCellValue(emailField.getText()); // B16
        
        // Phone number
        getOrCreateCell(sheet, 15, 4).setCellValue(phoneField.getText()); // E16
        
        // Title
        getOrCreateCell(sheet, 19, 1).setCellValue(titleField.getText()); // B20
        
        // Initial scope / Situation
        getOrCreateCell(sheet, 22, 1).setCellValue(initialScopeArea.getText()); // B23
        
        // Background/ justification / reason for change
        getOrCreateCell(sheet, 25, 1).setCellValue(backgroundArea.getText()); // B26
        
        // Proposed change: final scope / Situation
        getOrCreateCell(sheet, 28, 1).setCellValue(proposedChangeArea.getText()); // B29
        
        // Design impact
        getOrCreateCell(sheet, 32, 3).setCellValue(designImpactComboBox.getValue()); // D33
        if ("Yes".equals(designImpactComboBox.getValue())) {
            getOrCreateCell(sheet, 32, 4).setCellValue(designImpactField.getText()); // E33
        }
        
        // Permit / Administrative impact
        getOrCreateCell(sheet, 33, 3).setCellValue(permitImpactComboBox.getValue()); // D34
        if ("Yes".equals(permitImpactComboBox.getValue())) {
            getOrCreateCell(sheet, 33, 4).setCellValue(permitImpactField.getText()); // E34
        }
        
        // Attachments (up to 5)
        String[] attachments = attachmentsArea.getText().split("\n");
        for (int i = 0; i < Math.min(attachments.length, 5); i++) {
            if (!attachments[i].trim().isEmpty()) {
                getOrCreateCell(sheet, 43 + i, 2).setCellValue(attachments[i].trim()); // C44 to C48
            }
        }
        
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
        if (rfcTypeComboBox.getValue() == null) {
            errorMessage.append("Please select an RFC Type.\n");
        }

        if (areaComboBox.getValue() == null) {
            errorMessage.append("Please select an Area of Application.\n");
        }

        if (isNullOrEmpty(projectCodeField.getText())) {
            errorMessage.append("Please enter a Project Code.\n");
        }

        if (cityComboBox.getValue() == null) {
            errorMessage.append("Please select a City.\n");
        }

        if (isNullOrEmpty(initiatorField.getText())) {
            errorMessage.append("Please enter Initiator information.\n");
        }

        if (isNullOrEmpty(emailField.getText()) || !isValidEmail(emailField.getText())) {
            errorMessage.append("Please enter a valid Email address.\n");
        }

        if (isNullOrEmpty(phoneField.getText())) {
            errorMessage.append("Please enter a Phone Number.\n");
        }

        if (isNullOrEmpty(titleField.getText())) {
            errorMessage.append("Please enter a Title for this request.\n");
        }

        if (isNullOrEmpty(initialScopeArea.getText())) {
            errorMessage.append("Please describe the Initial Scope or Situation.\n");
        }

        if (isNullOrEmpty(backgroundArea.getText())) {
            errorMessage.append("Please provide Background Justification.\n");
        }

        if (isNullOrEmpty(proposedChangeArea.getText())) {
            errorMessage.append("Please describe the Proposed Change.\n");
        }

        // Check impact details if "Yes" is selected
        if ("Yes".equals(designImpactComboBox.getValue()) && isNullOrEmpty(designImpactField.getText())) {
            errorMessage.append("Please provide Design Impact details.\n");
        }

        if ("Yes".equals(permitImpactComboBox.getValue()) && isNullOrEmpty(permitImpactField.getText())) {
            errorMessage.append("Please provide Permit/Administrative Impact details.\n");
        }

        // Check other impact areas similarly

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

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}