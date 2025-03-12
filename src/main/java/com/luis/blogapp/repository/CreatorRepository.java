package com.luis.blogapp.repository;

import com.luis.blogapp.domain.creator.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, UUID> {
    Creator findCreatorByName(String name);
    Creator findCreatorByEmail(String email);
}
