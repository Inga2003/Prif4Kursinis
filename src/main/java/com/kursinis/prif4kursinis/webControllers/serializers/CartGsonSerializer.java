package com.kursinis.prif4kursinis.webControllers.serializers;
import com.google.gson.*;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.Product;

import java.lang.reflect.Type;

public class CartGsonSerializer implements JsonSerializer<Cart> {

    @Override
    public JsonElement serialize(Cart cart, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dateCreated", cart.getDateCreated().toString());

        if (cart.getUser() != null) {
            jsonObject.addProperty("id", cart.getId());
            jsonObject.addProperty("userName", cart.getUser().getName());
            jsonObject.addProperty("userSurname", cart.getUser().getSurname());
        }

        // Include Product details
        if (cart.getItemsInCart() != null && !cart.getItemsInCart().isEmpty()) {
            JsonArray productsArray = new JsonArray();
            for (Product product : cart.getItemsInCart()) {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("title", product.getTitle());
                productsArray.add(productObject);
            }
            jsonObject.add("productsInCart", productsArray);
        }

        return jsonObject;
    }
}
