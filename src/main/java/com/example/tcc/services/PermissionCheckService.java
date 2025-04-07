package com.example.tcc.services;

import com.example.tcc.models.FileAssetModel;
import com.example.tcc.models.FileModel;
import com.example.tcc.models.ImageModel;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.repositories.FileRepository;
import com.example.tcc.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PermissionCheckService {
    private final FileRepository fileRepository;
    private final FileAssetRepository fileAssetRepository;
    private final ImageRepository imageRepository;

    public Boolean checkPermissionForAsset(String userEmail, Long assetId) {
        Optional<FileAssetModel> fileAssetModel = fileAssetRepository.findByAssetId(assetId);
        return fileAssetModel.map(assetModel -> assetModel.getFile().getUserEmail().equals(userEmail)).orElse(false);
    }

    public Boolean checkPermissionForFile(String userEmail, Long fileId) {
        Optional<FileModel> fileModel = fileRepository.findById(fileId);
        return fileModel.filter(model -> Objects.equals(model.getUserEmail(), userEmail)).isPresent();
    }

    public Boolean checkPermissionForImageFilename(String userEmail, String filename) {
        List<ImageModel> images = imageRepository.findByFilename(filename);
        Optional<FileAssetModel> fileAssetModel = fileAssetRepository.findByAssetId(images.getFirst().getAsset().getId());
        return fileAssetModel.map(assetModel -> assetModel.getFile().getUserEmail().equals(userEmail)).orElse(false);
    }
}
