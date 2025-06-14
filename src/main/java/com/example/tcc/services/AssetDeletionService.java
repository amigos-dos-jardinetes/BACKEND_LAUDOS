package com.example.tcc.services;

import com.example.tcc.repositories.AssetRepository;
import com.example.tcc.repositories.FileAssetRepository;
import com.example.tcc.repositories.ImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetDeletionService {
    private static final Logger logger = LoggerFactory.getLogger(AssetDeletionService.class);

    private final AssetRepository assetRepository;
    private final FileAssetRepository fileAssetRepository;
    private final ImageRepository imageRepository;
    private final AWSS3Service s3Service;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void delete(Long assetId) {
        logger.info("Iniciando deleção do asset com ID: {}", assetId);

        assetRepository.findById(assetId).ifPresentOrElse(asset -> {
            try {
                // Imagens relacionadas (inclui a imagem principal do asset)
                List<String> filenames = imageRepository.findFilenamesByAssetId(assetId);
                if (asset.getMainImage() != null) {
                    filenames.add(asset.getMainImage());
                }

                // Deleta as imagens do bucket
                s3Service.deleteMultipleObjects(filenames);

                // Deleta as imagens do banco
                imageRepository.deleteByAssetId(assetId);

                // Deleta relacionamento com arquivos
                fileAssetRepository.deleteByAssetId(assetId);

                entityManager.clear(); // Necessário por antes disso estar rodando alguns deletes com nativeQuery

                // Deleta o asset
                assetRepository.deleteById(assetId);
                logger.info("Asset e objetos relacionados deletados com sucesso.");
            } catch (Exception e) {
                logger.error("Erro ao deletar asset com ID {}: {}", assetId, e.getMessage(), e);
                throw new RuntimeException("Erro ao deletar asset com ID: " + assetId, e);
            }
        }, () -> logger.warn("Asset com ID {} não encontrado.", assetId));
    }
}
