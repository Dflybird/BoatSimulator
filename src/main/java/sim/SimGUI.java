package sim;

import ams.agent.usv.BuoyAgent;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
import conf.AgentConfig;
import conf.SceneConfig;
import engine.GameStepController;
import engine.PauseListener;
import environment.Ocean;
import conf.Config;
import ams.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
import gui.*;
import gui.graphic.light.DirectionalLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.obj.Model;
import gui.obj.usv.BoatObj;
import gui.obj.usv.BuoyObj;
import net.SimServer;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import physics.entity.usv.BoatEntity;
import physics.entity.usv.BuoyEntity;
import state.GUIState;
import util.AgentUtil;

import java.io.File;
import java.lang.Math;
import java.util.List;

import static conf.Constant.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class SimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

    private final Config config;
    private final SceneConfig sceneConfig;
    private final Camera camera;
    private final GUIState guiState;
    private final Scene scene;
    private final GUIRenderer renderer;
    private final PhysicsEngine physicsEngine;
    private final GameStepController stepController;
    private final SimServer server;
    private final Ocean ocean;
    private final Model boatModel;
    private final Model buoyModel;

    public static void main(String[] args) {
        main(args, new SimGUI());
    }

    public static void main(String[] args, SimGUI sim){
        sim.start();
    }


    public SimGUI() {
        config = Config.loadConfig();
        sceneConfig = SceneConfig.loadConfig();

        camera = new Camera(new Vector3f(0, 200, 0));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(LENGTH_X, LENGTH_Z, NUM_X, NUM_Z, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
        stepController = new GameStepController(GameStepController.SimType.valueOf(config.getStepType()), config.getStepSize());
        server = new SimServer(this, config.getPort());
        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
        buoyModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BUOY_OBJ_NAME));
    }

    @Override
    public void init(Window window){
        AgentManager.setPhysicsEngine(physicsEngine);
        AgentManager.registerSimStateListener(guiState);
        physicsEngine.init();
        ocean.init(scene, sceneConfig);
        renderer.init(window, camera, scene, guiState);
        server.start();
        stepController.init();

        //环境光
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
//        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1.0f, 0.6f, 0.3f), new Vector3f(1, 0.1f, -1),  1.0f);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1.0f, 0.8f, 0.8f), new Vector3f(1, 0.5f, -1),  1.0f);
        sceneLight.setDirectionalLight(directionalLight);
        scene.setSceneLight(sceneLight);

        initSimScene();
    }

    private final Vector3f cameraInc = new Vector3f();
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    @Override
    public void input(Window window, MouseEvent mouseEvent) {
        cameraInc.set(0,0,0);
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_W) == GLFW_PRESS) {
            cameraInc.z = -10;
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_S) == GLFW_PRESS) {
            cameraInc.z = 10;
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_A) == GLFW_PRESS) {
            cameraInc.x = -10;
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_D) == GLFW_PRESS) {
            cameraInc.x = 10;
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_Z) == GLFW_PRESS) {
            cameraInc.y = -10;
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_X) == GLFW_PRESS) {
            cameraInc.y = 10;
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_O) == GLFW_PRESS) {
            window.drawLine();
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_P) == GLFW_PRESS) {
            window.drawFill();
        }
        //修改相机
        camera.movePosition(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP);
        mouseEvent.input(window);
        if (mouseEvent.isRightButtonPressed()) {
            Vector2f rotVec = mouseEvent.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //reset
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_R) == GLFW_PRESS) {
            reset();
        }

        if (glfwGetKey(window.getWindowID(), GLFW_KEY_UP) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.SteerType.STRAIGHT));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_DOWN) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.SteerType.STOP));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_LEFT) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.SteerType.TURN_LEFT));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.SteerType.TURN_RIGHT));
        }
    }

    @Override
    public void update(double stepTime) {
        if (!stepController.isPause()) {
            //海浪等环境更新
            ocean.update(stepController.getElapsedTime());
            //Agent系统周期更新
            AgentManager.update(stepTime);
        }
    }

    @Override
    public void render(double alpha) {
        guiState.computeRenderState((float) alpha);
        renderer.render();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        server.stop();
        physicsEngine.cleanup();
        AgentManager.stop();
        logger.info("sim shut down");
    }

    @Override
    public void reset() {
//        pause();
        logger.info("reset");
        stepController.init();
        AgentManager.resetAllAgent();
//        play(null);
    }

    @Override
    public void pause() {
        stepController.pause();
    }

    @Override
    public void play(PauseListener listener) {
        stepController.play(listener);
    }

    public void start(){
        Window window = new Window("BoatSimulator", 300, 300, false);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }

    private void initSimScene() {
        //设置浮标，限制场地范围
//        List<AgentConfig> buoys = sceneConfig.getBuoys();
//        for (AgentConfig agentConfig : buoys) {
//            //模型初始朝向面向x轴正方向
//            Vector3f position = agentConfig.getPos();
//            Vector3f scale = new Vector3f(1,1,1);
//            Vector3f modelForward = new Vector3f(1,0,0);
//            Vector3f forward = agentConfig.getForward();
//            Vector3f u = new Vector3f();
//            modelForward.cross(forward, u);
//            float angle = forward.angle(modelForward);
//            u.mul((float) Math.sin(angle/2));
//            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));
//
//            BuoyEntity buoyEntity = new BuoyEntity(ocean,
//                    physicsEngine.getWorld(), physicsEngine.getSpace(),
//                    position, rotation, scale, buoyModel);
//            buoyEntity.createBuoyHelper();
//            BuoyAgent buoyAgent = new BuoyAgent("BUOY_"+agentConfig.getId());
//
//            buoyAgent.setEntity(buoyEntity);
//            AgentManager.addAgent(buoyAgent);
//            GameObj buoy = new BuoyObj(buoyAgent.getAgentID(),
//                    buoyEntity.getTranslation(),
//                    buoyEntity.getRotation(),
//                    buoyEntity.getScale(),
//                    buoyModel);
//            scene.setGameObj(buoy);
//        }
        //main ship
        {
            AgentConfig agentConfig = sceneConfig.getMainShip();
            //模型初始朝向面向x轴正方向
            Vector3f position = agentConfig.getPos();
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = agentConfig.getForward();
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, boatModel);
            boatEntity.createBuoyHelper();
            USVAgent boatAgent = new USVAgent(USVAgent.Camp.MAIN_SHIP, agentConfig.getId(), boatEntity);
            AgentManager.addAgent(boatAgent);
            BoatObj boat = new BoatObj(boatAgent.getAgentID(),
                    boatEntity.getTranslation(),
                    boatEntity.getRotation(),
                    boatEntity.getScale(),
                    boatModel);
            scene.setGameObj(boat);
        }
        //ally usv
        List<AgentConfig> allyUSVs = sceneConfig.getAllyUSVs();
        for (AgentConfig agentConfig : allyUSVs) {
            //模型初始朝向面向x轴正方向
            Vector3f position = agentConfig.getPos();
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = agentConfig.getForward();
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, boatModel);
            boatEntity.createBuoyHelper();
            USVAgent boatAgent = new USVAgent(USVAgent.Camp.ALLY, agentConfig.getId(), boatEntity);
            AgentManager.addAgent(boatAgent);
            BoatObj boat = new BoatObj(boatAgent.getAgentID(),
                    boatEntity.getTranslation(),
                    boatEntity.getRotation(),
                    boatEntity.getScale(),
                    boatModel);
            boat.setColor((float) 0xff/0xff,(float) 0x6e/0xff,(float) 0x40/0xff,1);
            scene.setGameObj(boat);
        }
        //enemy usv
        List<AgentConfig> enemyUSVs = sceneConfig.getEnemyUSVs();
        for (AgentConfig agentConfig : enemyUSVs) {
            //模型初始朝向面向x轴正方向
            Vector3f position = agentConfig.getPos();
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = agentConfig.getForward();
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, boatModel);
            boatEntity.createBuoyHelper();
            USVAgent boatAgent = new USVAgent(USVAgent.Camp.ENEMY, agentConfig.getId(), boatEntity);
            AgentManager.addAgent(boatAgent);
            BoatObj boat = new BoatObj(boatAgent.getAgentID(),
                    boatEntity.getTranslation(),
                    boatEntity.getRotation(),
                    boatEntity.getScale(),
                    boatModel);
            boat.setColor((float) 0x40/0xff,(float) 0xc4/0xff,(float) 0xff/0xff,1);
            scene.setGameObj(boat);
        }

    }

}
