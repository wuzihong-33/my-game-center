package com.mygame.gateway.server.handler;

import com.mygame.common.utils.NettyUtils;
import com.mygame.http.request.FirstMsgRequest;
import com.mygame.http.response.FirstMsgResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.game.GameMessageService;
import com.mygame.game.common.GameMessagePackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 测试handler
 */
public class TestGameMessageHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(TestGameMessageHandler.class);
    @Autowired
    private GameMessageService messageService;

    public TestGameMessageHandler(GameMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int messageId = gameMessagePackage.getHeader().getMessageId();
        if (messageId == 10001) {
            FirstMsgRequest request = new FirstMsgRequest();
            request.read(gameMessagePackage.getBody());
            logger.debug("接收到客户端消息：{}, url: {}", request.getValue(), NettyUtils.getRemoteUrl(ctx.channel()));
            FirstMsgResponse response = new FirstMsgResponse();
            response.setServerTime(System.currentTimeMillis());
            GameMessagePackage returnPackage = new GameMessagePackage();
            returnPackage.setHeader(response.getHeader());
            returnPackage.setBody(response.body());
            ctx.writeAndFlush(returnPackage);
        } 
        
//        else if (messageId == 10002) {
//            SecondMsgRequest request = (SecondMsgRequest) messageService.getRequestInstanceByMessageId(messageId);
//            request.read(gameMessagePackage.getBody());
//            logger.debug("收到request3:{}", request);
//            SecondMsgResponse response = new SecondMsgResponse();
//            response.getBodyObj().setResult1(System.currentTimeMillis());
//            response.getBodyObj().setResult2("服务器回复");
//            GameMessagePackage returnPackage = new GameMessagePackage();
//            returnPackage.setHeader(response.getHeader());
//            returnPackage.setBody(response.body());
//            ctx.writeAndFlush(returnPackage);
//        } else if(messageId == 10003) {
//            ThirdMsgRequest request = (ThirdMsgRequest) messageService.getRequestInstanceByMessageId(messageId);
//            request.read(gameMessagePackage.getBody());//反序列化客户端的请求消息
//            logger.debug("收到request4:{}",request.getRequestBody().getValue1());
//            ThirdMsgResponse response = new ThirdMsgResponse();//构造服务器响应的对应。
//            ThirdMsgResponseBody responseBody = ThirdMsgResponseBody.newBuilder().setValue1("服务器收到protobuf").setValue2(3).setValue3("服务器返回").build();
//            response.setResponseBody(responseBody);//设置服务器返回的数据
//            GameMessagePackage returnPackage = new GameMessagePackage();
//            returnPackage.setHeader(response.getHeader());
//            returnPackage.setBody(response.body());
//            ctx.writeAndFlush(returnPackage);//向客户端返回消息。
//        }
    }
}
