package engine;

import conf.Config;
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
    private int FPS = 60;
    /** 更新频率 **/
    private int UPS = 200;
    /** Agent系统每周期时间步长，单位毫秒 **/
    private double secsPreUpdate = 1.0d / UPS;
    /** 每帧渲染时间，单位毫秒 **/
    private double secsPreFrame = 1.0d / FPS;
    /** 快进速度 **/
    private double fastForwardSpeed = 1.0d;

    private int fpsCount = 0;
    private int upsCount = 0;

    private Window window;
    private SimTimer timer;

    private final GameLogic gameLogic;
    private final Config config;

    private boolean running = false;

    public GameEngine(Window window, GameLogic gameLogic, Config config) {
        this.window = window;
        this.gameLogic = gameLogic;
        this.config = config;
        this.timer = new SimTimer();
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
        running = true;
    }

    private void input() {
        gameLogic.input();
    }

    private void update() {
        gameLogic.update(secsPreUpdate * fastForwardSpeed);
    }

    private void render(double alpha) {
        window.clean();
        gameLogic.render(alpha);

        //TODO 渲染
        window.render();
        timer.updateFPS();
    }

    private void cleanup(){
        gameLogic.cleanup();
        window.cleanup();
    }

    protected void gameLoop() {
        //可以用来更新逻辑的时间
        double accumulator = 0;

        while (running && !window.isClosed()) {
            double frameTime = timer.getDelta();

            accumulator += frameTime;

            input();

            while (accumulator >= secsPreUpdate) {
                update();
                timer.updateUPS();
                accumulator-= secsPreUpdate;
            }

            double alpha = accumulator / secsPreUpdate;

            render(alpha);

            timer.update();
            //打印fps和ups
            logger.debug("FPS: {} | UPS: {}", timer.getFPS(), timer.getUPS());

            //如果没有开启垂直同步，通过sleep休眠CPU，控制刷新帧率
            if (!window.isvSync()) {
                sync();
            }
        }

        cleanup();
    }

    private void sync() {
        double frameEndTime = timer.getLastLoopTime() + secsPreFrame;
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
