package com.microservicetest.authserver.repo;

import com.microservicetest.authserver.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IUserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
