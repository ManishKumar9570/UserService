package com.lcwd.user.service.service;

import com.lcwd.user.service.entities.User;

import java.util.List;

public interface UserService {
    //user operations

    //create user
    User saveUser(User user);

    //get all users
    List<User> getAllUsers();

    //get single user of the given userId

    User getUser(String userId);

    //todo : delete
    //todo : update


}
