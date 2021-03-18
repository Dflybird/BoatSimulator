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
 * @Date: 2021/3/17 16:23
 */
public class TestRewardConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestRewardConfig.class);

    @Test
    public void testWriteConfig() throws Exception {
        Type type = RewardConfig.class;
        Class<?> clazz = Class.forName(type.getTypeName());
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        RewardConfig rewardConfig = (RewardConfig) constructor.newInstance();

        Field instance = clazz.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(rewardConfig, rewardConfig);

        Field outOfRange = clazz.getDeclaredField("outOfRange");
        outOfRange.setAccessible(true);
        outOfRange.setFloat(rewardConfig, 100);

        Field destroyUSV = clazz.getDeclaredField("destroyUSV");
        destroyUSV.setAccessible(true);
        destroyUSV.setFloat(rewardConfig, 10);

        Field enemyDestroyMainShip = clazz.getDeclaredField("enemyDestroyMainShip");
        enemyDestroyMainShip.setAccessible(true);
        enemyDestroyMainShip.setFloat(rewardConfig, 50);


//        File file = new File(RESOURCES_SCENES_DIR, "defend_main_ship_reward.json");
//        FileUtil.writeFile(file, rewardConfig.toString());
    }

    @Test
    public void testReadConfig(){
        RewardConfig rewardConfig = RewardConfig.loadConfig();
        logger.debug(rewardConfig.toString());
    }
}
