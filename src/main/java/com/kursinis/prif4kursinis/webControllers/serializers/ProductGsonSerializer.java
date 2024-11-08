package com.kursinis.prif4kursinis.webControllers.serializers;
import com.google.gson.*;
import com.kursinis.prif4kursinis.model.Product;

import java.lang.reflect.Type;

public class ProductGsonSerializer implements JsonSerializer<Product> {

    @Override
    public JsonElement serialize(Product product, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", product.getTitle());
        jsonObject.addProperty("type", product.getProductType().toString());
        jsonObject.addProperty("description", product.getDescription());
        jsonObject.addProperty("manufacturer", product.getManufacturer());
        return jsonObject;
    }
}
