package ams.agent;

import ams.AgentMessageHandler;
import ams.agent.usv.USVAgent;
import ams.msg.AgentMessage;
import ams.msg.OnDoneMessage;
import conf.SceneConfig;

/**
 * @Author: gq
 * @Date: 2021/3/17 15:30
 */
public class OnDoneAgent extends Agent implements AgentMessageHandler {

    private boolean done;
    private final SceneConfig sceneConfig;

    private int onDoneAllyNum;
    private int onDoneEnemyNum;
    private boolean onDoneMainShip;

    public OnDoneAgent(String agentID) {
        super(agentID);
        sceneConfig = SceneConfig.loadConfig();
        onDoneAllyNum = 0;
        onDoneEnemyNum = 0;
        onDoneMainShip = false;
    }

    @Override
    protected void update(double stepTime) throws Exception {
        receiveAll(this);
    }

    @Override
    public void reset() {
        super.reset();
        onDoneAllyNum = 0;
        onDoneEnemyNum = 0;
        onDoneMainShip = false;
    }

    @Override
    public void handle(AgentMessage msg) {
        if (msg.getCorrespondingMessageClass() == OnDoneMessage.class) {
            OnDoneMessage onDoneMessage = (OnDoneMessage) msg;
            if (onDoneMessage.getCamp() == USVAgent.Camp.ALLY) {
                onDoneAllyNum++;
                if (onDoneAllyNum == sceneConfig.getAllyNum()) {
                    done = true;
                }
            }
            else if (onDoneMessage.getCamp() == USVAgent.Camp.ENEMY) {
                onDoneEnemyNum++;
                if (onDoneEnemyNum == sceneConfig.getEnemyNum()) {
                    done = true;
                }
            }
            else if (onDoneMessage.getCamp() == USVAgent.Camp.MAIN_SHIP) {
                onDoneMainShip = true;
                done = true;
            }
        }
    }

    public boolean isDone() {
        return done;
    }
}
