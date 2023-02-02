package com.mygame.game.message.xinyue;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.BuyArenaChallengeTimesMsgResponse.ResponseBody;

@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.RESPONSE, serviceId = 102)
public class BuyArenaChallengeTimesMsgResponse extends AbstractJsonGameMessage<ResponseBody> {
    
    public static class ResponseBody {
        private int times;

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }
        
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
