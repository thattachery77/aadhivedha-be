package com.av.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import com.av.exception.StorageFileNotFoundException;
import com.av.model.Customer;
import com.av.model.FileInfo;
import com.av.repository.CustomerRepository;
import com.av.services.FileSystemStorageService;
import com.av.services.StorageService;

@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "https://switeco.com")

@RestController
@RequestMapping("/api")
public class CustomerController {

  private final FileSystemStorageService fileSystemStorageService;

  @Autowired
  CustomerRepository customerRepository;

  private final StorageService storageService;

  @Autowired
  public CustomerController(StorageService storageService,
      FileSystemStorageService fileSystemStorageService) {
    this.storageService = storageService;
    this.fileSystemStorageService = fileSystemStorageService;
  }

  /**
   * @purpose : Create Customer data.
   */

  @PostMapping("/customer")
  public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    try {
      customerRepository.count();
      customer.setCode(customerRepository.findTopByOrderByCodeDesc().getCode() + 1);
      return new ResponseEntity<>(customerRepository.save(setCustomerTab(customer)),
          HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  /**
   * @purpose : set profile photo.
   */

  @PostMapping("/profile")
  public ResponseEntity<Boolean> setProfileImage(@RequestParam("code") String code,
      @RequestParam("tab") String tab, @RequestParam("filename") String fileName) {
    try {
         Files.copy(Paths.get("uploads/"+code+"/"+tab+"/"+fileName), Paths.get("uploads/"+code+"/"+tab+"/MY_PROFILE.jpg"), 
           StandardCopyOption.REPLACE_EXISTING);

      return new ResponseEntity<>(true, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
 
  }

  /**
   * @purpose : set CUstomer data as per Tab / save click for saving partial information.
   */
  private Customer setCustomerTab(Customer _customer) {
    Customer customer = new Customer();
    customer.setCode(_customer.getCode());
    if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.PER_TAB)) {// save
                                                                                     // personal
                                                                                     // data
      customer.setPersonal(_customer.getPersonal());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.PROJ_TAB)) {// save
                                                                                             // project
                                                                                             // data
      customer.setProject(_customer.getProject());
    } else { // save all data
      return _customer;
    }
    return customer;
  }

  /**
   * @purpose : Get all customer details
   */
  @GetMapping("/customers")
  public ResponseEntity<List<Customer>> getAllCustomers() {

    try {
      List<Customer> customers = new ArrayList<Customer>();
      customerRepository.findAll().forEach(customers::add);
      if (customers.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(customers, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }



  @GetMapping("/customerfiles")
  public ResponseEntity<List<FileInfo>> getCustomerFiles(@RequestParam("code") String code,
      @RequestParam("subfolder") String subfolder) {
    System.out.println(code);
    List<FileInfo> fileInfos = new ArrayList<>();
    try {
      fileInfos = storageService.loadCustomerFiles(code, subfolder).map(path -> {
        String filename = path.getFileName().toString();
        String url = MvcUriComponentsBuilder
            .fromMethodName(CustomerController.class, "getFile", path.getFileName().toString())
            .build().toString();
        byte[] bytes;
        try {
          bytes = Files.readAllBytes(
              Paths.get("uploads/" + code + "/" + subfolder + "/" + path.getFileName()));
          return new FileInfo(filename, url, bytes, path.getFileName().toString());

        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return null;
      }).collect(Collectors.toList());
    } catch (Exception e) {
      // e.printStackTrace();
    }

    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
  }


  /*
   * @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) public
   * ResponseEntity<List<FileInfo>> uploadFile(@RequestParam("code") String
   * code, @RequestParam("tab") String tab,@RequestParam("file") MultipartFile file) { try {
   * List<FileInfo> fileInfos = new ArrayList<>(); storageService.save(file, code + "/" + tab);
   * String url = MvcUriComponentsBuilder.fromMethodName(CustomerController.class, "getFile",
   * file.getOriginalFilename().toString()).build().toString(); fileInfos.add(new
   * FileInfo(file.getOriginalFilename(), url,file.getBytes(),file.getOriginalFilename())); return
   * ResponseEntity.status(HttpStatus.OK).body(fileInfos);
   * 
   * } catch (Exception e) { return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
   * } }
   */

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadFile(@RequestParam("code") String code,
      @RequestParam("tab") String tab, @RequestParam("file") MultipartFile file) {
    String message = "";
    try {
      storageService.save(file, code + "/" + tab);
      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(message);
    } catch (Exception e) {
      message =
          "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
    }
  }


  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    Resource file = storageService.load(filename);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }


  /*
   * @GetMapping("/customerfiles") public ResponseEntity<List<FileInfo>>
   * getCustomerFiles(@RequestParam("code") String code,@RequestParam("subfolder") String subfolder
   * ) { System.out.println(code); List<FileInfo> fileInfos =
   * storageService.loadCustomerFiles(code,subfolder).map(path -> { String filename =
   * path.getFileName().toString(); String url = MvcUriComponentsBuilder
   * .fromMethodName(CustomerController.class, "getFile",
   * path.getFileName().toString()).build().toString();
   * 
   * return new FileInfo(filename, url,null,null); }).collect(Collectors.toList());
   * 
   * return ResponseEntity.status(HttpStatus.OK).body(fileInfos); }
   */


  /**
   * @purpose : get customer details by customer id.
   */
  @GetMapping("/customer/code")
  public ResponseEntity<String> getCustomerCode() {
    int code = customerRepository.findTopByOrderByCodeDesc().getCode() + 1;
    deleteAllFiles("AV_" + code, 0, "");
    return new ResponseEntity<>("AV_" + code, HttpStatus.OK);
  }

  /**
   * @purpose : get customer details by customer id.
   */
  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Customer> getCustomerById(@PathVariable("id") String id) {
    Optional<Customer> customer = customerRepository.findById(id);

    // if (tutorialData.isPresent()) {
    return new ResponseEntity<>(customer.get(), HttpStatus.OK);
    /*
     * } else { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
     */
  }

  /**
   * @purpose : Update customer data.
   */
  @PutMapping("/customer/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable("id") String id,
      @RequestBody Customer customer) {
    Optional<Customer> customerData = customerRepository.findById(id);
    if (customerData.isPresent()) {
      Customer _customer = customerData.get();
      _customer.setName(customer.getName());
      _customer.setAadharno(customer.getAadharno());
      _customer.setNameofproject(customer.getNameofproject());
      _customer.setDescription(customer.getDescription());
      return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * @purpose : Delete customer by id.
   */
  @DeleteMapping("/customer/{id}")
  public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") String id) {
    try {
      customerRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }



  /**
   * @purpose : Delete customer by id.
   */
  @DeleteMapping("/deleteCustomerFile")
  public ResponseEntity<HttpStatus> deleteCustomerFile(@RequestParam("code") String code,
      @RequestParam("subfolder") String subfolder, @RequestParam("filename") String filename) {
    try {
      storageService.delete(code, subfolder, filename);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * @purpose : Delete customer by id.
   */
  @DeleteMapping("/deleteAllFiles")
  public ResponseEntity<Boolean> deleteAllFiles(@RequestParam("code") String code,
      @RequestParam("mode") int mode, @RequestParam("subfolder") String subfolder) {
    try {
      return new ResponseEntity<>(storageService.deleteAll(code, mode, subfolder), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /*
   * @GetMapping("/files/{filename:.+}")
   * 
   * @ResponseBody public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
   * 
   * Resource file = storageService.loadAsResource(filename);
   * 
   * if (file == null) return ResponseEntity.notFound().build();
   * 
   * return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
   * file.getFilename() + "\"").body(file); }
   */

  /**
   * @purpose : get ll files
   */
  @GetMapping("/getfiles")
  public ResponseEntity<HttpStatus> getfiles() {
    System.out.println("get files");
    return new ResponseEntity<>(HttpStatus.OK);

  }

  /*
   * @PostMapping("/upload") public ResponseEntity<String> handleFileUpload(@RequestParam("file")
   * MultipartFile file, RedirectAttributes redirectAttributes) { storageService.save(file);
   * redirectAttributes.addFlashAttribute("message", "You successfully uploaded " +
   * file.getOriginalFilename() + "!");
   * 
   * return new ResponseEntity<>("{'message':'success'}", HttpStatus.OK); }
   */

  /*
   * @GetMapping("/files") public Model listUploadedFiles(Model model) throws IOException {
   * 
   * model.addAttribute("files", storageService.loadAll().map( path ->
   * MvcUriComponentsBuilder.fromMethodName(CustomerController.class, "serveFile",
   * path.getFileName().toString()).build().toUri().toString()) .collect(Collectors.toList()));
   * 
   * return model;
   * 
   * }
   */


  /*
   * @GetMapping("/files") public ResponseEntity<List<FileInfo>> getListFiles() { List<FileInfo>
   * fileInfos = storageService.loadAll().map(path -> { String filename =
   * path.getFileName().toString(); String url = MvcUriComponentsBuilder
   * .fromMethodName(CustomerController.class, "getFile",
   * path.getFileName().toString()).build().toString();
   * 
   * return null; }).collect(Collectors.toList());
   * 
   * return ResponseEntity.status(HttpStatus.OK).body(fileInfos); }
   */

  /*
   * @GetMapping("/files") public ResponseEntity<List<FileInfo>> getListFiles() { List<FileInfo>
   * fileInfos = storageService.loadAll().map(path -> { String filename =
   * path.getFileName().toString(); String url = MvcUriComponentsBuilder
   * .fromMethodName(CustomerController.class, "getFile", path.getFileName().toString()).build()
   * .toString();
   * 
   * return new FileInfo(filename, url); }).collect(Collectors.toList());
   * 
   * return ResponseEntity.status(HttpStatus.OK).body(fileInfos); }
   */

  /*
   * @GetMapping("/files/{filename:.+}")
   * 
   * @ResponseBody public ResponseEntity<Resource> getFile(@PathVariable String filename) { //
   * Resource file = storageService.load(filename); Resource file =
   * storageService.loadAsResource(filename); return ResponseEntity.ok()
   * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
   * .body(file); }
   */

  /*
   * @GetMapping("/files/{filename:.+}")
   * 
   * @ResponseBody public ResponseEntity<Resource> getFile(@PathVariable String filename) { Resource
   * file = storageService.load(filename); return ResponseEntity.ok()
   * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() +
   * "\"").body(file); }
   */



  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }

  /*  *//**
         * @purpose : download files
         *//*
            * @PostMapping("/download") public ResponseEntity<HttpStatus> download() {
            * System.out.println("download"); return new ResponseEntity<>(HttpStatus.OK);
            * 
            * }
            */

}
