package gui;

import core.SimState;
import core.StateUpdateListener;
import gui.obj.Camera;
import gui.obj.Scene;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:02
 */
public class GUIState implements StateUpdateListener {

    private SyncedCacheBuffer<SimState> simStateBuffer;
    private Window window;

    public void init(Window window) {
        this.window = window;
    }

    public void render(Window window, Camera camera, Scene scene) {

    }

    @Override
    public void stateInit(SimState simState) {
        this.simStateBuffer = new SyncedCacheBuffer<>(simState);
    }

    @Override
    public void stateUpdated(SimState simState) {
        simStateBuffer.update(simState);
    }
}
