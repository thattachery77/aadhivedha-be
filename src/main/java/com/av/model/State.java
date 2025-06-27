package com.av.model;

import java.util.ArrayList;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "state")
public class State {


  @Id
  private String id;

  private String state;
  private String stateCode;
  public ArrayList<District> districts;

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public ArrayList<District> getDistricts() {
    return districts;
  }

  public void setDistricts(ArrayList<District> districts) {
    this.districts = districts;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }



}


