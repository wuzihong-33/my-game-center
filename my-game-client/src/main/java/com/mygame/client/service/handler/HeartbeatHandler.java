package com.mygame.client.service.handler;

import com.mygame.game.message.HeartbeatMsgRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private boolean confirmSuccess;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 在一段时间内没有向服务器写数据了，则发送心跳包
            if (event.state() == IdleState.WRITER_IDLE) {
                if (confirmSuccess) {// 连接认证成功
                    HeartbeatMsgRequest request = new HeartbeatMsgRequest();
                    ctx.writeAndFlush(request);
                }
            }
        }
    }
    public void setConfirmSuccess(boolean confirmSuccess) {
        this.confirmSuccess = confirmSuccess;
    }
}
