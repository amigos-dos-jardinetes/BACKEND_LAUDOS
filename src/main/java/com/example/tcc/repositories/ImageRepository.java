package com.example.tcc.repositories;

import com.example.tcc.models.ImageModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<ImageModel, Long> {
    @Query(value = "SELECT filename FROM Images WHERE asset_id = :assetId", nativeQuery = true)
    List<String> findFilenamesByAssetId(@Param("assetId") Long assetId);

    @Query("SELECT i FROM ImageModel i WHERE i.asset.id IN :assetIds")
    List<ImageModel> findFilenamesByAssetIds(@Param("assetIds") List<Long> assetIds);

    @Modifying
    @Query(value = "DELETE FROM Images WHERE asset_id = :assetId", nativeQuery = true)
    void deleteByAssetId(@Param("assetId") Long assetId);

    List<ImageModel> findByFilename(String filename);
}