package com.mygame.game.message.xinyue;

import java.util.Map;
import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.GetPlayerByIdMsgResponse.ResponseBody;
@GameMessageMetadata(messageId = 202, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetPlayerByIdMsgResponse extends AbstractJsonGameMessage<ResponseBody> {
    public static class ResponseBody {
        private long playerId;
        private String nickName;
        private Map<String, String> heros;

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public Map<String, String> getHeros() {
            return heros;
        }

        public void setHeros(Map<String, String> heros) {
            this.heros = heros;
        }


    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
