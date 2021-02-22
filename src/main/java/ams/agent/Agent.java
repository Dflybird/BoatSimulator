package ams.agent;

import ams.AgentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.Entity;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:43
 */
public abstract class Agent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);

    protected final String agentID;

    public Agent(String agentID) {
        this.agentID = agentID;
    }

    @Override
    public void run() {
        update();
        AgentManager.onDone();
    }

    protected abstract void update();

    protected void send() {

    }

    protected void receive(){

    }

    public String getAgentID() {
        return agentID;
    }


    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
