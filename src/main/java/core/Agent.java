package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:43
 */
public abstract class Agent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);


    @Override
    public void run() {

        AgentManager.onDone();
    }


    public void send() {

    }
}
