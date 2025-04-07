package com.example.tcc.services;

import com.example.tcc.dto.AssetDetailsDto;
import com.example.tcc.models.ImageModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetDetailsService {
    private final String bucketURL;
    private final AssetRepository assetRepository;
    private final ImageRepository imageRepository;

    public List<AssetDetailsDto> getAssets(Long fileId) {
        List<AssetDetailsDto> assets = assetRepository.findAssetsByFileId(fileId);

        List<Long> assetIds = assets.stream()
                .map(AssetDetailsDto::getId)
                .collect(Collectors.toList());

        List<ImageModel> images = imageRepository.findFilenamesByAssetIds(assetIds);

        Map<Long, List<String>> imagesMap = images.stream()
                .collect(Collectors.groupingBy(
                        image -> image.getAsset().getId(),
                        Collectors.mapping(ImageModel::getFilename, Collectors.toList())
                ));

        assets.forEach(asset -> asset.setImages(imagesMap.getOrDefault(asset.getId(), Collections.emptyList())));
        return assets;
    }

    private void concatenateStringToAssets(List<AssetDetailsDto> assets, String prefix) {
        assets.forEach(asset -> {
            if (asset.getMainImage() != null) {
                asset.setMainImage(prefix + asset.getMainImage());
            }

            List<String> updatedImages = asset.getImages().stream()
                    .map(image -> prefix + image)
                    .collect(Collectors.toList());
            asset.setImages(updatedImages);
        });
    }
    public List<AssetDetailsDto> getAssetsWithURL(Long fileId) {
        List<AssetDetailsDto> assets = getAssets(fileId);
        concatenateStringToAssets(assets, bucketURL);
        return assets;
    }
}
