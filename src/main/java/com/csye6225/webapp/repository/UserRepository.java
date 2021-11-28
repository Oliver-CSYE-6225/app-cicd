package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
// import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional("tm2")
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUserName(String user_name);

    @Transactional("tm2")
    @Query("SELECT password FROM User  WHERE username = ?1")
    String findUserPassword(String user_name);

}
