package com.mygame.controller;

import com.mygame.common.error.GameErrorException;
import com.mygame.common.error.IServerError;
import com.mygame.common.utils.JWTUtil;
import com.mygame.db.entity.UserAccount;
import com.mygame.http.MessageCode;
import com.mygame.http.request.LoginParam;
import com.mygame.http.response.LoginResult;
import com.mygame.http.response.ResponseEntity;
import com.mygame.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request")
public class UserController {
    @Autowired
    private UserService userService;
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
    
}
