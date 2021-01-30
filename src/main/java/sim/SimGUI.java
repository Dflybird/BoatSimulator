package sim;

import conf.Config;
import core.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
import gui.GUIState;
import gui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class SimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

    private AgentManager agentManager = AgentManager.getInstance();
    private Config config;
    private GUIState guiState;

    public static void main(String[] args) {
        main(args, new SimGUI());
    }

    public static void main(String[] args, SimGUI sim){
        sim.start();
    }

    public SimGUI() {
        guiState = new GUIState();
    }

    @Override
    public void init(Window window){
        guiState.init(window);
        AgentManager.registerSimStateListener(guiState);
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
        //GUIState guiState = currentSimState * alpha + previousSimState * ( 1.0 - alpha );
    }

    @Override
    public void cleanup() {

    }

    public void start(){
        config = Config.loadConfig();
        Window window = new Window("BoatSimulator", 300, 300, false);
        GameEngine engine = new GameEngine(window, this, config);
        engine.run();
    }
}
