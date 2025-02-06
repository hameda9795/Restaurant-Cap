package com.example.Restaurant.repository;

import com.example.Restaurant.model.Role;
import com.example.Restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByIsActiveTrue();
    List<User> findByRole(Role role);

}