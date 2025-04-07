package com.example.tcc.services;

import com.example.tcc.models.FileModel;
import com.example.tcc.repositories.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileCreationService {
    private final FileRepository fileRepository;

    public FileCreationService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileModel create(String userEmail) {
        return fileRepository.save(new FileModel(userEmail));
    }

    public Boolean canCreateFile(String userEmail) {
        List<FileModel> userFiles = fileRepository.findByUserEmail(userEmail);
        List<FileModel> notConsolidatedFiles = userFiles.stream().filter(file -> !file.getConsolidated()).toList();
        return notConsolidatedFiles.isEmpty();
    }
}
