package com.av.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.av.model.State;

public interface StateRepository extends MongoRepository<State, String> {

  State findByStateCode(String stateCode);

  // List<District> findByStateCode(String stateCode);



}
