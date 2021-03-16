package util;

import ams.agent.usv.USVAgent;

/**
 * @Author: gq
 * @Date: 2021/3/16 11:00
 */
public class AgentUtil {

    public static String assembleName(USVAgent.Camp camp, int id) {
        return camp.name() + "_" + id;
    }
}
