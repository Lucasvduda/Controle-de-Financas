package com.gastos.repository;

import com.gastos.model.CategoriaGasto;
import com.gastos.model.Gasto;
import com.gastos.model.TipoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositório de gastos - operações na tabela "gastos".
 * Métodos com nome findBy... são "traduzidos" pelo Spring em consultas SQL automaticamente.
 * Ex: findByTipo(tipo) vira "SELECT * FROM gastos WHERE tipo = ?"
 */
@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    // Busca todos os gastos cujo tipo seja o informado (FIXO ou VARIAVEL)
    List<Gasto> findByTipo(TipoGasto tipo);

    // Gastos que pertencem ao cartão com o ID informado
    List<Gasto> findByCartaoId(Long cartaoId);

    // Gastos de uma categoria específica (ex: todos de MERCADO)
    List<Gasto> findByCategoria(CategoriaGasto categoria);

    // Gastos cuja data está entre inicio e fim (útil para relatórios por período)
    List<Gasto> findByDataGastoBetween(LocalDate inicio, LocalDate fim);

    // Soma o valor de todos os gastos do tipo informado. COALESCE(SUM..., 0) evita null quando não há registros
    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.tipo = :tipo")
    BigDecimal somarPorTipo(@Param("tipo") TipoGasto tipo);

    // Soma o valor de todos os gastos de um cartão específico
    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.cartao.id = :cartaoId")
    BigDecimal somarPorCartao(@Param("cartaoId") Long cartaoId);

    // Retorna uma lista de [categoria, soma]: ex. [MERCADO, 500.00], [ALUGUEL, 1500.00]. Usado no dashboard para gráfico por categoria
    @Query("SELECT g.categoria, COALESCE(SUM(g.valor), 0) FROM Gasto g GROUP BY g.categoria")
    List<Object[]> somarPorCategoria();
}
