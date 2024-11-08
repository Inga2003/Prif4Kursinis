package com.kursinis.prif4kursinis.webControllers.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import com.kursinis.prif4kursinis.webControllers.serializers.LocalDateGsonSerializer;
import com.kursinis.prif4kursinis.webControllers.serializers.ProductGsonSerializer;
import com.kursinis.prif4kursinis.webControllers.serializers.UserGsonSerializer;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ProductWeb {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
    CustomHib customHib = new CustomHib(entityManagerFactory);

    @RequestMapping(value = "/getProductById/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getProductById(@PathVariable(name = "id") int id){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
                .registerTypeAdapter(Food.class, new ProductGsonSerializer())
                .registerTypeAdapter(Medicines.class, new ProductGsonSerializer())
                .registerTypeAdapter(Clothes.class, new ProductGsonSerializer());

        Product product = customHib.getProductByIdWeb(Product.class, id);
        Gson gson = builder.create();
        return product != null ? gson.toJson(product) : "";
    }


    @RequestMapping(value = "getProductByTitle", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getProductByTitle(@RequestBody String data){
        Gson parser = new Gson();
        Properties properties = parser.fromJson(data, Properties.class);
        return customHib.getProductByTitle(properties.getProperty("title")) != null ? "true" : "false";
    }

    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String updateProduct(@RequestBody String data){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        Gson parser = builder.create();
        String json = data.toLowerCase();
        Product updatedProduct;

        if (json.contains("color")) {
            updatedProduct = parser.fromJson(data, Clothes.class);
        } else if (json.contains("expirationdate")) {
            updatedProduct = parser.fromJson(data, Food.class);
        } else if (json.contains("instruction")) {
            updatedProduct = parser.fromJson(data, Medicines.class);
        } else {
            updatedProduct = parser.fromJson(data, Product.class);
        }

        Product existingProduct = customHib.getEntityById(Product.class, updatedProduct.getId());

        if (existingProduct == null) {
            return "Product not found";
        }

        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        if (existingProduct instanceof Food && updatedProduct instanceof Food) {
            Food existingFood = (Food) existingProduct;
            Food updatedFood = (Food) updatedProduct;
            existingFood.setExpirationDate(updatedFood.getExpirationDate());
        } else if (existingProduct instanceof Medicines && updatedProduct instanceof Medicines) {
            Medicines existingMedicines = (Medicines) existingProduct;
            Medicines updatedMedicines = (Medicines) updatedProduct;
            existingMedicines.setInstruction(updatedMedicines.getInstruction());
        } else if (existingProduct instanceof Clothes && updatedProduct instanceof Clothes) {
            Clothes existingClothes = (Clothes) existingProduct;
            Clothes updatedClothes = (Clothes) updatedProduct;
            existingClothes.setColor(updatedClothes.getColor());
        }
        customHib.update(existingProduct);
        return "Product updated successfully";
    }

    @RequestMapping(value = "/deleteProductById/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String deleteProductById(@PathVariable(name = "id") int id) {
        Product productToDelete = customHib.getEntityById(Product.class, id);
        if (productToDelete == null) {
            return "Product not found";
        }
        customHib.deleteProduct(id);
        return "Product deleted successfully";
    }


    @RequestMapping(value = "/createProduct", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createProduct(@RequestBody String data) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer());
        Gson parser = builder.create();
        JsonObject jsonObject = parser.fromJson(data, JsonObject.class);
        String productType = jsonObject.get("productType").getAsString();

        if ("CLOTHES".equals(productType)) {
            Clothes newClothes = parser.fromJson(data, Clothes.class);
            customHib.create(newClothes);
            return "Clothes created successfully";
        } else if ("MEDICINES".equals(productType)) {
            Medicines newMedicines = parser.fromJson(data, Medicines.class);
            customHib.create(newMedicines);
            return "Medicines created successfully";
        } else if ("FOOD".equals(productType)){
            Food newFood = parser.fromJson(data, Food.class);
            customHib.create(newFood);
        }
        Product newProduct = parser.fromJson(data, Product.class);
        try {
            customHib.create(newProduct);
            return "Product created successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create product";
        }
    }
    @RequestMapping(value = "/getAllProducts", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getAllProducts() {
        List<String> productTitles = customHib.getProductTitles();
        Gson gson = new Gson();
        return gson.toJson(productTitles);
    }

}
