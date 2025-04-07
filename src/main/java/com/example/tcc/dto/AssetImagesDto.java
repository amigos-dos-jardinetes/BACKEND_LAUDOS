package com.example.tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetImagesDto {
    private Long assetId;
    private String assetNumber;
    private byte[] mainImageBytes;
    private byte[] mainImageLabeledBytes;
    private List<byte[]> imagesBytes;
    private List<byte[]> imagesLabeledBytes;

    public AssetImagesDto(Long assetId, String assetNumber) {
        this.assetId = assetId;
        this.assetNumber = assetNumber;
        this.imagesBytes = new ArrayList<>();
        this.imagesLabeledBytes = new ArrayList<>();
        this.mainImageBytes = new byte[0];
        this.mainImageLabeledBytes = new byte[0];
    }
}

