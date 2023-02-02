package com.mygame.xinyue.logic.handler;

import com.mygame.db.entity.manager.PlayerManager;
import com.mygame.game.message.im.SendIMMsgRequest;
import com.mygame.game.message.im.SendIMMsgeResponse;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.game.messagedispatcher.GameMessageMapping;
import com.mygame.gateway.message.context.GatewayMessageContext;

@GameMessageHandler
public class GameIMHandler {
//    @GameMessageMapping(SendIMMsgRequest.class)
//    public void sendMsg(SendIMMsgRequest request,GatewayMessageContext<PlayerManager> ctx) {
//        String chat = request.getBodyObj().getChat();
//        String sender = ctx.getPlayerManager().getPlayer().getNickName();
//        SendIMMsgeResponse response = new SendIMMsgeResponse();
//        response.getBodyObj().setChat(chat);
//        response.getBodyObj().setSender(sender);
//        ctx.broadcastMessage(response);
//    }
}
