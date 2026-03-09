package com.gastos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Column(precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_gasto")
    private LocalDate dataGasto;

    @Enumerated(EnumType.STRING)
    private CategoriaGasto categoria;

    @Enumerated(EnumType.STRING)
    private TipoGasto tipo;

    private int parcelas = 1;

    @Column(name = "parcela_atual")
    private int parcelaAtual = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartao_id")
    @JsonBackReference("cartao-gastos")
    private CartaoCredito cartao;

    @Column(name = "cartao_id", insertable = false, updatable = false)
    private Long cartaoId;

    private boolean pago = false;

    public Gasto() {}

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
    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }
}
