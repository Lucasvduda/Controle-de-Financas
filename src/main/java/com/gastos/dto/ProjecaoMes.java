package com.gastos.dto;

import java.math.BigDecimal;

public class ProjecaoMes {

    private int mes;
    private String label;
    private BigDecimal saldoInicio;
    private BigDecimal rendimento;
    private BigDecimal entrada;
    private BigDecimal saida;
    private BigDecimal saldoFim;

    public ProjecaoMes() {}

    public ProjecaoMes(int mes, String label, BigDecimal saldoInicio, BigDecimal rendimento,
                       BigDecimal entrada, BigDecimal saida, BigDecimal saldoFim) {
        this.mes = mes;
        this.label = label;
        this.rendimento = rendimento;
        this.entrada = entrada;
        this.saida = saida;
        this.saldoInicio = saldoInicio;
        this.saldoFim = saldoFim;
    }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getSaldoInicio() { return saldoInicio; }
    public void setSaldoInicio(BigDecimal saldoInicio) { this.saldoInicio = saldoInicio; }
    public BigDecimal getRendimento() { return rendimento; }
    public void setRendimento(BigDecimal rendimento) { this.rendimento = rendimento; }
    public BigDecimal getEntrada() { return entrada; }
    public void setEntrada(BigDecimal entrada) { this.entrada = entrada; }
    public BigDecimal getSaida() { return saida; }
    public void setSaida(BigDecimal saida) { this.saida = saida; }
    public BigDecimal getSaldoFim() { return saldoFim; }
    public void setSaldoFim(BigDecimal saldoFim) { this.saldoFim = saldoFim; }
}
