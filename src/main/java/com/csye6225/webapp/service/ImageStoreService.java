package com.csye6225.webapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.timgroup.statsd.StatsDClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageStoreService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    StatsDClient statsd;

    public JSONObject uploadFile(MultipartFile file, String user_id) {
        // Convert multipart file to file object
        File fileObject = null;
//        try {
            fileObject = convertMultiPartFileToFile(file);
       /* } catch (IOException e) {
            e.printStackTrace();
        }*/

        // Get File metadata
        String content_type = file.getContentType();
        long file_size = file.getSize();

        System.out.println("content_type"+content_type+"file_size"+file_size);

        // Get User
//        User u = userService.getUserByUsername(username);

        // from here you will get file metadata picture.get*
        String filename = "";
        if(content_type.equals("image/png")){
            filename = user_id +".png";
        } else if(content_type.equals("image/jpeg")){
            filename = user_id +".jpeg";
        } else {
            filename = user_id;
        }

        // Add picture to s3 bucket
        long startTime = System.currentTimeMillis();
        s3Client.putObject(new PutObjectRequest(bucketName, filename, fileObject));
        statsd.recordExecutionTime("Post Image S3", startTime -  System.currentTimeMillis());
        fileObject.delete();

        // Get Current Date
//        LocalDate datetoday = LocalDate.now();

        // Build file URL
//        String file_url = s3BucketName+"/"+u.getId()+"/"+filename;

        // Build Picture object to add to data
//        Picture p = new Picture(
//                u.getId(),
//                filename,
//                file_url,
//                datetoday,
//                u
//        );
//        System.out.println(p.toString());

        // Add picture object to database
//        pictureRepository.save(p);

//        return filename+" uploaded successfully.";






//        File fileObj = convertMultiPartFileToFile(file);
//        String fileName = user_id + "_" + file.getOriginalFilename();
//        String fileName = file.getOriginalFilename();
//        PutObjectResult result = s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
//        PutObjectResult result2 = s3Client.putObject(bucketName + "/" + user_id, fileName, fileObj);
        JSONObject metaObj = new JSONObject();
        metaObj.put("user_id", user_id);
        metaObj.put("url", bucketName +  "/" + filename);
        metaObj.put("filename", filename);

//        fileObj.delete();
//        result.get
        return metaObj;
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        long startTime = System.currentTimeMillis();
        s3Client.deleteObject(bucketName, fileName);
        statsd.recordExecutionTime("Delete Image S3", startTime -  System.currentTimeMillis());
        return fileName + " removed ...";
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("Error converting multipartFile to file" + e.getMessage());
        }
        return convertedFile;
    }
}
