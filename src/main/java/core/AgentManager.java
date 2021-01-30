package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:46
 */
public class AgentManager {

    private final Logger logger = LoggerFactory.getLogger(AgentManager.class);

    private static AgentManager instance = new AgentManager();
    private static final Integer processNum = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(processNum * 2);

    private CountDownLatch countDownLatch;
    private ConcurrentHashMap<String, Agent> agentMap = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<Agent> agentInsertEven = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> agentRemoveEven = new ConcurrentLinkedQueue<>();

    private static SimState simState = new SimState();
    private static final List<StateUpdateListener> listeners = new ArrayList<>();

    private AgentManager(){}

    public void update(double stepTime) {
        while (!agentRemoveEven.isEmpty()) {
            agentMap.remove(agentRemoveEven.poll());
        }
        while (!agentInsertEven.isEmpty()) {
            Agent agent = agentInsertEven.poll();
            agentMap.put(agent.getAgentID(), agent);
        }
        //TODO 网络ACC

        instance.countDownLatch = new CountDownLatch(agentMap.size());
        instance.agentMap.values().forEach(threadPool::submit);
        try {
            instance.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO 物理引擎


        //收集所有Agent状态
        simState = new SimState();
        agentMap.forEach((agentID, agent) -> {

        });

        listeners.forEach(listener -> listener.stateUpdated(simState));
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
        listeners.add(listener);
        listener.stateInit(simState);
    }
}
