package ams;

import ams.msg.AgentMessage;

/**
 * @Author Gq
 * @Date 2021/3/11 21:09
 * @Version 1.0
 **/
public interface AgentMessageHandler {

    void handle(AgentMessage msg);
}
