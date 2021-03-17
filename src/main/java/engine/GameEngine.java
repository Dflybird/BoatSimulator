package engine;

import conf.Config;
import conf.Constant;
import gui.MouseEvent;
import gui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/1/7 11:48
 * @Version 1.0
 **/
public class GameEngine implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(GameEngine.class);

    /** 渲染频率 **/
    private final int FPS;
    /** 更新频率 **/
    private final int UPS;
    /** Agent系统每周期时间步长，单位毫秒 **/
    private final double SECS_PRE_UPDATE;
    /** 每帧渲染时间，单位毫秒 **/
    private final double SECS_PRE_FRAME;
    /** 快进速度 **/
    private final double FAST_FORWARD_SPEED;

    private Window window;
    private SimTimer timer;
    private final MouseEvent mouseEvent;

    private final GameLogic gameLogic;
    private final Config config;

    private boolean running = false;

    public GameEngine(Window window, GameLogic gameLogic, Config config) {
        this.window = window;
        this.gameLogic = gameLogic;
        this.config = config;
        this.timer = new SimTimer();
        this.mouseEvent = new MouseEvent();
        this.FPS = config.getFPS();
        this.UPS = config.getUPS();
        this.SECS_PRE_FRAME = 1.0d / FPS;
        this.SECS_PRE_UPDATE = 1.0d / UPS;
        this.FAST_FORWARD_SPEED = config.getFastForwardSpeed();
    }

    @Override
    public void run() {
        init();
        gameLoop();
    }

    private void init() {
        window.init();
        timer.init();
        gameLogic.init(window);
        mouseEvent.init(window);
        running = true;
    }

    private void input() {
        gameLogic.input(window, mouseEvent);
    }

    private void update() {
        gameLogic.update(SECS_PRE_UPDATE * FAST_FORWARD_SPEED);
    }

    private void render(double alpha) {
        window.clean();
        gameLogic.render(alpha);
        window.render();
        timer.updateFPS();
    }

    private void cleanup(){
        gameLogic.cleanup();
        window.cleanup();
    }

    protected void gameLoop() {
        //可以用来更新逻辑的时间
        double frameTime;
        double accumulator = 0;

        while (running && !window.isClosed()) {
            frameTime = timer.getDelta();

            accumulator += frameTime;

            input();

            while (accumulator >= SECS_PRE_UPDATE) {
                update();
                timer.updateUPS();
                accumulator-= SECS_PRE_UPDATE;
            }

            double alpha = accumulator / SECS_PRE_UPDATE;

            render(alpha);

            timer.update();
            //打印fps和ups
//            logger.debug("FPS: {} | UPS: {}", timer.getFPS(), timer.getUPS());

            //如果没有开启垂直同步，通过sleep休眠CPU，控制刷新帧率
            if (!window.isvSync()) {
                sync();
            }
        }

        cleanup();
    }

    private void sync() {
        double frameEndTime = timer.getLastLoopTime() + SECS_PRE_FRAME;
        while (timer.getTime() < frameEndTime) {
            Thread.yield();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
