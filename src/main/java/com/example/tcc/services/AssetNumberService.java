package com.example.tcc.services;

import com.example.tcc.dto.AssetNumberRecognitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssetNumberService {
    private final BarcodeReaderService barcodeReaderService;
    private final OCRService ocrService;

    public AssetNumberRecognitionDto getAssetNumberAndConfidenceLevel(BufferedImage bufferedImage) {
        String barcode = barcodeReaderService.read(bufferedImage);
        if (barcode != null) {
            System.out.println(barcode + ", 1.0");
            return new AssetNumberRecognitionDto(barcode, "1.0");
        }

        AssetNumberRecognitionDto asset = ocrService.performOCR(bufferedImage);
        if(asset != null) {
            System.out.println(asset.getAssetNumber()+", "+asset.getConfidenceLevel());
            return asset;
        }

        return new AssetNumberRecognitionDto("", "0.0");
    }
}
