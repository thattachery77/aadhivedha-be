package com.av.model;

public class WorkingCapitol {

  private int boxrate;
  private int pcsrate;
  private int qty;
  private int amount;
  private String particular;

  // Constructors
  public WorkingCapitol() {}

  public WorkingCapitol(int boxrate, int pcsrate, int qty, int amount, String particular) {
    this.boxrate = boxrate;
    this.pcsrate = pcsrate;
    this.qty = qty;
    this.amount = amount;
    this.particular = particular;
  }

  public int getBoxrate() {
    return boxrate;
  }

  public void setBoxrate(int boxrate) {
    this.boxrate = boxrate;
  }

  public int getPcsrate() {
    return pcsrate;
  }

  public void setPcsrate(int pcsrate) {
    this.pcsrate = pcsrate;
  }

  public int getQty() {
    return qty;
  }

  public void setQty(int qty) {
    this.qty = qty;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getParticular() {
    return particular;
  }

  public void setParticular(String particular) {
    this.particular = particular;
  }


}
