package engine;

import ams.agent.Agent;
import ams.AgentManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.SimGUI;

/**
 * @Author: gq
 * @Date: 2021/1/21 14:32
 */
public class TestGameEngine {

    private final Logger logger = LoggerFactory.getLogger(TestGameEngine.class);

    @Test
    public void testEngine(){
        AgentManager.addAgent(new Agent("agent1") {
            @Override
            protected void update(double stepTime) {
                int sum = 0;
                for (int i = 0; i < 110; i++) {
                    sum += i;
                }
//                logger.debug("agent1 sum: {}", sum);
            }
        });
        AgentManager.addAgent(new Agent("agent2") {
            @Override
            protected void update(double stepTime) {
                int sum = 0;
                for (int i = 0; i < 120; i++) {
                    sum += i;
                }
//                logger.debug("agent2 sum: {}", sum);
            }
        });
        AgentManager.addAgent(new Agent("agent3") {
            @Override
            protected void update(double stepTime) {
                int sum = 0;
                for (int i = 0; i < 130; i++) {
                    sum += i;
                }
//                logger.debug("agent3 sum: {}", sum);

            }
        });
//        new SimGUI().start();

    }
}
