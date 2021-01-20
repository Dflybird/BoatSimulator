package sim;

import core.Agent;
import engine.GameEngine;
import gui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class Sim {

    private final Logger logger = LoggerFactory.getLogger(Sim.class);

    public static void main(String[] args) {
        main(args, new Sim());
    }

    public static void main(String[] args, Sim sim){
        sim.init();
        sim.start();
    }

    private void init(){

    }

    private void start(){
        Window window = new Window("BoatSimulator", 300, 300, false);
        GameEngine gameEngine = new GameEngine(window);
        gameEngine.run();
    }
}
