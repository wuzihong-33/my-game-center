package com.mygame.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.mygame.db.entity.Arena;

public interface ArenaRepository extends MongoRepository<Arena, Long>{

}
