package com.gastos.repository;

import com.gastos.model.ObjetivoEconomia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetivoEconomiaRepository extends JpaRepository<ObjetivoEconomia, Long> {
    // Apenas os métodos padrão do JpaRepository: findAll, findById, save, deleteById, etc.
}
