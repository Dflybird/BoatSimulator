package ams.agent;

import ams.AgentMessageHandler;
import ams.msg.AgentMessage;
import ams.msg.SteerMessage;
import org.joml.Vector3f;
import physics.entity.usv.BoatEngine;
import physics.entity.usv.BoatEntity;
import physics.entity.usv.BoatRadar;
import physics.entity.usv.BoatWeapon;


/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent implements AgentMessageHandler {

    enum Status {
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

    private final BoatEngine engine;
    private final BoatRadar radar;
    private final BoatWeapon weapon;

    private final Status status;

    public USVAgent(String agentID, BoatEntity entity) {
        super(agentID, entity);

        engine = new BoatEngine(entity, new Vector3f(-2f, -0.5f, 0f));
        radar = new BoatRadar(entity, new Vector3f(0f, 0f, 0f));
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
}
