package data_acquisition;

import ams.AgentManager;
import ams.agent.usv.BuoyAgent;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
import analysis.ChartOps;
import analysis.SimulationCharts;
import conf.AgentConfig;
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
import gui.obj.usv.BuoyObj;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import physics.buoy.ModifyBoatMesh;
import physics.entity.usv.BoatEntity;
import physics.entity.usv.BuoyEntity;
import state.GUIState;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static conf.Constant.*;
import static conf.Constant.BUOY_OBJ_NAME;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author: gq
 * @Date: 2021/3/22 15:32
 */
public class ExpSim implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(ExpSim.class);

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

    private ModifyBoatMesh modifyBoatMesh;

    private USVAgent boatAgent;

    public static void main(String[] args) {
        main(args, new ExpSim());
    }

    public static void main(String[] args, ExpSim sim){
        sim.start();
    }


    public ExpSim() {
        config = Config.loadConfig();

        camera = new Camera(new Vector3f(0, 2, 5), new Vector3f(0,0,90));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(10, 10, NUM_X, NUM_Z, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
        stepController = new GameStepController(GameStepController.SimType.valueOf(config.getStepType()), config.getStepSize());
        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
        buoyModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BUOY_OBJ_NAME));
    }

    @Override
    public void init(Window window){
        AgentManager.setPhysicsEngine(physicsEngine);
        AgentManager.registerSimStateListener(guiState);
        physicsEngine.init();
        ocean.init(scene, null);
        renderer.init(window, camera, scene, guiState);
        stepController.init();

        ChartOps.displayChartMatrix();

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

        float angle = 0;
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_UP) == GLFW_PRESS) {
            work.set(true);
//            AgentManager.sendAgentMessage("ENEMY_0", new SteerMessage(SteerMessage.ControllerType.SECOND_STRAIGHT));
//            AgentManager.sendAgentMessage("ENEMY_0", new SteerMessage(32000,0));
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_DOWN) == GLFW_PRESS) {
            work.set(false);
//            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.STOP));
        }
        else if (glfwGetKey(window.getWindowID(), GLFW_KEY_LEFT) == GLFW_PRESS) {
//            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.SECOND_TURN_LEFT));
            angle = -(float) (Math.PI/ 6);
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
//            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(SteerMessage.ControllerType.FIRST_TURN_HALF_RIGHT));
            angle = (float) (Math.PI/ 6);
        }

        if (work.get()) {
//            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(3.13f,0));
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(10f, angle));
        } else {
            AgentManager.sendAgentMessage("ALLY_0", new SteerMessage(0,0));
        }
    }

    AtomicBoolean work = new AtomicBoolean(false);

    int writeRate = 0;
    int dataSize = 0;
    USVBuoyData usvBuoyData = new USVBuoyData();
    int step = 0;
    float ela = 0f;
    SpeedData speedData = new SpeedData();
    Vector3f lastPosition = new Vector3f();

    @Override
    public void update(double stepTime) {
        if (!stepController.isPause()) {
            //海浪等环境更新
            ocean.update(stepController.getElapsedTime());
            //Agent系统周期更新
            AgentManager.update(stepTime);

            if (ela >= 0.1) {
                ela -=0.1;
                dataSize++;
                USVAgent usvAgent = (USVAgent) AgentManager.getAgent("ALLY_0");
                BoatEntity boatEntity = (BoatEntity) usvAgent.getEntity();
                Vector3f s =  boatEntity.getLinearVelocity();
                float speed = (float) Math.sqrt(s.x * s.x + s.z * s.z);
                SimulationCharts.SPEED.getSeries()[0].getseriesData().record(step, speed);
                SimulationCharts.FORCE.getSeries()[0].getseriesData().record(step, usvAgent.getEngine().getCurrentEnginePower());
                SimulationCharts.FORCE.getSeries()[1].getseriesData().record(step, boatEntity.getDamp().length());
                SimulationCharts.BUOY.getSeries()[0].getseriesData().record(step, boatEntity.getBuoyancyForce().length());
                SimulationCharts.WAVE_LEVEL.getSeries()[0].getseriesData().record(step, ocean.getWaveHeight(boatEntity.getTranslation().x,boatEntity.getTranslation().z));
                ChartOps.updateChartData(SimulationCharts.SPEED);
                ChartOps.updateChartData(SimulationCharts.FORCE);
                ChartOps.updateChartData(SimulationCharts.BUOY);
                ChartOps.updateChartData(SimulationCharts.WAVE_LEVEL);
                step++;
            }
            ela += stepTime;

            Vector3f p =  AgentManager.getAgent("ALLY_0").getEntity().getTranslation();
            camera.movePosition(p.x-lastPosition.x, p.y-lastPosition.y, p.z-lastPosition.z);
            lastPosition.set(p);
//            if (work.get()) {
//                if (ela >= 0.1) {
//                    ela -=0.1;
//                    dataSize++;
//                    Vector3f s =  AgentManager.getAgent("ALLY_0").getEntity().getLinearVelocity();
//                    float speed = (float) Math.sqrt(s.x * s.x + s.z * s.z);
//                    speedData.speed.add(speed);
//
//                    SimulationCharts.SPEED.getSeries()[0].getseriesData().record(step, speed);
//                    ChartOps.updateChartData(SimulationCharts.SPEED);
//                    step++;
//                }
//                ela += stepTime;
//                if (dataSize >= 150) {
//                    speedData.writeToFile();
//                    logger.info("write file");
//                    work.set(false);
//                }
//            }
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
        {

            //ally usv
            //模型初始朝向面向x轴正方向
            float weight = 12;
            Vector3f position = new Vector3f(-50,0,0);
//            Vector3f scale = new Vector3f(0.2f,0.25f,0.2f);
            Vector3f scale = new Vector3f(0.2f,0.2f,0.2f);
//            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = new Vector3f(1,0,0);
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            //默认长5m 宽2m 高1m
            BoatEntity boatEntity = new BoatEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale,
                    weight, boatModel);
            boatEntity.createBuoyHelper();
            boatAgent = new USVAgent(USVAgent.Camp.ALLY, 0, boatEntity);
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

        //每隔十米放置一个浮标
        for (int loc = -5; loc < 5; loc++) {
            //模型初始朝向面向x轴正方向
            Vector3f position = new Vector3f(loc*10, 0, -10);
            Vector3f scale = new Vector3f(1,1,1);
            Vector3f modelForward = new Vector3f(1,0,0);
            Vector3f forward = new Vector3f(1,0,0);
            Vector3f u = new Vector3f();
            modelForward.cross(forward, u);
            float angle = forward.angle(modelForward);
            u.mul((float) Math.sin(angle/2));
            Quaternionf rotation = new Quaternionf(u.x, u.y, u.z, (float) Math.cos(angle/2));

            BuoyEntity buoyEntity = new BuoyEntity(ocean,
                    physicsEngine.getWorld(), physicsEngine.getSpace(),
                    position, rotation, scale, buoyModel);
            buoyEntity.createBuoyHelper();
            BuoyAgent buoyAgent = new BuoyAgent("BUOY_"+loc);

            buoyAgent.setEntity(buoyEntity);
            AgentManager.addAgent(buoyAgent);
            BuoyObj buoy = new BuoyObj(buoyAgent.getAgentID(),
                    buoyEntity.getTranslation(),
                    buoyEntity.getRotation(),
                    buoyEntity.getScale(),
                    buoyModel);
            buoy.setColor((float) 0xff/0xff,(float) 0xc4/0xff,(float) 0x00/0xff,1);
            scene.setGameObj(buoy);
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
