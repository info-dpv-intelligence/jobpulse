package com.jobpulse.auth_service.repository;

import com.jobpulse.auth_service.model.RefreshToken;

import com.jobpulse.auth_service.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUser(User user);
}