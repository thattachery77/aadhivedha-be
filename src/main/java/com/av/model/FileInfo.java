package com.av.model;

public class FileInfo {

	 private String name;
	 private String url;
	 private byte[] bytes;
 	 private String   retrievedImage;

	  public FileInfo(String name, String url,byte[] bytes,String  retrievedImage) {
	    this.name = name;
	    this.url = url;
	    this.bytes = bytes ;
	    this.retrievedImage = retrievedImage;
	  }

	  public String getName() {
	    return this.name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public String getUrl() {
	    return this.url;
	  }

	  public void setUrl(String url) {
	    this.url = url;
	  }
 

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getRetrievedImage() {
		return retrievedImage;
	}

	public void setRetrievedImage(String retrievedImage) {
		this.retrievedImage = retrievedImage;
	}
	  
	  
}
