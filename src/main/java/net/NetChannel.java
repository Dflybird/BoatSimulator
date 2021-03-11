package net;

import engine.GameLogic;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.nio.ByteOrder;

public class NetChannel {

    private final GameLogic gameLogic;

    private final NioSocketChannel socket;
    private final Node node;

    private MessageQueue messageQueue;

    public NetChannel(GameLogic gameLogic, NioSocketChannel socket, Node node) {
        this.gameLogic = gameLogic;
        this.socket = socket;
        this.node = node;

//        this.messageQueue = new MessageQueue(kernel.getConfig());
    }

    public void init(ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 0, 4, 0, 4, true));
        pipeline.addLast(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
//        pipeline.addLast(new CRMSMsgDecoder());
//        pipeline.addLast(new CRMSMsgEncoder());
//        CRMSNodeHandler crmsNodeHandler = new CRMSNodeHandler(kernel, this);
//        pipeline.addLast(crmsNodeHandler);
    }

    public ChannelFuture close() {
        return socket.close();
    }

    public Node getNode() {
        return node;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }
}
