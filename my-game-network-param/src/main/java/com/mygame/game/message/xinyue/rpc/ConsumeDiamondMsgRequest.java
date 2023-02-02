package com.mygame.game.message.xinyue.rpc;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.rpc.ConsumeDiamondMsgRequest.RequestBody;

@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.RPC_REQUEST, serviceId = 101)
public class ConsumeDiamondMsgRequest extends AbstractJsonGameMessage<RequestBody> {

    public static class RequestBody {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
