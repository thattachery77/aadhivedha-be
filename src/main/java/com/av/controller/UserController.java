package com.av.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.av.model.User;
import com.av.repository.UserRepository;
import com.av.services.Configuration;
import com.av.services.UserService;



@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "https://switeco.com")
@RestController
@RequestMapping("/api/auth")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;


  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User loginReq) {

    // userService = new UserService(userRepository);
    // userService.registerUser("rajesh", "rajesh", "rajeshnileshwar@gmail.com"); // Example usage
    // of
    // User user = userRepository.findByUsername(loginReq.getUsername());
    User user = userRepository.findByEmail(loginReq.getEmail());
    if (null != user && userService.checkPassword(loginReq.getEmail(), loginReq.getPassword())) {
      return ResponseEntity.ok(Configuration.SUCCESS);
    }
    /*
     * if (user != null && user.getPassword().equals(loginReq.getPassword())) { return
     * ResponseEntity.ok(Configuration.SUCCESS); } else { return
     * ResponseEntity.ok(Configuration.FAILURE); }
     */
    // if (userRepository.existsByEmail(loginReq.getEmail())) {
    // return ResponseEntity.ok(Configuration.SUCCESS);
    // }
    return ResponseEntity.ok(Configuration.FAILURE);

  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody User newUser) {
    if (userRepository.findByUsername(newUser.getUsername()) != null) {
      return ResponseEntity.badRequest().body("Username already exists");
    }
    userRepository.save(newUser);
    return ResponseEntity.ok("User registered");
  }
}
