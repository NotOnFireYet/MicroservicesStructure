package com.microservicetest.authserver.repo;

import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.domain.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITokenRepo extends JpaRepository<TokenEntity, Long> {
    TokenEntity findByUser(AppUser user);

    TokenEntity findByRefreshToken(String token);
}