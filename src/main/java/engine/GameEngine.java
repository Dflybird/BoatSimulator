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
    private int UPS = 500;
    /** Agent系统每周期时间步长，单位毫秒 **/
    private double stepTime = 1.0d / UPS;
    /** 每帧渲染时间，单位毫秒 **/
    private double secsPreFrame = 1.0d / FPS;
    private double previous;

    private Window window;
    private AgentManager agentManager;

    public GameEngine(Window window) {
        this.window = window;
    }

    @Override
    public void run() {
        init();
        gameLoop();
    }

    protected void init() {
        window.init();
    }

    protected void gameLoop() {
        //可以用来更新逻辑的时间
        double accumulator = 0;
        previous = TimeUtil.currentTime();

        while (true) {
            double current = TimeUtil.currentTime();
            double frameTime = current - previous;
            previous = current;

            accumulator += frameTime;

            processInput();

            while (accumulator >= stepTime) {
                //Agent系统周期更新

                accumulator-=stepTime;
            }

            double alpha = accumulator / stepTime;
            //State state = currentState * alpha + previousState * ( 1.0 - alpha );

            //TODO 渲染

            //如果没有开启垂直同步，通过sleep休眠CPU，控制刷新帧率
            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        double frameEndTime = previous + secsPreFrame;
        while (frameEndTime < TimeUtil.currentTime()) {
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
