package com.kursinis.prif4kursinis.fxControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.hibernateControllers.UserHib;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    private EntityManagerFactory entityManagerFactory;
    private UserHib userHib;


    public void registerNewUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("registration.fxml"));
        Parent parent = fxmlLoader.load();
        //Po sios dalies as galiu pasiekti kontrolerius
        RegistrationController registrationController = fxmlLoader.getController();
        registrationController.setData(entityManagerFactory, false);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("Shop");
        stage.setScene(scene);
        stage.show();
    }

    public void validateAndConnect() throws IOException {
        userHib = new UserHib(entityManagerFactory);
        User user = userHib.getUserByCredentials(loginField.getText(), passwordField.getText());

        if (user != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("main-shop.fxml"));
            Parent parent = fxmlLoader.load();
            MainShopController mainShopController = fxmlLoader.getController();
            mainShopController.setData(entityManagerFactory, user);
            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Shop");
            stage.setScene(scene);
            stage.show();
        } else {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.INFORMATION, "Login INFO", "Wrong data", "Please check credentials, no such user");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
    }
}
