package com.gastos.repository;

import com.gastos.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    List<ContaPagar> findByPagaFalseOrderByDataVencimentoAsc();

    List<ContaPagar> findByDataVencimentoBetweenAndPagaFalse(LocalDate inicio, LocalDate fim);

    List<ContaPagar> findByDataVencimentoBeforeAndPagaFalse(LocalDate data);

    List<ContaPagar> findByAlertaEnviadoFalseAndPagaFalseAndDataVencimentoBetween(
            LocalDate inicio, LocalDate fim);

    List<ContaPagar> findByDataVencimentoAndPagaFalse(LocalDate data);
}
