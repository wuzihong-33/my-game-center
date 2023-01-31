package com.mygame.client.service;

import com.mygame.common.utils.CommonField;
import com.mygame.common.utils.GameHttpClient;
import com.mygame.game.messagedispatcher.DispatchGameMessageService;
import com.mygame.http.MessageCode;
import com.mygame.http.request.SelectGameGatewayParam;
import com.mygame.http.response.GameGatewayInfoMsg;
import com.mygame.http.response.ResponseEntity;
import org.apache.logging.log4j.core.config.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 负责客户端启动后做一些初始化操作
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Service
public class GameClientInitService {
    private Logger logger = LoggerFactory.getLogger(GameClientInitService.class);
    @Autowired
    private GameClientConfig gameClientConfig;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        // 扫描加载要处理的消息类型
        DispatchGameMessageService.scanGameMessages(applicationContext, 0, "com.mygame.client");
        this.selectGateway();
    }

    private void selectGateway() {
        if (gameClientConfig.isUseGameCenter()) {
            // 构造请求参数
            SelectGameGatewayParam param = new SelectGameGatewayParam();
            param.setOpenId("test_openId");
            param.setPlayerId(1);
            param.setUserId(1);
            param.setZoneId("1");
            GameGatewayInfoMsg gateGatewayMsg = this.getGameGatewayInfoFromGameCenter(param);
            // 替换默认的游戏网关信息
            // 疑惑：config实例还能被修改？？
            if (gateGatewayMsg != null) {
                gameClientConfig.setDefaultGameGatewayHost(gateGatewayMsg.getIp());
                gameClientConfig.setDefaultGameGatewayPort(gateGatewayMsg.getPort());
                gameClientConfig.setGatewayToken(gateGatewayMsg.getToken());
                gameClientConfig.setRsaPrivateKey(gateGatewayMsg.getRsaPrivateKey());
            } else {
                logger.debug("从游戏服务中心得到的游戏网关信息为空, 将使用默认网关信息");
                // throw new IllegalArgumentException("从服务中心获取游戏网关信息失败，没有可使用的游戏网关信息");
            }
        }
    }
    
    /**
     * 请求游戏中心获取游戏网关信息
     * @param selectGameGatewayParam
     * @return
     */
    public GameGatewayInfoMsg getGameGatewayInfoFromGameCenter(SelectGameGatewayParam selectGameGatewayParam) {
        String uri = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH
                + MessageCode.SELECT_GAME_GATEWAY;
        String response = GameHttpClient.post(uri, selectGameGatewayParam);
        if (response == null) {
            logger.warn("从游戏服务中心[{}]获取游戏网关信息失败", uri);
            return null;
        }
        ResponseEntity<GameGatewayInfoMsg> responseEntity = ResponseEntity.parseObject(response, GameGatewayInfoMsg.class);
        GameGatewayInfoMsg gateGatewayMsg = responseEntity.getData();
        return gateGatewayMsg;
    }
}
