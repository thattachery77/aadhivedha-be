/*
 * package com.av.controller;
 * 
 * import java.util.ArrayList; import java.util.List; import java.util.Optional;
 * import java.util.stream.Collectors;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.core.io.Resource; import
 * org.springframework.http.HttpHeaders; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.web.bind.annotation.CrossOrigin; import
 * org.springframework.web.bind.annotation.DeleteMapping; import
 * org.springframework.web.bind.annotation.ExceptionHandler; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.PutMapping; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.bind.annotation.ResponseBody; import
 * org.springframework.web.bind.annotation.RestController; import
 * org.springframework.web.multipart.MultipartFile; import
 * org.springframework.web.servlet.mvc.method.annotation.
 * MvcUriComponentsBuilder; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import com.av.exception.StorageFileNotFoundException; import
 * com.av.model.Customer; import com.av.model.FileInfo; import
 * com.av.repository.CustomerRepository; import com.av.services.StorageService;
 * 
 * @CrossOrigin(origins = "http://localhost:4200") // @CrossOrigin(origins =
 * "https://switeco.com")
 * 
 * @RestController
 * 
 * @RequestMapping("/api") public class CustomerController2 {
 * 
 * @Autowired CustomerRepository customerRepository;
 * 
 * private final StorageService storageService;
 * 
 * @Autowired public CustomerController2(StorageService storageService) {
 * this.storageService = storageService; }
 * 
 *//**
	 * @purpose : Create Customer data.
	 */
/*
 * 
 * @PostMapping("/customer") public ResponseEntity<Customer>
 * createCustomer(@RequestBody Customer customer) { try { Customer _tutorial =
 * customerRepository.save(customer); return new ResponseEntity<>(_tutorial,
 * HttpStatus.CREATED); } catch (Exception e) { return new
 * ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); } }
 * 
 *//**
	 * @purpose : Get all customer details
	 */
/*
 * @GetMapping("/customers") public ResponseEntity<List<Customer>>
 * getAllCustomers() {
 * 
 * try { List<Customer> customers = new ArrayList<Customer>();
 * customerRepository.findAll().forEach(customers::add); if
 * (customers.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }
 * return new ResponseEntity<>(customers, HttpStatus.OK); } catch (Exception e)
 * { return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); } }
 * 
 *//**
	 * @purpose : get customer details by customer id.
	 */
/*
 * @GetMapping("/tutorials/{id}") public ResponseEntity<Customer>
 * getCustomerById(@PathVariable("id") String id) { Optional<Customer> customer
 * = customerRepository.findById(id);
 * 
 * // if (tutorialData.isPresent()) { return new
 * ResponseEntity<>(customer.get(), HttpStatus.OK);
 * 
 * } else { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
 * 
 * }
 * 
 *//**
	 * @purpose : Update customer data.
	 */
/*
 * @PutMapping("/customer/{id}") public ResponseEntity<Customer>
 * updateCustomer(@PathVariable("id") String id, @RequestBody Customer customer)
 * { Optional<Customer> customerData = customerRepository.findById(id); if
 * (customerData.isPresent()) { Customer _customer = customerData.get();
 * _customer.setName(customer.getName());
 * _customer.setAadharno(customer.getAadharno());
 * _customer.setNameofproject(customer.getNameofproject());
 * _customer.setDescription(customer.getDescription()); return new
 * ResponseEntity<>(customerRepository.save(customer), HttpStatus.OK); } else {
 * return new ResponseEntity<>(HttpStatus.NOT_FOUND); } }
 * 
 *//**
	 * @purpose : Delete customer by id.
	 */
/*
 * @DeleteMapping("/customer/{id}") public ResponseEntity<HttpStatus>
 * deleteCustomer(@PathVariable("id") String id) { try {
 * customerRepository.deleteById(id); return new
 * ResponseEntity<>(HttpStatus.NO_CONTENT); } catch (Exception e) { return new
 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } }
 * 
 * 
 * @GetMapping("/files/{filename:.+}")
 * 
 * @ResponseBody public ResponseEntity<Resource> serveFile(@PathVariable String
 * filename) {
 * 
 * Resource file = storageService.loadAsResource(filename);
 * 
 * if (file == null) return ResponseEntity.notFound().build();
 * 
 * return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
 * "attachment; filename=\"" + file.getFilename() + "\"").body(file); }
 * 
 * 
 *//**
	 * @purpose : get ll files
	 */
/*
 * @GetMapping("/getfiles") public ResponseEntity<HttpStatus> getfiles() {
 * System.out.println("get files"); return new ResponseEntity<>(HttpStatus.OK);
 * 
 * }
 * 
 * @PostMapping("/upload") public ResponseEntity<String>
 * handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes
 * redirectAttributes) { storageService.save(file);
 * redirectAttributes.addFlashAttribute("message", "You successfully uploaded "
 * + file.getOriginalFilename() + "!");
 * 
 * return new ResponseEntity<>("{'message':'success'}", HttpStatus.OK); }
 * 
 * 
 * @GetMapping("/files") public Model listUploadedFiles(Model model) throws
 * IOException {
 * 
 * model.addAttribute("files", storageService.loadAll().map( path ->
 * MvcUriComponentsBuilder.fromMethodName(CustomerController.class, "serveFile",
 * path.getFileName().toString()).build().toUri().toString())
 * .collect(Collectors.toList()));
 * 
 * return model;
 * 
 * }
 * 
 * 
 * @GetMapping("/files") public ResponseEntity<List<FileInfo>> getListFiles() {
 * List<FileInfo> fileInfos = storageService.loadAll().map(path -> { String
 * filename = path.getFileName().toString(); String url =
 * MvcUriComponentsBuilder .fromMethodName(CustomerController2.class, "getFile",
 * path.getFileName().toString()).build() .toString();
 * 
 * return new FileInfo(filename, url); }).collect(Collectors.toList());
 * 
 * return ResponseEntity.status(HttpStatus.OK).body(fileInfos); }
 * 
 * @GetMapping("/files/{filename:.+}")
 * 
 * @ResponseBody public ResponseEntity<Resource> getFile(@PathVariable String
 * filename) { // Resource file = storageService.load(filename); Resource file =
 * storageService.load(filename); return ResponseEntity.ok()
 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
 * file.getFilename() + "\"") .body(file); }
 * 
 * @ExceptionHandler(StorageFileNotFoundException.class) public
 * ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc)
 * { return ResponseEntity.notFound().build(); }
 * 
 *//**
	 * @purpose : download files
	 *//*
		 * @PostMapping("/download") public ResponseEntity<HttpStatus> download() {
		 * System.out.println("download"); return new ResponseEntity<>(HttpStatus.OK);
		 * 
		 * }
		 * 
		 * 
		 * }
		 */