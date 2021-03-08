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
    protected void update(double stepTime) {
        entity.updateState();
        if (buoyHelper != null) {
            try {
                buoyHelper.handleBuoyancy((float) stepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setBuoyHelper(BuoyHelper buoyHelper) {
        this.buoyHelper = buoyHelper;
    }
}
