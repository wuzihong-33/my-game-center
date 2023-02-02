package com.mygame.game.message.xinyue;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.BuyArenaChallengeTimesMsgRequest.RequestBody;

@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.REQUEST, serviceId = 102)
public class BuyArenaChallengeTimesMsgRequest extends AbstractJsonGameMessage<RequestBody> {
    
    public static class RequestBody {
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
