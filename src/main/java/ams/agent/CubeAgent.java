package ams.agent;

import physics.buoy.BuoyHelper;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class CubeAgent extends Agent {

    BuoyHelper buoyHelper;
    public CubeAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update() {
        if (buoyHelper != null) {
            buoyHelper.handleBuoyancy(entity);
        }
    }

    public void setBuoyHelper(BuoyHelper buoyHelper) {
        this.buoyHelper = buoyHelper;
    }
}
