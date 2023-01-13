package com.mygame.gateway.server.hander;

import com.mygame.common.utils.JWTUtil;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ConfirmHandler extends ChannelInboundHandlerAdapter {
    private JWTUtil.TokenBody tokenBody;

    public JWTUtil.TokenBody getTokenBody() {
        return tokenBody;
    }

}
