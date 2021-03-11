package net;

import conf.Config;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

public class MessageQueue {

    Logger logger  = LoggerFactory.getLogger(MessageQueue.class);

    private final int SEND_MSG_NUM_PER_SECOND;

    private final Config config;
    private static final int processNum = Runtime.getRuntime().availableProcessors();

    private static final ScheduledExecutorService threadPool =
            Executors.newScheduledThreadPool(processNum, new DefaultThreadFactory("message queue thread pool"));
    private ScheduledFuture<?> future;
    private ChannelHandlerContext ctx;

    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

    public MessageQueue(Config config) {
        this.config = config;
        this.SEND_MSG_NUM_PER_SECOND = 100;
    }

    public void start(ChannelHandlerContext ctx){
        this.ctx = ctx;
        //每0.1s调用一次发送函数
        this.future = threadPool.scheduleAtFixedRate(this::send, 1000, 100, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        future.cancel(true);
    }

    public void add(Message msg) {
        queue.offer(msg);
    }

    public int size() {
        return queue.size();
    }

    private void send() {
//        if (size() > config.msgQueueMaxCapacity()) {
//            logger.warn("The number of msg in the msg queue reaches the maximum, size: " + size() + ", maximum: " + config.msgQueueMaxCapacity());
//        }
//        int numPerMilliSeconds;
//        //最大每秒发送消息数低于10，每0.1s最多发送一条，否则每0.1s发送最大每秒发送消息数/10
//        if (SEND_MSG_NUM_PER_SECOND < 10){
//            numPerMilliSeconds = Math.min(size(), 1);
//        } else {
//            numPerMilliSeconds = Math.min(size(), SEND_MSG_NUM_PER_SECOND/10);
//        }
//
//        if (numPerMilliSeconds == 0){
//            return;
//        }

//        for (int i = 0; i < numPerMilliSeconds; i++) {
//            Message message = queue.poll();
//            ctx.write(message);
//        }

        ctx.flush();
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }
}
