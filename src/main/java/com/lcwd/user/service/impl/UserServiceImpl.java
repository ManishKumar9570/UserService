package com.lcwd.user.service.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exception.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;
    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
    List<User> userList=new ArrayList<>();
        for (User user : userRepository.findAll()) {
            //here we can also use serviceName to call the api by using @LoadBalanced annotation on restTemlate Bean configuration instead of host and port (like above)
            // Note :- if you will use the @LoadBalanced annotation then we must have to use the serviceName to call the external microservices api else it will give the exception saying
            //No instances available for localhost for that particular interanl api call wherever don't use serviceName to call the internal api
            Rating[] ratings = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
            List<Rating> ratingList = Arrays.stream(ratings).toList();
            List<Rating> ratingsOfUser = ratingList.stream().map(rating -> {
                //api call to hotel service to get the hotel
                //set the hotel to rating
                //return the rating
              //  Hotel hotel = restTemplate.getForObject("http://localhost:8082/hotels/" + rating.getHotelId(), Hotel.class);
                //here we can also use serviceName to call the api by using @LoadBalanced annotation on restTemlate Bean configuration instead of host and port (like above)
                //Hotel hotel = restTemplate.getForObject("http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class);

                //now calling api using feign client
               Hotel hotel =  hotelService.getHotel(rating.getHotelId());
                rating.setHotel(hotel);
                return rating;
            }).collect(Collectors.toList());

            user.setRatings(ratingsOfUser);
            userList.add(user);
        }
        return userList;
    }

    @Override
    public User getUser(String userId) {
    // get user from database with the help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !!"));
        //fetch rating of the above user from rating service
        //http://localhost:8083/ratings/users/a0bbe7e8-f945-4b45-9cdb-76fcc0ee6f15
        // Note :- if you will use the @LoadBalanced annotation then we must have to use the serviceName to call the external microservices api else it will give the exception saying
        //No instances available for localhost for that particular interanl api call wherever don't use serviceName to call the internal api
        // here must use the serviceName to call the below api instead of localhost and port else it will give the exception. See above note for more details
        Rating[] ratingsOfUser= restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+userId, Rating[].class);
        logger.info("ratingList {}",ratingsOfUser);
        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
        List<Rating> ratingList = ratings.stream().map(rating -> {
            //api call to hotel service to get the hotel
            //set the hotel to rating
            //return the rating
            // Note :- if you will use the @LoadBalanced annotation then we must have to use the serviceName to call the external microservices api else it will give the exception saying
            //No instances available for localhost for that particular interanl api call wherever don't use serviceName to call the internal api
            // here must use the serviceName to call the below api instead of localhost and port else it will give the exception. See above note for more details
            Hotel hotel = restTemplate.getForObject("http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class);
          rating.setHotel(hotel);
            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return user;
    }
}
