package com.example.tcc.services;

import com.example.tcc.models.AssetModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.repositories.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetDeletionService {
    private final AssetRepository assetRepository;
    private final FileAssetRepository fileAssetRepository;
    private final ImageRepository imageRepository;
    private final AWSS3Service s3Service;

    @Transactional
    public void delete(Long assetId) {
        Optional<AssetModel> asset = assetRepository.findById(assetId);
        if (asset.isPresent()) {
            // Obtenha os filenames das imagens relacionadas
            List<String> filenames = imageRepository.findFilenamesByAssetId(assetId);
            filenames.add(asset.get().getMainImage());

            // Deleta as imagens do bucket
            s3Service.deleteMultipleObjects(filenames);

            // Deleta as imagens relacionadas
            imageRepository.deleteByAssetId(assetId);

            // Deleta os arquivos relacionados
            fileAssetRepository.deleteByAssetId(assetId);

            // Deleta o próprio asset
            assetRepository.deleteById(assetId);
        }
    }
}
