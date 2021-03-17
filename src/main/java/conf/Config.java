package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @Author Gq
 * @Date 2021/1/28 19:52
 * @Version 1.0
 **/
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static Config instance;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Config loadConfig() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    String jsonString;
                    try {
                        jsonString = FileUtil.readFileCharacter(new File(Constant.DEFAULT_RESOURCES_DIR, Constant.CONFIG_FILE_NAME));
                        instance = gson.fromJson(jsonString, Config.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("Failed to load config file");
                    }

                }
            }
        }
        return instance;
    }

    /** 网络 **/
    private int port;
    /** 仿真循环方式 STEP:循环一定步长后停止 ROLL:持续循环 **/
    private String stepType;
    /** STEP模式下循环stepSize后仿真暂停 **/
    private int stepSize;
    /** 渲染频率 **/
    private int FPS;
    /** 更新频率 **/
    private int UPS;
    /** 快进速度 **/
    private double fastForwardSpeed;
    /** 是否渲染 **/
    private boolean isRender;

    private String sceneConfigFile;

    private String rewardConfigFile;

    public int getPort() {
        return port;
    }

    public String getStepType() {
        return stepType;
    }

    public int getStepSize() {
        return stepSize;
    }

    public int getFPS() {
        return FPS;
    }

    public int getUPS() {
        return UPS;
    }

    public double getFastForwardSpeed() {
        return fastForwardSpeed;
    }

    public boolean isRender() {
        return isRender;
    }

    public String getSceneConfigFile() {
        return sceneConfigFile;
    }

    public String getRewardConfigFile() {
        return rewardConfigFile;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
