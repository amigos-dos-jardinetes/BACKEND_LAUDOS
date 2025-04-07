package com.example.tcc.services;

import com.example.tcc.models.AssetModel;
import com.example.tcc.models.FileAssetModel;
import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.responses.AssetConfirmationResponseDto;
import com.example.tcc.responses.AssetInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static com.example.tcc.util.RemoveLeadingZeros.removeLeadingZeros;

@Service
@RequiredArgsConstructor
public class AssetConfirmationService {
    private final AssetInfoService assetInfoService;
    private final AssetDeletionService assetDeletionService;
    private final AssetRepository assetRepository;
    private final FileAssetRepository fileAssetRepository;

    private Boolean isAssetConditionValid(AssetInfoResponseDto assetInfo) {
        return (Objects.equals(assetInfo.getEstadoConservacao(), "BOM") && Objects.equals(assetInfo.getSituacao(), "EM USO"));
    }

    private Boolean isOnlyOneResponsible(AssetInfoResponseDto assetInfo, Long fileId) {
        List<String> responsibles = assetRepository.findAssetResponsiblesByFileId(fileId);

        for (String responsible : responsibles) {
            if (!responsible.isBlank() && !responsible.equals(assetInfo.getResponsavel())) {
                return false; // Encontrou um responsável diferente
            }
        }

        return true; // Todos os assets têm o mesmo responsável
    }

    private Boolean isAssetNumberAlreadyInTheFile(AssetInfoResponseDto assetInfo, Long fileId) {
        List<FileAssetModel> fileAssetModelList = fileAssetRepository.findByFileId(fileId);
        if(fileAssetModelList.isEmpty()) return false;
        for(FileAssetModel fileAssetModel : fileAssetModelList) {
            if(Objects.equals(fileAssetModel.getAsset().getAssetNumber(), assetInfo.getTombo()))
                return true;
        }
        return false;
    }

    private void updateAsset(AssetModel asset, AssetInfoResponseDto assetInfo) {
        asset.setAssetNumber(assetInfo.getTombo());
        asset.setFormerAssetNumber(assetInfo.getTomboAntigo());
        asset.setPlace(assetInfo.getLocal());
        asset.setDescription(assetInfo.getDescricao());
        asset.setConservationState(assetInfo.getEstadoConservacao());
        asset.setSituation(assetInfo.getSituacao());
        asset.setResponsible(assetInfo.getResponsavel());
        assetRepository.save(asset);
    }

    public AssetConfirmationResponseDto confirm(String assetNumber, Long assetId) throws Exception {
        Optional<FileAssetModel> fileAsset = fileAssetRepository.findByAssetId(assetId);

        if (fileAsset.isEmpty()) {
            throw new Exception("Asset ID não associado a nenhum arquivo");
        }
        Long fileId = fileAsset.get().getFile().getId();

        AssetInfoResponseDto assetInfo = assetInfoService.getAssetInfo(removeLeadingZeros(assetNumber));

        if(assetInfo == null) {
            throw new Exception("Não foi possível encontrar bem com esse número de patrimônio");
        } else if(!isAssetConditionValid(assetInfo)) {
            throw new Exception("Estado de conservação ou situação inválidos");
        } else if(!isOnlyOneResponsible(assetInfo, fileId)) {
            throw new Exception("Arquivo contém mais de um responsável pelos bens");
        } else if(isAssetNumberAlreadyInTheFile(assetInfo, fileId)) {
            throw new Exception("Arquivo já contém esse bem");
        }

        AssetModel asset = fileAsset.get().getAsset();
        updateAsset(asset, assetInfo);
        return new AssetConfirmationResponseDto(
            assetInfo.getId(),
            assetInfo.getTombo(),
            assetInfo.getTomboAntigo(),
            assetInfo.getDescricao(),
            assetInfo.getEstadoConservacao(),
            assetInfo.getSituacao(),
            assetInfo.getLocal(),
            assetInfo.getResponsavel()
        );
    }
}
