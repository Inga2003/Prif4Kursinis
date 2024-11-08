package com.kursinis.prif4kursinis.model;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerTableData {
    private SimpleStringProperty name;
    private SimpleStringProperty surname;
    private SimpleStringProperty birthdate;
    private SimpleStringProperty address;
    private SimpleStringProperty cardNumber;
    private SimpleStringProperty login;
    private SimpleStringProperty password;
    private Customer customer;

    public CustomerTableData(Customer customer) {
        this.customer = customer;
        this.name = new SimpleStringProperty(customer.getName() != null ? customer.getName() : "");
        this.surname = new SimpleStringProperty(customer.getSurname() != null ? customer.getSurname() : "");
        this.birthdate = new SimpleStringProperty(customer.getBirthDate() != null ? customer.getBirthDate().toString() : "");
        this.address = new SimpleStringProperty(customer.getAddress() != null ? customer.getAddress() : "");
        this.cardNumber = new SimpleStringProperty(customer.getCardNo() != null ? customer.getCardNo() : "");
        this.login = new SimpleStringProperty(customer.getLogin() != null ? customer.getLogin() : "");
        this.password = new SimpleStringProperty(customer.getPassword() != null ? customer.getPassword() : "");

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

    public String getAddress() {
        return address == null ? "" : address.get();
    }

    public String getLogin() {
        return login == null ? "" : login.get();
    }

    public String getPassword() {
        return password == null ? "" : password.get();
    }

    public String getCardNumber() {
        return cardNumber == null ? "" : cardNumber.get();
    }

    public Customer getCustomer() {
        return customer;
    }

}