package com.example.tcc.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetConfirmationResponseDto {
    private Long id;
    private String assetNumber;
    private String formerAssetNumber;
    private String description;
    private String conservationState;
    private String situation;
    private String place;
    private String responsible;
}
