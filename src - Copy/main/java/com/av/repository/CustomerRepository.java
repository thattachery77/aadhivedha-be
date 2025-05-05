package com.av.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.av.model.Customer;


public interface CustomerRepository extends MongoRepository<Customer, String> {
	
	//Customer findFirstByCodeDesc();
	
	  // @Query("SELECT MAX(c.code) FROM Customer c")
	  //  int findTopByNameOrderByTimestampDesc();
	Customer findFirstByOrderByCodeAsc();
}
