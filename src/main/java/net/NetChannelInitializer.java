package net;

import engine.GameLogic;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NetChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final Node remoteNode;
    private final GameLogic gameLogic;

    private ChannelManager channelManager;

    public NetChannelInitializer(Node remoteNode, GameLogic gameLogic) {
        this.remoteNode = remoteNode;
        this.gameLogic = gameLogic;

//        this.channelManager = gameLogic.getChannelManager();
    }

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        NetChannel netChannel;
        if (inServer()) {
            netChannel = new NetChannel(gameLogic, nioSocketChannel, new Node(nioSocketChannel.remoteAddress()));
        } else {
            netChannel = new NetChannel(gameLogic, nioSocketChannel, remoteNode);
        }
        netChannel.init(nioSocketChannel.pipeline());

        channelManager.add(netChannel);

        nioSocketChannel.closeFuture().addListener(future -> channelManager.remove(netChannel));

    }

    public boolean inServer(){
        return remoteNode == null;
    }
}
