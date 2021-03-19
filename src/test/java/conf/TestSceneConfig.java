package conf;

import environment.Wind;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static conf.Constant.RESOURCES_SCENES_DIR;

/**
 * @Author: gq
 * @Date: 2021/3/17 10:36
 */
public class TestSceneConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestSceneConfig.class);

    @Test
    public void testAnnotation() throws Exception {
        Type type = SceneConfig.class;
        Class<?> clazz = Class.forName(type.getTypeName());
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        SceneConfig sceneConfig = (SceneConfig) constructor.newInstance();
        Field allyAttackRange = clazz.getDeclaredField("allyAttackRange");
        allyAttackRange.setAccessible(true);
        allyAttackRange.setFloat(sceneConfig, 30);

        Field instance = clazz.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(sceneConfig, sceneConfig);
        logger.debug("aat1: {}", sceneConfig.getAllyAttackRange());
        logger.debug("aat2: {}", SceneConfig.loadConfig().getAllyAttackRange());
    }

    @Test
    public void testWriteSceneConfig() throws Exception {
        float fogVisibility = 100;
        Wind wind = new Wind(30, new Vector2f(1,0));
        Vector3f sceneOrigin = new Vector3f(0,0,0);
        float sceneX = 100;
        float sceneZ = 100;
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
                buoyConfig.setId(i+j);
                buoyConfig.setForward(new Vector3f(1,0,0));
                buoyConfig.setPos(new Vector3f(boundaryX[i], 0, boundaryZ[j]));
                buoys.add(buoyConfig);
            }
        }
        float allyAttackRange = 100;
        float allyDetectRange = 100;
        float allyAttackAngle = 30;
        List<AgentConfig> allyUSVs = new ArrayList<>();
        AgentConfig a1 = new AgentConfig();
        a1.setId(0);
        a1.setForward(new Vector3f(1,0,0));
        a1.setPos(new Vector3f(10,0,10));
        allyUSVs.add(a1);
        AgentConfig a2 = new AgentConfig();
        a2.setId(1);
        a2.setForward(new Vector3f(1,0,0));
        a2.setPos(new Vector3f(10,0,10));
        allyUSVs.add(a2);
        int allyNum = allyUSVs.size();

        float enemyAttackRange = 100;
        float enemyDetectRange = 100;
        float enemyAttackAngle = 30;
        List<AgentConfig> enemyUSVs = new ArrayList<>();
        int enemyNum = enemyUSVs.size();

        AgentConfig mainShip = new AgentConfig();
        mainShip.setId(0);
        mainShip.setForward(new Vector3f(0,0,1));
        mainShip.setPos(new Vector3f(0,0,0));

        //反射构建对象
        Type type = SceneConfig.class;
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

//        File file = new File(RESOURCES_SCENES_DIR, "defend_main_ship_scene.json");
//        FileUtil.writeFile(file, sceneConfig.toString());
    }

    @Test
    public void testReadSceneConfig() {
        SceneConfig sceneConfig = SceneConfig.loadConfig();
        logger.debug(sceneConfig.toString());
        for (AgentConfig agentConfig : sceneConfig.getBuoys()) {
            logger.debug(agentConfig.toString());
        }
//        for (AgentConfig agentConfig : sceneConfig.getAllyUSVs()) {
//            logger.debug(agentConfig.toString());
//        }
    }
}
