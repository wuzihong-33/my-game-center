package com.mygame.client.command;

import com.mygame.common.utils.NettyUtils;
import com.mygame.game.message.ConfirmMsgRequest;
import com.mygame.http.request.FirstMsgRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import com.mygame.client.service.GameClientBoot;
import com.mygame.client.service.GameClientConfig;
//import com.mygame.game.message.ConfirmMsgRequest;
//import com.mygame.game.message.FirstMsgRequest;
//import com.mygame.game.message.SecondMsgRequest;
//import com.mygame.game.message.ThirdMsgRequest;
//import com.mygame.game.message.body.ThirdMsgBody.ThirdMsgRequestBody;
//import com.mygame.game.message.xinyue.BuyArenaChallengeTimesMsgRequest;
//import com.mygame.game.message.xinyue.EnterGameMsgRequest;

/**
 * 指令格式，比如connectServer方法，那么就对应connect-server host port
 */
@ShellComponent
public class GameClientCommand {
    private Logger logger = LoggerFactory.getLogger(GameClientCommand.class);
    @Autowired
    private GameClientBoot gameClientBoot;
    @Autowired
    private GameClientConfig gameClientConfig;

    /**
     * 默认去连接游戏网关
     * @param host
     * @param port
     */
    @ShellMethod("连接服务器，格式：connect-server  [host] [port]")
    public void connectServer(@ShellOption(defaultValue= "")String host,@ShellOption(defaultValue = "0")int port) {
        if(!host.isEmpty()) {//如果默认的host不为空，说明是连接指定的host，如果没有指定host，使用配置中的默认host和端口
            if(port == 0) {
                logger.error("请输入服务器端口号");
                return;
            }
            gameClientConfig.setDefaultGameGatewayHost(host);
            gameClientConfig.setDefaultGameGatewayPort(port);
        }
        gameClientBoot.launch();// 启动客户端并连接游戏网关
    }
    
    @ShellMethod("关闭连接")
    public void close() {
        gameClientBoot.getChannel().close();
        gameClientBoot.removeChannel();
    }
    
    @ShellMethod("发送测试消息，格式：send-msg 消息号")
    public void sendMsg(int messageId) {
        // 自动去连接
        if (gameClientBoot.getChannel() == null) {
            String host = gameClientConfig.getDefaultGameGatewayHost();
            int port = gameClientConfig.getDefaultGameGatewayPort();
            this.connectServer(host, port);
            logger.info("url: {}", NettyUtils.getRemoteUrl(gameClientBoot.getChannel()));
        }
       if(messageId == 1) {//发送认证请求
           ConfirmMsgRequest request = new ConfirmMsgRequest();
           request.getBodyObj().setToken(gameClientConfig.getGatewayToken());
           gameClientBoot.getChannel().writeAndFlush(request);
       }
       if(messageId == 10001) {
            // 向服务器发送一条消息
            FirstMsgRequest request = new FirstMsgRequest();
            request.setValue("Hello,server !!");
            request.getHeader().setClientSendTime(System.currentTimeMillis());
            gameClientBoot.getChannel().writeAndFlush(request);
       }
       logger.info("消息写出成功, messageId: {}", messageId);
//       if(messageId == 10002) {
//           SecondMsgRequest request = new SecondMsgRequest();
//           request.getBodyObj().setValue1("你好，这是测试请求");
//           request.getBodyObj().setValue2(System.currentTimeMillis());
//           gameClientBoot.getChannel().writeAndFlush(request);
//       }
//       if(messageId == 10003) {
//           ThirdMsgRequest request = new ThirdMsgRequest();
//           ThirdMsgRequestBody requestBody = ThirdMsgRequestBody.newBuilder().setValue1("我是Protocol Buffer序列化的").setValue2(System.currentTimeMillis()).build();
//           request.setRequestBody(requestBody);
//           gameClientBoot.getChannel().writeAndFlush(request);
//       }
//       if(messageId == 201) {//进入游戏请求
//           EnterGameMsgRequest request = new EnterGameMsgRequest();
//           gameClientBoot.getChannel().writeAndFlush(request);
//       }
//       if(messageId == 210) {
//           BuyArenaChallengeTimesMsgRequest request = new BuyArenaChallengeTimesMsgRequest();
//           gameClientBoot.getChannel().writeAndFlush(request);
//       }
    }
}
