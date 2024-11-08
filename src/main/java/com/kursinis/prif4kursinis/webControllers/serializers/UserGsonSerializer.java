package com.kursinis.prif4kursinis.webControllers.serializers;
import com.google.gson.*;
import com.kursinis.prif4kursinis.model.User;

import java.lang.reflect.Type;

public class UserGsonSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("login", user.getLogin());
        jsonObject.addProperty("password", user.getPassword());
        jsonObject.addProperty("name", user.getName());
        jsonObject.addProperty("surname", user.getSurname());
        jsonObject.addProperty("birthDate", user.getBirthDate().toString());
        return jsonObject;
    }
}