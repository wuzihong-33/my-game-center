package com.mygame.game.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.ConfirmMsgRequest.ConfirmBody;
@GameMessageMetadata(messageId=1,messageType= EnumMessageType.REQUEST,serviceId=1)
public class ConfirmMsgRequest extends AbstractJsonGameMessage<ConfirmBody> {
    public static class ConfirmBody {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @Override
    protected Class<ConfirmBody> getBodyObjClass() {
        return ConfirmBody.class;
    }

}
