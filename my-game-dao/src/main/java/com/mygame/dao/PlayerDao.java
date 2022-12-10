package com.mygame.dao;

import com.mygame.db.entity.Player;
import com.mygame.db.repository.PlayerRepository;
import com.mygame.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerDao extends AbstractDao<Player, Long> {
    @Autowired
    private PlayerRepository playerRepository;
    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.PLAYER_INFO;
    }

    @Override
    protected MongoRepository<Player, Long> getMongoRepository() {
        return playerRepository;
    }

    @Override
    protected Class<Player> getEntityClass() {
        return Player.class;
    }
}
