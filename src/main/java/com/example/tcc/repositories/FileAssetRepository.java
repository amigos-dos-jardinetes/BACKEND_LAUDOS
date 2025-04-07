package com.example.tcc.repositories;

import com.example.tcc.models.FileAssetModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAssetRepository extends CrudRepository<FileAssetModel, Long> {
    @Modifying
    @Query(value = "DELETE FROM files_assets WHERE asset_id = :assetId", nativeQuery = true)
    void deleteByAssetId(@Param("assetId") Long assetId);

    Optional<FileAssetModel> findByAssetId(Long assetId);
    List<FileAssetModel> findByFileId(Long fileId);
}