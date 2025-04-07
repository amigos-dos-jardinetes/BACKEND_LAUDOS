package com.example.tcc.controllers;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import com.example.tcc.models.PatrimonioModel;
import com.example.tcc.repositories.PatrimonioRepository;
import com.example.tcc.responses.PatrimonioResponseDto;
import com.example.tcc.services.PatrimonioSearchService;
import com.example.tcc.util.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/patrimonio")
@RequiredArgsConstructor
public class PatrimonioController {
    private final PatrimonioSearchService patrimonioSearchService;
    private final PatrimonioRepository patrimonioRepository;

    @GetMapping
    public ResponseEntity<?> retrieve(
        @RequestParam(value = "tombo", required = false) String assetNumber,
        @RequestParam(value = "tombo_antigo", required = false) String formerAssetNumber
    ) {
        try {
            PatrimonioModel asset;
            if(assetNumber != null) {
                asset = patrimonioSearchService.getPatrimonioByAssetNumber(assetNumber);
            } else if(formerAssetNumber != null) {
                asset = patrimonioSearchService.getPatrimonioByFormerAssetNumber(formerAssetNumber);
            } else {
                throw new Exception("Nenhum tombo ou tombo antigo foi fornecido");
            }
            if(asset == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(new PatrimonioResponseDto(
                asset.getTombo(),
                asset.getTomboAntigo(),
                asset.getDescricao(),
                asset.getResponsavel()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> create(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo CSV está vazio.");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim())) {

            // Truncar a tabela
            patrimonioRepository.truncateTable();

            List<PatrimonioModel> patrimonios = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                PatrimonioModel patrimonio = new PatrimonioModel();
                patrimonio.setLocal(record.get("local"));
                patrimonio.setTombo(record.get("tombo"));
                patrimonio.setTomboAntigo(record.get("tombo_antigo"));
                patrimonio.setResponsavel(record.get("responsavel"));
                patrimonio.setDescricao(record.get("descricao"));
                patrimonio.setEstadoConservacao("BOM");
                patrimonio.setSituacao("EM USO");
                patrimonios.add(patrimonio);
            }

            patrimonioRepository.saveAll(patrimonios);
            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao processar o arquivo.");
        }
    }
}