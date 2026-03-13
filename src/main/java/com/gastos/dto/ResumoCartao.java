package com.gastos.dto;

import java.math.BigDecimal;
import java.util.List;

/** DTO do detalhe do cartão: dados do cartão + lista de gastos em formato simples (evita referência circular no JSON) */
public class ResumoCartao {

    private Long id;
    private String nome;
    private String bandeira;
    private String cor;
    private BigDecimal limiteTotal;
    private BigDecimal limiteUsado;
    private BigDecimal limiteDisponivel;
    private int diaFechamento;
    private int diaVencimento;
    private List<GastoResumo> gastos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public BigDecimal getLimiteTotal() { return limiteTotal; }
    public void setLimiteTotal(BigDecimal limiteTotal) { this.limiteTotal = limiteTotal; }
    public BigDecimal getLimiteUsado() { return limiteUsado; }
    public void setLimiteUsado(BigDecimal limiteUsado) { this.limiteUsado = limiteUsado; }
    public BigDecimal getLimiteDisponivel() { return limiteDisponivel; }
    public void setLimiteDisponivel(BigDecimal limiteDisponivel) { this.limiteDisponivel = limiteDisponivel; }
    public int getDiaFechamento() { return diaFechamento; }
    public void setDiaFechamento(int diaFechamento) { this.diaFechamento = diaFechamento; }
    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento; }
    public List<GastoResumo> getGastos() { return gastos; }
    public void setGastos(List<GastoResumo> gastos) { this.gastos = gastos; }

    public static class GastoResumo {
        private Long id;
        private String descricao;
        private BigDecimal valor;
        private String data;
        private String categoria;
        private int parcelas;
        private int parcelaAtual;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        public int getParcelas() { return parcelas; }
        public void setParcelas(int parcelas) { this.parcelas = parcelas; }
        public int getParcelaAtual() { return parcelaAtual; }
        public void setParcelaAtual(int parcelaAtual) { this.parcelaAtual = parcelaAtual; }
    }
}
