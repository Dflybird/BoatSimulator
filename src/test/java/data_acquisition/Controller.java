package data_acquisition;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @Author: gq
 * @Date: 2021/3/23 18:38
 */
public class Controller implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private static final String AGENT_ID = "ALLY_";

    public static final Random random = new Random(System.currentTimeMillis());

    private final ControllerAPIGrpc.ControllerAPIBlockingStub blockingStub;
    private final ControllerAPIGrpc.ControllerAPIStub asyncStub;

    private final CountDownLatch countDownLatch;
    private final int controllerID;

    public Controller(int controllerID, Channel channel, CountDownLatch countDownLatch) {
        this.controllerID = controllerID;
        this.blockingStub = ControllerAPIGrpc.newBlockingStub(channel);
        this.asyncStub = ControllerAPIGrpc.newStub(channel);
        this.countDownLatch = countDownLatch;
    }

    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:12345";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        int controllerNum = 10;
        Controller[] controllers = new Controller[controllerNum];
        CountDownLatch countDownLatch = new CountDownLatch(controllerNum);
        ExecutorService executor = Executors.newFixedThreadPool(controllerNum);
        logger.info("controller ready to start.");
        for (int i = 0; i < controllerNum; i++) {
            controllers[i] = new Controller(i, channel, countDownLatch);
            executor.submit(controllers[i]);
        }
        countDownLatch.await();
        executor.shutdown();
        channel.shutdown();
        boolean shutDown = executor.awaitTermination(5, TimeUnit.SECONDS);
        logger.info("executor shutdown: {}", shutDown);
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        int step = 300;
        while (step-- > 0) {
            random();
            try {
//                Thread.sleep(90+(long) (20*random.nextDouble()));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }

    private void random() {
        ControllerAPIProto.AgentAction action = ControllerAPIProto.AgentAction.newBuilder()
                .setAgentId(AGENT_ID + controllerID)
                .setActionType(1).build();
        ControllerAPIProto.AgentInfo agentInfo = ControllerAPIProto.AgentInfo.newBuilder()
                .setAgentID(AGENT_ID + controllerID).build();

        ControllerAPIProto.AgentObservation observation = blockingStub.getObservation(agentInfo);
        Null rsp = blockingStub.setAction(action);
    }
}
