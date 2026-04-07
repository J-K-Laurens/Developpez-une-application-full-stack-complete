package com.openclassrooms.mddapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.User;

/**
 * Repository for accessing User entity data.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByFirstName(String firstName);

	Optional<User> findByEmail(String email);

	Optional<User> findByFirstName(String firstName);
}

