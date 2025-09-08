package com.av.model;

public class Sales {

  private int rate;
  private int qty;
  private int amount;
  private String particular;

  // Constructors
  public Sales() {}

  public Sales(int rate, int qty, int amount, String particular) {
    this.rate = rate;
    this.qty = qty;
    this.amount = amount;
    this.particular = particular;
  }

  public int getRate() {
    return rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
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
