package ams;

import ams.agent.Agent;
import physics.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:01
 */
public class SimState {

    private final Map<String, StateInfo> stateMap = new HashMap<>();

    public SimState() {}
    public SimState(SimState state) {
        clone(state);
    }

    public void clone(SimState state) {
        state.stateMap.forEach((key, value) -> stateMap.put(key, new StateInfo(value)));
    }

    public void collect(String id, float[] translation, float[] rotation, float scale) {
        StateInfo stateInfo = stateMap.computeIfAbsent(id, key -> new StateInfo());
        stateInfo.translation = translation;
        stateInfo.rotation = rotation;
        stateInfo.scale = scale;
    }

    public void collect(Agent agent) {
        StateInfo stateInfo = stateMap.computeIfAbsent(agent.getAgentID(), key -> new StateInfo());
        Entity entity = agent.getEntity();
        stateInfo.translation = entity.getTranslation();
        stateInfo.rotation = entity.getRotation();
        stateInfo.scale = entity.getScale();
    }

    public StateInfo getState(String id) {
        return stateMap.get(id);
    }

    public SimState zero(){
        stateMap.clear();
        return this;
    }

    public SimState mul(double num) {
        stateMap.forEach((key, value) -> value.mul(num));
        return this;
    }

    public SimState mul(double num, SimState dest) {
        dest.zero();
        dest.clone(this);
        return dest.mul(num);
    }

    public SimState add(SimState state) {
        state.stateMap.forEach((key, value) -> {
            StateInfo stateInfo = stateMap.computeIfAbsent(key, k -> new StateInfo());
            stateInfo.add(value);
        });
        return this;
    }

    public SimState add(SimState state, SimState dest) {
        dest.zero();
        dest.clone(this);
        return dest.add(state);
    }

    public SimState sub(SimState state) {
        state.stateMap.forEach((key, value) -> {
            StateInfo stateInfo = stateMap.computeIfAbsent(key, k -> new StateInfo());
            stateInfo.sub(value);
        });
        return this;
    }

    public SimState sub(SimState state, SimState dest) {
        dest.zero();
        dest.clone(this);
        return dest.sub(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimState simState = (SimState) o;
        return stateMap.equals(simState.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateMap);
    }

    public static class StateInfo{
        protected float[] translation = new float[3];  //len = 3
        protected float[] rotation = new float[3]; //len = 3
        protected float scale = 0;

        public StateInfo() {}

        public StateInfo(StateInfo stateInfo) {
            translation = stateInfo.translation;
            rotation = stateInfo.rotation;
            scale = stateInfo.scale;
        }

        public StateInfo mul(double num) {
            for (int i = 0; i < 3; i++) {
                translation[i] *= num;
                rotation[i] *= num;
            }
            scale *= num;
            return this;
        }

        public StateInfo add(StateInfo info) {
            for (int i = 0; i < 3; i++) {
                translation[i] += info.translation[i];
                rotation[i] += info.rotation[i];
            }
            scale += info.scale;
            return this;
        }

        public StateInfo sub(StateInfo info) {
            for (int i = 0; i < 3; i++) {
                translation[i] -= info.translation[i];
                rotation[i] -= info.rotation[i];
            }
            scale -= info.scale;
            return this;
        }

        public float[] getTranslation() {
            return translation;
        }

        public float[] getRotation() {
            return rotation;
        }

        public float getScale() {
            return scale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateInfo stateInfo = (StateInfo) o;
            return Float.compare(stateInfo.scale, scale) == 0 && Arrays.equals(translation, stateInfo.translation) && Arrays.equals(rotation, stateInfo.rotation);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(scale);
            result = 31 * result + Arrays.hashCode(translation);
            result = 31 * result + Arrays.hashCode(rotation);
            return result;
        }
    }
}
