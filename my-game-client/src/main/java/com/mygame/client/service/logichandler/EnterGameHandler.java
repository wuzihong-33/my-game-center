package com.mygame.client.service.logichandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.client.service.handler.GameClientChannelContext;
import com.mygame.game.message.im.IMSendIMMsgeResponse;
import com.mygame.game.message.im.SendIMMsgeResponse;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.game.messagedispatcher.GameMessageMapping;

@GameMessageHandler
public class EnterGameHandler {
    private Logger logger = LoggerFactory.getLogger(EnterGameHandler.class);
//    @GameMessageMapping(EnterGameMsgResponse.class)
//    public void enterGameResponse(EnterGameMsgResponse response,GameClientChannelContext ctx) {
//        logger.debug("进入游戏成功：{}",response.getBodyObj().getNickname());
//    }
//    @GameMessageMapping(BuyArenaChallengeTimesMsgResponse.class)
//    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgResponse response,GameClientChannelContext ctx) {
//        logger.debug("购买竞技场挑战次数成功");
//    }
    
    @GameMessageMapping(SendIMMsgeResponse.class)
    public void chatMsg(SendIMMsgeResponse response,GameClientChannelContext ctx) {
    	logger.info("聊天信息-{}说：{}",response.getBodyObj().getSender(),response.getBodyObj().getChat());
    }

    @GameMessageMapping(IMSendIMMsgeResponse.class)
    public void chatMsgIM(IMSendIMMsgeResponse response,GameClientChannelContext ctx) {
        logger.info("聊天信息-{}说：{}",response.getBodyObj().getSender(),response.getBodyObj().getChat());
    }
}
