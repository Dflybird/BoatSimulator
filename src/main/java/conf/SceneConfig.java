package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    public static SceneConfig loadConfig() {
        if (instance == null) {
            synchronized (SceneConfig.class) {
                if (instance == null) {
                    String jsonString;
                    try {
                        File file = new File(RESOURCES_SCENES_DIR, Config.loadConfig().getSceneFile());
                        jsonString = FileUtil.readFileCharacter(file);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    protected Vector3f sceneOrigin;
    protected float sceneX;
    protected float sceneZ;
    protected float maxBoundaryX;
    protected float minBoundaryX;
    protected float maxBoundaryZ;
    protected float minBoundaryZ;

    protected List<AgentConfig> buoys;
    protected List<AgentConfig> allyUSVs;
    protected List<AgentConfig> enemyUSVs;
    protected AgentConfig mainShip;

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

    public List<AgentConfig> getAllyUSVs() {
        return allyUSVs;
    }

    public List<AgentConfig> getEnemyUSVs() {
        return enemyUSVs;
    }

    public AgentConfig getMainShip() {
        return mainShip;
    }
}
