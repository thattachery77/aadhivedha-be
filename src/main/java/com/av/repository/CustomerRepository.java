package com.av.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.av.model.Customer;


public interface CustomerRepository extends MongoRepository<Customer, String> {

  // Customer findFirstByCodeDesc();

  // @Query("SELECT MAX(c.code) FROM Customer c")
  // int findTopByNameOrderByTimestampDesc();
  Customer findTopByOrderByCodeDesc();

  Customer findByCode(int code);

  Customer findByCode(String code);



}
