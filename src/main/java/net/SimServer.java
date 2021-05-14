package net;

import engine.GameLogic;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: gq
 * @Date: 2021/3/15 15:17
 */
public class SimServer {
    private static final Logger logger = LoggerFactory.getLogger(SimServer.class);

    private final GameLogic gameLogic;
    private final int port;
    private final Server server;

    public SimServer(GameLogic gameLogic, int port) {
        this.gameLogic = gameLogic;
        this.port = port;
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        this.server = serverBuilder.addService(new ControllerRPCServices(gameLogic)).build();
    }

    public void start(){
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            SimServer.this.stop();
//        }));
    }

    public void stop(){
        if (server != null) {
            try {
                server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                logger.info("network server shutdown.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
