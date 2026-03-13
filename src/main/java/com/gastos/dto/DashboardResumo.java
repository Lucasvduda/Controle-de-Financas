package com.gastos.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** DTO com todos os dados exibidos na tela inicial: salário, totais, contas a vencer, gastos por categoria */
public class DashboardResumo {

    private BigDecimal salario;
    private BigDecimal totalInvestimentos;
    private BigDecimal rendimentoMensal;
    private BigDecimal totalGastosFixos;
    private BigDecimal totalGastosVariaveis;
    private BigDecimal totalGastos;
    private BigDecimal saldoMensal;
    private String tempoRestante;
    private int contasVencendoHoje;
    private int contasVencendoSemana;
    private int contasAtrasadas;
    private Map<String, BigDecimal> gastosPorCategoria;
    private List<ContaAlerta> proximasContas;

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public BigDecimal getTotalInvestimentos() { return totalInvestimentos; }
    public void setTotalInvestimentos(BigDecimal totalInvestimentos) { this.totalInvestimentos = totalInvestimentos; }
    public BigDecimal getRendimentoMensal() { return rendimentoMensal; }
    public void setRendimentoMensal(BigDecimal rendimentoMensal) { this.rendimentoMensal = rendimentoMensal; }
    public BigDecimal getTotalGastosFixos() { return totalGastosFixos; }
    public void setTotalGastosFixos(BigDecimal totalGastosFixos) { this.totalGastosFixos = totalGastosFixos; }
    public BigDecimal getTotalGastosVariaveis() { return totalGastosVariaveis; }
    public void setTotalGastosVariaveis(BigDecimal totalGastosVariaveis) { this.totalGastosVariaveis = totalGastosVariaveis; }
    public BigDecimal getTotalGastos() { return totalGastos; }
    public void setTotalGastos(BigDecimal totalGastos) { this.totalGastos = totalGastos; }
    public BigDecimal getSaldoMensal() { return saldoMensal; }
    public void setSaldoMensal(BigDecimal saldoMensal) { this.saldoMensal = saldoMensal; }
    public String getTempoRestante() { return tempoRestante; }
    public void setTempoRestante(String tempoRestante) { this.tempoRestante = tempoRestante; }
    public int getContasVencendoHoje() { return contasVencendoHoje; }
    public void setContasVencendoHoje(int contasVencendoHoje) { this.contasVencendoHoje = contasVencendoHoje; }
    public int getContasVencendoSemana() { return contasVencendoSemana; }
    public void setContasVencendoSemana(int contasVencendoSemana) { this.contasVencendoSemana = contasVencendoSemana; }
    public int getContasAtrasadas() { return contasAtrasadas; }
    public void setContasAtrasadas(int contasAtrasadas) { this.contasAtrasadas = contasAtrasadas; }
    public Map<String, BigDecimal> getGastosPorCategoria() { return gastosPorCategoria; }
    public void setGastosPorCategoria(Map<String, BigDecimal> gastosPorCategoria) { this.gastosPorCategoria = gastosPorCategoria; }
    public List<ContaAlerta> getProximasContas() { return proximasContas; }
    public void setProximasContas(List<ContaAlerta> proximasContas) { this.proximasContas = proximasContas; }

    public static class ContaAlerta {
        private Long id;
        private String descricao;
        private BigDecimal valor;
        private String dataVencimento;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
        public String getDataVencimento() { return dataVencimento; }
        public void setDataVencimento(String dataVencimento) { this.dataVencimento = dataVencimento; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
