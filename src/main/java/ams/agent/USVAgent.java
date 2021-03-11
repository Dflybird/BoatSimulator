package ams.agent;

import ams.AgentMessageHandler;
import ams.msg.AgentMessage;
import ams.msg.SteerMessage;
import physics.buoy.BuoyHelper;
import physics.entity.usv.BoatEntity;


/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent implements AgentMessageHandler {
    public USVAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update(double stepTime) throws Exception {
        entity.updateState(stepTime);
        receiveAll(this);
    }

    @Override
    public void handle(AgentMessage msg) {
        if (msg.getCorrespondingMessageClass() == SteerMessage.class) {
            SteerMessage steerMessage = (SteerMessage) msg;
            BoatEntity boatEntity = (BoatEntity) entity;
            boatEntity.getEngine().setEnginePower(steerMessage.getPower());
            boatEntity.getEngine().setEngineRotation(steerMessage.getAngle());
        }
    }
}
