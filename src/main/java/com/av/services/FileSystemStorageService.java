package com.av.services;

 
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.av.exception.StorageException;
import com.av.exception.StorageFileNotFoundException;
import java.io.File;

@Service
public class FileSystemStorageService implements StorageService {

/*	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
        
		System.out.println(properties.getLocation());
        if(properties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
        }

		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	} */
	
	
	private final Path root = Paths.get("uploads");

	  @Override
	  public void init() {
	    try {
	      Files.createDirectories(root);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not initialize folder for upload!");
	    }
	  }

	  @Override
	  public void save(MultipartFile file,String subfolder) {
	    try {
	    	if (!Files.exists(Paths.get("uploads/"+subfolder), LinkOption.NOFOLLOW_LINKS)) {
	  	      Files.createDirectories(Paths.get("uploads/"+subfolder));
	    	}
	    	Files.copy(file.getInputStream(), Paths.get("uploads/"+subfolder+"/").resolve(file.getOriginalFilename()));
	    } catch (Exception e) {
	      if (e instanceof FileAlreadyExistsException) {
	        throw new RuntimeException("A file of that name already exists.");
	      }

	      throw new RuntimeException(e.getMessage());
	    }
	  }

	  @Override
	  public Resource load(String filename) {
	    try {
	      Path file = root.resolve(filename);
	      Resource resource = new UrlResource(file.toUri());

	      if (resource.exists() || resource.isReadable()) {
	        return resource;
	      } else {
	        throw new RuntimeException("Could not read the file!");
	      }
	    } catch (MalformedURLException e) {
	      throw new RuntimeException("Error: " + e.getMessage());
	    }
	  }

	  @Override
	  public boolean delete(String code , String subfolder,String filename) {
	    try {
	      Path file = root.resolve(code+"/"+subfolder+"/"+filename);
	      return Files.deleteIfExists(file);
	    } catch (IOException e) {
	      throw new RuntimeException("Error: " + e.getMessage());
	    }
	  }

	  @Override
	  public boolean deleteAll(String code,int mode,String subfolder) {
  	       try {
			deleteSubfolders(mode==0 ? new File("uploads/"+code) : new File("uploads/"+code+"/"+subfolder));
			return true;
		} catch (Exception e) {
 		}
  	       return false;
	  }
	  
	  private void deleteSubfolders(File directory) {
		  if (!directory.exists()) {
	            System.out.println("Directory does not exist.");
	            return;
	        }

	        if (!directory.isDirectory()) {
	             System.out.println("Not a directory.");
	            return;
	        }

	        File[] files = directory.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                if (file.isDirectory()) {
	                    deleteSubfolders(file); // Recursive call for subdirectories
	                }
	                file.delete(); // Delete files and empty directories
	            }
	        }
	        directory.delete(); // Delete the main directory after its contents are deleted
	  }
	  @Override
	  public Stream<Path> loadAll() {
	    try {
	      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
	    } catch (IOException e) {
	      throw new RuntimeException("Could not load the files!");
	    }
	  }

	@Override
	public Stream<Path> loadCustomerFiles(String code,String subfolder) {
		   try {
			      return Files.walk(Paths.get("uploads/"+code+"/"+subfolder), 1).filter(path -> !path.equals(Paths.get("uploads/"+code+"/"+subfolder))).map(Paths.get("uploads/"+code+"/"+subfolder)::relativize);
			    } catch (IOException e) {
			      throw new RuntimeException("Could not load the files!");
			    } 
	}


}