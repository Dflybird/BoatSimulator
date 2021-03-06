package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conf.gson.AgentConfigAdapter;
import conf.gson.SceneConfigAdapter;
import conf.gson.Vector2fAdapter;
import conf.gson.Vector3fAdapter;
import environment.Wind;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static conf.Constant.RESOURCES_SCENES_DIR;

/**
 * @Author: gq
 * @Date: 2021/3/16 16:38
 */
public class SceneConfig {

    private static final Logger logger = LoggerFactory.getLogger(SceneConfig.class);

    private static SceneConfig instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
            .registerTypeAdapter(Vector2f.class, new Vector2fAdapter())
            .registerTypeAdapter(AgentConfig.class, new AgentConfigAdapter())
            .registerTypeAdapter(SceneConfig.class, new SceneConfigAdapter())
            .create();

    private SceneConfig() {}

    public static SceneConfig loadConfig() {
        if (instance == null) {
            synchronized (SceneConfig.class) {
                if (instance == null) {
                    String jsonString;
                    try {
                        File file = new File(RESOURCES_SCENES_DIR, Config.loadConfig().getSceneConfigFile());
                        jsonString = FileUtil.readFileCharacter(file);
                        instance = gson.fromJson(jsonString, SceneConfig.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("Failed to load config file");
                    }
                }
            }
        }

        return instance;
    }

    //海面环境参数
    private float fogVisibility;
    private Wind wind;

    private Vector3f sceneOrigin;
    private float sceneX;
    private float sceneZ;
    //根据场景原点坐标和长宽生成边界
    private float minBoundaryX;
    private float maxBoundaryX;
    private float minBoundaryZ;
    private float maxBoundaryZ;
    //根据生成四个角上的浮标
    private List<AgentConfig> buoys;

    private int allyNum;
    private float allyAttackRange;
    private float allyDetectRange;
    //攻击角度，0~180，单位度
    private float allyAttackAngle;
    private float allyMaxPower;
    private float allyMaxSteeringAngle;
    private float allyMaxSpeed;
    private float allyMinSpeed;
    private List<AgentConfig> allyUSVs;
    private int enemyNum;
    private float enemyAttackRange;
    private float enemyDetectRange;
    private float enemyAttackAngle;
    private float enemyMaxPower;
    private float enemyMaxSteeringAngle;
    private float enemyMaxSpeed;
    private float enemyMinSpeed;
    private List<AgentConfig> enemyUSVs;

    private AgentConfig mainShip;

    public float getFogVisibility() {
        return fogVisibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Vector3f getSceneOrigin() {
        return sceneOrigin;
    }

    public float getSceneX() {
        return sceneX;
    }

    public float getSceneZ() {
        return sceneZ;
    }

    public float getMaxBoundaryX() {
        return maxBoundaryX;
    }

    public float getMinBoundaryX() {
        return minBoundaryX;
    }

    public float getMaxBoundaryZ() {
        return maxBoundaryZ;
    }

    public float getMinBoundaryZ() {
        return minBoundaryZ;
    }

    public List<AgentConfig> getBuoys() {
        return buoys;
    }

    public int getAllyNum() {
        return allyNum;
    }

    public float getAllyAttackRange() {
        return allyAttackRange;
    }

    public float getAllyDetectRange() {
        return allyDetectRange;
    }

    public List<AgentConfig> getAllyUSVs() {
        return allyUSVs;
    }

    public int getEnemyNum() {
        return enemyNum;
    }

    public float getEnemyAttackRange() {
        return enemyAttackRange;
    }

    public float getEnemyDetectRange() {
        return enemyDetectRange;
    }

    public List<AgentConfig> getEnemyUSVs() {
        return enemyUSVs;
    }

    public AgentConfig getMainShip() {
        return mainShip;
    }

    public float getAllyAttackAngle() {
        return allyAttackAngle;
    }

    public float getEnemyAttackAngle() {
        return enemyAttackAngle;
    }

    public float getAllyMaxPower() {
        return allyMaxPower;
    }

    public float getAllyMaxSteeringAngle() {
        return allyMaxSteeringAngle;
    }

    public float getEnemyMaxPower() {
        return enemyMaxPower;
    }

    public float getEnemyMaxSteeringAngle() {
        return enemyMaxSteeringAngle;
    }

    public float getAllyMaxSpeed() {
        return allyMaxSpeed;
    }

    public float getAllyMinSpeed() {
        return allyMinSpeed;
    }

    public float getEnemyMaxSpeed() {
        return enemyMaxSpeed;
    }

    public float getEnemyMinSpeed() {
        return enemyMinSpeed;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
