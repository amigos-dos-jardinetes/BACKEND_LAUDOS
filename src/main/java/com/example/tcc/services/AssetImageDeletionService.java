package com.example.tcc.services;

import com.example.tcc.models.ImageModel;
import com.example.tcc.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssetImageDeletionService {
    private final ImageRepository imageRepository;
    private final AWSS3Service s3Service;

    public void delete(String filename) {
        List<ImageModel> image = imageRepository.findByFilename(filename);

        if (!image.isEmpty()) {
            imageRepository.delete(image.getFirst());
            s3Service.deleteObject(filename);
        } else {
            throw new NoSuchElementException("Image with filename " + filename + " not found");
        }
    }
}

