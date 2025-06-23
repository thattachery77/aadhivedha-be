package com.av.repository;


import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.av.model.ComboList;


public interface ComboListRepository extends MongoRepository<ComboList, String> {
	
 	List<ComboList> findByGroupOrderByDisplayOrder(String group);

}
