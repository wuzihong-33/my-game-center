package com.mygame.controller;

import com.mygame.common.error.GameErrorException;
import com.mygame.common.error.IServerError;
import com.mygame.common.error.TokenException;
import com.mygame.common.utils.JWTUtil;
import com.mygame.common.utils.JWTUtil.TokenBody;
import com.mygame.db.entity.Player;
import com.mygame.db.entity.UserAccount;
import com.mygame.db.entity.UserAccount.ZonePlayerInfo;

import com.mygame.error.GameCenterError;
import com.mygame.http.MessageCode;
import com.mygame.http.request.CreatePlayerParam;
import com.mygame.http.request.LoginParam;
import com.mygame.http.response.LoginResult;
import com.mygame.http.response.ResponseEntity;
import com.mygame.service.PlayerService;
import com.mygame.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/request")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    private Logger logger = LoggerFactory.getLogger((UserController.class));
    
    
    @GetMapping("/test")
    public String test() {
        logger.debug("success");
        return "success";
    }
    
    @PostMapping(MessageCode.USER_LOGIN)
    public ResponseEntity<LoginResult> login(@RequestBody LoginParam loginParam) {
        loginParam.checkParam();
        // 去第三方提供的服务器地址验证openId
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

    @PostMapping(MessageCode.CREATE_PLAYER)
    public ResponseEntity<ZonePlayerInfo> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) {
        param.checkParam();
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
    
}
