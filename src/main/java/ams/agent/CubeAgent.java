package ams.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.buoy.BuoyHelper;

/**
 * @Author Gq
 * @Date 2021/2/1 17:31
 * @Version 1.0
 **/
public class CubeAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(CubeAgent.class);

    private BuoyHelper buoyHelper;
    public CubeAgent(String agentID) {
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
