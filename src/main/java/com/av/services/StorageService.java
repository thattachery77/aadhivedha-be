package com.av.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	
	/*
	 * void init();
	 * 
	 * void store(MultipartFile file);
	 * 
	 * Stream<Path> loadAll();
	 * 
	 * Path load(String filename);
	 * 
	 * Resource loadAsResource(String filename);
	 * 
	 * void deleteAll();
	 */
	
	
	public void init();

	  public void save(MultipartFile file,String subfolder);

	  public Resource load(String filename);
	  
	  public boolean delete(String code , String subfolder,String filename);

	  public boolean deleteAll(String code , int mode);

	  public Stream<Path> loadAll();
	  
	  public Stream<Path> loadCustomerFiles(String code,String subfolder);

}
