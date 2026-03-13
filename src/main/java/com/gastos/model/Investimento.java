package com.gastos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um investimento (CDB, Tesouro Direto, etc.).
 * Guarda o valor aplicado e a taxa de rendimento mensal para calcular o rendimento na projeção e no dashboard.
 */
@Entity
@Table(name = "investimentos")
public class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;  // Ex: "CDB Banco X", "Tesouro Selic"

    @Column(name = "valor_aplicado", precision = 12, scale = 2)
    private BigDecimal valorAplicado = BigDecimal.ZERO;

    @Column(name = "taxa_rendimento_mensal", precision = 8, scale = 6)  // Ex: 0.009 = 0,9% ao mês
    private BigDecimal taxaRendimentoMensal = new BigDecimal("0.009");

    @Column(name = "data_aplicacao")
    private LocalDate dataAplicacao;

    private String tipo;  // Ex: "Renda Fixa", "Ações"

    public Investimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValorAplicado() { return valorAplicado; }
    public void setValorAplicado(BigDecimal valorAplicado) { this.valorAplicado = valorAplicado; }
    public BigDecimal getTaxaRendimentoMensal() { return taxaRendimentoMensal; }
    public void setTaxaRendimentoMensal(BigDecimal taxaRendimentoMensal) { this.taxaRendimentoMensal = taxaRendimentoMensal; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Rendimento que este investimento gera por mês (valor * taxa).
     * @Transient = não é coluna no banco, calculado na hora.
     */
    @Transient
    public BigDecimal getRendimentoMensal() {
        return valorAplicado.multiply(taxaRendimentoMensal);
    }
}
