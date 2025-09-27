package com.av.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
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
import com.av.model.District;
import com.av.model.FileInfo;
import com.av.model.Machinery;
import com.av.model.Sales;
import com.av.model.State;
import com.av.model.SubDistrict;
import com.av.model.WorkingCapitol;
import com.av.repository.CustomerRepository;
import com.av.repository.StateRepository;
import com.av.services.Configuration;
import com.av.services.FileStorageService;
import com.av.services.FileSystemStorageService;
import com.av.services.StorageService;
import jakarta.annotation.PostConstruct;

@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "https://aadhivedha-be-10.onrender.com")

@RestController
@RequestMapping("/api")
public class CustomerController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
  private final FileSystemStorageService fileSystemStorageService;

  @Autowired
  private FileStorageService fileStorageService;


  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  StateRepository stateRepository;

  private final StorageService storageService;
  private static State state;

  @PostConstruct
  public void init() {
    try {
      state = stateRepository.findByStateCode(Configuration.KL);
    } catch (Exception e) {
      logger.error("Error initializing storage service: " + e.getMessage());
    }
  }

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
      // customerRepository.count();
      // customer.setCode(customerRepository.findTopByOrderByCodeDesc().getCode() + 1);
      Customer existingCustomer = customerRepository.findByCode(customer.getCode());
      if (existingCustomer != null) {// customer already exists for other Tab, so update.
        if (null != customer.getAction() && customer.getAction().equals(Configuration.UPDATE)) {
          return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(updateCustomer(existingCustomer, customer), HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>(customerRepository.save(setCustomerTab(customer)),
            HttpStatus.CREATED);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private Customer updateCustomer(Customer existingCustomer, Customer newCustomer) {
    if (newCustomer.getTab().equals(Configuration.PER_TAB)) { // save personal
      existingCustomer.setPersonal(newCustomer.getPersonal());
    } else if (newCustomer.getTab().equals(Configuration.PROJ_TAB)) { // save project
      existingCustomer.setProject(newCustomer.getProject());
    } else if (newCustomer.getTab().equals(Configuration.UDYAM_TAB)) { // save udya
      existingCustomer.setUdyam(newCustomer.getUdyam());
    } else if (newCustomer.getTab().equals(Configuration.PMEGP_TAB)) { // save pmegp
      existingCustomer.setPmegp(newCustomer.getPmegp());
    } else if (newCustomer.getTab().equals(Configuration.KSWIFT_TAB)) { // save kswift
      existingCustomer.setKswift(newCustomer.getKswift());
    } else if (newCustomer.getTab().equals(Configuration.BANK_TAB)) { // save bank
      existingCustomer.setBankdetail(newCustomer.getBankdetail());
    }
    customerRepository.save(existingCustomer);
    return existingCustomer;
  }

  /**
   * @purpose : set profile photo.
   */

  @PostMapping("/profile")
  public ResponseEntity<Boolean> setProfileImage(@RequestParam("code") String code,
      @RequestParam("tab") String tab, @RequestParam("filename") String fileName) {
    code = code.contains("AV_") ? code : "AV_" + code;
    try {
      Files.copy(Paths.get("uploads/" + code + "/" + tab + "/" + fileName),
          Paths.get("uploads/" + code + "/" + tab + "/MY_PROFILE.jpg"),
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
    if (null != _customer.getAction() && _customer.getAction().equals(Configuration.SAVE_ALL)) {
      return _customer; // save all data
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.PER_TAB)) {// save
      customer.setPersonal(_customer.getPersonal());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.PROJ_TAB)) {// save
      customer.setProject(_customer.getProject());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.UDYAM_TAB)) {// save
      customer.setUdyam(_customer.getUdyam());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.PMEGP_TAB)) {// save
      customer.setPmegp(_customer.getPmegp());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.KSWIFT_TAB)) {// save
      customer.setKswift(_customer.getKswift());
    } else if (_customer.getTab().equalsIgnoreCase(com.av.services.Configuration.BANK_TAB)) {// save
      customer.setBankdetail(_customer.getBankdetail());
    }
    return customer;
  }

  /**
   * @purpose : Get all customer details
   */
  @GetMapping("/customers")
  public ResponseEntity<List<Customer>> getAllCustomers() {

    logger.trace("TRACE log");
    logger.debug("DEBUG log");
    logger.info("INFO log");
    logger.warn("WARN log");
    logger.error("ERROR log");

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
    final String newCode = code.contains("AV_") ? code : "AV_" + code;;
    List<FileInfo> fileInfos = new ArrayList<>();
    try {
      fileInfos = storageService.loadCustomerFiles(newCode, subfolder).map(path -> {
        String filename = path.getFileName().toString();
        String url = MvcUriComponentsBuilder
            .fromMethodName(CustomerController.class, "getFile", path.getFileName().toString())
            .build().toString();
        byte[] bytes;
        try {
          bytes = Files.readAllBytes(
              Paths.get("uploads/" + newCode + "/" + subfolder + "/" + path.getFileName()));
          return new FileInfo(filename, url, bytes, path.getFileName().toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }).collect(Collectors.toList());
    } catch (Exception e) {
    }
    List<FileInfo> fileInfosNew = new ArrayList<>();
    for (FileInfo fileInfo : fileInfos) {
      // if (!fileInfo.getName().contains("MY_PROFILE")) {
      fileInfosNew.add(fileInfo);
      // }
    }
    return ResponseEntity.status(HttpStatus.OK).body(fileInfosNew);
  }


  // Get uplpoaded files from mongo db.
  @GetMapping("/customerfiles-db")
  public ResponseEntity<List<FileInfo>> getCustomerFiles(@RequestParam("code") String code) {
    final String newCode = code.contains("AV_") ? code : "AV_" + code;;
    Customer existingCustomer =
        customerRepository.findByCode(Integer.parseInt(newCode.replace("AV_", "")));
    List<FileInfo> fileInfos = new ArrayList<>();
    if (existingCustomer != null) {
      for (String fileName : existingCustomer.getPersonal().getFileIds()) {
        try {
          fileInfos.add(new FileInfo(fileName,
              MvcUriComponentsBuilder.fromMethodName(CustomerController.class, "getFile", fileName)
                  .build().toString(),
              StreamUtils.copyToByteArray(
                  fileStorageService.downloadFileByName(fileName).getInputStream()),
              fileName));
        } catch (IllegalStateException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
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
      // storageService.save(file, code.contains("AV_") ? code : "AV_" + code + "/" + tab);
      storageService.save(file, code.contains("AV_") ? code + "/" + tab : "AV_" + code + "/" + tab);
      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(message);
    } catch (Exception e) {
      message =
          "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
    }
  }

  /*
   * ORG
   * 
   * @PostMapping(value = "/upload-folder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) public
   * ResponseEntity<String> handleFolderUpload(@RequestParam("code") String
   * code, @RequestParam("tab") String tab, @RequestParam("files") MultipartFile[] files) { String
   * message = ""; try { for (MultipartFile file : files) {
   * storageService.saveFolder(file,code.contains("AV_") ? code + "/" + tab : "AV_" + code + "/" +
   * tab); } message = "Uploaded the file(s) successfully: "; return
   * ResponseEntity.status(HttpStatus.OK).body(message); } catch (Exception e) { message =
   * "Could not upload the files!   Error: " + e.getMessage(); return
   * ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message); } }
   */


  @PostMapping(value = "/upload-folder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> handleFolderUpload(@RequestParam("code") String code,
      @RequestParam("tab") String tab, @RequestParam("files") MultipartFile[] files) {
    String message = "";
    String fileId = "";
    List<String> fileIds = new ArrayList<>();
    code = code.contains("AV_") ? code : "AV_" + code;
    try {
      fileId = fileStorageService.uploadFolder(code, files);
      message = "Uploaded the file(s) successfully: ";
      return ResponseEntity.status(HttpStatus.OK).body(message);
    } catch (Exception e) {
      message = "Could not upload the files!   Error: " + e.getMessage();
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
  @GetMapping("/customerview")
  public ResponseEntity<Customer> getCustomerByCode(@RequestParam("code") int code) {
    return new ResponseEntity<>(customerRepository.findByCode(code), HttpStatus.OK);
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
      // _customer.setName(customer.getName());
      /*
       * _customer.setAadharno(customer.getAadharno());
       * _customer.setNameofproject(customer.getNameofproject());
       */ _customer.setDescription(customer.getDescription());
      return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/deleteCustomer")
  public ResponseEntity<HttpStatus> deleteCustomerByCode(@RequestParam("code") int code) {
    try {
      Customer customer = customerRepository.findByCode(code);
      if (customer != null) {
        customerRepository.delete(customer);
        return new ResponseEntity<>(HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
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
    code = code.contains("AV_") ? code : "AV_" + code;
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
    code = code.contains("AV_") ? code : "AV_" + code;
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

  /**
   * @purpose : Get all combo List values.x`
   */
  @Cacheable("districts")
  @GetMapping("/disctricts")
  public ResponseEntity<List<District>> getDistricts(@RequestParam("statecode") String stateCode) {
    try {
      return new ResponseEntity<>(state.getDistricts(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * @purpose : Get all subdistricts by district name.`
   */
  @Cacheable("subdistricts")
  @GetMapping("/subdisctricts")
  public ResponseEntity<List<SubDistrict>> getSubDistricts(
      @RequestParam("districtcode") String districtcode) {
    try {
      District districtObj = state.getDistricts().stream()
          .filter(d -> d.getDistrict().equalsIgnoreCase(districtcode)).findFirst().orElse(null);
      return new ResponseEntity<>(districtObj.getSubDistricts(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /*  *//**
         * @purpose : Get all villege by district name.`
         *//*
            * @Cacheable("villeges")
            * 
            * @GetMapping("/villeges") public ResponseEntity<List<String>>
            * getVilleges(@RequestParam("district") String district,
            * 
            * @RequestParam("subdistrict") String subdistrict) { try { District districtObj =
            * state.getDistricts().stream() .filter(d ->
            * d.getDistrict().equalsIgnoreCase(district)).findFirst().orElse(null); SubDistrict
            * subDistrictObj = districtObj.getSubDistricts().stream() .filter(sd ->
            * sd.getSubDistrict().equalsIgnoreCase(subdistrict)).findFirst().orElse(null); return
            * new ResponseEntity<>(subDistrictObj.getVillages(), HttpStatus.OK);
            * 
            * } catch (Exception e) { return new ResponseEntity<>(null,
            * HttpStatus.INTERNAL_SERVER_ERROR); } }
            */

  @Cacheable("villeges")
  @GetMapping("/villeges")
  public ResponseEntity<List<String>> getVillages(@RequestParam("district") String district) {
    try {
      District districtObj = state.getDistricts().stream()
          .filter(d -> d.getDistrict().equalsIgnoreCase(district)).findFirst().orElse(null);

      if (districtObj != null) {
        // Aggregate all villages from all subdistricts
        List<String> villages = districtObj.getSubDistricts().stream()
            .flatMap(subDistrict -> subDistrict.getVillages().stream())
            .collect(Collectors.toList());
        return new ResponseEntity<>(villages, HttpStatus.OK);
      }
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // District not found
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @purpose : Check for duplicate machinery.
   */
  @GetMapping("/isDuplicateMachinery")
  public ResponseEntity<String> checkDuplicateMachinery(@RequestParam("code") String code,
      @RequestParam("particular") String particular) {
    int customerCode = Integer.parseInt(code.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(customerCode);
    if (customer != null) {
      boolean isDuplicate = customer.getMachinery().stream()
          .anyMatch(machinery -> machinery.getParticular().equalsIgnoreCase(particular));
      if (isDuplicate) {
        return ResponseEntity.ok("YES");
      } else {
        return ResponseEntity.ok("NO");
      }
    }
    return ResponseEntity.ok("NO");
  }



  /**
   * @purpose : Check for duplicate machinery.
   */
  @GetMapping("/isDuplicateWC")
  public ResponseEntity<String> checkDuplicateWC(@RequestParam("code") String code,
      @RequestParam("particular") String particular) {
    int customerCode = Integer.parseInt(code.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(customerCode);
    if (customer != null) {
      boolean isDuplicate = customer.getWorkingCapitol().stream()
          .anyMatch(wc -> wc.getParticular().equalsIgnoreCase(particular));
      if (isDuplicate) {
        return ResponseEntity.ok("YES");
      } else {
        return ResponseEntity.ok("NO");
      }
    }
    return ResponseEntity.ok("NO");
  }


  /**
   * @purpose : Check for duplicate sales.
   */
  @GetMapping("/isDuplicateSales")
  public ResponseEntity<String> checkDuplicateSales(@RequestParam("code") String code,
      @RequestParam("particular") String particular) {
    int customerCode = Integer.parseInt(code.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(customerCode);
    if (customer != null) {
      boolean isDuplicate = customer.getSales().stream()
          .anyMatch(sales -> sales.getParticular().equalsIgnoreCase(particular));
      if (isDuplicate) {
        return ResponseEntity.ok("YES");
      } else {
        return ResponseEntity.ok("NO");
      }
    }
    return ResponseEntity.ok("NO");
  }

  /**
   * @purpose : Save Machinery details.
   */
  @PostMapping("/machinery")
  public ResponseEntity<Machinery> saveMachinery(@RequestParam("customerCode") String customerCode,
      @RequestBody Machinery machinery) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {// customer already exists for other Tab, so update.
      customer.getMachinery().add(machinery);
      customerRepository.save(customer);
      return new ResponseEntity<>(machinery, HttpStatus.CREATED);
    } else {
      customer = new Customer();
      customer.setCode(code);
      customer.getMachinery().add(machinery);
      customerRepository.save(customer);
      return new ResponseEntity<>(machinery, HttpStatus.CREATED);
    }
  }

  @PostMapping("/workingcapitol")
  public ResponseEntity<WorkingCapitol> saveWC(@RequestParam("customerCode") String customerCode,
      @RequestBody WorkingCapitol wc) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {// customer already exists for other Tab, so update.
      customer.getWorkingCapitol().add(wc);
      customerRepository.save(customer);
      return new ResponseEntity<>(wc, HttpStatus.CREATED);
    } else {
      customer = new Customer();
      customer.setCode(code);
      customer.getWorkingCapitol().add(wc);
      customerRepository.save(customer);
      return new ResponseEntity<>(wc, HttpStatus.CREATED);
    }
  }

  @PostMapping("/sales")
  public ResponseEntity<Sales> saveSales(@RequestParam("customerCode") String customerCode,
      @RequestBody Sales sales) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {// customer already exists for other Tab, so update.
      customer.getSales().add(sales);
      customerRepository.save(customer);
      return new ResponseEntity<>(sales, HttpStatus.CREATED);
    } else {
      customer = new Customer();
      customer.setCode(code);
      customer.getSales().add(sales);
      customerRepository.save(customer);
      return new ResponseEntity<>(sales, HttpStatus.CREATED);
    }
  }

  @PostMapping("/editmachinery")
  public ResponseEntity<Machinery> editMachinery(@RequestParam("customerCode") String customerCode,
      @RequestBody Machinery updatedMachinery) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<Machinery> machineryList = customer.getMachinery();
      Machinery existingMachinery = machineryList.stream()
          .filter(m -> m.getParticular().equalsIgnoreCase(updatedMachinery.getParticular()))
          .findFirst().orElse(null);

      if (existingMachinery != null) {
        // Update the existing machinery with new details
        existingMachinery.setRate(updatedMachinery.getRate());
        existingMachinery.setQty(updatedMachinery.getQty());
        existingMachinery.setAmount(updatedMachinery.getAmount());
        // Add other fields as necessary
        customerRepository.save(customer);
        return new ResponseEntity<>(existingMachinery, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
  }

  @PostMapping("/editworkingcapitol")
  public ResponseEntity<WorkingCapitol> editWC(@RequestParam("customerCode") String customerCode,
      @RequestBody WorkingCapitol updatedWC) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<WorkingCapitol> wcList = customer.getWorkingCapitol();
      WorkingCapitol existingWC =
          wcList.stream().filter(m -> m.getParticular().equalsIgnoreCase(updatedWC.getParticular()))
              .findFirst().orElse(null);

      if (existingWC != null) {
        // Update the existing machinery with new details
        existingWC.setBoxrate(updatedWC.getBoxrate());
        existingWC.setQty(updatedWC.getQty());
        existingWC.setAmount(updatedWC.getAmount());
        existingWC.setPcsrate(updatedWC.getPcsrate());
        customerRepository.save(customer);
        return new ResponseEntity<>(existingWC, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
  }

  @PostMapping("/editsales")
  public ResponseEntity<Sales> editSales(@RequestParam("customerCode") String customerCode,
      @RequestBody Machinery updatedSales) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<Sales> salesList = customer.getSales();
      Sales existingSale = salesList.stream()
          .filter(m -> m.getParticular().equalsIgnoreCase(updatedSales.getParticular())).findFirst()
          .orElse(null);

      if (existingSale != null) {
        // Update the existing machinery with new details
        existingSale.setRate(updatedSales.getRate());
        existingSale.setQty(updatedSales.getQty());
        existingSale.setAmount(updatedSales.getAmount());
        // Add other fields as necessary
        customerRepository.save(customer);
        return new ResponseEntity<>(existingSale, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
  }


  @PostMapping("/deletemachinery")
  public ResponseEntity<Machinery> deleteMachinery(
      @RequestParam("customerCode") String customerCode, @RequestBody Machinery machinery) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<Machinery> machineryList = customer.getMachinery();
      Machinery existingMachinery = machineryList.stream()
          .filter(m -> m.getParticular().equalsIgnoreCase(machinery.getParticular())).findFirst()
          .orElse(null);
      machineryList.remove(existingMachinery);
      if (existingMachinery != null) {
        customerRepository.save(customer);
        return new ResponseEntity<>(existingMachinery, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
  }

  @PostMapping("/deleteworkingcapitol")
  public ResponseEntity<WorkingCapitol> deleteWC(@RequestParam("customerCode") String customerCode,
      @RequestBody WorkingCapitol wc) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<WorkingCapitol> wcList = customer.getWorkingCapitol();
      WorkingCapitol existingWC =
          wcList.stream().filter(m -> m.getParticular().equalsIgnoreCase(wc.getParticular()))
              .findFirst().orElse(null);
      wcList.remove(existingWC);
      if (existingWC != null) {
        customerRepository.save(customer);
        return new ResponseEntity<>(existingWC, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
  }

  @PostMapping("/deletesales")
  public ResponseEntity<Sales> deleteSales(@RequestParam("customerCode") String customerCode,
      @RequestBody Sales sales) {
    int code = Integer.parseInt(customerCode.replace("AV_", ""));
    Customer customer = customerRepository.findByCode(code);
    if (customer != null) {
      List<Sales> salesList = customer.getSales();
      Sales existingSale =
          salesList.stream().filter(m -> m.getParticular().equalsIgnoreCase(sales.getParticular()))
              .findFirst().orElse(null);
      salesList.remove(existingSale);
      if (existingSale != null) {
        customerRepository.save(customer);
        return new ResponseEntity<>(existingSale, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Machinery not found
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Customer not found
    }
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
