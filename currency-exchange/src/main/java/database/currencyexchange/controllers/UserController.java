package database.currencyexchange.controllers;

import database.currencyexchange.models.User;
import database.currencyexchange.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping(method = RequestMethod.GET)
    public Iterable<User> getAllUsers(){
        return userService.getUsers();
    }

    @GetMapping("/me")
    public ResponseEntity getUserData(){
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findUserByEmail(userDetails.getUsername());
            Map<Object, Object> model = new HashMap<>();
            model.put("name", user.getName());
            model.put("id", user.getId());
            model.put("email", user.getEmail());
            model.put("amountOfPLN", user.getAmountOfPLN());
            return ok(model);
        } catch(Exception e){
            return new ResponseEntity("Cannot return user data", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/me")
    public  User updateAmount(@RequestBody User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User oldUser = userService.findUserByEmail(userDetails.getUsername());
        if(user.getAmountOfPLN() != Double.NaN){
            oldUser.setAmountOfPLN(user.getAmountOfPLN());
        }
        userService.updateUser(oldUser);
        return oldUser;
    }


}
