package com.lcwd.user.service.controller;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
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
    int retryCount=1;
    @GetMapping("/{userId}")
    //@CircuitBreaker(name = "ratingHotelBreaker",fallbackMethod = "ratingHotelFallback")
    @Retry(name= "ratingHotelService",fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId){
        logger.info("Get single user on the basis of userId");
        logger.info("Retry count : {}",retryCount++);
       // return new ResponseEntity<>(userService.getUser(userId),HttpStatus.OK);
        return ResponseEntity.ok(userService.getUser(userId));
    }
    //creating fallback method for circuitBreaker
      public ResponseEntity<User> ratingHotelFallback(String userId,Exception exception){
        logger.info("Fallback is executed because service is down : {}", exception.getMessage());
        User dummyUser = User.builder().
                email("dummy@gmail.com").
                name("Dummy").
                about("This user is created dummy because some service(s) is(are) down")
                .userId("14234")
                .build();

        return new ResponseEntity<>(dummyUser,HttpStatus.OK);
    }
    //all user get
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(){
        //return new ResponseEntity<List<User>>(userService.getAllUsers(),HttpStatus.OK);
        return ResponseEntity.ok(userService.getAllUsers());
    }


}
