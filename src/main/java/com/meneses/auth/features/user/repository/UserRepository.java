package com.meneses.auth.features.user.repository;

import com.meneses.auth.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT obj FROM User obj " +
            "WHERE LOWER(obj.email) LIKE LOWER(CONCAT('%', :email))")
    Page<User> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);

    @Query("SELECT obj FROM User obj LEFT JOIN FETCH obj.roles WHERE obj.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
}
