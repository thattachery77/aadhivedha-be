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
	    private String name;
	    private String contactno;
	    private String aadharno;
	    private String unitaddress;
	    private String residentialaddress;
	    private String nameofproject;
	    private String loanschema;
	    private String yearofloan;
	    private String image1;
	    private String image2;
	    private String image3;
	    private String email;
	    private String description;
	    private String createdOn;
	    private Date expiryDate;  
	    private Boolean approvalStatus;
	    private Boolean active;
	    
	    private String tab;
	    private Personal personal=  new Personal();
	    private Project project =  new Project();
	    
	    
	    
	    public Customer() {

	    }

	    public Customer(String name, int code, String aadharno) {
	      this.name = name;
	      this.code = code;
	      this.aadharno = aadharno;
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

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContactno() {
			return contactno;
		}
		public void setContactno(String contactno) {
			this.contactno = contactno;
		}
		public String getAadharno() {
			return aadharno;
		}
		public void setAadharno(String aadharno) {
			this.aadharno = aadharno;
		}
		public String getUnitaddress() {
			return unitaddress;
		}
		public void setUnitaddress(String unitaddress) {
			this.unitaddress = unitaddress;
		}
		public String getResidentialaddress() {
			return residentialaddress;
		}
		public void setResidentialaddress(String residentialaddress) {
			this.residentialaddress = residentialaddress;
		}
		public String getNameofproject() {
			return nameofproject;
		}
		public void setNameofproject(String nameofproject) {
			this.nameofproject = nameofproject;
		}
		public String getLoanschema() {
			return loanschema;
		}
		public void setLoanschema(String loanschema) {
			this.loanschema = loanschema;
		}
		public String getYearofloan() {
			return yearofloan;
		}
		public void setYearofloan(String yearofloan) {
			this.yearofloan = yearofloan;
		}
		public String getImage1() {
			return image1;
		}
		public void setImage1(String image1) {
			this.image1 = image1;
		}
		public String getImage2() {
			return image2;
		}
		public void setImage2(String image2) {
			this.image2 = image2;
		}
		public String getImage3() {
			return image3;
		}
		public void setImage3(String image3) {
			this.image3 = image3;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
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
	    
	    

}
