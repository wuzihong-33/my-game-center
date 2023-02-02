package com.mygame.center.controller;

import com.mygame.center.dataconfig.GameGatewayInfo;
import com.mygame.center.service.GameGatewayService;
import com.mygame.common.error.GameErrorException;
import com.mygame.common.error.IServerError;
import com.mygame.common.utils.JWTUtil;
import com.mygame.db.entity.Player;
import com.mygame.db.entity.UserAccount;
import com.mygame.db.entity.UserAccount.ZonePlayerInfo;

import com.mygame.http.MessageCode;
import com.mygame.http.request.CreatePlayerParam;
import com.mygame.http.request.LoginParam;
import com.mygame.http.request.SelectGameGatewayParam;
import com.mygame.http.response.GameGatewayInfoMsg;
import com.mygame.http.response.LoginResult;
import com.mygame.http.response.ResponseEntity;
import com.mygame.center.service.PlayerService;
import com.mygame.center.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/request")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private GameGatewayService gameGatewayService;
    @Autowired
    private PlayerService playerService;
    
    @GetMapping("/test")
    public String test() {
        logger.debug("/test request success");
        return "success";
    }

    // {"openId":"012345", "token": "aaabbbccc"}
    // http://localhost:5003/request/10001
    @PostMapping(MessageCode.USER_LOGIN)
    public ResponseEntity<LoginResult> login(@RequestBody LoginParam loginParam) {
        loginParam.checkParam();
        // 去第三方提供的服务器地址验证openId
        // 这里的token，是sdk带过来的token，非服务器自己生成的
        IServerError serverError = userService.verifySdkToken(loginParam.getOpenId(), loginParam.getToken());
        if (serverError != null) {
            throw GameErrorException.newBuilder(serverError).build();
        }
        // 执行正常的登录流程
        UserAccount userAccount = userService.login(loginParam);
        LoginResult loginResult = new LoginResult();
        loginResult.setUserId(userAccount.getUserId());
        String token = JWTUtil.getUserToken(userAccount.getOpenId(), userAccount.getUserId());
        loginResult.setToken(token);
        logger.debug("user {} 登陆成功", userAccount);
        return new ResponseEntity<LoginResult>(loginResult);
    }

    /**
     * 测试用例：
     * header加上openId：012345
     * body: {"zoneId":"012345", "nickName": "lll"}
     */
    @PostMapping(MessageCode.CREATE_PLAYER)
    public ResponseEntity<ZonePlayerInfo> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) {
        param.checkParam();
        /**
        String token = request.getHeader("token"); // 从http包头里面获取token的值
        if (token == null) {
            throw GameErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
        }
        TokenBody tokenBody ;
        try {
            tokenBody = JWTUtil.getTokenBody(token);
        } catch (TokenException e) {
            throw GameErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
        }
        String openId = tokenBody.getOpenId();
        */
        // 在web网关处统一做token权限验证，服务中心则可以不再对token进行验证
        String openId = userService.getOpenIdFromHeader(request);
        UserAccount userAccount = userService.getUserAccountByOpenId(openId);

        String zoneId = param.getZoneId();
        ZonePlayerInfo zonePlayerInfo;
        if (!userAccount.exitZonePlayerInfo(zoneId)) {
            // 执行创建角色逻辑
            Player player = playerService.createPlayer(zoneId, param.getNickName());
            zonePlayerInfo = new ZonePlayerInfo(player.getPlayerId(), System.currentTimeMillis());
            userAccount.addZonePlayerInfo(zoneId, zonePlayerInfo);
            userService.updateUserAccount(userAccount);
        } else {
            zonePlayerInfo = userAccount.getZonePlayerInfo(zoneId);
        }
        ResponseEntity<ZonePlayerInfo> response = new ResponseEntity<ZonePlayerInfo>(zonePlayerInfo);
        return response;
    }

    /**
     * {
     *     "openId":"test_openId",
     *     "playerId":1,
     *     "userId":1,
     *     "zoneId":"1"
     * }
     */
    @PostMapping(MessageCode.SELECT_GAME_GATEWAY)
    public Object selectGameGateway(@RequestBody SelectGameGatewayParam param) throws Exception {
        param.checkParam();
        long playerId = param.getPlayerId();
        GameGatewayInfo gameGatewayInfo = gameGatewayService.getGameGatewayInfo(playerId);
        GameGatewayInfoMsg gameGatewayInfoMsg = new GameGatewayInfoMsg(gameGatewayInfo.getId(), gameGatewayInfo.getIp(),
                gameGatewayInfo.getPort());
//        Map<String, Object> keyPair = RSAUtils.genKeyPair();// 生成rsa的公钥和私钥
//        byte[] publickKeyBytes = RSAUtils.getPublicKey(keyPair);// 获取公钥
//        String publickKey = Base64Utils.encodeToString(publickKeyBytes);// 为了方便传输，对bytes数组进行一下base64编码
        String token = playerService.createToken(param, gameGatewayInfo.getIp(), null);// 根据这些参数生成token
        gameGatewayInfoMsg.setToken(token);
//        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);
//        String privateKey = Base64Utils.encodeToString(privateKeyBytes);
//        gameGatewayInfoMsg.setRsaPrivateKey(privateKey);// 给客户端返回私钥
        logger.debug("player {} 获取游戏网关信息成功：{}", playerId, gameGatewayInfoMsg);
        ResponseEntity<GameGatewayInfoMsg> responseEntity = new ResponseEntity<>(gameGatewayInfoMsg);
        return responseEntity;
    }
    
}
