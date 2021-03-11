package net;

public abstract class Message {
    protected final Class<?> correspondingMessageClass;
    protected byte[] msgData;

    public Message(Class<?> correspondingMessageClass) {
        this.correspondingMessageClass = correspondingMessageClass;
    }

    public Class<?> getCorrespondingMessageClass() {
        return correspondingMessageClass;
    }


    public byte[] getMsgData() {
        return msgData;
    }
}
