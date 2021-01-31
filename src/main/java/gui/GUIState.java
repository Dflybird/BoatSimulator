package gui;

import core.SimState;
import core.StateUpdateListener;

/**
 * 存储并渲染实体对象
 * @Author: gq
 * @Date: 2021/1/22 13:02
 */
public class GUIState implements StateUpdateListener {

    private SyncedCacheBuffer<SimState> simStateBuffer;
    private SimState renderState;

    public void computeRenderState(double alpha) {

    }

    @Override
    public void stateInit(SimState simState) {
        this.simStateBuffer = new SyncedCacheBuffer<>(simState);
    }

    @Override
    public void stateUpdated(SimState simState) {
        simStateBuffer.update(simState);
    }

    public SimState getRenderState() {
        return renderState;
    }
}
