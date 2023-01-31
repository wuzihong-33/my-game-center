package com.mygame.client.service.logichandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import com.mygame.client.service.GameClientConfig;
import com.mygame.client.service.handler.GameClientChannelContext;
//import com.mygame.client.service.handler.HeartbeatHandler;
import com.mygame.client.service.handler.codec.DecodeHandler;
import com.mygame.client.service.handler.codec.EncodeHandler;
//import com.mygame.common.utils.GameBase64Utils;
import com.mygame.common.utils.GameTimeUtil;
import com.mygame.common.utils.RSAUtils;
import com.mygame.game.message.ConfirmMsgResponse;
import com.mygame.game.message.HeartbeatMsgResponse;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.game.messagedispatcher.GameMessageMapping;

@GameMessageHandler
public class SystemMessageHandler {
    @Autowired
    private GameClientConfig gameClientConfig;
    private static Logger logger = LoggerFactory.getLogger(SystemMessageHandler.class);

//    @GameMessageMapping(ConfirmMsgResponse.class)
//    public void confirmResponse(ConfirmMsgResponse response, GameClientChannelContext ctx) {
//        String encryptAesKey = response.getBodyObj().getSecretKey();
//        byte[] content = Base64Utils.decodeFromString(encryptAesKey);
//        try {
//            byte[] privateKey = GameBase64Utils.decodeFromString(gameClientConfig.getRsaPrivateKey());
//            byte[] valueBytes = RSAUtils.decryptByPrivateKey(content, privateKey);
//            String value = new String(valueBytes);// 得到明文的aes加密密钥
//            DecodeHandler decodeHandler = (DecodeHandler) ctx.getChannel().pipeline().get("DecodeHandler");
//            decodeHandler.setAesScreteKey(value);// 把密钥给解码Handler
//            EncodeHandler encodeHandler = (EncodeHandler) ctx.getChannel().pipeline().get("EncodeHandler");
//            encodeHandler.setAesScreteKey(value);// 把密钥给编码Handler
//            HeartbeatHandler heartbeatHandler = (HeartbeatHandler) ctx.getChannel().pipeline().get("HeartbeatHandler");
//            heartbeatHandler.setConfirmSuccess(true);
//            logger.debug("连接认证成功,channelId:{}",ctx.getChannel().id().asShortText());
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    @GameMessageMapping(HeartbeatMsgResponse.class)
    public void heartbeatResponse(HeartbeatMsgResponse response,GameClientChannelContext ctx) {
        logger.debug("服务器心跳返回，当前服务器时间：{}",GameTimeUtil.getStringDate(response.getBodyObj().getServerTime()));
    }
    
}
