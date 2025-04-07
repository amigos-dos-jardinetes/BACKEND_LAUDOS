package com.example.tcc.repositories;

import com.example.tcc.dto.AssetDetailsDto;
import com.example.tcc.models.AssetModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface AssetRepository extends CrudRepository<AssetModel, Long> {

    @Query(value="SELECT a.responsible FROM assets a " +
                "JOIN files_assets fa ON " +
                "a.id = fa.asset_id WHERE fa.file_id = :fileId", nativeQuery = true)
    List<String> findAssetResponsiblesByFileId(Long fileId);

    @Query("SELECT new com.example.tcc.dto.AssetDetailsDto( "+
                "fa.file.id, " +
                "a.id, " +
                "a.assetNumber, " +
                "a.conservationState, " +
                "a.description, " +
                "a.formerAssetNumber, " +
                "a.mainImage, " +
                "a.place, " +
                "a.responsible, " +
                "a.situation" +
                ") "+
            "FROM AssetModel a "+
            "JOIN FileAssetModel fa ON a.id = fa.asset.id "+
            "WHERE fa.file.id = :fileId")
    List<AssetDetailsDto> findAssetsByFileId(Long fileId);
}