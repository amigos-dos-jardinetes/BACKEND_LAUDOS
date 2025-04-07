package com.example.tcc.responses;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetInfoResponseDto {
    private Long id;
    private String tombo;
    private String tomboAntigo;
    private String descricao;
    private String estadoConservacao;
    private String situacao;
    private String local;
    private String responsavel;
}
