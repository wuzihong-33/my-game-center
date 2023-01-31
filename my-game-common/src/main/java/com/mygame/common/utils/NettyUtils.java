package com.mygame.common.utils;

import java.net.InetSocketAddress;
import io.netty.channel.Channel;

public class NettyUtils {
    public static String  getRemoteIP(Channel channel) {
        InetSocketAddress ipSocket = (InetSocketAddress)channel.remoteAddress();
        String remoteHost = ipSocket.getAddress().getHostAddress();
        return remoteHost;
    }

    public static String  getRemoteUrl(Channel channel) {
        InetSocketAddress ipSocket = (InetSocketAddress)channel.remoteAddress();
        String remoteAddress = ipSocket.getAddress().getHostAddress();
        int port = ipSocket.getPort();
        return remoteAddress+":"+port;
    }
}
