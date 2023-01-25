package com.mygame.gateway.server.handler;

import com.mygame.game.message.GatewayMessageCode;
import com.mygame.message.HeartbeatMsgResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.game.common.GameMessagePackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
    private int heartbeatCount = 0;// 心跳计数器，如果一直接收到的是心跳消息，达到一定数量之后，说明客户端一直没有用户操作了，服务器就主动断开连接。
    private int maxHeartbeatCount = 66;// 最大心跳数

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {// 在这里接收channel中的事件信息
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {// 一定时间内，既没有收到客户端信息，则断开连接
                ctx.close();
                logger.debug("连接读取空闲，断开连接，channelId:{}", ctx.channel().id().asShortText());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;// 拦截心跳请求，并处理
        if (gameMessagePackage.getHeader().getMessageId() == GatewayMessageCode.Heartbeat.getMessageId()) {
            logger.debug("收到心跳信息,channel id:{}", ctx.channel().id().asShortText());
            HeartbeatMsgResponse response = new HeartbeatMsgResponse();
            response.getBodyObj().setServerTime(System.currentTimeMillis());// 返回服务器时间
            GameMessagePackage returnPackage = new GameMessagePackage();
            response.getHeader().setClientSeqId(gameMessagePackage.getHeader().getClientSeqId());
            returnPackage.setHeader(response.getHeader());
            returnPackage.setBody(response.body());
            ctx.writeAndFlush(returnPackage);
            this.heartbeatCount++;
            if (this.heartbeatCount > maxHeartbeatCount) {
                ctx.close();
            }
        } else {
            this.heartbeatCount = 0;// 收到非心跳消息之后，重新计数
            ctx.fireChannelRead(msg);
        }
    }
}
