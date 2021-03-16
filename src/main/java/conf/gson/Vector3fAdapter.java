package conf.gson;

import com.google.gson.*;
import org.joml.Vector3f;

import java.lang.reflect.Type;

/**
 * @Author Gq
 * @Date 2021/3/16 20:01
 * @Version 1.0
 **/
public class Vector3fAdapter implements JsonDeserializer<Vector3f>, JsonSerializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new Vector3f(
                jsonObject.get("x").getAsFloat(),
                jsonObject.get("y").getAsFloat(),
                jsonObject.get("z").getAsFloat());
    }

    @Override
    public JsonElement serialize(Vector3f vector3f, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", vector3f.x);
        jsonObject.addProperty("y", vector3f.y);
        jsonObject.addProperty("z", vector3f.z);
        return jsonObject;
    }
}
