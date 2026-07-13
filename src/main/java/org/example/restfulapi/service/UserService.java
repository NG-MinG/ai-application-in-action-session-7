package org.example.restfulapi.service;

import org.example.restfulapi.dto.CreateUserRequest;
import org.example.restfulapi.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User createUser(CreateUserRequest request);
}
