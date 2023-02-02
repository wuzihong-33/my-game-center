package com.mygame.game.message.xinyue;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.GetPlayerByIdMsgRequest.RequestBody;

@GameMessageMetadata(messageId = 202, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class GetPlayerByIdMsgRequest extends AbstractJsonGameMessage<RequestBody> {
    public static class RequestBody {
        private int playerId;

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }


    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
