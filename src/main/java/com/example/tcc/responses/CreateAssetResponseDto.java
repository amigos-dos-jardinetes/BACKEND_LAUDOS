package com.example.tcc.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CreateAssetResponseDto {
    // Getters e Setters
    private Long fileId;
    private Long assetId;
    private String mainImage;
    private String assetNumber;
    private String confidenceLevel;

}
