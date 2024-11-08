package com.kursinis.prif4kursinis.model;

import jakarta.persistence.ManyToMany;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ManagerTableData {
    private SimpleStringProperty name;
    private SimpleStringProperty surname;
    private SimpleStringProperty birthdate;
    private SimpleStringProperty login;
    private SimpleStringProperty password;
    private SimpleStringProperty employeeId;
    private SimpleStringProperty medCertificate;
    private SimpleStringProperty employmentDate;
    private SimpleStringProperty isAdmin;
    private SimpleStringProperty worksAtWarehouse;
    private Manager manager;


    public String getIsAdmin() {
        return isAdmin == null ? "" : String.valueOf(isAdmin.get());
    }
    public ManagerTableData(Manager manager) {
        this.manager = manager;
        this.name = new SimpleStringProperty(manager.getName() != null ? manager.getName() : "");
        this.surname = new SimpleStringProperty(manager.getSurname() != null ? manager.getSurname() : "");
        this.birthdate = new SimpleStringProperty(manager.getBirthDate() != null ? manager.getBirthDate().toString() : "");
        this.login = new SimpleStringProperty(manager.getLogin() != null ? manager.getLogin() : "");
        this.password = new SimpleStringProperty(manager.getPassword() != null ? manager.getPassword() : "");
        this.employeeId = new SimpleStringProperty(manager.getEmployeeId() != null ? manager.getEmployeeId() : "");
        this.medCertificate = new SimpleStringProperty(manager.getMedCertificate() != null ? manager.getMedCertificate() : "");
        this.employmentDate = new SimpleStringProperty(manager.getEmploymentDate() != null ? String.valueOf(manager.getEmploymentDate()) : "");
        this.isAdmin = new SimpleStringProperty(String.valueOf(manager.isAdmin()));

    }

    public String getName() {
        return name == null ? "" : name.get();
    }

    public String getSurname() {
        return surname == null ? "" : surname.get();
    }

    public String getBirthdate() {
        return birthdate == null ? "" : birthdate.get();
    }

    public String getLogin() {
        return login == null ? "" : login.get();
    }

    public String getPassword() {
        return password == null ? "" : password.get();
    }

    public String getEmployeeId() {
        return employeeId == null ? "" : employeeId.get();
    }

    public String getMedCertificate() {
        return medCertificate == null ? "" : medCertificate.get();
    }

    public String getEmploymentDate() {
        return employmentDate == null ? "" : employmentDate.get();
    }

    public Manager getManager() {
        return manager;
    }
}