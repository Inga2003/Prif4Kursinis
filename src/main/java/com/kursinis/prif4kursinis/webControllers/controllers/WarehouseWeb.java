package com.kursinis.prif4kursinis.webControllers.controllers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import com.kursinis.prif4kursinis.webControllers.serializers.LocalDateGsonSerializer;

import com.kursinis.prif4kursinis.webControllers.serializers.UserGsonSerializer;
import com.kursinis.prif4kursinis.webControllers.serializers.WarehouseGsonSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Properties;

@Controller
public class WarehouseWeb {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
    CustomHib customHib = new CustomHib(entityManagerFactory);
    @RequestMapping(value = "/getWarehouseById/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getWarehouseById(@PathVariable(name = "id") int id){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Warehouse.class, new WarehouseGsonSerializer());
        Warehouse warehouse = customHib.getEntityById(Warehouse.class, id);
        Gson gson = builder.create();
        return warehouse != null ? gson.toJson(warehouse) : "No such warehouse.";
    }

    @RequestMapping(value = "/updateWarehouse", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String updateWarehouse(@RequestBody String data) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        builder.registerTypeAdapter(Warehouse.class, new WarehouseGsonSerializer());
        Gson gson = builder.create();
        Gson parser = builder.create();

        Warehouse updatedWarehouse = parser.fromJson(data, Warehouse.class);
        Warehouse existingWarehouse = customHib.getEntityById(Warehouse.class, updatedWarehouse.getId());
        if (existingWarehouse == null) {
            return "Warehouse not found.";
        }
        existingWarehouse.setTitle(updatedWarehouse.getTitle());
        existingWarehouse.setAddress(updatedWarehouse.getAddress());
        customHib.update(existingWarehouse);
        return "Warehouse updated successfully";
    }

    @RequestMapping(value = "/createWarehouse", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createWarehouse(@RequestBody String data) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        builder.registerTypeAdapter(Warehouse.class, new WarehouseGsonSerializer());
        Gson gson = builder.create();
        Gson parser = builder.create();

        Warehouse newWarehouse = parser.fromJson(data, Warehouse.class);
        customHib.create(newWarehouse);
        return "Warehouse created successfully";
    }

    @RequestMapping(value = "/deleteWarehouse/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String deleteWarehouse(@PathVariable(name = "id") int id) {
        Warehouse warehouseToDelete = customHib.getEntityById(Warehouse.class, id);

        if (warehouseToDelete == null) {
            return "Warehouse not found.";
        }

        customHib.deleteWarehouse(id);
        return "Warehouse deleted successfully";
    }
}
