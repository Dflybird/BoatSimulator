package core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:01
 */
public class SimState {

    private List<StateUpdateListener> listeners = new ArrayList<>();

    public void addListener(StateUpdateListener listener) {
        listeners.add(listener);
    }
}
