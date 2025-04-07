package com.example.tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetDetailsDto {
    private Long fileId;
    private Long id;
    private String assetNumber;
    private String conservationState;
    private String description;
    private String formerAssetNumber;
    private String mainImage;
    private String place;
    private String responsible;
    private String situation;
    private List<String> images;

    public AssetDetailsDto(Long fileId, Long id, String assetNumber, String conservationState,
                           String description, String formerAssetNumber, String mainImage,
                           String place, String responsible, String situation) {
        this.fileId = fileId;
        this.id = id;
        this.assetNumber = assetNumber;
        this.conservationState = conservationState;
        this.description = description;
        this.formerAssetNumber = formerAssetNumber;
        this.mainImage = mainImage;
        this.place = place;
        this.responsible = responsible;
        this.situation = situation;
    }
}
