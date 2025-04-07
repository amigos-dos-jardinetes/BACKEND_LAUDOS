package com.example.tcc.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "Patrimonios")
public class PatrimonioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tombo")
    private String tombo;

    @Column(name = "tombo_antigo")
    private String tomboAntigo;

    @Column(name = "descricao", length = 2048)
    private String descricao;

    @Column(name = "estado_conservacao")
    private String estadoConservacao;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "local")
    private String local;

    @Column(name = "responsavel")
    private String responsavel;

    @Column(name = "data_incorporacao")
    private String dataIncorporacao;

    @Column(name = "codigo_campus")
    private String codigoCampus;

    @Column(name = "descricao_campus")
    private String descricaoCampus;
}
