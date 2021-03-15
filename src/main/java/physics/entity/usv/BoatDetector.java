package physics.entity.usv;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.USVAgent;
import org.joml.Vector3f;
import physics.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:39
 */
public class BoatDetector {

    //观测范围，单位m
    public static final float DETECT_RANGE = 100;

    private final Entity entity;
    private final Vector3f detectorRelativeCoordinate;

    public BoatDetector(Entity entity, Vector3f detectorRelativeCoordinate) {
        this.entity = entity;
        this.detectorRelativeCoordinate = detectorRelativeCoordinate;
    }

    //与除自己外所有Agent距离
    public Map<String, Float> detect(){
        Vector3f detectorCoordinate = new Vector3f(entity.getTranslation());
        detectorCoordinate.add(detectorRelativeCoordinate);
        Map<String, Float> targets = new HashMap<>();
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (entity.equals(agent.getEntity())) {
                continue;
            }
            float distance = detectorCoordinate.distance(agent.getEntity().getTranslation());
            if (distance <= DETECT_RANGE) {
                targets.put(agent.getAgentID(), distance);
            }
        }
        return targets;
    }

    public float detect(Agent agent) {
        Vector3f detectorCoordinate = new Vector3f(entity.getTranslation());
        detectorCoordinate.add(detectorRelativeCoordinate);

        return detectorCoordinate.distance(agent.getEntity().getTranslation());
    }
}
