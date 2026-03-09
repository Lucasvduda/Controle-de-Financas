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

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByTipo(TipoGasto tipo);

    List<Gasto> findByCartaoId(Long cartaoId);

    List<Gasto> findByCategoria(CategoriaGasto categoria);

    List<Gasto> findByDataGastoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.tipo = :tipo")
    BigDecimal somarPorTipo(@Param("tipo") TipoGasto tipo);

    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.cartao.id = :cartaoId")
    BigDecimal somarPorCartao(@Param("cartaoId") Long cartaoId);

    @Query("SELECT g.categoria, COALESCE(SUM(g.valor), 0) FROM Gasto g GROUP BY g.categoria")
    List<Object[]> somarPorCategoria();
}
