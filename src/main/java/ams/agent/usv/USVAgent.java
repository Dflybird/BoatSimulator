package ams.agent.usv;

import ams.AgentManager;
import ams.AgentMessageHandler;
import ams.agent.Agent;
import ams.agent.OnDoneAgent;
import ams.msg.*;
import conf.Constant;
import conf.RewardConfig;
import conf.SceneConfig;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.usv.BoatEngine;
import physics.entity.usv.BoatEntity;
import physics.entity.usv.BoatDetector;
import physics.entity.usv.BoatWeapon;
import util.AgentUtil;

import java.util.Map;


/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent implements AgentMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(USVAgent.class);

    public enum Status {
        DEAD(0),
        ALIVE(1);

        private final int code;

        private static final Status[] STATUSES = new Status[]{
                DEAD, ALIVE
        };

        Status(int code) {
            this.code = code;
        }

        public static Status stateOf(int code) {
            return STATUSES[code];
        }

        public int toInteger(){
            return  code;
        }
    }

    public enum Camp {
        ALLY(0),
        ENEMY(1),
        MAIN_SHIP(2);

        private final int code;

        private static final Camp[] CAMPS = new Camp[] {
                ALLY, ENEMY, MAIN_SHIP
        };

        Camp(int code) {
            this.code = code;
        }

        public static Camp campOf(int code) {
            return CAMPS[code];
        }

        public int toInteger() {
            return code;
        }
    }

    private final RewardConfig rewardConfig;
    private final SceneConfig sceneConfig;

    private final BoatEngine engine;
    private BoatDetector detector;
    private BoatWeapon weapon;

    private final Camp camp;
    private final int id;
    private Status status;
    private float reward;

    public USVAgent(Camp camp, int id, BoatEntity entity) {
        super(AgentUtil.assembleName(camp, id), entity);
        rewardConfig = RewardConfig.loadConfig();
        sceneConfig = SceneConfig.loadConfig();

        this.camp = camp;
        this.id = id;
        engine = new BoatEngine(entity, new Vector3f(-2f, -0.5f, 0f));
        if (camp == Camp.ALLY) {
            detector = new BoatDetector(entity, new Vector3f(0f, 0f, 0f), sceneConfig.getAllyDetectRange());
            weapon = new BoatWeapon(entity, new Vector3f(0f, 0f, 0f), sceneConfig.getAllyAttackRange());
        } else if (camp == Camp.ENEMY) {
            detector = new BoatDetector(entity, new Vector3f(0f, 0f, 0f), sceneConfig.getEnemyDetectRange());
            weapon = new BoatWeapon(entity, new Vector3f(0f, 0f, 0f), sceneConfig.getEnemyAttackRange());
        }

        status = Status.ALIVE;
        reward = 0;
    }

    @Override
    public void reset() {
        super.reset();
        status = Status.ALIVE;
        reward = 0;
        engine.setEnginePower(0);
        engine.setEngineRotation(0);
    }

    @Override
    protected void update(double stepTime) throws Exception {
        receiveAll(this);
        if (status == Status.ALIVE) {
            //解算上一周期的状态和reward
            entity.updateState(stepTime);
            autoAttack();
            calcReward();
            //处理这一周期的行为
            engine.updateEngine();
        } else {
            //不渲染 物理引擎不计算
            render = false;
            entity.destroy();
        }
    }

    @Override
    public void handle(AgentMessage msg) {
        if (msg.getCorrespondingMessageClass() == SteerMessage.class) {
            if (status == Status.ALIVE) {
                SteerMessage steerMessage = (SteerMessage) msg;
                engine.setEnginePower(steerMessage.getPower());
                engine.setEngineRotation(steerMessage.getAngle());
            }
        }
        else if (msg.getCorrespondingMessageClass() == AttackMessage.class) {
            //被击毁
            if (status == Status.ALIVE) {
                AttackMessage attackMessage = (AttackMessage) msg;
                status = Status.DEAD;
                if (camp == Camp.MAIN_SHIP) {
                    reward -= rewardConfig.getEnemyDestroyMainShip();
                    send(attackMessage.getAttacker(), new RewardMessage(rewardConfig.getEnemyDestroyMainShip()));
                } else {
                    reward -= rewardConfig.getDestroyUSV();
                    send(attackMessage.getAttacker(), new RewardMessage(rewardConfig.getDestroyUSV()));
                }
                send(Constant.ON_DONE_AGENT, new OnDoneMessage(camp));
            }
        }
        else if (msg.getCorrespondingMessageClass() == RewardMessage.class) {
            RewardMessage rewardMessage = (RewardMessage) msg;
            reward += rewardMessage.getReward();
        }
    }

    private void autoAttack() {
        if (camp == Camp.ENEMY) {
            //优先攻击主舰
            String mainShipID = AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, sceneConfig.getMainShip().getId());
            if (weapon.attack(mainShipID)) {
                send(mainShipID, new AttackMessage(agentID));
            }
            //其次攻击最近Ally
            USVAgent closestEnemy = null;
            float minDistance = Float.MAX_VALUE;
            for (Agent agent : AgentManager.getAgentMap().values()) {
                if (this.equals(agent)) {
                    continue;
                }
                if (agent instanceof USVAgent) {
                    USVAgent usvAgent = (USVAgent) agent;
                    if (usvAgent.getCamp() != null && usvAgent.getCamp() == Camp.ALLY) {
                        float distance = detector.detect(usvAgent);
                        if (distance >=0 && distance < minDistance) {
                            minDistance = distance;
                            closestEnemy = usvAgent;
                        }
                    }
                }
            }
            if (closestEnemy != null && weapon.attack(closestEnemy.agentID)) {
                send(closestEnemy.agentID, new AttackMessage(agentID));
            }
        }
        else if (camp == Camp.ALLY) {
            //攻击最近Enemy
            USVAgent closestEnemy = null;
            float minDistance = Float.MAX_VALUE;
            for (Agent agent : AgentManager.getAgentMap().values()) {
                if (this.equals(agent)) {
                    continue;
                }
                if (agent instanceof USVAgent) {
                    USVAgent usvAgent = (USVAgent) agent;
                    if (usvAgent.getCamp() != null && usvAgent.getCamp() == Camp.ENEMY) {
                        float distance = detector.detect(usvAgent);
                        if (distance >=0 && distance < minDistance) {
                            minDistance = distance;
                            closestEnemy = usvAgent;
                        }
                    }
                }
            }
            if (closestEnemy != null && weapon.attack(closestEnemy.agentID)) {
                send(closestEnemy.agentID, new AttackMessage(agentID));
            }
        }
    }

    private void calcReward() {
        Vector3f pos = entity.getTranslation();
        if (pos.x > sceneConfig.getMaxBoundaryX() &&
                pos.x < sceneConfig.getMinBoundaryX() &&
                pos.z > sceneConfig.getMaxBoundaryZ() &&
                pos.z < sceneConfig.getMinBoundaryZ()) {
            reward += rewardConfig.getOutOfRange();
        }
        if (camp == Camp.ENEMY) {
            //每靠近主舰enemy回报增加，ally回报
//            Agent MainShip = AgentManager.getAgent(AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, sceneConfig.getMainShip().getId()));

        }
    }

    //TODO 修改成消息驱动？
    public Vector3f closestEnemyPos() {
        USVAgent closestEnemy = null;
        float minDistance = Float.MAX_VALUE;
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (this.equals(agent)) {
                continue;
            }
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                //拥有阵营但和自己不是同阵营的都属于敌方
                if (usvAgent.getCamp() != null && usvAgent.getCamp() != camp) {
                    float distance = detector.detect(usvAgent);
                    if (distance >=0 && distance < minDistance) {
                        minDistance = distance;
                        closestEnemy = usvAgent;
                    }
                }
            }
        }
        if (closestEnemy == null) {
            return new Vector3f();
        }
        return closestEnemy.getEntity().getTranslation();
    }

    public Vector3f closestAllyPos() {
        USVAgent closestAlly = null;
        float minDistance = Float.MAX_VALUE;
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (this.equals(agent)) {
                continue;
            }
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                //拥有阵营但和自己是同阵营的都属于友方
                if (usvAgent.getCamp() != null && usvAgent.getCamp() == camp) {
                    float distance = detector.detect(usvAgent);
                    if (distance >=0 && distance > minDistance) {
                        minDistance = distance;
                        closestAlly = usvAgent;
                    }
                }
            }
        }
        if (closestAlly == null) {
            return new Vector3f();
        }
        return closestAlly.getEntity().getTranslation();
    }

    public Vector3f getCurrForward() {
        Vector3f currForward = new Vector3f(entity.getForward());
        currForward.rotate(entity.getRotation());
        currForward.normalize();
        return currForward;
    }

    public Vector3f relativeCoordinateToSelf(Vector3f coordinate) {
        Vector3f relativeCoordinate = new Vector3f(coordinate);
        relativeCoordinate.sub(entity.getTranslation());
        return relativeCoordinate;
    }

    public Status getStatus() {
        return status;
    }

    public Camp getCamp() {
        return camp;
    }

    /**
     *
     * @param clean 是否保留reward
     * @return
     */
    public float getReward(boolean clean) {
        float result = reward;
        if (clean) {
            reward = 0;
        }
        return result;
    }

    public int getId() {
        return id;
    }
}
