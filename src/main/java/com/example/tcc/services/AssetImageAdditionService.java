package com.example.tcc.services;

import com.example.tcc.models.AssetModel;
import com.example.tcc.models.ImageModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetImageAdditionService {
    private final String bucketURL;
    private final AssetRepository assetRepository;
    private final ImageRepository imageRepository;
    private final ImageUploadService imageUploadService;

    public String add(Long assetId, MultipartFile image) {
        String filename = imageUploadService.saveImage(image);
        String path = bucketURL + filename;

        Optional<AssetModel> asset = assetRepository.findById(assetId);

        if (asset.isPresent()) {
            imageRepository.save(new ImageModel(asset.get(), filename));
            return path;
        } else {
            // Tratar o caso onde o Asset não existe, por exemplo:
            throw new NoSuchElementException("Asset with id " + assetId + " not found");
        }
    }
}
