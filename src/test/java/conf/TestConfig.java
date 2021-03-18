package conf;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static conf.Constant.RESOURCES_SCENES_DIR;

/**
 * @Author: gq
 * @Date: 2021/3/17 13:52
 */
public class TestConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

    @Test
    public void testWriteConfig() throws Exception {
        Type type = Config.class;
        Class<?> clazz = Class.forName(type.getTypeName());
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Config config = (Config) constructor.newInstance();

        Field instance = clazz.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(config, config);

        Field port = clazz.getDeclaredField("port");
        port.setAccessible(true);
        port.setInt(config, 10086);

        Field stepType = clazz.getDeclaredField("stepType");
        stepType.setAccessible(true);
        stepType.set(config, "STEP");

        Field stepSize = clazz.getDeclaredField("stepSize");
        stepSize.setAccessible(true);
        stepSize.setInt(config, 5);

        Field FPS = clazz.getDeclaredField("FPS");
        FPS.setAccessible(true);
        FPS.setInt(config, 30);

        Field UPS = clazz.getDeclaredField("UPS");
        UPS.setAccessible(true);
        UPS.setInt(config, 150);

        Field fastForwardSpeed = clazz.getDeclaredField("fastForwardSpeed");
        fastForwardSpeed.setAccessible(true);
        fastForwardSpeed.setDouble(config, 1);

        Field isRender = clazz.getDeclaredField("isRender");
        isRender.setAccessible(true);
        isRender.setBoolean(config, true);

        Field sceneConfigFile = clazz.getDeclaredField("sceneConfigFile");
        sceneConfigFile.setAccessible(true);
        sceneConfigFile.set(config, "defend_main_ship_scene.json");

        Field rewardConfigFile = clazz.getDeclaredField("rewardConfigFile");
        rewardConfigFile.setAccessible(true);
        rewardConfigFile.set(config, "defend_main_ship_reward.json");

//        File file = new File(Constant.DEFAULT_RESOURCES_DIR, Constant.CONFIG_FILE_NAME);
//        FileUtil.writeFile(file, config.toString());
    }

    @Test
    public void testReadConfig(){
        Config config = Config.loadConfig();
        logger.debug(config.toString());
    }
}
