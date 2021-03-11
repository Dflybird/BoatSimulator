package net;

import conf.Config;
import engine.GameLogic;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地网络服务端，接收处理远程节点的连接请求
 * @Author: gq
 * @Date: 2020/12/18 14:49
 */
public class NodeServer {
    private static final Logger logger = LoggerFactory.getLogger(NodeServer.class);

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        final AtomicInteger count = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "server thread pool - " + count.getAndIncrement());
        }
    };

    protected Channel channel;

    private GameLogic gameLogic;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public NodeServer() {
    }

    public void start(){
        //TODO 本机ip port
    }

    public void start(String ip, int port){
        start(new Node(ip, port));
    }

    public void start(Node node) {
        if (isRunning()) {
            return;
        }

        try {
            bossGroup = new NioEventLoopGroup(1, threadFactory);
            workerGroup = new NioEventLoopGroup(4, threadFactory);

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new NetChannelInitializer(null, gameLogic));
            channel = serverBootstrap.bind(node.getAddress()).sync().channel();

            logger.info("net server started: address = {}:{}", node.getIp(), node.getPort());
        } catch (InterruptedException e) {
            logger.error("Failed to start peer server", e);
        }
    }

    public void stop() {
        if (isRunning() && channel.isOpen()) {
            try {
                channel.close().sync();

                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();

                channel = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("net server shut down");
        }
    }

    public boolean isRunning() {
        return channel != null;
    }
}
