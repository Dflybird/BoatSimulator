package ams.msg;

import ams.agent.usv.USVAgent;

/**
 * @Author: gq
 * @Date: 2021/3/17 15:34
 */
public class OnDoneMessage extends AgentMessage {

    private final USVAgent.Camp camp;

    public OnDoneMessage(USVAgent.Camp camp) {
        super(OnDoneMessage.class);
        this.camp = camp;
    }

    public USVAgent.Camp getCamp() {
        return camp;
    }
}
