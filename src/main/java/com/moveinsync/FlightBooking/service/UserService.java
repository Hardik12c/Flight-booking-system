package com.moveinsync.FlightBooking.service;

import com.moveinsync.FlightBooking.configuration.JwtUtil;
import com.moveinsync.FlightBooking.configuration.PasswordUtil;
import com.moveinsync.FlightBooking.model.User;
import com.moveinsync.FlightBooking.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepo userRepo;

    public User usernameExists(String username) {
        return userRepo.findByUsername(username);
    }

    public List<User> showall() {
        return userRepo.findAll();
    }
    public String save(User user) {
        userRepo.save(user);
        return ("Saved");
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(User userDto) {
//        // create a new user
//        User user = new User();
//        user.setUsername(userDto.getUsername());
//        user.setPassword(userDto.getPassword());
//        user.setEmail(userDto.getEmail());
//        user.setAdmin(userDto.getAdmin);

        // save the user to the database
        userDto.setPassword(PasswordUtil.encode(userDto.getPassword()));
        User savedUser = userRepo.save(userDto);

        // generate a JWT token
        String token = jwtUtil.generateToken(savedUser);

        // set the JWT token on the user object
        savedUser.setToken(token);

        return savedUser;
    }

    public String loginUser(User userDto) {
        // authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

        // get the authenticated user
        User user = (User) authentication.getPrincipal();

        // generate a JWT token
        String token = jwtUtil.generateToken(user);

        // set the JWT token on the user object
        user.setToken(token);

        return token;
    }
}