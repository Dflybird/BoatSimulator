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

    public static Config loadConfig() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    String jsonString;
                    try {
                        jsonString = FileUtil.readFileCharacter(new File(Constant.DEFAULT_RESOURCES_DIR, Constant.CONFIG_FILE_NAME));
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    private String sceneFile;

    public String getSceneFile() {
        return sceneFile;
    }

    public void setSceneFile(String sceneFile) {
        this.sceneFile = sceneFile;
    }
}
