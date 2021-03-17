package sim;

import conf.Constant;
import engine.GameLogic;
import engine.PauseListener;
import gui.MouseEvent;
import gui.Window;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: gq
 * @Date: 2021/3/16 10:25
 */
public class SimCLI implements GameLogic {

    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    public static void main(String[] args) {
        main(args, new SimCLI());
    }

    public static void main(String[] args, SimCLI sim){
        sim.start();
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
