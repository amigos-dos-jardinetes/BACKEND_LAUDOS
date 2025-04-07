package com.example.tcc.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "FilesAssets")
public class FileAssetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private FileModel file;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetModel asset;

    public FileAssetModel(FileModel file, AssetModel asset) {
        this.file = file;
        this.asset = asset;
    }
}
