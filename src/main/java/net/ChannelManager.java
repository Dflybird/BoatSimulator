package net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    private final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private ConcurrentHashMap<Node, NetChannel> channels = new ConcurrentHashMap<>();

    public void add(NetChannel channel) {
        channels.put(channel.getNode(), channel);
    }

    public void remove(NetChannel channel) {
        if (channels.remove(channel.getNode()) != null) {
            logger.debug("remove channel");
        }
    }

    public void removeAndClose(Node node) {
        NetChannel channel = channels.get(node);
        channel.close();
        channels.remove(node);
    }

    public void closeAllChannel(){
        for (Map.Entry<Node, NetChannel> entry : channels.entrySet()) {
            NetChannel channel = entry.getValue();
            remove(channel);
            channel.close();
        }
    }

    public ConcurrentHashMap<Node, NetChannel> getChannels() {
        return channels;
    }

    public NetChannel getChannel(Node node) throws NullPointerException {
        return channels.get(node);
    }
}
