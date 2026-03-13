package com.gastos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma conta a pagar (luz, água, aluguel, etc.) com data de vencimento.
 * Usada para listar contas pendentes, marcar como paga e enviar alertas por e-mail quando está perto do vencimento.
 */
@Entity
@Table(name = "contas_pagar")
public class ContaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;  // Ex: "Conta de Luz - Março"

    @NotNull(message = "Valor é obrigatório")
    @Column(precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data de vencimento é obrigatória")
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;  // Dia em que a conta vence

    @Enumerated(EnumType.STRING)
    private CategoriaGasto categoria;

    private boolean paga = false;  // true quando o usuário marca "Pagar"

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;  // Preenchido quando paga = true

    private boolean recorrente = false;  // Se true, ao marcar como paga o sistema cria outra conta para o mês seguinte

    @Column(name = "alerta_enviado")
    private boolean alertaEnviado = false;  // Evita enviar o mesmo alerta por e-mail mais de uma vez

    private String observacao;  // Notas livres do usuário

    public ContaPagar() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public CategoriaGasto getCategoria() { return categoria; }
    public void setCategoria(CategoriaGasto categoria) { this.categoria = categoria; }
    public boolean isPaga() { return paga; }
    public void setPaga(boolean paga) { this.paga = paga; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public boolean isRecorrente() { return recorrente; }
    public void setRecorrente(boolean recorrente) { this.recorrente = recorrente; }
    public boolean isAlertaEnviado() { return alertaEnviado; }
    public void setAlertaEnviado(boolean alertaEnviado) { this.alertaEnviado = alertaEnviado; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
