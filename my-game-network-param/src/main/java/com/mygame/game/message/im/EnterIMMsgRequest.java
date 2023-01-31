package com.mygame.game.message.im;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.im.EnterIMMsgRequest.EnterIMMsgBody;
@GameMessageMetadata(messageId = 310, messageType = EnumMessageType.REQUEST, serviceId = 103)
public class EnterIMMsgRequest extends AbstractJsonGameMessage<EnterIMMsgBody>{
    public static class EnterIMMsgBody {
        
    }

    @Override
    protected Class<EnterIMMsgBody> getBodyObjClass() {
        return null;
    }

}
