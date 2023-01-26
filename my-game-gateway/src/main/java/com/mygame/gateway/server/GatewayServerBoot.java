package com.mygame.gateway.server;

import java.util.concurrent.TimeUnit;

import com.mygame.gateway.server.handler.ConfirmHandler;
import com.mygame.gateway.server.handler.DispatchGameMessageHandler;
import com.mygame.gateway.server.handler.HeartbeatHandler;
import com.mygame.gateway.server.handler.RequestRateLimiterHandler;
import com.mygame.gateway.server.handler.codec.DecodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.RateLimiter;
import com.mygame.common.cloud.PlayerServiceInstance;
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
public class GatewayServerBoot {
    @Autowired
    private GatewayServerConfig serverConfig;// 注入网关服务配置
    @Autowired
    private PlayerServiceInstance playerServiceInstance;
    @Autowired
    private ChannelService channelService;
//    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate = null;
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    private Logger logger = LoggerFactory.getLogger(GatewayServerBoot.class);
    private RateLimiter globalRateLimiter;
    @Autowired
    private ApplicationContext applicationContext;

    public void startServer() {
//        globalRateLimiter = RateLimiter.create(serverConfig.getGlobalRequestPerSecond());
        bossGroup = new NioEventLoopGroup(serverConfig.getBossThreadCount());
        workerGroup = new NioEventLoopGroup(serverConfig.getWorkThreadCount());
        int port = this.serverConfig.getPort();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(createChannelInitializer());
            logger.info("开始启动服务，端口:{}", serverConfig.getPort());
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
            stopServer();
        }
    }

    private ChannelInitializer<Channel> createChannelInitializer() {
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast("EncodeHandler", new EncodeHandler());
                p.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, -4, 0));// 添加拆包
                p.addLast("DecodeHandler", new DecodeHandler());// 添加解码
                p.addLast("ConfirmHandler", new ConfirmHandler());
                // 添加限流handler
                p.addLast("RequestLimit", new RequestRateLimiterHandler(globalRateLimiter, serverConfig.getRequestPerSecond()));

                int readerIdleTimeSeconds = serverConfig.getReaderIdleTimeSeconds();
                int writerIdleTimeSeconds = serverConfig.getWriterIdleTimeSeconds();
                int allIdleTimeSeconds = serverConfig.getAllIdleTimeSeconds();
                p.addLast(new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
                p.addLast("HeartbeatHandler", new HeartbeatHandler());
                p.addLast(new DispatchGameMessageHandler(kafkaTemplate, playerServiceInstance, serverConfig));
                // p.addLast(new TestGameMessageHandler(gameMessageService));//添加业务实现
            }
        };
        return channelInitializer;
    }

    public void stopServer() {// 优雅的关闭服务
        int quietPeriod = 5;
        int timeout = 30;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        workerGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
        bossGroup.shutdownGracefully(quietPeriod, timeout, timeUnit);
    }
}
