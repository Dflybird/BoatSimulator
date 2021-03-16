package conf.gson;

import com.google.gson.*;
import conf.AgentConfig;
import org.joml.Vector3f;

import java.lang.reflect.Type;

/**
 * @Author Gq
 * @Date 2021/3/16 19:29
 * @Version 1.0
 **/
public class AgentConfigAdapter implements JsonDeserializer<AgentConfig>, JsonSerializer<AgentConfig> {
    @Override
    public AgentConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setId(jsonObject.get("id").getAsInt());
        agentConfig.setPos(jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("pos"), Vector3f.class));
        agentConfig.setForward(jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("forward"), Vector3f.class));
        return agentConfig;
    }

    @Override
    public JsonElement serialize(AgentConfig agentConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", agentConfig.getId());
        jsonObject.add("pos", jsonSerializationContext.serialize(agentConfig.getPos(), Vector3f.class));
        jsonObject.add("forward", jsonSerializationContext.serialize(agentConfig.getForward()));
        return jsonObject;
    }
}
