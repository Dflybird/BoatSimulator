package net;

import java.net.InetSocketAddress;

public class Node {

    private final String ip;
    private final int port;
    private final InetSocketAddress address;

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.address = new InetSocketAddress(ip, port);
    }

    public Node(InetSocketAddress address) {
        this.address = address;
        this.ip = address.getHostName();
        this.port = address.getPort();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && address.equals(((Node) obj).address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
