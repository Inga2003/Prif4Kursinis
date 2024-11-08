package com.kursinis.prif4kursinis.fxControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.hibernateControllers.UserHib;
import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public RadioButton customerCheckbox;
    @FXML
    public ToggleGroup userType;
    @FXML
    public RadioButton managerCheckbox;
    @FXML
    public TextField addressField;
    @FXML
    public TextField cardNoField;
    @FXML
    public DatePicker birthDateField;
    @FXML
    public TextField employeeIdField;
    @FXML
    public TextField medCertificateField;
    @FXML
    public DatePicker employmentDateField;
    @FXML
    public CheckBox isAdminCheck;

    private EntityManagerFactory entityManagerFactory;
    private UserHib userHib;

    public void setData(EntityManagerFactory entityManagerFactory, boolean showManagerFields) {
        this.entityManagerFactory = entityManagerFactory;
        disableFields(showManagerFields);
    }

    public void setData(EntityManagerFactory entityManagerFactory, Manager manager) {
        this.entityManagerFactory = entityManagerFactory;
        toggleFields(customerCheckbox.isSelected());
    }

    private void disableFields(boolean showManagerFields) {
        if (!showManagerFields) {
            employeeIdField.setVisible(false);
            medCertificateField.setVisible(false);
            employmentDateField.setVisible(false);
            isAdminCheck.setVisible(false);
            managerCheckbox.setVisible(true);
        }
    }

    private boolean isManagerSelected = false;

    private void toggleFields(boolean isManager) {
        isManagerSelected = isManager;
        if (isManager) {
            addressField.setDisable(true);
            cardNoField.setDisable(true);
            employeeIdField.setDisable(false);
            medCertificateField.setDisable(false);
            employmentDateField.setDisable(false);


            employeeIdField.setVisible(true);
            medCertificateField.setVisible(true);
            employmentDateField.setVisible(true);
            isAdminCheck.setVisible(true);
        } else {
            addressField.setDisable(false);
            cardNoField.setDisable(false);
            employeeIdField.setDisable(true);
            medCertificateField.setDisable(true);
            employmentDateField.setDisable(true);

            employeeIdField.setVisible(false);
            medCertificateField.setVisible(false);
            employmentDateField.setVisible(false);
            isAdminCheck.setVisible(false);
        }

        isAdminCheck.setDisable(!isManagerSelected);
    }


    public void createUser() {
        userHib = new UserHib(entityManagerFactory);
        if (customerCheckbox.isSelected()) {
            User user = new Customer(loginField.getText(), passwordField.getText(), birthDateField.getValue(), nameField.getText(), surnameField.getText(), addressField.getText(), cardNoField.getText());
            userHib.createUserWithHashedPassword(user);
        } else if (managerCheckbox.isSelected()) {
            User user = new Manager(loginField.getText(), passwordField.getText(), birthDateField.getValue(), nameField.getText(), surnameField.getText(), employeeIdField.getText(), medCertificateField.getText(), employmentDateField.getValue(), isAdminCheck.isSelected());

            userHib.createUser(user);
        }
}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        managerCheckbox.setOnAction(event -> {
            boolean isManagerSelected = managerCheckbox.isSelected();
            toggleFields(isManagerSelected);
        });
    }

    public void goToLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        //stage.setTitle("CandyShop");
        stage.setScene(scene);
        stage.show();
    }

    public void isCustomer() {
        addressField.setDisable(false);
        cardNoField.setDisable(false);
        employeeIdField.setDisable(true);
        medCertificateField.setDisable(true);
        employmentDateField.setDisable(true);

        employeeIdField.setVisible(false);
        medCertificateField.setVisible(false);
        employmentDateField.setVisible(false);
        isAdminCheck.setVisible(false);

    }
}
