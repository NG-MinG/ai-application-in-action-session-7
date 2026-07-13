package org.example.restfulapi.service;

import org.example.restfulapi.dto.CreateUserRequest;
import org.example.restfulapi.model.User;
import org.example.restfulapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User createUser(CreateUserRequest request) {
        User user = new User(request.getFullname());
        return this.userRepository.save(user);
    }
}

