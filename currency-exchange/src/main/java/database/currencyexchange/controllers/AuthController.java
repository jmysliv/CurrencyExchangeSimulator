package database.currencyexchange.controllers;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.Map;

import database.currencyexchange.configs.JwtTokenProvider;
import database.currencyexchange.models.User;
import database.currencyexchange.repositories.UserRepository;
import database.currencyexchange.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository users;

    @Autowired
    private UserService userService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthBody data) {
        try {
            String username = data.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username);
            Map<Object, Object> model = new HashMap<>();
            model.put("email", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        if(user.getEmail() == null){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Email field is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(user.getPassword() == null){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Password field is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(user.getName() == null){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Name field is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            Map<Object, Object> response = new HashMap<>();
            response.put("message","User with email: " + user.getEmail() + " already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        user.setAmountOfPLN(1000);
        userService.saveUser(user);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "User created   successfully");
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }


}