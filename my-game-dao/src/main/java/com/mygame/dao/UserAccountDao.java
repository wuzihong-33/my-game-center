package com.mygame.dao;

import com.mygame.db.entity.UserAccount;
import com.mygame.db.repository.UserAccountRepository;
import com.mygame.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserAccountDao extends AbstractDao<UserAccount, String> {
    @Autowired
    private UserAccountRepository repository;   // 实际具有io能力
    
    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.USER_ACCOUNT;
    }

    @Override
    protected MongoRepository<UserAccount, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<UserAccount> getEntityClass() {
        return UserAccount.class;
    }
    
    public long getNextUserId() {
        String key = EnumRedisKey.USER_ID_INCR.getKey();
        long userId = redisTemplate.opsForValue().increment(key);
        return userId;
    }
}
