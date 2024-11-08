package com.kursinis.prif4kursinis.webControllers.serializers;
import com.google.gson.*;
import com.kursinis.prif4kursinis.model.Warehouse;
import java.lang.reflect.Type;

public class WarehouseGsonSerializer implements JsonSerializer<Warehouse> {

    @Override
    public JsonElement serialize(Warehouse warehouse, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", warehouse.getTitle());
        jsonObject.addProperty("address", warehouse.getAddress());
        return jsonObject;
    }
}