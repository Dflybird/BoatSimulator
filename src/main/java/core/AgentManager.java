package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:46
 */
public class AgentManager {

    private final Logger logger = LoggerFactory.getLogger(AgentManager.class);

    private static AgentManager instance = new AgentManager();
    private static final Integer processNum = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private CountDownLatch countDownLatch;
    private ConcurrentHashMap<String, Agent> agentMap = new ConcurrentHashMap<>();

    private AgentManager(){
    }

    public void update() throws InterruptedException {


        instance.countDownLatch = new CountDownLatch(agentMap.size());
        instance.agentMap.values().forEach(threadPool::submit);
        instance.countDownLatch.await();
    }

    public static AgentManager getInstance(){
        return instance;
    }

    public static void onDone(){
        instance.countDownLatch.countDown();
    }

}
