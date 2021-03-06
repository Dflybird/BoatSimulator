package ams.agent;

import physics.buoy.BuoyHelper;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class CubeAgent extends Agent {

    private BuoyHelper buoyHelper;
    public CubeAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update() {
        entity.updateState();
        if (buoyHelper != null) {
            buoyHelper.handleBuoyancy();
        }
    }

    public void setBuoyHelper(BuoyHelper buoyHelper) {
        this.buoyHelper = buoyHelper;
    }
}
