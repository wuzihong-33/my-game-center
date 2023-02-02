package com.mygame.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import com.mygame.db.entity.Arena;
import com.mygame.db.repository.ArenaRepository;
import com.mygame.redis.EnumRedisKey;
@Service
public class ArenaDao extends AbstractDao<Arena, Long>{
    @Autowired
    private ArenaRepository arenaRepository;
    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ARENA;
    }

    @Override
    protected MongoRepository<Arena, Long> getMongoRepository() {
        return arenaRepository;
    }

    @Override
    protected Class<Arena> getEntityClass() {
        return Arena.class;
    }
    
    

}
