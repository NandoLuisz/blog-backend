package com.luis.blogapp.repository;

import com.luis.blogapp.domain.creator.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, UUID> {
    UserDetails findByUsername(String username);

    Creator getCreatorByUsername(String username);

    boolean existsByEmail(String email);
}
