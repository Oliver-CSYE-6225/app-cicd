package com.csye6225.webapp.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "imagetable")
public class Image {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String file_name;
    private String url;

    private UUID user_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date upload_date;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getUser_id() {
        return user_id;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", file_name='" + file_name + '\'' +
                ", url='" + url + '\'' +
                ", user_id=" + user_id +
                ", upload_date=" + upload_date +
                '}';
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public Date getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(Date upload_date) {
        this.upload_date = upload_date;
    }

//    public Image(UUID id, String file_name, String url, UUID user_id, Date upload_date) {
//        this.id = id;
//        this.file_name = file_name;
//        this.url = url;
//        this.user_id = user_id;
//        this.upload_date = upload_date;
//    }

    @PrePersist
    protected void onCreate() {
        upload_date = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        upload_date = new Date();
    }
}
