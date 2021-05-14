package data_acquisition;

import ams.AgentManager;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
import conf.Config;
import engine.GameEngine;
import engine.GameLogic;
import engine.GameStepController;
import engine.PauseListener;
import environment.Ocean;
import gui.*;
import gui.graphic.light.DirectionalLight;
import gui.obj.Camera;
import gui.obj.Model;
import gui.obj.usv.BoatObj;
import net.ControllerServer;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import physics.buoy.ModifyBoatMesh;
import physics.entity.usv.BoatEntity;
import state.GUIState;
import util.TimeUtil;

import java.io.File;
import java.util.Random;

import static conf.Constant.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author: gq
 * @Date: 2021/3/22 15:32
 */
public class ExpRemoteSim implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(ExpRemoteSim.class);

    private final Config config;
    private final Camera camera;
    private final GUIState guiState;
    private final Scene scene;
    private final GUIRenderer renderer;
    private final PhysicsEngine physicsEngine;
    private final GameStepController stepController;
    private final Ocean ocean;
    private final Model boatModel;
    private final Model buoyModel;
    private final ControllerServer server;

    private ModifyBoatMesh modifyBoatMesh;

    private USVAgent boatAgent;

    public static void main(String[] args) {
        main(args, new ExpRemoteSim());
    }

    public static void main(String[] args, ExpRemoteSim sim){
        sim.start();
    }


    public ExpRemoteSim() {
        config = Config.loadConfig();

        camera = new Camera(new Vector3f(0, 50, 0));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(LENGTH_X, LENGTH_Z, NUM_X, NUM_Z, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
        stepController = new GameStepController(GameStepController.SimType.valueOf(config.getStepType()), config.getStepSize());
        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
        buoyModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BUOY_OBJ_NAME));
        server = new ControllerServer(this, 12345);
    }

    @Override
    public void init(Window window){
        AgentManager.setPhysicsEngine(physicsEngine);
        AgentManager.registerSimStateListener(guiState);
        server.start();
        physicsEngine.init();
        ocean.init(scene, null);
        renderer.init(window, camera, scene, guiState);
        stepController.init();

        //环境光
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.5f, 0.5f, 0.5f));
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
//            AgentManager.sendAgentMessage("ENEMY_0", new SteerMessage(SteerMessage.ControllerType.SECOND_STRAIGHT));
            AgentManager.sendAgentMessage("ENEMY_0", new SteerMessage(32000,0));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_DOWN) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.STOP));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_LEFT) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.SECOND_TURN_LEFT));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.FIRST_TURN_HALF_RIGHT));
        }
    }

    private int nodeNum = 10;

    int writeRate = 0;
    int dataSize = 0;
    USVBuoyData usvBuoyData = new USVBuoyData();
    DistributeData distributeData = new DistributeData(nodeNum);
    MsgData msgData = new MsgData();
    double accTime = 0;
    int initTime = 200;
    boolean initData = true;
    boolean began = false;
    int secNum = 0;
    Random random = new Random(System.currentTimeMillis());
    @Override
    public void update(double stepTime) {
        if (!stepController.isPause()) {
            //海浪等环境更新
            ocean.update(stepController.getElapsedTime());
            double s = TimeUtil.currentTime();
            //Agent系统周期更新
            AgentManager.update(stepTime);
            double e = TimeUtil.currentTime();




            if (initData && initTime-- < 0) {
                initData = false;
                began = true;
                server.getMsgNum();
                AgentManager.getMsgNum();
                logger.info("start to collect.");
            }

            if (began && dataSize < 1000) {
                dataSize++;
//                {
//                    accTime += stepController.getDeltaTime();
//                    if (accTime >= 1) {
//                        accTime -= 1;
//                        secNum++;
//                        distributeData.msgNum += server.getMsgNum();
//                        logger.info("msg {}", distributeData.msgNum);
//                    }
//                    distributeData.elapsedTime += stepController.getDeltaTime();
//                    distributeData.updateTime += (e-s);
//                    if (dataSize == 500) {
//                        distributeData.msgNum /= secNum;
//                        distributeData.elapsedTime /= 500;
//                        distributeData.updateTime /= 500;
//                        distributeData.writeToFile();
//                        logger.info("write data file done");
//                    }
//                }
                {
                    msgData.agentMsg.add(AgentManager.getMsgNum());
                    msgData.netMsg.add(server.getMsgNum());
                    if (dataSize == 1000) {
                        msgData.writeToFile();
                        logger.info("write data file done");
                    }
                }
            }

//        if (dataSize < 10 && boatAgent.getEntity().getTranslation().x > dataSize * 10) {
//            dataSize++;
//            usvBuoyData.buoyData.add(boatAgent.getEntity().getTranslation().z);
//            if (dataSize==10) {
//                usvBuoyData.writeToFile();
//                logger.info("write data file done");
//            }
//        }
//        AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(5000, 0));
        }
    }

    @Override
    public void render(double alpha) {
        guiState.computeRenderState((float) alpha);
        renderer.render();

        Vector3f newScale = new Vector3f(modifyBoatMesh.getEntity().getScale());
        renderer.renderMeshes(modifyBoatMesh.getUnderwaterModel(),
                modifyBoatMesh.getEntity().getTranslation(),
                modifyBoatMesh.getEntity().getRotation(),
                newScale.mul(1.001f,1.001f,1.001f));
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
        for (int i = 0; i < nodeNum; i++) {


            //ally usv
            //模型初始朝向面向x轴正方向
            Vector3f position = new Vector3f(random.nextFloat() * 200,0,random.nextFloat() * 200);
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = new Vector3f(1,0,0);
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, boatModel);
            boatEntity.createBuoyHelper();
            boatAgent = new USVAgent(USVAgent.Camp.ALLY, i, boatEntity);
            AgentManager.addAgent(boatAgent);
            BoatObj boat = new BoatObj(boatAgent.getAgentID(),
                    boatEntity.getTranslation(),
                    boatEntity.getRotation(),
                    boatEntity.getScale(),
                    boatModel);
            boat.setColor((float) 0xff/0xff,(float) 0x6e/0xff,(float) 0x40/0xff,1);
            scene.setGameObj(boat);

            modifyBoatMesh = boatEntity.getBuoyHelper().getModifyBoatMesh();
        }

        for (int i = 0; i < 100; i++) {


            //ally usv
            //模型初始朝向面向x轴正方向
            Vector3f position = new Vector3f(random.nextFloat() * 200 + 100,0,random.nextFloat() * 200);
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = new Vector3f(1,0,0);
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, boatModel);
            boatEntity.createBuoyHelper();
            boatAgent = new USVAgent(USVAgent.Camp.ENEMY, i, boatEntity);
            AgentManager.addAgent(boatAgent);
            BoatObj boat = new BoatObj(boatAgent.getAgentID(),
                    boatEntity.getTranslation(),
                    boatEntity.getRotation(),
                    boatEntity.getScale(),
                    boatModel);
            boat.setColor((float) 0xff/0xff,(float) 0x6e/0xff,(float) 0x40/0xff,1);
            scene.setGameObj(boat);

            modifyBoatMesh = boatEntity.getBuoyHelper().getModifyBoatMesh();
        }

//        {
//            //模型初始朝向面向x轴正方向
//            Vector3f position = new Vector3f(-1,0,0);
//            Vector3f scale = new Vector3f(1,1,1);
//            Vector3f modelForward = new Vector3f(1,0,0);
//            Vector3f forward = new Vector3f(1,0,0);
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
//            BuoyAgent buoyAgent = new BuoyAgent("BUOY_0");
//
//            buoyAgent.setEntity(buoyEntity);
//            AgentManager.addAgent(buoyAgent);
//            BuoyObj buoy = new BuoyObj(buoyAgent.getAgentID(),
//                    buoyEntity.getTranslation(),
//                    buoyEntity.getRotation(),
//                    buoyEntity.getScale(),
//                    buoyModel);
//            buoy.setColor((float) 0xff/0xff,(float) 0xc4/0xff,(float) 0x00/0xff,1);
//            scene.setGameObj(buoy);
//        }
    }
}
