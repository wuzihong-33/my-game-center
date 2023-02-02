package com.mygame.game.message.xinyue.rpc;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.rpc.ConsumeDiamondMsgResponse.ResponseBody;
@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.RPC_RESPONSE, serviceId = 102)
public class ConsumeDiamondMsgResponse  extends AbstractJsonGameMessage<ResponseBody>{

    public static class ResponseBody {
        
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
