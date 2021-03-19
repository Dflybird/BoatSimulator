package conf.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import conf.AgentConfig;
import conf.SceneConfig;
import environment.Wind;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2021/3/16 17:43
 */
public class SceneConfigAdapter implements JsonDeserializer<SceneConfig>, JsonSerializer<SceneConfig> {
    @Override
    public SceneConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            if (jsonElement != null) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                
                float fogVisibility = jsonObject.get("fogVisibility").getAsFloat();
                Wind wind = jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("wind"), Wind.class);
                Vector3f sceneOrigin = jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("sceneOrigin"), Vector3f.class);
                float sceneX = jsonObject.get("sceneX").getAsFloat();
                float sceneZ = jsonObject.get("sceneZ").getAsFloat();
                //0:minBoundaryX 1:maxBoundaryX
                float[] boundaryX = new float[2];
                //0:minBoundaryZ 1:maxBoundaryZ
                float[] boundaryZ = new float[2];
                boundaryX[0] = sceneOrigin.x - sceneX / 2;
                boundaryX[1] = sceneOrigin.x + sceneX / 2;
                boundaryZ[0] = sceneOrigin.z - sceneZ / 2;
                boundaryZ[1] = sceneOrigin.z + sceneZ / 2;
                List<AgentConfig> buoys = new ArrayList<>(4);
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        AgentConfig buoyConfig = new AgentConfig();
                        buoyConfig.setId(i+j*2);
                        buoyConfig.setForward(new Vector3f(1,0,0));
                        buoyConfig.setPos(new Vector3f(boundaryX[i], 0, boundaryZ[j]));
                        buoys.add(buoyConfig);
                    }
                }

                Type agentConfigListType = new TypeToken<ArrayList<AgentConfig>>(){}.getType();

                float allyAttackRange = jsonObject.get("allyAttackRange").getAsFloat();
                float allyDetectRange = jsonObject.get("allyDetectRange").getAsFloat();
                float allyAttackAngle = jsonObject.get("allyAttackAngle").getAsFloat();
                List<AgentConfig> allyUSVs = jsonDeserializationContext.deserialize(jsonObject.getAsJsonArray("allyUSVs"), agentConfigListType);
                int allyNum = allyUSVs.size();

                float enemyAttackRange = jsonObject.get("enemyAttackRange").getAsFloat();
                float enemyDetectRange = jsonObject.get("enemyDetectRange").getAsFloat();
                float enemyAttackAngle = jsonObject.get("enemyAttackAngle").getAsFloat();
                List<AgentConfig> enemyUSVs = jsonDeserializationContext.deserialize(jsonObject.getAsJsonArray("enemyUSVs"), agentConfigListType);
                int enemyNum = enemyUSVs.size();

                AgentConfig mainShip = jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("mainShip"), AgentConfig.class);
                
                //反射构建对象
                Class<?> clazz = Class.forName(type.getTypeName());
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                SceneConfig sceneConfig = (SceneConfig) constructor.newInstance();
                //为单例赋值
                Field instance = clazz.getDeclaredField("instance");
                instance.setAccessible(true);
                instance.set(sceneConfig, sceneConfig);
                //为其他成员变量赋值
                Field fogVisibilityField = clazz.getDeclaredField("fogVisibility");
                fogVisibilityField.setAccessible(true);
                fogVisibilityField.set(sceneConfig, fogVisibility);
                
                Field windField = clazz.getDeclaredField("wind");
                windField.setAccessible(true);
                windField.set(sceneConfig, wind);
                
                Field sceneOriginField = clazz.getDeclaredField("sceneOrigin");
                sceneOriginField.setAccessible(true);
                sceneOriginField.set(sceneConfig, sceneOrigin);

                Field sceneXField = clazz.getDeclaredField("sceneX");
                sceneXField.setAccessible(true);
                sceneXField.set(sceneConfig, sceneX);

                Field sceneZField = clazz.getDeclaredField("sceneZ");
                sceneZField.setAccessible(true);
                sceneZField.set(sceneConfig, sceneZ);

                Field minBoundaryXField = clazz.getDeclaredField("minBoundaryX");
                minBoundaryXField.setAccessible(true);
                minBoundaryXField.set(sceneConfig, boundaryX[0]);

                Field maxBoundaryXField = clazz.getDeclaredField("maxBoundaryX");
                maxBoundaryXField.setAccessible(true);
                maxBoundaryXField.set(sceneConfig, boundaryX[1]);

                Field minBoundaryZField = clazz.getDeclaredField("minBoundaryZ");
                minBoundaryZField.setAccessible(true);
                minBoundaryZField.set(sceneConfig, boundaryZ[0]);

                Field maxBoundaryZField = clazz.getDeclaredField("maxBoundaryZ");
                maxBoundaryZField.setAccessible(true);
                maxBoundaryZField.set(sceneConfig, boundaryZ[1]);

                Field buoysField = clazz.getDeclaredField("buoys");
                buoysField.setAccessible(true);
                buoysField.set(sceneConfig, buoys);

                Field allyNumField = clazz.getDeclaredField("allyNum");
                allyNumField.setAccessible(true);
                allyNumField.set(sceneConfig, allyNum);

                Field allyAttackRangeField = clazz.getDeclaredField("allyAttackRange");
                allyAttackRangeField.setAccessible(true);
                allyAttackRangeField.set(sceneConfig, allyAttackRange);

                Field allyDetectRangeField = clazz.getDeclaredField("allyDetectRange");
                allyDetectRangeField.setAccessible(true);
                allyDetectRangeField.set(sceneConfig, allyDetectRange);

                Field allyAttackAngleField = clazz.getDeclaredField("allyAttackAngle");
                allyAttackAngleField.setAccessible(true);
                allyAttackAngleField.set(sceneConfig, allyAttackAngle);

                Field allyUSVsField = clazz.getDeclaredField("allyUSVs");
                allyUSVsField.setAccessible(true);
                allyUSVsField.set(sceneConfig, allyUSVs);

                Field enemyNumField = clazz.getDeclaredField("enemyNum");
                enemyNumField.setAccessible(true);
                enemyNumField.set(sceneConfig, enemyNum);

                Field enemyAttackRangeField = clazz.getDeclaredField("enemyAttackRange");
                enemyAttackRangeField.setAccessible(true);
                enemyAttackRangeField.set(sceneConfig, enemyAttackRange);

                Field enemyDetectRangeField = clazz.getDeclaredField("enemyDetectRange");
                enemyDetectRangeField.setAccessible(true);
                enemyDetectRangeField.set(sceneConfig, enemyDetectRange);

                Field enemyAttackAngleField = clazz.getDeclaredField("enemyAttackAngle");
                enemyAttackAngleField.setAccessible(true);
                enemyAttackAngleField.set(sceneConfig, enemyAttackAngle);

                Field enemyUSVsField = clazz.getDeclaredField("enemyUSVs");
                enemyUSVsField.setAccessible(true);
                enemyUSVsField.set(sceneConfig, enemyUSVs);

                Field mainShipField = clazz.getDeclaredField("mainShip");
                mainShipField.setAccessible(true);
                mainShipField.set(sceneConfig, mainShip);
                
                return sceneConfig;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(SceneConfig sceneConfig, Type type, JsonSerializationContext jsonSerializationContext) {
        if (sceneConfig == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fogVisibility", sceneConfig.getFogVisibility());
        jsonObject.add("wind", jsonSerializationContext.serialize(sceneConfig.getWind()));
        jsonObject.add("sceneOrigin", jsonSerializationContext.serialize(sceneConfig.getSceneOrigin()));
        jsonObject.addProperty("sceneX", sceneConfig.getSceneX());
        jsonObject.addProperty("sceneZ", sceneConfig.getSceneZ());
        jsonObject.addProperty("allyAttackRange", sceneConfig.getAllyAttackRange());
        jsonObject.addProperty("allyDetectRange", sceneConfig.getAllyDetectRange());
        jsonObject.addProperty("allyAttackAngle", sceneConfig.getAllyAttackAngle());
        jsonObject.add("allyUSVs", jsonSerializationContext.serialize(sceneConfig.getAllyUSVs()));
        jsonObject.addProperty("enemyAttackRange", sceneConfig.getEnemyAttackRange());
        jsonObject.addProperty("enemyDetectRange", sceneConfig.getEnemyDetectRange());
        jsonObject.addProperty("enemyAttackAngle", sceneConfig.getEnemyAttackAngle());
        jsonObject.add("enemyUSVs", jsonSerializationContext.serialize(sceneConfig.getEnemyUSVs()));
        jsonObject.add("mainShip", jsonSerializationContext.serialize(sceneConfig.getMainShip()));

        return jsonObject;
    }
}
