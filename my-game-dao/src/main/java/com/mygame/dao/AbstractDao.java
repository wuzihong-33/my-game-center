package com.mygame.dao;

import com.mygame.redis.EnumRedisKey;
//import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.alibaba.fastjson.JSON;

import java.time.Duration;
import java.util.Optional;

public abstract class AbstractDao<Entity, ID> {
    private static final String RedisDefaultValue = "#null#";
    @Autowired
    protected StringRedisTemplate redisTemplate;
    
    protected abstract EnumRedisKey getRedisKey();
    protected abstract MongoRepository<Entity, ID> getMongoRepository();
    protected abstract Class<Entity> getEntityClass();
    
    public Optional<Entity> findById(ID id) {
        String key = this.getRedisKey().getKey();
        String value = redisTemplate.opsForValue().get(key);
        Entity entity = null;
        if (value == null) { //  redis中没有缓存
            key = key.intern();//保证字符串在常量池中
            synchronized (key) {
                value = redisTemplate.opsForValue().get(key); // 二次获取一下
                if (value == null) { // 从数据库中获取
                    Optional<Entity> op = this.getMongoRepository().findById(id);
                    if (op.isPresent()) {
                        entity = op.get();
                        this.updateRedis(entity, id);
                    } else {
                        this.setRedisDefaultValue(key);//设置默认值，防止缓存穿透
                    }
                } else if (value.equals(RedisDefaultValue)) {
                    value = null;
                }
            }
        } else if (value.equals(RedisDefaultValue)) {
            value = null;
        }
        if (value != null) {
            entity = JSON.parseObject(value, this.getEntityClass());
        }
        
        return Optional.ofNullable(entity);
    }
    
    private void setRedisDefaultValue(String key) {
        Duration duration = Duration.ofMinutes(1);
        redisTemplate.opsForValue().set(key, RedisDefaultValue, duration);
    }

    private void updateRedis(Entity entity, ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = JSON.toJSONString(entity);
        Duration duration = this.getRedisKey().getTimeout();
        if (duration != null) {
            redisTemplate.opsForValue().set(key, value, duration);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void saveOrUpdate(Entity entity, ID id) {
        this.updateRedis(entity, id);
        this.getMongoRepository().save(entity);
    }

    public void saveOrUpdateToDB(Entity entity) {
        this.getMongoRepository().save(entity);
    }
    
    public void saveOrUpdateToRedis(Entity entity,ID id) {
        this.updateRedis(entity, id);
    }
    
    
}
