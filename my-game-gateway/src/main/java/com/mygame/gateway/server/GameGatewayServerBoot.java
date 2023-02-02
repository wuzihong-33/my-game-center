package com.mygame.gateway.server;

import java.util.concurrent.TimeUnit;

import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.game.GameMessageService;
import com.mygame.gateway.server.handler.*;
import com.mygame.gateway.server.handler.codec.DecodeHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.RateLimiter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import com.mygame.gateway.server.handler.codec.EncodeHandler;


@Service
public class GameGatewayServerBoot {
    private Logger logger = LoggerFactory.getLogger(GameGatewayServerBoot.class);
    @Autowired
    private GatewayServerConfig serverConfig;// 注入网关服务配置
    @Autowired
    private GameMessageService gameMessageService;
    @Autowired
    private PlayerServiceInstance playerServiceInstance;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate = null;
    @Autowired
    private ApplicationContext applicationContext;
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    private RateLimiter globalRateLimiter;
    
    public void startServer() {
//        globalRateLimiter = RateLimiter.create(serverConfig.getGlobalRequestPerSecond());
        bossGroup = new NioEventLoopGroup(serverConfig.getBossThreadCount());
        workerGroup = new NioEventLoopGroup(serverConfig.getWorkThreadCount());
        int port = this.serverConfig.getPort();
        // bug出在stop server了
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(createChannelInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap
                .bind(port)
                .addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            logger.info("绑定端口: {} 成功", port);
//                                logger.info("收到连接, {}", future.get());
//                                logger.info("收到连接, remoteAddress: {}", channelFuture.channel().remoteAddress());
                        } else {
                            logger.info("绑定端口失败, cause: {}", future.get());
//                                future.channel().closeFuture().sync();
                        }
                    }
                });
//        finally {
//            stopServer();
//        }
    }
    
    public void stopServer() {// 优雅的关闭服务
        int quietPeriod = 5;
        int timeout = 30;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        workerGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
        bossGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
        logger.info("=========stop server=======");
    }

    private ChannelInitializer<Channel> createChannelInitializer() {
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast("EncodeHandler", new EncodeHandler(serverConfig));
                p.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, -4, 0));
                p.addLast("DecodeHandler", new DecodeHandler());
//                p.addLast("ConfirmHandler", new ConfirmHandler(serverConfig, channelService,kafkaTemplate,applicationContext));
//                // 添加限流handler
//                p.addLast("RequestLimit", new RequestRateLimiterHandler(globalRateLimiter, serverConfig.getRequestPerSecond()));
                p.addLast(new IdleStateHandler(serverConfig.getReaderIdleTimeSeconds(), serverConfig.getWriterIdleTimeSeconds(), serverConfig.getAllIdleTimeSeconds()));
                p.addLast("HeartbeatHandler", new HeartbeatHandler());
                p.addLast(new DispatchGameMessageHandler(kafkaTemplate, playerServiceInstance, serverConfig));
//                p.addLast(new TestGameMessageHandler(gameMessageService));// 测试
            }
        };
        return channelInitializer;
    }
}
