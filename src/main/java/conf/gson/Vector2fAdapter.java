package conf.gson;

import com.google.gson.*;
import org.joml.Vector2f;
import org.joml.Vector2f;

import java.lang.reflect.Type;

/**
 * @Author Gq
 * @Date 2021/3/16 20:01
 * @Version 1.0
 **/
public class Vector2fAdapter implements JsonDeserializer<Vector2f>, JsonSerializer<Vector2f> {
    @Override
    public Vector2f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new Vector2f(
                jsonObject.get("x").getAsFloat(),
                jsonObject.get("z").getAsFloat());
    }

    @Override
    public JsonElement serialize(Vector2f vector3f, Type type, JsonSerializationContext jsonSerializationContext) {
        if (vector3f == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", vector3f.x);
        jsonObject.addProperty("z", vector3f.y);
        return jsonObject;
    }
}
