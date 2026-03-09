package com.gastos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cartoes_credito")
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do cartão é obrigatório")
    private String nome;

    private String bandeira;

    @Column(name = "limite_total", precision = 12, scale = 2)
    private BigDecimal limiteTotal = BigDecimal.ZERO;

    @Column(name = "dia_fechamento")
    private int diaFechamento;

    @Column(name = "dia_vencimento")
    private int diaVencimento;

    private String cor;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cartao-gastos")
    private List<Gasto> gastos = new ArrayList<>();

    public CartaoCredito() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
    public BigDecimal getLimiteTotal() { return limiteTotal; }
    public void setLimiteTotal(BigDecimal limiteTotal) { this.limiteTotal = limiteTotal; }
    public int getDiaFechamento() { return diaFechamento; }
    public void setDiaFechamento(int diaFechamento) { this.diaFechamento = diaFechamento; }
    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public List<Gasto> getGastos() { return gastos; }
    public void setGastos(List<Gasto> gastos) { this.gastos = gastos; }

    @Transient
    public BigDecimal getLimiteUsado() {
        return gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getLimiteDisponivel() {
        return limiteTotal.subtract(getLimiteUsado());
    }
}
