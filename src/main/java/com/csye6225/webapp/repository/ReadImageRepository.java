package com.csye6225.webapp.repository;

import java.util.Optional;
import java.util.UUID;

import com.csye6225.webapp.entity.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@com.csye6225.webapp.config.ReadOnlyRepository
public interface ReadImageRepository extends JpaRepository<Image, UUID>{
    @Query("SELECT i FROM Image i WHERE i.user_id = ?1")
    Optional<Image> findByUserId(UUID user_id);
}
