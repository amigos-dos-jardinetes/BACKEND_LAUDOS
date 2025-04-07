package com.example.tcc.services;

import com.example.tcc.models.PatrimonioModel;
import com.example.tcc.repositories.PatrimonioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.tcc.util.RemoveLeadingZeros.removeLeadingZeros;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatrimonioSearchService {
    private final PatrimonioRepository patrimonioRepository;

    public PatrimonioModel getPatrimonioByAssetNumber(String assetNumber) {
        List<PatrimonioModel> assets = patrimonioRepository.findByTombo(removeLeadingZeros(assetNumber));
        if(!assets.isEmpty()) {
            return assets.getFirst();
        }
        return null;
    }

    public PatrimonioModel getPatrimonioByFormerAssetNumber(String formerAssetNumber) {
        List<PatrimonioModel> assets = patrimonioRepository.findByTomboAntigo(removeLeadingZeros(formerAssetNumber));
        if(!assets.isEmpty()) {
            return assets.getFirst();
        }
        return null;
    }
}
