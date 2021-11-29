package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.Image;
import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.ImageRepository;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.timgroup.statsd.StatsDClient;

import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    StatsDClient statsd;

    // @Transactional("tm2")
    public Image getImageMetaData(UUID user_id) throws NotFoundException {
        long startTime = System.currentTimeMillis();

        Optional<Image> image = imageRepository.findByUserId(user_id);

        statsd.recordExecutionTime("Fetch Image Meta Execution Time", startTime -  System.currentTimeMillis());

//        try{
//            image.orElseThrow(() -> new NotFoundException("Not found: " + user_id));
//        }catch(Exception e){
//            System.out.println("Image metadata" +  image);
//        }
        image.orElseThrow(() -> new NotFoundException("No image found for: " + user_id));
        System.out.println("User Image" +  image);
        return image.get();
    }

    // @Transactional("tm1")
    public Image saveImageMetaData(Image image) {
        long startTime = System.currentTimeMillis();
        Image foo = imageRepository.saveAndFlush(image);
        statsd.recordExecutionTime("Save Image Meta Execution Time", startTime -  System.currentTimeMillis());
        return foo;

    }

    // @Transactional("tm1")
    public void deleteImageMetaData(Image image) {
        long startTime = System.currentTimeMillis();
         imageRepository.deleteById(image.getId());
         statsd.recordExecutionTime("Delete Image Meta Execution Time", startTime -  System.currentTimeMillis());
    }

}
