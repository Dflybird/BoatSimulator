package ams;

import state.SimState;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:04
 */
public interface StateUpdateListener {

    void stateInit(SimState simState);

    /**
     * simState是新创建的对象
     * @param simState
     */
    void stateUpdated(SimState simState);
}
