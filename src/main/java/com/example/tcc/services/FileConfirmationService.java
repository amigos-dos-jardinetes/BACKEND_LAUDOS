package com.example.tcc.services;

import com.example.tcc.dto.AssetDetailsDto;
import com.example.tcc.dto.AssetImagesDto;
import com.example.tcc.models.FileModel;
import com.example.tcc.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileConfirmationService {
    private final String bucketURL;
    private final AssetDetailsService assetDetailsService;
    private final ImageLabelingService imageLabelingService;
    private final FileGenerationService fileGenerationService;
    private final FileRepository fileRepository;
    private final AWSS3Service s3Service;

    private void labelAllImages(List<AssetImagesDto> assetImages) {
        for (AssetImagesDto asset : assetImages) {
            asset.setMainImageLabeledBytes(imageLabelingService.drawString(asset.getMainImageBytes(), asset.getAssetNumber()));
            for (byte[] image : asset.getImagesBytes()) {
                byte[] labeledImage = imageLabelingService.drawString(image, asset.getAssetNumber());
                asset.getImagesLabeledBytes().add(labeledImage);
            }
        }
    }

    private List<AssetImagesDto> downloadAllImages(List<AssetDetailsDto> assets) {
        List<CompletableFuture<AssetImagesDto>> futures = assets.stream().map(asset ->
                CompletableFuture.supplyAsync(() -> {
                    AssetImagesDto assetImages = new AssetImagesDto(asset.getId(), asset.getAssetNumber());

                    // Download da imagem principal em paralelo
                    CompletableFuture<byte[]> mainImageFuture = CompletableFuture.supplyAsync(() -> s3Service.downloadImage(asset.getMainImage()));
                    assetImages.setMainImageBytes(mainImageFuture.join());

                    // Download das outras imagens em paralelo
                    List<CompletableFuture<byte[]>> imageFutures = asset.getImages().stream()
                            .map(image -> CompletableFuture.supplyAsync(() -> s3Service.downloadImage(image)))
                            .toList();

                    // Coleta os resultados dos downloads paralelos
                    assetImages.setImagesBytes(imageFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

                    return assetImages;
                })
        ).toList();

        // Aguarda que todos os downloads estejam completos
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private Boolean areAssetsValid(List<AssetDetailsDto> assets) {
        for (AssetDetailsDto asset : assets) {
            if(asset.getAssetNumber().isBlank() || asset.getMainImage().isBlank() || asset.getImages().size() < 2) {
                return false;
            }
        }
        return true;
    }

    private FileModel updateFileOnDatabase(FileModel updatedFile, List<AssetDetailsDto> assets, String filename) {
        updatedFile.setFilename(filename);
        updatedFile.setAssetQuantity((long) assets.size());
        updatedFile.setResponsible(assets.getFirst().getResponsible());
        updatedFile.setConsolidated(true);
        updatedFile.setConsolidatedAt(LocalDateTime.now());
        FileModel response = fileRepository.save(updatedFile);
        response.setFilename(bucketURL+filename);
        return response;
    }

    public FileModel confirm(Long fileId) throws Error, IOException {
        Optional<FileModel> file = fileRepository.findById(fileId);
        if(file.isPresent()) {
            List<AssetDetailsDto> assets = assetDetailsService.getAssets(fileId);
            if(assets.isEmpty() || !areAssetsValid(assets)) { throw new Error("Bens inválidos"); }

            List<AssetImagesDto> assetImages = downloadAllImages(assets);

            labelAllImages(assetImages);

            String filename = fileGenerationService.saveFile(assets, assetImages);
            return updateFileOnDatabase(file.get(), assets, filename);
        }
        throw new Error("File not found");
    }
}
