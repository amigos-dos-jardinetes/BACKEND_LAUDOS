package com.example.tcc.repositories;

import com.example.tcc.models.FileModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends CrudRepository<FileModel, Long> {
    List<FileModel> findByUserEmail(String userEmail);
}