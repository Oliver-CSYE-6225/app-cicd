package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.Image;
import com.csye6225.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    @Transactional(readOnly = true)
    @Query("SELECT i FROM Image i WHERE i.user_id = ?1")
    Optional<Image> findByUserId(UUID user_id);

//    @Query("SELECT password FROM User  WHERE username = ?1")
//    String findUserPassword(String user_name);
}
