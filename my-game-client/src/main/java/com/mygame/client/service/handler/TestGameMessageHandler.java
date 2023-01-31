package com.mygame.client.service.handler;

import com.mygame.http.response.FirstMsgResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单打印响应到控制台
 */
public class TestGameMessageHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(TestGameMessageHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FirstMsgResponse) {
            FirstMsgResponse response = (FirstMsgResponse)msg;
            logger.info("收到服务器响应:{}",response.getServerTime());
        }
//        if(msg instanceof SecondMsgResponse) {
//            SecondMsgResponse response = (SecondMsgResponse) msg;
//            logger.info("second msg response :{}",response.getBodyObj().getResult1());
//        }
//        if(msg instanceof ThirdMsgResponse) {
//            ThirdMsgResponse response = (ThirdMsgResponse)msg;
//            logger.info("third msg response:{}",response.getResponseBody().getValue1());
//        }
    }
}
