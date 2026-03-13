package com.gastos.model;

/**
 * Enum que classifica o gasto em FIXO (recorrente todo mês, ex: aluguel) ou VARIAVEL (eventual, ex: compras).
 * Usado para somar separadamente na projeção e no dashboard.
 */
public enum TipoGasto {
    FIXO("Fixo"),
    VARIAVEL("Variável");

    private final String descricao;

    TipoGasto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
