package sim;

import conf.Config;
import core.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
import gui.*;
import gui.obj.Camera;
import gui.obj.geom.Cube;
import gui.obj.usv.Boat;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    @Override
    public void init(Window window){
        AgentManager.registerSimStateListener(guiState);
        renderer.init(window, camera, scene, guiState);

        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        scene.setSceneLight(sceneLight);

        scene.setGameObj(new Boat(new Vector3f(0,-1,0), new Vector3f(-90,0,0), 0.5f));
    }

    @Override
    public void input() {

    }

    @Override
    public void update(double stepTime) {
        //Agent系统周期更新
        agentManager.update(stepTime);
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
    }

    public void start(){
        config = Config.loadConfig();
        Window window = new Window("BoatSimulator", 300, 300, true);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }
}
