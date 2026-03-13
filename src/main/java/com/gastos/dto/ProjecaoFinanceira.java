package com.gastos.dto;

import java.math.BigDecimal;
import java.util.List;

/** DTO da projeção: totais atuais + lista de ProjecaoMes (saldo mês a mês) + texto "tempo restante" */
public class ProjecaoFinanceira {

    private BigDecimal saldoMensal;
    private BigDecimal totalGastosFixos;
    private BigDecimal totalGastosVariaveis;
    private BigDecimal totalGastos;
    private BigDecimal totalInvestimentos;
    private BigDecimal rendimentoMensal;
    private BigDecimal salario;
    private String tempoRestante;
    private List<ProjecaoMes> projecaoMeses;

    public BigDecimal getSaldoMensal() { return saldoMensal; }
    public void setSaldoMensal(BigDecimal saldoMensal) { this.saldoMensal = saldoMensal; }
    public BigDecimal getTotalGastosFixos() { return totalGastosFixos; }
    public void setTotalGastosFixos(BigDecimal totalGastosFixos) { this.totalGastosFixos = totalGastosFixos; }
    public BigDecimal getTotalGastosVariaveis() { return totalGastosVariaveis; }
    public void setTotalGastosVariaveis(BigDecimal totalGastosVariaveis) { this.totalGastosVariaveis = totalGastosVariaveis; }
    public BigDecimal getTotalGastos() { return totalGastos; }
    public void setTotalGastos(BigDecimal totalGastos) { this.totalGastos = totalGastos; }
    public BigDecimal getTotalInvestimentos() { return totalInvestimentos; }
    public void setTotalInvestimentos(BigDecimal totalInvestimentos) { this.totalInvestimentos = totalInvestimentos; }
    public BigDecimal getRendimentoMensal() { return rendimentoMensal; }
    public void setRendimentoMensal(BigDecimal rendimentoMensal) { this.rendimentoMensal = rendimentoMensal; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public String getTempoRestante() { return tempoRestante; }
    public void setTempoRestante(String tempoRestante) { this.tempoRestante = tempoRestante; }
    public List<ProjecaoMes> getProjecaoMeses() { return projecaoMeses; }
    public void setProjecaoMeses(List<ProjecaoMes> projecaoMeses) { this.projecaoMeses = projecaoMeses; }
}
