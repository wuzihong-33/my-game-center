package com.mygame.center.service;

import com.mygame.common.error.IServerError;
import com.mygame.common.utils.CommonField;
import com.mygame.dao.UserAccountDao;
import com.mygame.db.entity.UserAccount;
import com.mygame.http.request.LoginParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserAccountDao userAccountDao;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public IServerError verifySdkToken(String openId, String token) {
        // 这里调用sdk服务端验证接口
        return null;
    }
    
    public UserAccount login(LoginParam loginParam) {
        String openId = loginParam.getOpenId();
        openId.intern(); // 确保放到常量池
        synchronized (openId) {  //防止用户点击多次注册多次
            Optional<UserAccount> op = userAccountDao.findById(openId);
            UserAccount userAccount = null;
            if (!op.isPresent()) {
                userAccount = this.register(loginParam);// 自动执行注册
            } else {
                userAccount = op.get();
            }
            return userAccount;
        }
    }
    
    private UserAccount register(LoginParam loginParam) {
        long userId = userAccountDao.getNextUserId();// 使用redis自增保证userId全局唯一
        UserAccount userAccount = new UserAccount();
        userAccount.setOpenId(loginParam.getOpenId());
        userAccount.setCreateTime(System.currentTimeMillis());
        userAccount.setUserId(userId);
        this.updateUserAccount(userAccount);
        logger.debug("user {} 注册成功", userAccount);

        return userAccount;
    }
    
    public void updateUserAccount(UserAccount userAccount) {
        this.userAccountDao.saveOrUpdate(userAccount, userAccount.getOpenId());
    }
    
    public UserAccount getUserAccountByOpenId(String openId) {
        return userAccountDao.findById(openId).get();
    }
    
    public String getOpenIdFromHeader(HttpServletRequest request) {
        return request.getHeader(CommonField.OPEN_ID);
    }
    
}
