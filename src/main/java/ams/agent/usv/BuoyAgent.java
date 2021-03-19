package ams.agent.usv;

import ams.agent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class BuoyAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(BuoyAgent.class);

    public BuoyAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update(double stepTime) throws Exception {
        entity.updateState(stepTime);
    }

    @Override
    public void reset() {
        super.reset();
    }
}
