package gui;

import core.SimState;
import core.StateUpdateListener;

import java.util.List;

/**
 * 存储并渲染实体对象
 * @Author: gq
 * @Date: 2021/1/22 13:02
 */
public class GUIState implements StateUpdateListener {

    private SyncedCacheBuffer<SimState> simStateBuffer;
    private SimState renderState;

    public void computeRenderState(double alpha) {
        List<SimState> bufferList = simStateBuffer.getLatest();
        SimState currentState = bufferList.get(0);
        SimState previousState = bufferList.get(1);

        //不改变缓存
        //guiState = (currentSimState - previousSimState) * alpha + previousSimState;
        renderState = currentState.zero().sub(previousState).mul(alpha).add(previousState);
    }

    @Override
    public void stateInit(SimState simState) {
        this.simStateBuffer = new SyncedCacheBuffer<>(simState);
        this.renderState = new SimState(simState);
    }

    @Override
    public void stateUpdated(SimState simState) {
        simStateBuffer.update(simState);
    }

    public SimState getRenderState() {
        return renderState;
    }
}
