package ams.msg;

import org.joml.Vector3f;

/**
 * @Author: gq
 * @Date: 2021/3/16 10:23
 */
public class DetectMessage extends AgentMessage {

    private String targetID;
    private Vector3f position;


    public DetectMessage() {
        super(DetectMessage.class);
    }
}
