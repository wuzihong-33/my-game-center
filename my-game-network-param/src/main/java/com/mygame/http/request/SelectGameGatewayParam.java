package com.mygame.http.request;

import com.mygame.common.utils.CommonField;
import com.mygame.error.GameCenterError;
import org.springframework.util.StringUtils;

public class SelectGameGatewayParam extends AbstractHttpRequestParam {
    private String openId; // 第三方用户唯一id
    private long playerId; // 角色id
    private long userId; // 用户id
    private String zoneId = "0"; // 选择的区id
    
    @Override
    protected void haveError() {
        if (StringUtils.isEmpty(openId)) {
            this.error = GameCenterError.OPENID_IS_EMPTY;
        } else if (openId.length() > CommonField.OPEN_ID_LENGTH) {
            this.error = GameCenterError.OPENID_LEN_ERROR;
        } else if (StringUtils.isEmpty(this.zoneId)) {
            this.error = GameCenterError.ZONE_ID_IS_EMPTY;
        }
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
}
