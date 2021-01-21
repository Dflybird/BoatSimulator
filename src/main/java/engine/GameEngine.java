package engine;

import core.AgentManager;
import gui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.TimeUtil;

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
    private double stepTime = 1.0d / UPS;
    /** 每帧渲染时间，单位毫秒 **/
    private double secsPreFrame = 1.0d / FPS;

    private int fpsCount = 0;
    private int upsCount = 0;

    private Window window;
    private AgentManager agentManager;
    private SimTimer timer;

    public GameEngine(Window window, AgentManager agentManager) {
        this.window = window;
        this.agentManager = agentManager;
        this.timer = new SimTimer();
    }

    @Override
    public void run() {
        init();
        gameLoop();
    }

    protected void init() {
        window.init();
        timer.init();
    }

    protected void gameLoop() {
        //可以用来更新逻辑的时间
        double accumulator = 0;

        while (true) {
            double frameTime = timer.getDelta();

            accumulator += frameTime;

            processInput();

            while (accumulator >= stepTime) {
                //Agent系统周期更新
                agentManager.update(stepTime);
                timer.updateUPS();
                accumulator-=stepTime;
            }

            double alpha = accumulator / stepTime;
            //GUIState guiState = currentSimState * alpha + previousSimState * ( 1.0 - alpha );

            //TODO 渲染
            window.render();
            timer.updateFPS();

            timer.update();
            //打印fps和ups
            logger.info("FPS: {} | UPS: {}", timer.getFPS(), timer.getUPS());

            //如果没有开启垂直同步，通过sleep休眠CPU，控制刷新帧率
            if (!window.isvSync()) {
                sync();
            }
        }
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

    private void processInput() {

    }

}
