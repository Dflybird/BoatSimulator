package physics.entity.usv;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.usv.USVAgent;
import org.joml.Vector3f;
import physics.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:39
 */
public class BoatDetector {

    //观测范围，单位m
    private final float detectRange;

    private final Entity entity;
    private final Vector3f detectorRelativeCoordinate;

    public BoatDetector(Entity entity, Vector3f detectorRelativeCoordinate, float detectRange) {
        this.detectRange = detectRange;
        this.entity = entity;
        this.detectorRelativeCoordinate = detectorRelativeCoordinate;
    }

    //与除自己外所有Agent距离
    public Map<String, Float> detect(){
        Vector3f detectorCoordinate = new Vector3f(entity.getTranslation());
        detectorCoordinate.add(detectorRelativeCoordinate);
        Map<String, Float> targets = new HashMap<>();
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent.getEntity() == null || entity.equals(agent.getEntity())) {
                continue;
            }
            float distance = detectorCoordinate.distance(agent.getEntity().getTranslation());
            if (distance <= detectRange) {
                targets.put(agent.getAgentID(), distance);
            }
        }
        return targets;
    }

    public List<USVAgent> usvInRange() {
        Vector3f detectorCoordinate = new Vector3f(entity.getTranslation());
        detectorCoordinate.add(detectorRelativeCoordinate);
        List<USVAgent> targets = new ArrayList<>();
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent.getEntity() == null || entity.equals(agent.getEntity())) {
                continue;
            }
            if (!(agent instanceof USVAgent)) {
                continue;
            }
            float distance = detectorCoordinate.distance(agent.getEntity().getTranslation());
            if (distance <= detectRange) {
                targets.add((USVAgent) agent);
            }
        }
        return targets;
    }

    public float detect(Agent agent) {
        Vector3f detectorCoordinate = new Vector3f(entity.getTranslation());
        detectorCoordinate.add(detectorRelativeCoordinate);
        float distance = detectorCoordinate.distance(agent.getEntity().getTranslation());
        if (distance <= detectRange) {
            return distance;
        }
        return -1;
    }

    public float getDetectRange() {
        return detectRange;
    }
}
