package com.av.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.av.model.User;
import com.av.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder(); // or @Bean config
  }

  public void registerUser(String username, String plainPassword, String email) {
    String hashedPassword = passwordEncoder.encode(plainPassword);
    User user = new User();
    user.setUsername(username);
    user.setPassword(hashedPassword);
    user.setEmail(email); // Assuming User has an email field
    userRepository.save(user);
  }

  public boolean checkPassword(String email, String rawPassword) {
    String storedHashedPassword = userRepository.findByEmail(email).getPassword(); // Example
                                                                                   // username
    return passwordEncoder.matches(rawPassword, storedHashedPassword);
  }
}
