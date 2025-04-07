package com.example.tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssetNumberRecognitionDto {
    private String assetNumber;
    private String confidenceLevel;
}
