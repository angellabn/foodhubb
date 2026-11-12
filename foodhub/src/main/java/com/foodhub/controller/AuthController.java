package com.foodhub.controller;

import com.foodhub.model.User;
import com.foodhub.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository repo;

    public AuthController(UserRepository repo) {
        this.repo = repo;
    }

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return "EMAIL_EXISTS";
        }
        repo.save(user);
        return "REGISTERED";
    }

    // LOGIN
    @PostMapping("/login")
    public User login(@RequestBody User req) {
        return repo.findByEmail(req.getEmail())
                .filter(u -> u.getPassword().equals(req.getPassword()))
                .orElse(null);
    }
}
