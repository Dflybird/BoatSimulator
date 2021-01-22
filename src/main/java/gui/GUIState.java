package gui;

import core.SimState;
import core.StateUpdateListener;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:02
 */
public class GUIState implements StateUpdateListener {

    private SyncedCacheBuffer<SimState> simStateBuffer;

    @Override
    public void stateUpdated(SimState simState) {
        simStateBuffer.update(simState);
    }
}
