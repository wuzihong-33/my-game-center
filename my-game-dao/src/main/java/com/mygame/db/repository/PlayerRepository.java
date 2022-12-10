package com.mygame.db.repository;

import com.mygame.db.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player, Long> {
}
