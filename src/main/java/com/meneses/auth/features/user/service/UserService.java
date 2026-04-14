package com.meneses.auth.features.user.service;

import com.meneses.auth.exceptions.DataBaseException;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.dto.UserRequestDTO;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id){
        return userRepository.findById(id)
                .map(user -> {
                    logger.warn("Tentativa de buscar usuário inexistente. ID: [{}]", id);
                    return new UserResponseDTO(user.getEmail());
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(String email, Pageable pageable) {
        return userRepository
                .findByEmailContainingIgnoreCase(email != null ? email : "", pageable)
                .map(user -> new UserResponseDTO(user.getEmail()));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO responseDto){
        try{
            User entity = userRepository.getReferenceById(id);
            entity.setEmail(responseDto.getEmail());
            entity = userRepository.save(entity);

            logger.info("Usuário do email [{}] atualizado com sucesso.", responseDto.getEmail());
            return mapToResponse(entity);
        }
        catch (EntityNotFoundException e){
            logger.error("Falha ao atualizar: Usuário com email: [{}] não encontrado.", responseDto.getEmail());
            throw  new ResourceNotFoundException("Usuario nao enconntrado");
        }
    }

    @Transactional
    public void addRoleToUser(Long userId, String roleName) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role nao encontrado"));

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("Role [{}] vinculada ao usuário ID [{}] com sucesso.", roleName, userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id){
        if (!userRepository.existsById(id)) {
            logger.warn("Tentativa de excluir usuário inexistente. ID: [{}]", id);
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        try{
            userRepository.deleteById(id);
            logger.warn("Usuário ID [{}] removido com sucesso.", id);
        }
        catch (DataIntegrityViolationException e) {
            logger.error("Violação de integridade ao deletar usuário ID [{}].", id);
            throw new DataBaseException("Não é possível remover o usuário: violação de integridade.");
        }
    }

    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(user.getEmail());
    }

}
