package physics.entity.usv;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.usv.USVAgent;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;
import util.TimeUtil;

import java.util.Random;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:50
 */
public class BoatWeapon {
    private static final Logger logger = LoggerFactory.getLogger(BoatWeapon.class);

    //武器攻击范围，单位m
    private final float attackRange;
    //武器攻击角度，从船的朝向开始算，左右各attackAngle度，单位弧度
    private final float attackAngle;
    //开火间隔，单位s
    public static final float ATTACK_INTERVAL = 0.1f;
    //击毁概率，百分之八十
    public static final float ATTACK_PROBABILITY = 0.8f;

    private final Random random;

    private double lastAttackTime;

    private final Entity entity;
    private final Vector3f weaponRelativeCoordinate;

    public BoatWeapon(Entity entity, Vector3f weaponRelativeCoordinate, float attackRange, float attackAngle) {
        this.entity = entity;
        this.attackRange = attackRange;
        this.weaponRelativeCoordinate = weaponRelativeCoordinate;
        this.lastAttackTime = TimeUtil.currentTime();
        this.random = new Random(System.currentTimeMillis());
        this.attackAngle = (float) (attackAngle * Math.PI/180);
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
                Vector3f forward = new Vector3f(entity.getForward());
                forward.rotate(entity.getRotation()).normalize();

                Vector3f direction = new Vector3f(usvAgent.getEntity().getTranslation());
                direction.sub(entity.getTranslation()).normalize();

                float angle = forward.angle(direction);
                return angle < attackAngle;
            }
        }
        return false;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public float getAttackAngle() {
        return attackAngle;
    }
}
