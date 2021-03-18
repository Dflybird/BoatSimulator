package sim;

import conf.Config;
import conf.Constant;
import conf.SceneConfig;
import engine.GameLogic;
import engine.GameStepController;
import engine.PauseListener;
import environment.Ocean;
import gui.GUIRenderer;
import gui.MouseEvent;
import gui.Scene;
import gui.Window;
import gui.obj.Camera;
import gui.obj.Model;
import net.SimServer;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import state.GUIState;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static conf.Constant.*;
import static conf.Constant.BUOY_OBJ_NAME;

/**
 * @Author: gq
 * @Date: 2021/3/16 10:25
 */
public class SimCLI implements GameLogic {

    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

    private final Config config;
    private final SceneConfig sceneConfig;
    private final PhysicsEngine physicsEngine;
    private final GameStepController stepController;
    private final SimServer server;
    private final Ocean ocean;
    private final Model boatModel;
    private final Model buoyModel;

    public static void main(String[] args) {
        main(args, new SimCLI());
    }

    public static void main(String[] args, SimCLI sim){
        sim.start();
    }

    public SimCLI() {
        config = Config.loadConfig();
        sceneConfig = SceneConfig.loadConfig();

        ocean = new Ocean(LENGTH_X, LENGTH_Z, NUM_X, NUM_Z, new Vector3f());
        physicsEngine = new PhysicsEngine();
        stepController = new GameStepController(GameStepController.SimType.valueOf(config.getStepType()), config.getStepSize());
        server = new SimServer(this, config.getPort());
        boatModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
        buoyModel = Model.loadObj(new File(RESOURCES_MODELS_DIR, BUOY_OBJ_NAME));
    }

    @Override
    public void init(Window window) {

    }

    @Override
    public void input(Window window, MouseEvent mouseEvent) {

    }

    @Override
    public void update(double stepTime) {

    }

    @Override
    public void render(double alpha) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void play(PauseListener listener) {

    }

    public void start(){
//        int FPS = config.getFPS();
//        int UPS = config.getUPS();
//        this.SECS_PRE_FRAME = 1.0d / FPS;
//        this.SECS_PRE_UPDATE = 1.0d / UPS;
//        this.FAST_FORWARD_SPEED = config.getFastForwardSpeed();
//
//        init(null);
//
//        while (atomicBoolean.get()) {
//            update(SECS_PRE_UPDATE * FAST_FORWARD_SPEED);
//        }
//
//        cleanup();
    }

}
