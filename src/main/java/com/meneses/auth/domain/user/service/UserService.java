package com.meneses.auth.domain.user.service;

import com.meneses.auth.domain.role.entity.Role;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.domain.role.repository.RoleRepository;
import com.meneses.auth.domain.user.entity.User;
import com.meneses.auth.domain.user.dto.UserResponse;
import com.meneses.auth.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserResponse findById(Long id){

        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario nao encontrado com o ID: " + id)
        );

        UserResponse response = new UserResponse(user.getEmail(),user.getRoles()
                .stream().map(Role::getName)
                .collect(Collectors.toList()));

        return response;
    }

    public UserResponse update(Long id, UserResponse responseDto){
        try{
            User entity = userRepository.getReferenceById(id);
            entity.setEmail(responseDto.getEmail());
            entity = userRepository.save(entity);
            return mapToResponse(entity);
        }
        catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Usuario nao enconntrado com o ID: " + id);
        }
    }

    public void addRoleToUser(Long userId, String roleName) {

        User user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findByName(roleName).orElseThrow();

        user.getRoles().add(role);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
    }

}
