package sim;

import ams.agent.Agent;
import ams.agent.CubeAgent;
import ams.agent.USVAgent;
import environment.Ocean;
import conf.Config;
import ams.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
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
import physics.buoy.BuoyHelper;
import physics.PhysicsEngine;
import physics.buoy.ModifyBoatMesh;
import physics.entity.Entity;
import physics.entity.geom.CubeEntity;
import physics.entity.usv.BoatEntity;
import state.GUIState;
import util.TimeUtil;

import java.io.File;

import static conf.Constant.BOAT_OBJ_NAME;
import static conf.Constant.RESOURCES_MODELS_DIR;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class SimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

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
        main(args, new SimGUI());
    }

    public static void main(String[] args, SimGUI sim){
        sim.start();
    }


    public SimGUI() {
        camera = new Camera(new Vector3f(0, 0, 4f));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(128, 128, 4, 4, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
    }

    @Override
    public void init(Window window){
        camera.setPosition(0,50,0);
        AgentManager.registerSimStateListener(guiState);
        renderer.init(window, camera, scene, guiState);
        physicsEngine.init();
        ocean.init(scene);

        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));


        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(1000,100,-1000), 1);
//        sceneLight.setPointLightList(new PointLight[]{pointLight});
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1.0f, 0.6f, 0.3f), new Vector3f(1, 0.1f, -1),  1.0f);
        sceneLight.setDirectionalLight(directionalLight);

        scene.setSceneLight(sceneLight);


        //海洋平铺
//        float Lx = 256;
//        float Lz = 256;
//        wave = new Wave(Lx, Lx, 128, 128, new Wind(30, new Vector2f(1,0)), 0.000005f);
//        Material material = new Material(
//                new Vector4f(0.0f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(0.5f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(1.0f, 0.25f, 0.0f,  1.0f),
//                1, null);
//        Mesh mesh = new Mesh(wave.getModel(), material);
//        List<GameObj> oceanBlocks = new ArrayList<>();
//        for (int i = -2; i < 2; i++) {
//            for (int j = -2; j < 2; j++) {
//                OceanObj obj = new OceanObj(new Vector3f(Lx * i, 0, Lz * -j), new Quaternionf(), new Vector3f(1,1,1));
//                obj.setMesh(mesh);
//                oceanBlocks.add(obj);
//            }
//        }
//        scene.setOceanBlock(oceanBlocks);

        //初始化Agent
//        Agent usvAgent = new USVAgent("usv0");
//        长5m 宽2m
        String id = "boat" + TimeUtil.currentTime();
        USVAgent boatAgent = new USVAgent(id);
        float x, y, z;
        x = 10;
        y = 100;
        z = -10;
//        Vector3f boatPos = new Vector3f(x, y, z);
//        Quaternionf boatRot = new Quaternionf();
//        Vector3f boatSca = new Vector3f(10,10,10);
//        GameObj boat = new BoatObj(id, boatPos, boatRot, boatSca, boatModel);
//        Entity boatEntity = new BoatEntity(physicsEngine.getWorld(), physicsEngine.getSpace(),
//                boatPos, boatRot, boatSca, boatModel);
//        boatAgent.setEntity(boatEntity);
//        BuoyHelper boatBuoyHelper = new BuoyHelper(ocean, boatEntity);
//        boatAgent.setBuoyHelper(boatBuoyHelper);
//        AgentManager.addAgent(boatAgent);
//        scene.setGameObj(boat);

        id = "cube" + TimeUtil.currentTime();
        x = 10;
        y = 100;
        z = 10;
        Vector3f cubePos = new Vector3f(x, y, z);
        Quaternionf cubeRot = new Quaternionf();
        Vector3f cubeSca = new Vector3f(10,10,10);
        GameObj cube = new CubeObj(id, cubePos, cubeRot, cubeSca);
        Entity cubeEntity = new CubeEntity(physicsEngine.getWorld(), physicsEngine.getSpace(),
                cubePos, cubeRot, cubeSca, cube.getMesh().getModel());
        CubeAgent cubeAgent = new CubeAgent(id);
        BuoyHelper cubeBuoyHelper = new BuoyHelper(ocean, cubeEntity);
        cubeAgent.setBuoyHelper(cubeBuoyHelper);
        cubeAgent.setEntity(cubeEntity);
        AgentManager.addAgent(cubeAgent);
        scene.setGameObj(cube);

        modifyBoatMesh = cubeBuoyHelper.getModifyBoatMesh();
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
            Entity cubeEntity = new CubeEntity(physicsEngine.getWorld(), physicsEngine.getSpace(),
                    cubePos, cubeRot, cubeSca, cube.getMesh().getModel());
            cubeAgent.setEntity(cubeEntity);
            cubeAgent.setBuoyHelper(new BuoyHelper(ocean, cubeEntity));
            AgentManager.addAgent(cubeAgent);
            scene.setGameObj(cube);
        }
        if (glfwGetKey(window.getWindowID(), GLFW_KEY_B) == GLFW_PRESS) {
            String id = "boat" + TimeUtil.currentTime();
            USVAgent boatAgent = new USVAgent(id);
            float x, y, z;
            x = (float) (10+Math.random()*50);
            y = 100;
            z = (float) (-10+Math.random()*50);
            Vector3f boatPos = new Vector3f(x, y, z);
            Quaternionf boatRot = new Quaternionf();
            Vector3f boatSca = new Vector3f(1,1,1);
            GameObj boat = new BoatObj(id, boatPos, boatRot, boatSca, boatModel);
            Entity boatEntity = new BoatEntity(physicsEngine.getWorld(), physicsEngine.getSpace(),
                    boatPos, boatRot, boatSca, boatModel);
            boatAgent.setEntity(boatEntity);
            boatAgent.setBuoyHelper(new BuoyHelper(ocean, boatEntity));
            AgentManager.addAgent(boatAgent);
            scene.setGameObj(boat);
        }
    }

    @Override
    public void update(double stepTime) {
        //海浪等环境更新
        ocean.update();
        //Agent系统周期更新
        agentManager.update(stepTime);
        physicsEngine.update(stepTime);
    }

    @Override
    public void render(double alpha) {
        guiState.computeRenderState((float) alpha);
        renderer.render();
        renderer.renderMeshes(modifyBoatMesh.getUnderwaterModel(),
                modifyBoatMesh.getEntity().getTranslation(),
                modifyBoatMesh.getEntity().getRotation(),
                modifyBoatMesh.getEntity().getScale());
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
}
