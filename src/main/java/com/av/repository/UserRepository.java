package com.av.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.av.model.User;

public interface UserRepository extends MongoRepository<User, String> {
  User findByUsername(String username);

  User findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
