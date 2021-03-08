package ams.agent;

import ams.AgentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:43
 */
public abstract class Agent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);

    protected final String agentID;
    //对象实体，用于物理引擎计算
    protected Entity entity;

    public Agent(String agentID) {
        this.agentID = agentID;
    }

    @Override
    public void run() {
        try {
            update(AgentManager.getStepTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AgentManager.onDone();
    }

    protected abstract void update(double stepTime) throws Exception;

    protected void send() {

    }

    protected void receive(){

    }

    public String getAgentID() {
        return agentID;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
