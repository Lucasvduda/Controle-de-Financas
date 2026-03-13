package com.gastos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um gasto (compra, conta, etc.).
 * Pode estar vinculado a um cartão de crédito (cartao) ou não (gastos fixos/variáveis sem cartão).
 * Cada objeto Gasto = uma linha na tabela "gastos".
 */
@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;  // Ex: "Supermercado", "Aluguel"

    @NotNull(message = "Valor é obrigatório")  // Não pode ser null (diferente de NotBlank que é para String)
    @Column(precision = 12, scale = 2)  // Valor em reais: até 12 dígitos, 2 casas decimais
    private BigDecimal valor;

    @Column(name = "data_gasto")  // Data em que o gasto foi feito
    private LocalDate dataGasto;  // LocalDate = data sem hora (ex: 2026-03-09)

    @Enumerated(EnumType.STRING)  // Salva no banco como texto ("MERCADO", "ALUGUEL") em vez de número
    private CategoriaGasto categoria;

    @Enumerated(EnumType.STRING)
    private TipoGasto tipo;  // FIXO (todo mês) ou VARIAVEL (eventual)

    private int parcelas = 1;  // Quantidade de parcelas (1 = à vista)

    @Column(name = "parcela_atual")  // Qual parcela é esta (ex: 2 de 12)
    private int parcelaAtual = 1;

    // Muitos gastos pertencem a UM cartão. FetchType.LAZY = só carrega o cartão quando acessar (economiza consultas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartao_id")  // Coluna cartao_id na tabela gastos guarda o ID do cartão
    @JsonBackReference("cartao-gastos")  // Lado "inverso" da relação - ao serializar para JSON não entra em loop com CartaoCredito
    private CartaoCredito cartao;  // Se null, o gasto não está em nenhum cartão (ex: gasto fixo direto)

    // Cópia do ID do cartão para consultas sem carregar o objeto cartão inteiro (insertable/updatable false = não grava aqui, grava via "cartao")
    @Column(name = "cartao_id", insertable = false, updatable = false)
    private Long cartaoId;

    private boolean pago = false;  // Se true, gasto já foi pago

    public Gasto() {}

    // ========== Getters e Setters ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataGasto() { return dataGasto; }
    public void setDataGasto(LocalDate dataGasto) { this.dataGasto = dataGasto; }
    public CategoriaGasto getCategoria() { return categoria; }
    public void setCategoria(CategoriaGasto categoria) { this.categoria = categoria; }
    public TipoGasto getTipo() { return tipo; }
    public void setTipo(TipoGasto tipo) { this.tipo = tipo; }
    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }
    public int getParcelaAtual() { return parcelaAtual; }
    public void setParcelaAtual(int parcelaAtual) { this.parcelaAtual = parcelaAtual; }
    public CartaoCredito getCartao() { return cartao; }
    public void setCartao(CartaoCredito cartao) { this.cartao = cartao; }
    public Long getCartaoId() { return cartaoId; }
    public void setCartaoId(Long cartaoId) { this.cartaoId = cartaoId; }
    public boolean isPago() { return pago; }  // Para boolean o getter pode ser isPago() em vez de getPago()
    public void setPago(boolean pago) { this.pago = pago; }
}
