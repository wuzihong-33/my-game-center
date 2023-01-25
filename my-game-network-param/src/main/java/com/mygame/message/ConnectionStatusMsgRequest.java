package com.mygame.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
@GameMessageMetadata(messageId=2,messageType= EnumMessageType.REQUEST,serviceId=1)
public class ConnectionStatusMsgRequest extends AbstractJsonGameMessage<ConnectionStatusMsgRequest.MessageBody> {
    public static class MessageBody {
        private boolean connect;//true是连接成功，false是连接断开
        public boolean isConnect() {
            return connect;
        }
        public void setConnect(boolean connect) {
            this.connect = connect;
        }

    }

    @Override
    protected Class<MessageBody> getBodyObjClass() {
        return MessageBody.class;
    }


}
