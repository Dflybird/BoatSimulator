package sim;

import ams.agent.Agent;
import ams.agent.TestCubeAgent;
import environment.Ocean;
import conf.Config;
import ams.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
import environment.Fog;
import environment.Wind;
import gui.*;
import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.obj.OceanObj;
import gui.obj.geom.CubeObj;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import physics.entity.Entity;
import physics.entity.geom.CubeEntity;
import state.GUIState;
import util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

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
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
    }

    @Override
    public void init(Window window){
        camera.setPosition(0,50,0);
        AgentManager.registerSimStateListener(guiState);
        renderer.init(window, camera, scene, guiState);
        physicsEngine.init();

        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(1000,100,-1000), 1);
        sceneLight.setPointLightList(new PointLight[]{pointLight});

        scene.setSceneLight(sceneLight);

        //雾
        scene.setFog(Fog.OCEAN_FLOG);

        //海洋平铺
        float Lx = 256;
        float Lz = 256;
        ocean = new Ocean(Lx, Lx, 128, 128, new Wind(30, new Vector2f(1,0)), 0.000005f);
        Material material = new Material(
                new Vector4f(0.0f, 0.65f, 0.75f, 1.0f),
                new Vector4f(0.5f, 0.65f, 0.75f, 1.0f),
                new Vector4f(1.0f, 0.25f, 0.0f,  1.0f),
                1, null);
        Mesh mesh = new Mesh(ocean.getModel(), material);
        List<GameObj> oceanBlocks = new ArrayList<>();
        for (int i = -2; i < 2; i++) {
            for (int j = -2; j < 2; j++) {
                OceanObj obj = new OceanObj(new Vector3f(Lx * i, 0, Lz * -j), new Vector3f(0,0,0), new Vector3f(1,1,1));
                obj.setMesh(mesh);
                oceanBlocks.add(obj);
            }
        }
        scene.setOceanBlock(oceanBlocks);

        //初始化Agent
        Agent cubeAgent = new TestCubeAgent("cube");
        Vector3f cubePos = new Vector3f(10,200,-100);
        Vector3f cubeRot = new Vector3f();
        Vector3f cubeSca = new Vector3f(10,10,10);
        GameObj cube = new CubeObj("cube", cubePos, cubeRot, cubeSca);
        Entity cubeEntity = new CubeEntity(physicsEngine.getWorld(), physicsEngine.getSpace(),
                new float[]{10,200,-100}, new float[]{0,0,0}, new float[]{10,10,10});
        cubeAgent.setEntity(cubeEntity);
        AgentManager.addAgent(cubeAgent);
        scene.setGameObj(cube);
//        scene.setGameObj(new Boat("test1", new Vector3f(0,0,-2), new Vector3f(-90,0,0), 0.5f));
//        Agent agent = new USVAgent("test1");
//        agent.setEntity(new Entity(new float[]{0,0,-2}, new float[]{-90,0,0}, 0.5f));
//        AgentManager.addAgent(agent);
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
    }

    @Override
    public void update(double stepTime) {
        //海浪等环境更新
        ocean.evaluateWavesFFT((float) TimeUtil.currentTime());
        //Agent系统周期更新
        agentManager.update(stepTime);
        physicsEngine.update(stepTime);
    }

    @Override
    public void render(double alpha) {
        guiState.computeRenderState(alpha);
        renderer.render();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        physicsEngine.cleanup();
    }

    public void start(){
        config = Config.loadConfig();
        Window window = new Window("BoatSimulator", 300, 300, true);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }
}
