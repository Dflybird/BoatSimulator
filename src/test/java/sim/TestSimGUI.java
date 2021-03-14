package sim;

import ams.AgentManager;
import ams.agent.CubeAgent;
import ams.agent.USVAgent;
import ams.msg.SteerMessage;
import conf.Config;
import engine.GameEngine;
import engine.GameLogic;
import environment.Ocean;
import gui.*;
import gui.graphic.light.DirectionalLight;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.obj.Model;
import gui.obj.geom.CubeObj;
import gui.obj.usv.BoatObj;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import physics.buoy.ModifyBoatMesh;
import physics.entity.geom.CubeEntity;
import physics.entity.usv.BoatEntity;
import state.GUIState;
import util.TimeUtil;

import java.io.File;

import static conf.Constant.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class TestSimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(TestSimGUI.class);

    private final AgentManager agentManager = AgentManager.getInstance();
    private Config config;
    private final Camera camera;
    private final GUIState guiState;
    private final Scene scene;
    private final GUIRenderer renderer;
    private final PhysicsEngine physicsEngine;

    private Ocean ocean;

    private Model boatModel;

    public static void main(String[] args) {
        main(args, new TestSimGUI());
    }

    public static void main(String[] args, TestSimGUI sim){
        sim.start();
    }


    public TestSimGUI() {
        camera = new Camera(new Vector3f(0, 0, 4f));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(64, 64, 16, 16, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
    }

    float enginePower;
    float engineAngle;
    @Override
    public void init(Window window){
        camera.setPosition(0,50,0);
        physicsEngine.init();
        ocean.init(scene);
        AgentManager.setPhysicsEngine(physicsEngine);
        AgentManager.registerSimStateListener(guiState);
        renderer.init(window, camera, scene, guiState);

        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));


        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(1000,100,-1000), 1);
//        sceneLight.setPointLightList(new PointLight[]{pointLight});
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1.0f, 0.6f, 0.3f), new Vector3f(1, 0.1f, -1),  1.0f);
        sceneLight.setDirectionalLight(directionalLight);

        scene.setSceneLight(sceneLight);

        //初始化Agent
//        Agent usvAgent = new USVAgent("usv0");
//        长5m 宽2m
        String boatId = "boat";
        Vector3f boatPos = new Vector3f(10, 20, -10);
        Quaternionf boatRot = new Quaternionf();
        Vector3f boatSca = new Vector3f(1,1,1);
        GameObj boat = new BoatObj(boatId, boatPos, boatRot, boatSca, boatModel);
        BoatEntity boatEntity = new BoatEntity(ocean, physicsEngine.getWorld(), physicsEngine.getSpace(),
                boatPos, boatRot, boatSca, boatModel);
        boatEntity.createBuoyHelper();

        USVAgent boatAgent = new USVAgent(boatId, boatEntity);
        AgentManager.addAgent(boatAgent);
        scene.setGameObj(boat);

        String cubeId = "cube";
        Vector3f cubePos = new Vector3f(10, 20, 10);
        Quaternionf cubeRot = new Quaternionf();
        Vector3f cubeSca = new Vector3f(1,1,1);
        GameObj cube = new CubeObj(cubeId, cubePos, cubeRot, cubeSca);
        CubeEntity cubeEntity = new CubeEntity(ocean, physicsEngine.getWorld(), physicsEngine.getSpace(),
                cubePos, cubeRot, cubeSca, cube.getMesh().getModel());
        CubeAgent cubeAgent = new CubeAgent(cubeId);
        cubeEntity.createBuoyHelper();
        cubeAgent.setEntity(cubeEntity);
        AgentManager.addAgent(cubeAgent);
        scene.setGameObj(cube);

        modifyBoatMesh = boatEntity.getBuoyHelper().getModifyBoatMesh();
    }
    ModifyBoatMesh modifyBoatMesh;

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

        // Update camera based on mouse
        mouseEvent.input(window);
        if (mouseEvent.isRightButtonPressed()) {
            Vector2f rotVec = mouseEvent.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //点击加入方块
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_C) == GLFW_PRESS) {
            String id = "cube" + TimeUtil.currentTime();
            CubeAgent cubeAgent = new CubeAgent(id);
            float x, y, z;
            x = (float) (10+Math.random()*50);
            y = 100;
            z = (float) (-10+Math.random()*50);
            Vector3f cubePos = new Vector3f(x, y, z);
            Quaternionf cubeRot = new Quaternionf();
            Vector3f cubeSca = new Vector3f(1,1,1);
            GameObj cube = new CubeObj(id, cubePos, cubeRot, cubeSca);
            CubeEntity cubeEntity = new CubeEntity(ocean, physicsEngine.getWorld(), physicsEngine.getSpace(),
                    cubePos, cubeRot, cubeSca, cube.getMesh().getModel());
            cubeEntity.createBuoyHelper();
            cubeAgent.setEntity(cubeEntity);
            AgentManager.addAgent(cubeAgent);
            scene.setGameObj(cube);
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_B) == GLFW_PRESS) {
            String id = "boat" + TimeUtil.currentTime();
            float x, y, z;
            x = (float) (10+Math.random()*50);
            y = 100;
            z = (float) (-10+Math.random()*50);
            Vector3f boatPos = new Vector3f(x, y, z);
            Quaternionf boatRot = new Quaternionf();
            Vector3f boatSca = new Vector3f(1,1,1);
            GameObj boat = new BoatObj(id, boatPos, boatRot, boatSca, boatModel);
            BoatEntity boatEntity = new BoatEntity(ocean, physicsEngine.getWorld(), physicsEngine.getSpace(),
                    boatPos, boatRot, boatSca, boatModel);
            boatEntity.createBuoyHelper();
            USVAgent boatAgent = new USVAgent(id, boatEntity);
            AgentManager.addAgent(boatAgent);
            scene.setGameObj(boat);
        }

        //控制船
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_UP) == GLFW_PRESS) {
            if (enginePower <= MAX_POWER) {
                enginePower += POWER_FACTOR;
            } else {
                enginePower = MAX_POWER;
            }
        } else {
            enginePower = 0;
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_LEFT) == GLFW_PRESS) {
            engineAngle -= ANGLE_FACTOR;
            if (engineAngle < -MAX_ANGLE) {
                engineAngle = -MAX_ANGLE;
            }
        } else if (glfwGetKey(window.getWindowID(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
            engineAngle += ANGLE_FACTOR;
            if (engineAngle > MAX_ANGLE) {
                engineAngle = MAX_ANGLE;
            }
        } else {
            engineAngle = 0;
        }

        SteerMessage steerMessage = new SteerMessage(enginePower, engineAngle);
        AgentManager.sendAgentMessage("boat", steerMessage);
    }

    @Override
    public void update(double stepTime) {
        //海浪等环境更新
        ocean.update();
        //Agent系统周期更新
        agentManager.update(stepTime);
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
    }

    public void start(){
        config = Config.loadConfig();
        Window window = new Window("BoatSimulator", 300, 300, false);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }

    public Config getConfig() {
        return config;
    }
}