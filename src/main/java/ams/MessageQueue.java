package ams;

import ams.msg.AgentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: gq
 * @Date: 2021/1/20 14:52
 */
public class MessageQueue {

    private final Logger logger = LoggerFactory.getLogger(MessageQueue.class);


    private Queue<AgentMessage> queue = new ConcurrentLinkedQueue<>();
}
