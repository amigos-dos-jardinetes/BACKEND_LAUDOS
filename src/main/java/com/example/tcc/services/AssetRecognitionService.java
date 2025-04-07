package com.example.tcc.services;

import com.example.tcc.dto.AssetNumberRecognitionDto;
import com.example.tcc.models.AssetModel;
import com.example.tcc.models.FileAssetModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.responses.CreateAssetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetRecognitionService {
    private final String bucketURL;
    private final AssetRepository assetRepository;
    private final FileAssetRepository fileAssetRepository;
    private final BarcodeDetectionService barcodeDetectionService;
    private final AssetNumberService assetNumberService;

    public CreateAssetResponseDto recognize(Long assetId) {
        Optional<AssetModel> asset = assetRepository.findById(assetId);
        Optional<FileAssetModel> fileAsset = fileAssetRepository.findByAssetId(assetId);
        if (asset.isPresent() && fileAsset.isPresent()) {
            AssetModel assetModel = asset.get();
            String filename = assetModel.getMainImage();
            BufferedImage bufferedImage = barcodeDetectionService.detectBarcode(filename);
            AssetNumberRecognitionDto assetInfo = assetNumberService.getAssetNumberAndConfidenceLevel(bufferedImage);

            return new CreateAssetResponseDto(fileAsset.get().getFile().getId(), assetModel.getId(), bucketURL + filename, assetInfo.getAssetNumber(), assetInfo.getConfidenceLevel());
        } else {
            throw new NoSuchElementException("Bem com id " + assetId + " não encontrado");
        }
    }
}
