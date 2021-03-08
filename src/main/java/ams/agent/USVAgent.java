package ams.agent;

import physics.buoy.BuoyHelper;

/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent{
    private BuoyHelper buoyHelper;
    public USVAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update(double stepTime) throws Exception {
        entity.updateState();
        if (buoyHelper != null) {
            buoyHelper.handleBuoyancy((float) stepTime);
        }
    }

    public void setBuoyHelper(BuoyHelper buoyHelper) {
        this.buoyHelper = buoyHelper;
    }
}
