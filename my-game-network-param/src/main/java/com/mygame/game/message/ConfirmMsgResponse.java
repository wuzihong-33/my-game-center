package com.mygame.game.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.ConfirmMsgResponse.ConfirmResponseBody;
@GameMessageMetadata(messageId=1,messageType= EnumMessageType.RESPONSE,serviceId=1)
public class ConfirmMsgResponse extends AbstractJsonGameMessage<ConfirmResponseBody> {
    public static class ConfirmResponseBody{
  
        private String secretKey; //对称加密密钥，客户端需要使用非对称加密私钥解密才能获得。
        public String getSecretKey() {
            return secretKey;
        }
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
        
    }

    @Override
    protected Class<ConfirmResponseBody> getBodyObjClass() {
        return ConfirmResponseBody.class;
    }
}
