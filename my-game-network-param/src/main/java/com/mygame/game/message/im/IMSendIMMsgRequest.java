package com.mygame.game.message.im;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.im.IMSendIMMsgRequest.SendIMMsgBody;

@GameMessageMetadata(messageId = 312, messageType = EnumMessageType.REQUEST, serviceId = 103)
public class IMSendIMMsgRequest extends AbstractJsonGameMessage<SendIMMsgBody> {

    public static class SendIMMsgBody {
        private String chat;
        private String sender;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }
    }

    @Override
    protected Class<SendIMMsgBody> getBodyObjClass() {
        return SendIMMsgBody.class;
    }
}
