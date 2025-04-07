package com.example.tcc.services;

import com.example.tcc.dto.AssetDetailsDto;
import com.example.tcc.dto.AssetNumberRecognitionDto;
import com.example.tcc.responses.CreateAssetResponseDto;
import com.example.tcc.models.AssetModel;
import com.example.tcc.models.FileAssetModel;
import com.example.tcc.models.FileModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetCreationService {
    private final String bucketURL;
    private final AssetRepository assetRepository;
    private final FileAssetRepository fileAssetRepository;
    private final FileRepository fileRepository;
    private final ImageUploadService imageUploadService;
    private final BarcodeDetectionService barcodeDetectionService;
    private final AssetNumberService assetNumberService;
    private final AssetDetailsService assetDetailsService;

    private Boolean areAssetsValid(List<AssetDetailsDto> assets) {
        for (AssetDetailsDto asset : assets) {
            if(asset.getAssetNumber().isBlank() || asset.getMainImage().isBlank() || asset.getImages().size() < 2) {
                return false;
            }
        }
        return true;
    }

    public CreateAssetResponseDto createAndRecognize(Long fileId, MultipartFile image) throws Exception {
        Optional<FileModel> file = fileRepository.findById(fileId);
        if (file.isPresent()) {
            List<AssetDetailsDto> assets = assetDetailsService.getAssets(fileId);
            if(!assets.isEmpty() && !areAssetsValid(assets)) { throw new Exception("Arquivo contém um bem incompleto. Não foi possível adicionar novo bem"); }

            String filename = imageUploadService.saveImage(image);
            BufferedImage bufferedImage = barcodeDetectionService.detectBarcode(filename);
            AssetNumberRecognitionDto assetInfo = assetNumberService.getAssetNumberAndConfidenceLevel(bufferedImage);

            AssetModel asset = assetRepository.save(new AssetModel(filename));
            fileAssetRepository.save(new FileAssetModel(file.get(), asset));
            return new CreateAssetResponseDto(fileId, asset.getId(), bucketURL + filename, assetInfo.getAssetNumber(), assetInfo.getConfidenceLevel());
        } else {
            throw new NoSuchElementException("Arquivo com id " + fileId + " não encontrado");
        }
    }
}
