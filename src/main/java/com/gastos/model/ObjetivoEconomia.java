package com.gastos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidade que representa um objetivo de economia (ex: "Viagem", "Carro").
 * O usuário define valor meta e quanto pretende economizar por mês; o sistema calcula percentual e meses restantes.
 */
@Entity
@Table(name = "objetivos_economia")
public class ObjetivoEconomia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do objetivo é obrigatório")
    private String nome;

    @Column(name = "valor_meta", precision = 12, scale = 2)
    private BigDecimal valorMeta = BigDecimal.ZERO;  // Quanto quer juntar no total

    @Column(name = "valor_atual", precision = 12, scale = 2)
    private BigDecimal valorAtual = BigDecimal.ZERO;  // Quanto já juntou (atualizado quando "deposita")

    @Column(name = "economia_mensal", precision = 12, scale = 2)
    private BigDecimal economiaMensal = BigDecimal.ZERO;  // Quanto pretende guardar por mês

    private String icone;  // Emoji ou ícone para exibir na tela (ex: "🎯")

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

    /** Percentual já alcançado (valorAtual / valorMeta * 100). Retorna 0 se meta for zero. */
    @Transient
    public BigDecimal getPercentualConcluido() {
        if (valorMeta.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorAtual.divide(valorMeta, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /** Quantos meses faltam para atingir a meta (falta / economiaMensal). Retorna -1 se economia mensal <= 0, 0 se já atingiu. */
    @Transient
    public int getMesesRestantes() {
        if (economiaMensal.compareTo(BigDecimal.ZERO) <= 0) return -1;
        BigDecimal falta = valorMeta.subtract(valorAtual);
        if (falta.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return falta.divide(economiaMensal, 0, RoundingMode.CEILING).intValue();  // CEILING = arredonda pra cima
    }
}
