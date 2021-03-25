package net;

import ams.AgentManager;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
import data_acquisition.ExpRemoteSim;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: gq
 * @Date: 2021/3/23 18:20
 */
public class ControllerServer {
    private static final Logger logger = LoggerFactory.getLogger(ControllerServer.class);

    private final Server server;

    private AtomicInteger msgNum;

    public ControllerServer(ExpRemoteSim sim, int port) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        this.server = serverBuilder.addService(new ControllerService(sim, this)).build();
        this.msgNum = new AtomicInteger();
    }

    public void start(){
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int getMsgNum() {
        return msgNum.getAndSet(0);
    }

    public void addMsgNum() {
        msgNum.incrementAndGet();
    }


    public static class ControllerService extends ControllerAPIGrpc.ControllerAPIImplBase {
        private final ExpRemoteSim sim;
        private final ControllerServer controllerServer;

        public ControllerService(ExpRemoteSim sim, ControllerServer controllerServer) {
            this.sim = sim;
            this.controllerServer = controllerServer;
        }

        @Override
        public void getObservation(ControllerAPIProto.AgentInfo request, StreamObserver<ControllerAPIProto.AgentObservation> responseObserver) {
            USVAgent usvAgent = (USVAgent) AgentManager.getAgent(request.getAgentID());
            ControllerAPIProto.AgentObservation observation = ControllerAPIProto.AgentObservation.newBuilder()
                    .setAgentId(usvAgent.getAgentID())
                    .setStatus(usvAgent.getStatus().toInteger())
                    .setSelfPos(newVector3(usvAgent.getEntity().getTranslation())).build();
            responseObserver.onNext(observation);
            responseObserver.onCompleted();
            controllerServer.addMsgNum();
        }

        @Override
        public void setAction(ControllerAPIProto.AgentAction request, StreamObserver<Null> responseObserver) {
            SteerMessage steerMessage = new SteerMessage(SteerMessage.SteerType.typeOf(request.getActionType()));
            AgentManager.sendAgentMessage(request.getAgentId(), steerMessage);
            responseObserver.onNext(Null.newBuilder().build());
            responseObserver.onCompleted();
            controllerServer.addMsgNum();
        }

        @Override
        public void reset(Null request, StreamObserver<Null> responseObserver) {
            super.reset(request, responseObserver);
            controllerServer.addMsgNum();
        }

        private Vector3 newVector3(Vector3f vector3f) {
            return Vector3.newBuilder()
                    .setX(vector3f.x)
                    .setY(vector3f.y)
                    .setZ(vector3f.z)
                    .build();
        }
    }
}
