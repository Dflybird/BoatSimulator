package ams.agent;

import ams.AgentManager;
import ams.AgentMessageHandler;
import ams.msg.AgentMessage;
import ams.msg.SteerMessage;
import org.joml.Vector3f;
import physics.entity.usv.BoatEngine;
import physics.entity.usv.BoatEntity;
import physics.entity.usv.BoatDetector;
import physics.entity.usv.BoatWeapon;


/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent implements AgentMessageHandler {

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
        ENEMY(1);

        private final int code;

        private static final Camp[] CAMPS = new Camp[] {
                ALLY, ENEMY
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

    private final BoatEngine engine;
    private final BoatDetector detector;
    private final BoatWeapon weapon;

    private final Status status;
    private Camp camp;
    private int id;

    public USVAgent(String agentID, BoatEntity entity) {
        super(agentID, entity);

        engine = new BoatEngine(entity, new Vector3f(-2f, -0.5f, 0f));
        detector = new BoatDetector(entity, new Vector3f(0f, 0f, 0f));
        weapon = new BoatWeapon(entity, new Vector3f(0f, 0f, 0f));

        status = Status.ALIVE;
    }

    @Override
    protected void update(double stepTime) throws Exception {
        entity.updateState(stepTime);
        receiveAll(this);
        engine.updateEngine();
    }

    @Override
    public void handle(AgentMessage msg) {
        if (msg.getCorrespondingMessageClass() == SteerMessage.class) {
            SteerMessage steerMessage = (SteerMessage) msg;
            engine.setEnginePower(steerMessage.getPower());
            engine.setEngineRotation(steerMessage.getAngle());
        }
    }

    //TODO 修改成消息驱动
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
                    if (distance < minDistance) {
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
                    if (distance > minDistance) {
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

    public int getId() {
        return id;
    }
}
