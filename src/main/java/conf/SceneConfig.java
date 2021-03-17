package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conf.gson.AgentConfigAdapter;
import conf.gson.SceneConfigAdapter;
import conf.gson.Vector3fAdapter;
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
            .registerTypeAdapter(AgentConfig.class, new AgentConfigAdapter())
            .registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
            .registerTypeAdapter(SceneConfig.class, new SceneConfigAdapter())
            .create();

    private SceneConfig() {}

    public static SceneConfig loadConfig() {
        if (instance == null) {
            synchronized (SceneConfig.class) {
                if (instance == null) {
                    String jsonString;
                    try {
//                        File file = new File(RESOURCES_SCENES_DIR, Config.loadConfig().getSceneFile());
                        File file = new File(RESOURCES_SCENES_DIR, "defend_main_ship.json");
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
    private List<AgentConfig> allyUSVs;
    private int enemyNum;
    private float enemyAttackRange;
    private float enemyDetectRange;
    private List<AgentConfig> enemyUSVs;

    private AgentConfig mainShip;

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

    @Override
    public String toString() {

        return gson.toJson(this);
    }
}
