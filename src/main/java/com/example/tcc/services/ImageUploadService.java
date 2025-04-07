package com.example.tcc.services;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final AWSS3Service s3Service;

    public String saveImage(MultipartFile photo) {
        return this.saveToBucket(photo);
    }

    public String saveToBucket(MultipartFile file) {
        try {
            // Gera um nome de arquivo único com a extensão .jpg
            String uniqueFilename = UUID.randomUUID() + ".jpg";

            s3Service.putImage(uniqueFilename, file);

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Erro no upload do arquivo", e);
        }
    }
}