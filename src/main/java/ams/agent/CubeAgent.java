package ams.agent;

import physics.BuoyHelper;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class CubeAgent extends Agent {

    BuoyHelper buoyHelper = new BuoyHelper();
    public CubeAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update() {
        buoyHelper.handleBuoyancy(entity);
    }
}
