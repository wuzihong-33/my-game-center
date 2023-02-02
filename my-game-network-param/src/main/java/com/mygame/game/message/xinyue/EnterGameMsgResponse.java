package com.mygame.game.message.xinyue;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.EnterGameMsgResponse.ResponseBody;
@GameMessageMetadata(messageId=201,messageType=EnumMessageType.RESPONSE,serviceId=101)
public class EnterGameMsgResponse extends AbstractJsonGameMessage<ResponseBody> {

    public static class ResponseBody {
        private String nickname;
        private long playerId;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
