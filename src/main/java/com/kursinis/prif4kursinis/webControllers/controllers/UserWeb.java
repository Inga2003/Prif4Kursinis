package com.kursinis.prif4kursinis.webControllers.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import com.kursinis.prif4kursinis.webControllers.serializers.LocalDateGsonSerializer;
import com.kursinis.prif4kursinis.webControllers.serializers.UserGsonSerializer;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Properties;

@Controller
public class UserWeb {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
    CustomHib customHib = new CustomHib(entityManagerFactory);

    @RequestMapping(value = "/getUserByCredentials", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUserByCredentials(@RequestBody String data){
        Gson parser = new Gson();
        Properties properties = parser.fromJson(data, Properties.class);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        builder.registerTypeAdapter(Manager.class, new UserGsonSerializer());
        builder.registerTypeAdapter(Customer.class, new UserGsonSerializer());
        builder.registerTypeAdapter(User.class, new UserGsonSerializer());
        Gson gson = builder.create();
        User user = customHib.getUserByCredentials(properties.getProperty("login"), properties.getProperty("password"));
        return user != null ? gson.toJson(user) : "Error";
    }

    @RequestMapping(value = "/getUserById/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUserById(@PathVariable(name = "id") int id){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                .registerTypeAdapter(Manager.class, new UserGsonSerializer())
                .registerTypeAdapter(Customer.class, new UserGsonSerializer());
        User user = customHib.getEntityById(User.class, id);

        Gson gson = builder.create();
        return user != null ? gson.toJson(user) : "";

    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createManager(@RequestBody String userData) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        Gson gson = builder.create();

        if (userData.contains("userType")) {
            String userType = gson.fromJson(userData, JsonObject.class).get("userType").getAsString();

            if (userType.equals("Manager")) {
                Manager newManager = gson.fromJson(userData, Manager.class);
                customHib.create(newManager);
                return "Manager created successfully";
            } else if (userType.equals("Customer")) {
                Customer newCustomer = gson.fromJson(userData, Customer.class);
                customHib.create(newCustomer);
                return "Customer created successfully";
            }
            else {
                return "Invalid user type specified";
            }
        } else {
            return "User type not specified";
        }
    }

    @RequestMapping(value = "/updateUserPassword/{userId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String updateUserPassword(@PathVariable(name = "userId") int userId, @RequestBody String newPassword) {
        Gson parser = new Gson();
        Properties properties = parser.fromJson(newPassword, Properties.class);
        User user = customHib.getEntityById(User.class, userId);
        if (user != null){
            customHib.updateUserPassword(userId, properties.getProperty("newPassword"));
            return "User password updated successfully";
        } else return "No such user.";
    }

}
