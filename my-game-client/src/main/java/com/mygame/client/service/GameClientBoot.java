package com.mygame.client.service;


import com.mygame.client.service.handler.DispatchGameMessageHandler;
import com.mygame.client.service.handler.codec.DecodeHandler;
import com.mygame.client.service.handler.codec.EncodeHandler;
import com.mygame.client.service.handler.test.TestGameMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameClientBoot {
    @Autowired
    private GameClientConfig gameClientConfig;
//    @Autowired
//    private GameMessageService gameMessageService;
//    @Autowired
//    private DispatchGameMessageService dispatchGameMessageService;
    private Bootstrap bootStrap;
    
    private EventLoopGroup eventGroup;
    private Logger logger = LoggerFactory.getLogger(GameClientBoot.class);
    private Channel channel;

    public void launch() {
        if(channel != null) {
            channel.close();
        }
        eventGroup = new NioEventLoopGroup(gameClientConfig.getWorkThreads());
        bootStrap = new Bootstrap();
        bootStrap.group(eventGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, gameClientConfig.getConnectTimeout() * 1000)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
//                    ch.pipeline().addLast("EncodeHandler", new EncodeHandler(gameClientConfig));// 添加编码
//                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 4, 0, 4, -4, 0));// 添加解码
//                    ch.pipeline().addLast("DecodeHandler", new DecodeHandler());// 添加解码
//                    ch.pipeline().addLast("responseHandler", new ResponseHandler(gameMessageService));//将响应消息转化为对应的响应对象
                     ch.pipeline().addLast(new TestGameMessageHandler());//测试handler
//                    ch.pipeline().addLast(new IdleStateHandler(150, 60, 200));//如果6秒之内没有消息写出，发送写出空闲事件，触发心跳
//                    ch.pipeline().addLast("HeartbeatHandler",new HeartbeatHandler());//心跳Handler
//                    ch.pipeline().addLast(new DispatchGameMessageHandler(dispatchGameMessageService));// 添加逻辑处理
                }
        });
        
        ChannelFuture future = bootStrap.connect(gameClientConfig.getDefaultGameGatewayHost(), gameClientConfig.getDefaultGameGatewayPort());
        channel = future.channel();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("连接{}:{}成功,channelId:{}", gameClientConfig.getDefaultGameGatewayHost(),
                            gameClientConfig.getDefaultGameGatewayPort(), future.channel().id().asShortText());

                } else {
                    Throwable e = future.cause();
                    logger.error("连接失败-{}", e);
                }
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }
}
