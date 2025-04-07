package com.example.tcc.repositories;

import com.example.tcc.models.PatrimonioModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrimonioRepository extends CrudRepository<PatrimonioModel, String> {
    List<PatrimonioModel> findByTombo(String tombo);
    List<PatrimonioModel> findByTomboAntigo(String tomboAntigo);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE Patrimonios", nativeQuery = true)
    void truncateTable();
}