package com.meneses.auth.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String token;
    private LocalDateTime expiration;
    @ManyToOne
    private User user;

    public RefreshToken(){
    }

    public RefreshToken(Long id, String token, LocalDateTime expiration, User user) {
        Id = id;
        this.token = token;
        this.expiration = expiration;
        this.user = user;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
