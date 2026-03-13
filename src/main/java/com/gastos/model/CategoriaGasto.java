package com.gastos.model;

/**
 * Enum = tipo com lista FIXA de valores. Em vez de guardar texto livre ("mercado", "Mercado", "MERCADO"),
 * usamos constantes com nome. Assim evitamos erros de digitação e padronizamos.
 * Cada valor pode ter um "atributo" extra - aqui: descricao (texto para exibir na tela).
 */
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

    private final String descricao;  // Texto amigável para mostrar na interface (ex: "Alimentação")

    /**
     * "Construtor" do enum - cada constante chama isso ao ser definida.
     * Ex: MERCADO("Mercado") chama CategoriaGasto("Mercado") e guarda em descricao.
     */
    CategoriaGasto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
