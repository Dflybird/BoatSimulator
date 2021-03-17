package physics.entity.usv;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.usv.USVAgent;
import org.joml.Vector3f;
import physics.entity.Entity;
import util.TimeUtil;

import java.util.Random;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:50
 */
public class BoatWeapon {

    //武器攻击范围，单位m
    public final float attackRange;
    //开火间隔，单位s
    public static final float ATTACK_INTERVAL = 0.1f;
    //击毁概率，百分之八十
    public static final float ATTACK_PROBABILITY = 0.8f;

    private final Random random;

    private double lastAttackTime;

    private final Entity entity;
    private final Vector3f weaponRelativeCoordinate;

    public BoatWeapon(Entity entity, Vector3f weaponRelativeCoordinate, float attackRange) {
        this.entity = entity;
        this.attackRange = attackRange;
        this.weaponRelativeCoordinate = weaponRelativeCoordinate;
        this.lastAttackTime = TimeUtil.currentTime();
        this.random = new Random(System.currentTimeMillis());
    }

    public boolean attack(String agentID){
        Vector3f weaponCoordinate = new Vector3f(entity.getTranslation());
        weaponCoordinate.add(weaponRelativeCoordinate);
        Agent target = AgentManager.getAgent(agentID);
        if (target instanceof USVAgent) {
            USVAgent usvAgent = (USVAgent)target;
            double currentTime = TimeUtil.currentTime();
            if (currentTime - lastAttackTime > ATTACK_INTERVAL &&   //距离上次开火时间大于开火间隔
                    usvAgent.getStatus() == USVAgent.Status.ALIVE &&    //目标目前存活
                    weaponCoordinate.distance(usvAgent.getEntity().getTranslation()) <= attackRange    //目标在攻击距离内
            ) {
                lastAttackTime = currentTime;
                return random.nextDouble() < ATTACK_PROBABILITY;
            }
        }
        return false;
    }
}
