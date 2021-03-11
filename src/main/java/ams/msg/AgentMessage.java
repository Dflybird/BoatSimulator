package ams.msg;

/**
 * @Author: gq
 * @Date: 2021/1/20 15:02
 */
public abstract class AgentMessage {
    protected final Class<?> correspondingMessageClass;

    public AgentMessage(Class<?> correspondingMessageClass) {
        this.correspondingMessageClass = correspondingMessageClass;
    }

    public Class<?> getCorrespondingMessageClass() {
        return correspondingMessageClass;
    }
}
