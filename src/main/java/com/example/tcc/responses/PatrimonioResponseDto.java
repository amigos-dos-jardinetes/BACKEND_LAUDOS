package com.example.tcc.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatrimonioResponseDto {
    private String assetNumber;
    private String formerAssetNumber;
    private String description;
    private String responsible;
}
