package sim;

import ams.agent.Agent;
import ams.agent.CubeAgent;
import ams.agent.USVAgent;
import ams.msg.SteerMessage;
import conf.Constant;
import engine.GameStepController;
import engine.PauseListener;
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
import net.SimServer;
import org.joml.*;
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
import java.lang.Math;

import static conf.Constant.*;
import static conf.Constant.MAX_ANGLE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class SimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

    private Config config;
    private final Camera camera;
    private final GUIState guiState;
    private final Scene scene;
    private final GUIRenderer renderer;
    private final PhysicsEngine physicsEngine;
    private final GameStepController stepController;
    private final SimServer server;
    private final Ocean ocean;
    private final Model boatModel;

    public static void main(String[] args) {
        main(args, new SimGUI());
    }

    public static void main(String[] args, SimGUI sim){
        sim.start();
    }


    public SimGUI() {
        camera = new Camera(new Vector3f(0, 50, 0));
        guiState = new GUIState();
        renderer = new GUIRenderer();
        ocean = new Ocean(LENGTH_X, LENGTH_Z, NUM_X, NUM_Z, new Vector3f());
        scene = new Scene();
        physicsEngine = new PhysicsEngine();
        stepController = new GameStepController(GameStepController.SimType.valueOf(STEP_TYPE), STEP_SIZE);
        server = new SimServer(this, PORT);
        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
    }

    @Override
    public void init(Window window){
        AgentManager.setPhysicsEngine(physicsEngine);
        AgentManager.registerSimStateListener(guiState);
        physicsEngine.init();
        ocean.init(scene);
        renderer.init(window, camera, scene, guiState);
        server.start();
        stepController.init();

        //环境光
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1.0f, 0.6f, 0.3f), new Vector3f(1, 0.1f, -1),  1.0f);
        sceneLight.setDirectionalLight(directionalLight);
        scene.setSceneLight(sceneLight);

        initAgent();
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
        pause();
        logger.debug("reset");
        stepController.init();
        AgentManager.resetAllAgent();
        play(null);
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
        config = Config.loadConfig();
        Window window = new Window("BoatSimulator", 300, 300, false);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }

    private void initAgent() {

    }

}
