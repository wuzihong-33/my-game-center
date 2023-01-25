package com.mygame.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
@GameMessageMetadata(messageId=1,messageType= EnumMessageType.RESPONSE,serviceId=1)
public class ConfirmMsgResponse extends AbstractJsonGameMessage<ConfirmMsgResponse.ConfirmResponseBody> {
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
