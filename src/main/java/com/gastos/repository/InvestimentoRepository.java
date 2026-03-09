package com.gastos.repository;

import com.gastos.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    @Query("SELECT COALESCE(SUM(i.valorAplicado), 0) FROM Investimento i")
    BigDecimal somarTotal();
}
