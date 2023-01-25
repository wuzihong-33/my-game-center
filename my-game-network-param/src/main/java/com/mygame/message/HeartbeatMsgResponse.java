package com.mygame.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
@GameMessageMetadata(messageId=2,messageType= EnumMessageType.RESPONSE,serviceId=1)
public class HeartbeatMsgResponse extends AbstractJsonGameMessage<HeartbeatMsgResponse.ResponseBody>{
    public static class ResponseBody{
        private long serverTime;
        public long getServerTime() {
            return serverTime;
        }
        public void setServerTime(long serverTime) {
            this.serverTime = serverTime;
        }
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
