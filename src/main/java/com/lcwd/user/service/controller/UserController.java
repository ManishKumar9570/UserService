package com.lcwd.user.service.controller;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    //create
    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user){

        User userDetails = userService.saveUser(user);
        //return new ResponseEntity<>(userDetails, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);
    }

    //single user get
    @GetMapping("/{userId}")
    public ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId){
       // return new ResponseEntity<>(userService.getUser(userId),HttpStatus.OK);
        return ResponseEntity.ok(userService.getUser(userId));
    }

    //all user get
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        //return new ResponseEntity<List<User>>(userService.getAllUsers(),HttpStatus.OK);
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
