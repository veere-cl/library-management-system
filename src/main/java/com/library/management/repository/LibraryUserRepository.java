package com.library.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.management.entity.LibraryUser;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {

    Optional<LibraryUser> findByUsername(String username);
}
