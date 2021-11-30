package com.csye6225.webapp.entity;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "usertable")
public class User {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String first_name;
    private String last_name;
    private String password;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date account_created;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date account_updated;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date account_verified;
    private boolean verified;

    public User() {
    }

    public boolean getVerified(){
        return verified;
    }

    public void setVerified(boolean verified){
        this.verified = verified;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getAccount_created() {
        return account_created;
    }

    public Date getAccount_updated() {
        return account_updated;
    }

    public void setAccount_verified(){
        account_verified = new Date();
    }

    public Date getAccount_verified(){
        return account_verified;
    }

    @PrePersist
    protected void onCreate() {
        account_created = new Date();
        account_updated = account_created;
        account_verified = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", account_created=" + account_created +
                ", account_updated=" + account_updated +
                '}';
    }

    @PreUpdate
    protected void onUpdate() {
        account_updated = new Date();
    }
}
