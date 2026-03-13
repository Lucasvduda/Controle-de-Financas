package com.gastos.repository;

import com.gastos.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório de contas a pagar - tabela "contas_pagar".
 * Os nomes dos métodos seguem o padrão do Spring Data: findBy + nome do campo + condição.
 * Ex: findByPagaFalse = WHERE paga = false. OrderByDataVencimentoAsc = ORDER BY data_vencimento ASC.
 */
@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    // Contas ainda não pagas, ordenadas pela data de vencimento (mais próximas primeiro)
    List<ContaPagar> findByPagaFalseOrderByDataVencimentoAsc();

    // Contas que vencem entre inicio e fim e ainda não foram pagas
    List<ContaPagar> findByDataVencimentoBetweenAndPagaFalse(LocalDate inicio, LocalDate fim);

    // Contas atrasadas: vencimento antes da data informada e não pagas
    List<ContaPagar> findByDataVencimentoBeforeAndPagaFalse(LocalDate data);

    // Contas que vencem entre inicio e fim, não pagas e para as quais ainda não enviamos alerta (evita repetir e-mail)
    List<ContaPagar> findByAlertaEnviadoFalseAndPagaFalseAndDataVencimentoBetween(
            LocalDate inicio, LocalDate fim);

    // Contas que vencem exatamente na data informada (ex: "vencem hoje")
    List<ContaPagar> findByDataVencimentoAndPagaFalse(LocalDate data);
}
