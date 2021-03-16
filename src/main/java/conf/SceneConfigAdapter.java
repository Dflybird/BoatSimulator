package conf;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @Author: gq
 * @Date: 2021/3/16 17:43
 */
public class SceneConfigAdapter implements JsonDeserializer<SceneConfig>, JsonSerializer<SceneConfig> {
    @Override
    public SceneConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        return null;
    }

    @Override
    public JsonElement serialize(SceneConfig sceneConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
