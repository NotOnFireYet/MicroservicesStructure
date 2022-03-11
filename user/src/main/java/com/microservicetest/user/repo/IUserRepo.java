package com.microservicetest.user.repo;

import com.microservicetest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    User findByUsername(String username);
}
