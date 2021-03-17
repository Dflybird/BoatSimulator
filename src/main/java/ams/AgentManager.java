package ams;

import ams.agent.Agent;
import ams.agent.OnDoneAgent;
import ams.msg.AgentMessage;
import conf.Constant;
import net.SimServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.PhysicsEngine;
import state.SimState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:46
 */
public class AgentManager {

    private static final Logger logger = LoggerFactory.getLogger(AgentManager.class);

    private static final AgentManager instance = new AgentManager();
    private static final Integer processNum = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(processNum * 2);

    private CountDownLatch countDownLatch;
    private PhysicsEngine physicsEngine;
    private static SimState simState = new SimState();

    private final ConcurrentHashMap<String, Agent> agentMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Agent> agentInsertEven = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> agentRemoveEven = new ConcurrentLinkedQueue<>();

    private final List<StateUpdateListener> listeners = new ArrayList<>();

    private double stepTime = 0;

    private AgentManager(){
        OnDoneAgent onDoneAgent = new OnDoneAgent(Constant.ON_DONE_AGENT);
        agentMap.put(onDoneAgent.getAgentID(), onDoneAgent);
    }

    public static void update(double stepTime) {
        instance.stepTime = stepTime;
        while (!instance.agentRemoveEven.isEmpty()) {
            instance.agentMap.remove(instance.agentRemoveEven.poll());
        }
        while (!instance.agentInsertEven.isEmpty()) {
            Agent agent = instance.agentInsertEven.poll();
            instance.agentMap.put(agent.getAgentID(), agent);
        }

        instance.countDownLatch = new CountDownLatch(instance.agentMap.size());
        instance.agentMap.values().forEach(threadPool::submit);
        try {
            instance.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (instance.physicsEngine != null) {
            instance.physicsEngine.update(stepTime);
        }

        //收集所有Agent状态
        simState = new SimState();
        instance.agentMap.values().forEach( agent -> simState.collect(agent));

        instance.listeners.forEach(listener -> listener.stateUpdated(simState));
    }

    public static AgentManager getInstance(){
        return instance;
    }

    public static void onDone(){
        instance.countDownLatch.countDown();
    }

    public static void addAgent(Agent agent) {
        instance.agentInsertEven.offer(agent);
    }

    public static void deleteAgent(String agentID){
        instance.agentRemoveEven.offer(agentID);
    }

    public static void registerSimStateListener(StateUpdateListener listener) {
        instance.listeners.add(listener);
        listener.stateInit(simState);
    }

    public static Agent getAgent(String agentID) {
        return instance.agentMap.get(agentID);
    }

    public static ConcurrentHashMap<String, Agent> getAgentMap() {
        return instance.agentMap;
    }

    public static void sendAgentMessage(String agentID, AgentMessage message) {
        Agent targetAgent = instance.agentMap.get(agentID);
        if (targetAgent != null) {
            targetAgent.putMessage(message);
        }
    }

    public static double getStepTime() {
        return instance.stepTime;
    }

    public static void setPhysicsEngine(PhysicsEngine physicsEngine) {
        instance.physicsEngine = physicsEngine;
    }

    public static void resetAllAgent() {
        for (Agent agent : instance.agentMap.values()) {
            agent.reset();
        }
    }

    public static void stop() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("agent manager shutdown.");
    }
}
