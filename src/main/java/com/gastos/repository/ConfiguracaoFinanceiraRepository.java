package com.gastos.repository;

import com.gastos.model.ConfiguracaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoFinanceiraRepository extends JpaRepository<ConfiguracaoFinanceira, Long> {
    // Em geral existe só uma configuração (id=1). O service busca findAll().get(0) ou cria uma nova se vazio.
}
