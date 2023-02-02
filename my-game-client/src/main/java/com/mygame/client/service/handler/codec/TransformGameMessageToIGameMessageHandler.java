package com.mygame.client.service.handler.codec;

import com.mygame.game.GameMessageService;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.game.common.IGameMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 自动将无差别的GameMessage根据messageId转换成具体的请求对象
 */
public class TransformGameMessageToIGameMessageHandler extends ChannelInboundHandlerAdapter {
    private GameMessageService gameMessageService;

    public TransformGameMessageToIGameMessageHandler(GameMessageService gameMessageService) {
        this.gameMessageService = gameMessageService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage messagePackage = (GameMessagePackage) msg;
        int messageId = messagePackage.getHeader().getMessageId();
        IGameMessage gameMessage = gameMessageService.getResponseInstanceByMessageId(messageId);
        gameMessage.setHeader(messagePackage.getHeader());
        gameMessage.read(messagePackage.getBody());
        ctx.fireChannelRead(gameMessage);
    }
}
