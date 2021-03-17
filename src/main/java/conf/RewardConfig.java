package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.io.IOException;

import static conf.Constant.RESOURCES_SCENES_DIR;

/**
 * @Author: gq
 * @Date: 2021/3/17 14:53
 */
public class RewardConfig {

    private static final Logger logger = LoggerFactory.getLogger(RewardConfig.class);

    private static RewardConfig instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private RewardConfig() {}

    public static RewardConfig loadConfig() {
        if (instance == null) {
            synchronized (RewardConfig.class) {
                if (instance == null) {
                    String jsonString;
                    try {
                        File file = new File(RESOURCES_SCENES_DIR, Config.loadConfig().getRewardConfigFile());
                        jsonString = FileUtil.readFileCharacter(file);
                        instance = gson.fromJson(jsonString, RewardConfig.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("Failed to load config file");
                    }
                }
            }
        }

        return instance;
    }

    private float outOfRange;
    private float destroyUSV;
    private float enemyDestroyMainShip;


    public float getOutOfRange() {
        return outOfRange;
    }

    public float getDestroyUSV() {
        return destroyUSV;
    }

    public float getEnemyDestroyMainShip() {
        return enemyDestroyMainShip;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
