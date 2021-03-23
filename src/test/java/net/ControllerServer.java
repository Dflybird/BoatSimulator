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

/**
 * @Author: gq
 * @Date: 2021/3/23 18:20
 */
public class ControllerServer {
    private static final Logger logger = LoggerFactory.getLogger(ControllerServer.class);

    private final Server server;

    public ControllerServer(ExpRemoteSim sim, int port) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        this.server = serverBuilder.addService(new ControllerService(sim)).build();
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

    public static class ControllerService extends ControllerAPIGrpc.ControllerAPIImplBase {
        private final ExpRemoteSim sim;

        public ControllerService(ExpRemoteSim sim) {
            this.sim = sim;
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
        }

        @Override
        public void setAction(ControllerAPIProto.AgentAction request, StreamObserver<Null> responseObserver) {
            SteerMessage steerMessage = new SteerMessage(SteerMessage.SteerType.typeOf(request.getActionType()));
            AgentManager.sendAgentMessage(request.getAgentId(), steerMessage);
        }

        @Override
        public void reset(Null request, StreamObserver<Null> responseObserver) {
            super.reset(request, responseObserver);
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
