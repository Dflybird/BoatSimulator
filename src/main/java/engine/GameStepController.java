package engine;

import util.TimeUtil;

/**
 * @Author: gq
 * @Date: 2021/3/15 18:21
 */
public class GameStepController {
    public enum SimType {
        STEP,
        ROLL
    }

    private final SimType simType;
    private final int stepSize;
    //停止时通知
    private PauseListener listener;

    public GameStepController(SimType simType, int stepSize) {
        this.simType = simType;
        this.stepSize = stepSize;
    }

    private double elapsedTime; //从仿真开始到现在的时间
    private double lastLoopTime;
    private double deltaTime;
    private int elapsedStep;    //仿真持续运行的步数
    private boolean pause;

    public void init() {
        elapsedTime = 0;
        lastLoopTime = TimeUtil.currentTime();
        elapsedStep = 0;
        pause = false;
    }

    public boolean isPause(){
        double time = TimeUtil.currentTime();
        deltaTime = time - lastLoopTime;
        lastLoopTime = time;
        if (pause) {
            return true;
        }
        elapsedTime += deltaTime;
        elapsedStep++;

        if (simType == SimType.STEP && elapsedStep > stepSize) {
            pause();
        }
        return pause;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    /**
     * pause可能在任何线程中被调用
     */
    public void pause() {
        pause = true;
        elapsedStep = 0;
        if (listener != null) {
            listener.onPause();
        }
    }

    public void play(PauseListener listener) {
        this.listener = listener;
        pause = false;
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}
