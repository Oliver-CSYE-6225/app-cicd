package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.Image;
import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.ImageRepository;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    @Autowired
    ImageRepository imageRepository;

    public Image getImageMetaData(UUID user_id) throws NotFoundException {
        Optional<Image> image = imageRepository.findByUserId(user_id);

//        try{
//            image.orElseThrow(() -> new NotFoundException("Not found: " + user_id));
//        }catch(Exception e){
//            System.out.println("Image metadata" +  image);
//        }
        image.orElseThrow(() -> new NotFoundException("No image found for: " + user_id));
        System.out.println("User Image" +  image);
        return image.get();
    }

    public Image saveImageMetaData(Image image) {
        return imageRepository.saveAndFlush(image);
    }

    public void deleteImageMetaData(Image image) {

         imageRepository.deleteById(image.getId());
    }

}
