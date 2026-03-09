package com.gastos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "objetivos_economia")
public class ObjetivoEconomia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do objetivo é obrigatório")
    private String nome;

    @Column(name = "valor_meta", precision = 12, scale = 2)
    private BigDecimal valorMeta = BigDecimal.ZERO;

    @Column(name = "valor_atual", precision = 12, scale = 2)
    private BigDecimal valorAtual = BigDecimal.ZERO;

    @Column(name = "economia_mensal", precision = 12, scale = 2)
    private BigDecimal economiaMensal = BigDecimal.ZERO;

    private String icone;

    public ObjetivoEconomia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValorMeta() { return valorMeta; }
    public void setValorMeta(BigDecimal valorMeta) { this.valorMeta = valorMeta; }
    public BigDecimal getValorAtual() { return valorAtual; }
    public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }
    public BigDecimal getEconomiaMensal() { return economiaMensal; }
    public void setEconomiaMensal(BigDecimal economiaMensal) { this.economiaMensal = economiaMensal; }
    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    @Transient
    public BigDecimal getPercentualConcluido() {
        if (valorMeta.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorAtual.divide(valorMeta, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Transient
    public int getMesesRestantes() {
        if (economiaMensal.compareTo(BigDecimal.ZERO) <= 0) return -1;
        BigDecimal falta = valorMeta.subtract(valorAtual);
        if (falta.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return falta.divide(economiaMensal, 0, RoundingMode.CEILING).intValue();
    }
}
