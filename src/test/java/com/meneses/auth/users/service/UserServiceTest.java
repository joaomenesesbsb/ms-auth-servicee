package com.meneses.auth.users.service;

import com.meneses.auth.exceptions.DataBaseException;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.features.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_USER");
        user = new User();
        user.setId(1L);
        user.setEmail("teste@email.com");
        user.setPassword("senhaCriptografada");
        user.setRoles(new HashSet<>());
        user.getRoles().add(role);
    }

    @Nested
    @DisplayName("Testes de Busca por ID")
    class FindById {

        @Test
        @DisplayName("Deve retornar UserResponseDTO quando o ID existir")
        void shouldReturnUserResponseDTO_whenIdExists() {

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            UserResponseDTO result = userService.findById(user.getId());

            assertNotNull(result);
            assertEquals("teste@email.com", result.getEmail());
            verify(userRepository, times(1)).findById(user.getId());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
        void shouldThrowException_whenIdDoesNotExist() {

            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));

            verify(userRepository, times(1)).findById(userId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando roles do usuário for null")
        void shouldThrowException_whenUserRolesIsNull() {

            user.setRoles(null);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

            assertThrows(NullPointerException.class,
                    () -> userService.addRoleToUser(user.getId(), role.getName()));
        }
    }

    @Nested
    @DisplayName("Testes de Buscar todos os email paginados")
    class FindAll {

        @Test
        @DisplayName("Deve retornar uma página de usuários ao buscar por e-mail")
        void shouldReturnPageOfUsers_whenSearchingByEmail() {

            String emailFilter = "teste";
            Pageable pageable = PageRequest.of(0,20);

            Page<User> userPage = new PageImpl<>(List.of(user));
            when(userRepository.findByEmailContainingIgnoreCase(emailFilter, pageable)).thenReturn(userPage);

            Page<UserResponseDTO> result = userService.findAll(emailFilter, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("teste@email.com", result.getContent().get(0).getEmail());
        }

        @Test
        @DisplayName("Deve retornar página vazia quando nenhum usuário for encontrado")
        void shouldReturnEmptyPage_whenNoUserFound() {

            String emailFilter = "inexistente";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> emptyPage = Page.empty(pageable);
            when(userRepository.findByEmailContainingIgnoreCase(emailFilter, pageable))
                    .thenReturn(emptyPage);

            Page<UserResponseDTO> result = userService.findAll(emailFilter, pageable);

            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Testes de adicionar ROLE ao usuario")
    class addRoleToUser {

        @Test
        @DisplayName("Deve adicionar uma role ao usuário com sucesso")
        void shouldAddRoleToUser_successfully() {

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

            userService.addRoleToUser(user.getId(),role.getName());

            assertTrue(user.getRoles().contains(role), "O usuário deve conter a nova role");
            verify(userRepository, times(1)).save(user);
            verify(roleRepository).findByName(role.getName());
            verify(userRepository).findById(user.getId());

        }

        @Test
        @DisplayName("Deve adicionar uma role sem duplicar")
        void shouldAddRoleToUserNoDuplicated_whenSuccessfully() {

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

            userService.addRoleToUser(user.getId(),role.getName());
            userService.addRoleToUser(user.getId(),role.getName());

            assertTrue(user.getRoles().size() == 1);
        }

        @Test
        @DisplayName("Deve lançar ResouceNotFoundException quando usuário nao existir")
        void shouldThrowException_whenUserOrRoleNotFound() {

            String roleName = "INVALID_ROLE";

            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.addRoleToUser(user.getId(), roleName));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a role não existir")
        void shouldThrowException_whenRoleNotFound() {

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.addRoleToUser(user.getId(), role.getName()));
            verify(userRepository, never()).save(any());
        }


    }

    @Nested
    @DisplayName("Testes de Deleção")
    class Delete {

        @Test
        @DisplayName("Deve deletar o usuário quando o ID existir")
        void shouldDeleteUser_whenIdExists() {

            when(userRepository.existsById(user.getId())).thenReturn(true);
            doNothing().when(userRepository).deleteById(user.getId());

            userService.delete(user.getId());

            verify(userRepository, times(1)).deleteById(user.getId());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
        void shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
            Long userId = 99L;
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));
            verify(userRepository, never()).deleteById(userId);
        }

        @Test
        @DisplayName("Deve lançar DataBaseException em caso de violação de integridade")
        void shouldThrowDataBaseException_whenDataIntegrityViolationOccurs() {

            when(userRepository.existsById(user.getId())).thenReturn(true);

            doThrow(new DataIntegrityViolationException("Erro de integridade"))
                    .when(userRepository).deleteById(user.getId());

            assertThrows(DataBaseException.class, () -> userService.delete(user.getId()));
        }
    }

}
