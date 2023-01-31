package com.mygame.client.service.logichandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.client.service.handler.GameClientChannelContext;
import com.mygame.game.message.FirstMsgResponse;
//import com.mygame.game.message.SecondMsgResponse;
//import com.mygame.game.message.ThirdMsgResponse;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.game.messagedispatcher.GameMessageMapping;

@GameMessageHandler
public class TestMessageHandler {
    private Logger logger = LoggerFactory.getLogger(TestMessageHandler.class);
    
    @GameMessageMapping(FirstMsgResponse.class)
    public void firstMessage(FirstMsgResponse response,GameClientChannelContext ctx) {
        logger.info("收到服务器响应:{}",response.getServerTime());
    }
//    @GameMessageMapping(SecondMsgResponse.class)
//    public void secondMessage(SecondMsgResponse response,GameClientChannelContext ctx) {
//        logger.info("second msg response :{}",response.getBodyObj().getResult1());
//    }
//    @GameMessageMapping(ThirdMsgResponse.class)
//    public void thirdMessage(ThirdMsgResponse response,GameClientChannelContext ctx) {
//        logger.info("third msg response:{}",response.getResponseBody().getValue1());
//    }
}
