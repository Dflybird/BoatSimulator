package ams.agent;

import ams.AgentManager;
import ams.msg.AgentMessage;
import ams.AgentMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:43
 */
public abstract class Agent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);

    protected final String agentID;
    //对象实体，用于物理引擎计算
    protected Entity entity;
    //消息队列，存放Agent间的通信消息
    private final Queue<AgentMessage> queue = new ConcurrentLinkedQueue<>();

    public Agent(String agentID) {
        this.agentID = agentID;
    }

    public Agent(String agentID, Entity entity) {
        this.agentID = agentID;
        this.entity =entity;
    }

    public void reset() {
        if (entity != null) {
            entity.reset();
        }
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

    protected void send(String id, AgentMessage msg) {
        Agent targetAgent = AgentManager.getAgent(id);
        targetAgent.queue.offer(msg);
    }

    protected AgentMessage receive() {
        return queue.poll();
    }

    protected void receiveAll(AgentMessageHandler handler){
        while (queue.size() > 0) {
            handler.handle(queue.poll());
        }
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

    public void putMessage(AgentMessage msg) {
        queue.offer(msg);
    }
}
