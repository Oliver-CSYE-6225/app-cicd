package com.csye6225.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.csye6225.webapp.config.ReadOnlyRepository;
import com.csye6225.webapp.entity.User;

/**
 * This is a read only repository
 */
@com.csye6225.webapp.config.ReadOnlyRepository
public interface ReadUserRepository extends JpaRepository<User, Long> {

    // @Transactional("tm2")
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUserName(String user_name);

    // @Transactional("tm2")
    @Query("SELECT password,verified FROM User  WHERE username = ?1")
    String findUserPassword(String user_name);

}