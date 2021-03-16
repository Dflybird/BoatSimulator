package physics.entity.usv;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.usv.USVAgent;
import org.joml.Vector3f;
import physics.entity.Entity;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:50
 */
public class BoatWeapon {

    //武器攻击范围，单位m
    public static final float ATTACK_RANGE = 100;

    private final Entity entity;
    private final Vector3f weaponRelativeCoordinate;

    public BoatWeapon(Entity entity, Vector3f weaponRelativeCoordinate) {
        this.entity = entity;
        this.weaponRelativeCoordinate = weaponRelativeCoordinate;
    }

    public boolean attack(String agentID){
        Vector3f weaponCoordinate = new Vector3f(entity.getTranslation());
        weaponCoordinate.add(weaponRelativeCoordinate);
        Agent target = AgentManager.getAgent(agentID);
        if (target instanceof USVAgent) {
            USVAgent usvAgent = (USVAgent)target;
            return usvAgent.getStatus() == USVAgent.Status.ALIVE &&
                    weaponCoordinate.distance(usvAgent.getEntity().getTranslation()) <= ATTACK_RANGE;
        }
        return false;
    }
}
