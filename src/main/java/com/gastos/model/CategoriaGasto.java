package com.gastos.model;

public enum CategoriaGasto {
    ALUGUEL("Aluguel"),
    ALIMENTACAO("Alimentação"),
    MERCADO("Mercado"),
    TRANSPORTE("Transporte"),
    SAUDE("Saúde"),
    EDUCACAO("Educação"),
    LAZER("Lazer"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CONTA_LUZ("Conta de Luz"),
    CONTA_AGUA("Conta de Água"),
    CONTA_INTERNET("Internet"),
    CONTA_TELEFONE("Telefone"),
    CONTA_GAS("Gás"),
    ASSINATURA("Assinatura"),
    COMPRAS("Compras"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaGasto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
