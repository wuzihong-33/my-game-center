package com.mygame.center.service;

import com.mygame.common.error.GameErrorException;
import com.mygame.common.utils.JWTUtil;
import com.mygame.dao.PlayerDao;
import com.mygame.db.entity.Player;
import com.mygame.error.GameCenterError;
import com.mygame.http.request.SelectGameGatewayParam;
import com.mygame.redis.EnumRedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PlayerDao playerDao;
    private Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private String getNickNameRedisKey(String zoneId,String nickName) {
        String key = EnumRedisKey.PLAYER_NICKNAME.getKey(zoneId + "_" + nickName);
        return key;
    }


    private boolean saveNickNameIfAbsent(String zoneId, String nickName) {
        String key = this.getNickNameRedisKey(zoneId, nickName);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "0"); // value先使用一个默认值
        if (result == null) {
            return false;
        }
        return result;
    }

    private void updatePlayerIdForNickName(String zoneId, String nickName, long playerId) {
        String key = this.getNickNameRedisKey(zoneId, nickName);
        this.redisTemplate.opsForValue().set(key, String.valueOf(playerId));
    }
    
    
    public Player createPlayer(String zoneId, String nickName) {
        boolean saveNickName = this.saveNickNameIfAbsent(zoneId, nickName); // 此处可以保证只有第一个请求可以成功，之后的都失败
        if (!saveNickName) {
            throw new GameErrorException.Builder(GameCenterError.NICKNAME_EXIST).message(nickName).build();
        }
        long playerId = this.nextPlayerId(zoneId);
        Player player = new Player();
        player.setPlayerId(playerId);
        player.setNickName(nickName);
        player.setLastLoginTime(System.currentTimeMillis());
        player.setCreateTime(player.getLastLoginTime());
        this.updatePlayerIdForNickName(zoneId, nickName, playerId);// 再次更新一下nickName对应的playerId
        playerDao.saveOrUpdate(player, playerId);
        logger.info("创建角色成功,{}", player);
        return player;
    }
    private long nextPlayerId(String zoneId) {
        String key = EnumRedisKey.PLAYER_ID_INCR.getKey(zoneId);
        return redisTemplate.opsForValue().increment(key);
    }
    
    public String createToken(SelectGameGatewayParam param, String gatewayIp, String publicKey) {
        String openId = param.getOpenId();
        String zoneId = param.getZoneId();
        long userId = param.getUserId();
        long playerId = param.getPlayerId();

        String token = JWTUtil.getUserToken(openId, userId);
        return token;
    }
    
}
