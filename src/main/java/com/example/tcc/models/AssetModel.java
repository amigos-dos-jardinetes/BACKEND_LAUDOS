package com.example.tcc.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Assets")
public class AssetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_number")
    private String assetNumber;

    @Column(name = "main_image")
    private String mainImage;

    @Column(name = "former_asset_number")
    private String formerAssetNumber;

    private String description;

    @Column(name = "conservation_state")
    private String conservationState;

    private String situation;

    private String place;

    private String responsible;

    public AssetModel() {}

    public AssetModel(String mainImage, String assetNumber, String formerAssetNumber, String description, String conservationState, String situation, String place, String responsible) {
        this.mainImage = mainImage;
        this.assetNumber = assetNumber;
        this.formerAssetNumber = formerAssetNumber;
        this.description = description;
        this.conservationState = conservationState;
        this.situation = situation;
        this.place = place;
        this.responsible = responsible;
    }

    public AssetModel(String mainImage) {
        this.mainImage = mainImage;
        this.assetNumber = "";
        this.formerAssetNumber = "";
        this.description = "";
        this.conservationState = "";
        this.situation = "";
        this.place = "";
        this.responsible = "";
    }

}
