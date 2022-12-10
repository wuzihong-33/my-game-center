package com.mygame.db.entity;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserAccount")
public class UserAccount {
    @Id                         // 标记为数据库主键
    private String openId;      // 用户的账号ID，一般是第三方SDK的openId
    private long userId;        // 用户唯一ID，由服务器维护，需要全局唯一
    private long createTime;    // 创建时间
    private String registIp;    // 注册ip
    private String lastLoginIp; // 上次登录的ip
    private Map<String, ZonePlayerInfo> zonePlayerInfos = new HashMap<>();// 记录已创建角色的基本信息

    public ZonePlayerInfo getZonePlayerInfo(String zoneId) {
        return zonePlayerInfos.get(zoneId);
    }
    public void addZonePlayerInfo(String zoneId, ZonePlayerInfo zonePlayerInfo) {
        zonePlayerInfos.put(zoneId, zonePlayerInfo);
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRegistIp() {
        return registIp;
    }

    public void setRegistIp(String registIp) {
        this.registIp = registIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Map<String, ZonePlayerInfo> getZonePlayerInfo() {
        return zonePlayerInfos;
    }

    public void setZonePlayerInfo(Map<String, ZonePlayerInfo> zonePlayerInfo) {
        this.zonePlayerInfos = zonePlayerInfo;
    }
    public boolean exitZonePlayerInfo(String zoneId) {
        return this.zonePlayerInfos.containsKey(zoneId);
    }
    
    @Override
    public String toString() {
        return "UserAccount [openId=" + openId + ", userId=" + userId + ", createTime=" + createTime + ", registIp=" + registIp + ", lastLoginIp=" + lastLoginIp + ", zonePlayerInfo=" + zonePlayerInfos + "]";
    }



    public static class ZonePlayerInfo {
        private long playerId;//此区内的角色Id
        private long lastEnterTime;//最近一次进入此区的时间

        public ZonePlayerInfo() {}
        public ZonePlayerInfo(long playerId, long lastEnterTime) {
            super();
            this.playerId = playerId;
            this.lastEnterTime = lastEnterTime;
        }

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }

        public long getLastEnterTime() {
            return lastEnterTime;
        }

        public void setLastEnterTime(long lastEnterTime) {
            this.lastEnterTime = lastEnterTime;
        }
        @Override
        public String toString() {
            return "ZoneInfo [playerId=" + playerId + ", lastEnterTime=" + lastEnterTime + "]";
        }


    }
}
