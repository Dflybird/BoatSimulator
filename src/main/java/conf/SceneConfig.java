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

/**
 * @Author: gq
 * @Date: 2021/3/16 16:38
 */
public class SceneConfig {

    private static final Logger logger = LoggerFactory.getLogger(SceneConfig.class);


    public static SceneConfig loadConfig(File sceneConfigFile) {
        String jsonString;
        try {
//            jsonString = FileUtil.readFileCharacter(new File(Constant.RESOURCES_SCENES_DIR, Config.loadConfig().getSceneFile()));
            jsonString = FileUtil.readFileCharacter(sceneConfigFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(jsonString, SceneConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to load config file");
        }
        return null;
    }

    private Vector3f sceneOrigin;
    private float sceneX;
    private float sceneZ;
    private float maxBoundaryX;
    private float minBoundaryX;
    private float maxBoundaryZ;
    private float minBoundaryZ;

    private List<Vector3f> buoyPos;




}
