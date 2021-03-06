package state;

import ams.agent.Agent;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: gq
 * @Date: 2021/1/22 13:01
 */
public class SimState {
    private final Logger logger = LoggerFactory.getLogger(SimState.class);

    //<agent的ID, obj状态对象>
    private final Map<String, ObjStateInfo> stateMap = new HashMap<>();

    public SimState() {}
    public SimState(SimState state) {
        clone(state);
    }

    public void clone(SimState state) {
        state.stateMap.forEach((key, value) -> stateMap.put(key, new ObjStateInfo(value)));
    }

    public void collect(String id, Vector3f translation, Quaternionf rotation, Vector3f scale) {
        ObjStateInfo objStateInfo = stateMap.computeIfAbsent(id, key -> new ObjStateInfo());
        objStateInfo.setTranslation(translation);
        objStateInfo.setRotation(rotation);
        objStateInfo.setScale(scale);
    }

    /**
     * 覆盖存储
     * @param agent
     */
    public void collect(Agent agent) {
        ObjStateInfo objStateInfo = stateMap.computeIfAbsent(agent.getAgentID(), key -> new ObjStateInfo());
        Entity entity = agent.getEntity();
        objStateInfo.setTranslation(entity.getTranslation());
        objStateInfo.setRotation(entity.getRotation());
        objStateInfo.setScale(entity.getScale());
    }

    public ObjStateInfo getStateInfo(String id) {
        return stateMap.get(id);
    }

    public SimState zero(){
        stateMap.clear();
        return this;
    }

    public SimState mul(float num) {
        stateMap.forEach((key, value) -> value.mul(num));
        return this;
    }

    public SimState mul(float num, SimState dest) {
        dest.zero();
        dest.clone(this);
        return dest.mul(num);
    }

    public SimState add(SimState state) {
        state.stateMap.forEach((key, value) -> {
            ObjStateInfo objStateInfo = stateMap.computeIfAbsent(key, k -> new ObjStateInfo());
            objStateInfo.add(value);
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
            ObjStateInfo objStateInfo = stateMap.computeIfAbsent(key, k -> new ObjStateInfo());
            objStateInfo.sub(value);
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

}
