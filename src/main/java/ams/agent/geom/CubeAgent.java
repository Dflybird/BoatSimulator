package ams.agent.geom;

import ams.agent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class CubeAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(CubeAgent.class);

    public CubeAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update(double stepTime) throws Exception {
        entity.updateState(stepTime);
    }

}
