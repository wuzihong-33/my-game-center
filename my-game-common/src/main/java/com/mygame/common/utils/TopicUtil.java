package com.mygame.common.utils;

public class TopicUtil {
    public static String generateTopic(String prefix,int serverId) {
        return prefix + "-" + serverId;
    }
}
