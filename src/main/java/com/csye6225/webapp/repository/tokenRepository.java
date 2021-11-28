package com.csye6225.webapp.repository;

import java.util.Optional;

import com.csye6225.webapp.entity.token;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface tokenRepository extends
        CrudRepository<token, String> {

    @Query("SELECT i FROM Image i WHERE i.user_id = ?1")
    Optional<token> findByUserId(String emailId);
    // Optional<token> findById(String emailId);
}
