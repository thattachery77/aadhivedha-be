package com.av.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "combolist")
public class ComboList {

  @Id
  private String id;
  private String value;
  private String group;
  private int displayOrder;

  
  
  public ComboList() {
  
 }
  
  public ComboList(String id, String value, String group) {
     this.id = id;
    this.value = value;
    this.group = group;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
  }
  

}
