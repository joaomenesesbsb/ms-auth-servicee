package com.meneses.auth.services;

import com.meneses.auth.dto.UserResponse;
import com.meneses.auth.entities.Role;
import com.meneses.auth.entities.User;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.repositories.RoleRepository;
import com.meneses.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserResponse findById(Long id){

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario nao encontrado")
        );

        UserResponse response = new UserResponse(user.getEmail(),user.getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.toList()));

        return response;
    }

    public void addRoleToUser(Long userId, String roleName) {

        User user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findByName(roleName).orElseThrow();

        user.getRoles().add(role);
        userRepository.save(user);
    }

}
