package com.av.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.av.model.ComboList;
import com.av.repository.ComboListRepository;

@CrossOrigin(origins = "http://localhost:4200")
 
@RestController
@RequestMapping("/api")
public class ComboListController {

 
  @Autowired
  ComboListRepository combolistRepository;

  

 
  /**
   * @purpose : Get all combo List values.x`
   */
  @Cacheable("combolist")
  @GetMapping("/combolist")
  public ResponseEntity<List<ComboList>> getComboList(@RequestParam("group") String group) {

    try {
      List<ComboList> comboList = new ArrayList<ComboList>();
      combolistRepository.findByGroupOrderByDisplayOrder(group).forEach(comboList::add);
      if (comboList.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(comboList, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }



  
}
