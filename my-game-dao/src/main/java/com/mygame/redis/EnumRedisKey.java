package com.mygame.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;

public enum EnumRedisKey {
    USER_ID_INCR(null),            // UserId 自增key
    USER_ACCOUNT(Duration.ofDays(7)),      // 用户信息
    PLAYER_ID_INCR(null),         // playerId 自增key
    PLAYER_NICKNAME(null),
    PLAYER_INFO(Duration.ofDays(7)),
    ARENA(Duration.ofDays(7));
    
    private Duration timeout;// expire时间,如果为null，表示value永远不过期

    private EnumRedisKey(Duration timeout) {
        this.timeout = timeout;
    }

    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + "_" + id;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public String getKey() {
        return this.name();
    }

}
