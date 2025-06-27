package com.av.model;

import java.util.ArrayList;

public class District {

  private ArrayList<SubDistrict> subDistricts;
  private String district;

  public ArrayList<SubDistrict> getSubDistricts() {
    return subDistricts;
  }

  public void setSubDistricts(ArrayList<SubDistrict> subDistricts) {
    this.subDistricts = subDistricts;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }



}
