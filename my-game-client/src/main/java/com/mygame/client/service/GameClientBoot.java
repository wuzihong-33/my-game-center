package com.mygame.client.service;

import com.mygame.client.service.handler.DispatchGameMessageHandler;
import com.mygame.client.service.handler.TestGameMessageHandler;
import com.mygame.client.service.handler.codec.DecodeHandler;
import com.mygame.client.service.handler.codec.EncodeHandler;
import com.mygame.game.GameMessageService;
import com.mygame.game.messagedispatcher.DispatchGameMessageService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameClientBoot {
    @Autowired
    private GameClientConfig gameClientConfig;
    @Autowired
    private GameMessageService gameMessageService;
    @Autowired
    private DispatchGameMessageService dispatchGameMessageService;
    private Bootstrap serverBootStrap;
    
    private EventLoopGroup eventGroup;
    private Logger logger = LoggerFactory.getLogger(GameClientBoot.class);
    private Channel channel;

    public void launch() {
        if(channel != null) {
            channel.close();
        }
        eventGroup = new NioEventLoopGroup(gameClientConfig.getWorkThreads());
        serverBootStrap = new Bootstrap();
        serverBootStrap.group(eventGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, gameClientConfig.getConnectTimeout() * 1000)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast("EncodeHandler", new EncodeHandler(gameClientConfig));
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 4, 0, 4, -4, 0));
                    ch.pipeline().addLast("DecodeHandler", new DecodeHandler());
//                    ch.pipeline().addLast("responseHandler", new ResponseHandler(gameMessageService));//将响应消息转化为对应的响应对象
                     ch.pipeline().addLast(new TestGameMessageHandler());//测试handler
//                    ch.pipeline().addLast(new IdleStateHandler(150, 60, 200));//如果6秒之内没有消息写出，发送写出空闲事件，触发心跳
//                    ch.pipeline().addLast("HeartbeatHandler",new HeartbeatHandler());//心跳Handler
//                    ch.pipeline().addLast(new DispatchGameMessageHandler(dispatchGameMessageService));// 添加逻辑处理
                }
        });
        String host = gameClientConfig.getDefaultGameGatewayHost();
        int port = gameClientConfig.getDefaultGameGatewayPort();
//        logger.info("程序启动，尝试连接:: {}:{}", host, port);
        ChannelFuture future = null;
        try {
            // 阻塞调用
            future = serverBootStrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (future.isSuccess()) {
            logger.info("连接 address: {}:{} 成功", host, port);
            channel = future.channel();
        } else {
            logger.error("连接 address: {}:{} 失败, cause: {}", host, port, future.cause());
        }
        logger.info("连接 address: {}:{} 成功", host, port);
        channel = future.channel();
        
//        channel = future.channel();
//        future.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (future.isSuccess()) {
//                    logger.info("连接 address: {}:{} 成功", host, port);
//                    channel = future.channel();
//                } else {
//                    logger.error("连接 address: {}:{} 失败, cause: {}", host, port, future.cause());
//                }
//            }
//        });
    }

    public Channel getChannel() {
        return channel;
    }
    public void removeChannel() {
        channel = null;
    }
}
