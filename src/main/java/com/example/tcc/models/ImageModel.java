package com.example.tcc.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Images")
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private AssetModel asset;

    @Column(nullable = false, unique = true)
    private String filename;

    public ImageModel() {}

    public ImageModel(AssetModel asset, String filename) {
        this.asset = asset;
        this.filename = filename;
    }

    public ImageModel(String filename) {
        this.asset = null;
        this.filename = filename;
    }

}
