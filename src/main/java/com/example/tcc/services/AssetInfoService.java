package com.example.tcc.services;

import com.example.tcc.models.PatrimonioModel;
import com.example.tcc.responses.AssetInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetInfoService {
    private final PatrimonioSearchService patrimonioSearchService;

    public AssetInfoResponseDto getAssetInfo(String assetNumber) {
        PatrimonioModel asset = patrimonioSearchService.getPatrimonioByAssetNumber(assetNumber);
        if(asset == null) {
            return null;
        }
        return new AssetInfoResponseDto(
            (long)asset.getId(),
            asset.getTombo(),
            asset.getTomboAntigo(),
            asset.getDescricao(),
            asset.getEstadoConservacao(),
            asset.getSituacao(),
            asset.getLocal(),
            asset.getResponsavel()
        );
    }
}
