package com.av.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
public class Customer {

  @Id
  private String id;

  private String key;
  private int code;
  private String description;
  private String createdOn;
  private Date expiryDate;
  private Boolean approvalStatus;
  private Boolean active;

  private String tab;
  private String action;

  private Personal personal = new Personal();
  private Project project = new Project();
  private BankDetail bankdetail = new BankDetail();
  private Udyam udyam = new Udyam();
  private Pmegp pmegp = new Pmegp();
  private KSwift kswift = new KSwift();

  public Customer() {

  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Boolean getApprovalStatus() {
    return approvalStatus;
  }

  public void setApprovalStatus(Boolean approvalStatus) {
    this.approvalStatus = approvalStatus;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Personal getPersonal() {
    return personal;
  }

  public void setPersonal(Personal personal) {
    this.personal = personal;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getTab() {
    return tab;
  }

  public void setTab(String tab) {
    this.tab = tab;
  }



  public BankDetail getBankdetail() {
    return bankdetail;
  }


  public void setBankdetail(BankDetail bankdetail) {
    this.bankdetail = bankdetail;
  }


  public String getAction() {
    return action;
  }


  public void setAction(String action) {
    this.action = action;
  }


  public Udyam getUdyam() {
    return udyam;
  }


  public void setUdyam(Udyam udyam) {
    this.udyam = udyam;
  }


  public Pmegp getPmegp() {
    return pmegp;
  }


  public void setPmegp(Pmegp pmegp) {
    this.pmegp = pmegp;
  }


  public KSwift getKswift() {
    return kswift;
  }


  public void setKswift(KSwift kswift) {
    this.kswift = kswift;
  }



}
