package com.finance.dashboard.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finance.dashboard.model.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
    
    // Spring generates: SELECT * FROM users WHERE email = ? AND is_active = ?
    Optional<User> findByEmailAndIsActive(String email, boolean isActive);
    
    // Spring generates: SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE email = ?
    boolean existsByEmail(String email);
}