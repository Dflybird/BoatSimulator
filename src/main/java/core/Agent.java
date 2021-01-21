package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:43
 */
public abstract class Agent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);

    private final String agentID;

    public Agent(String agentID) {
        this.agentID = agentID;
    }

    @Override
    public void run() {
        update();
        AgentManager.onDone();
    }

    protected abstract void update();

    public void send() {

    }

    public String getAgentID() {
        return agentID;
    }
}
