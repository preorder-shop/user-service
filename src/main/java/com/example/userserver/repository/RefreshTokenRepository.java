package com.example.userserver.repository;

import com.example.userserver.domain.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    void deleteByUserId(String userId);
    void deleteByToken(String token);

    boolean existsByToken(String token);
}
