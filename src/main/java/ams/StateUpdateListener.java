package ams;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:04
 */
public interface StateUpdateListener {

    void stateInit(SimState simState);

    void stateUpdated(SimState simState);
}
