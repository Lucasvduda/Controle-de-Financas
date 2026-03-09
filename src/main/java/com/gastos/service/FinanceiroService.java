package com.gastos.service;

import com.gastos.dto.*;
import com.gastos.model.*;
import com.gastos.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    private final GastoRepository gastoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final ConfiguracaoFinanceiraRepository configRepository;
    private final CartaoCreditoRepository cartaoRepository;

    private static final BigDecimal TAXA_RENDIMENTO = new BigDecimal("0.009");

    public FinanceiroService(GastoRepository gastoRepository,
                             InvestimentoRepository investimentoRepository,
                             ContaPagarRepository contaPagarRepository,
                             ConfiguracaoFinanceiraRepository configRepository,
                             CartaoCreditoRepository cartaoRepository) {
        this.gastoRepository = gastoRepository;
        this.investimentoRepository = investimentoRepository;
        this.contaPagarRepository = contaPagarRepository;
        this.configRepository = configRepository;
        this.cartaoRepository = cartaoRepository;
    }

    public DashboardResumo getDashboard() {
        DashboardResumo resumo = new DashboardResumo();
        ConfiguracaoFinanceira config = getConfig();

        BigDecimal salario = config.getSalario();
        BigDecimal totalInvestimentos = investimentoRepository.somarTotal();
        BigDecimal rendimentoMensal = totalInvestimentos.multiply(TAXA_RENDIMENTO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalFixos = gastoRepository.somarPorTipo(TipoGasto.FIXO);
        BigDecimal totalVariaveis = gastoRepository.somarPorTipo(TipoGasto.VARIAVEL);
        BigDecimal totalGastos = totalFixos.add(totalVariaveis);
        BigDecimal saldoMensal = salario.add(rendimentoMensal).subtract(totalGastos);

        resumo.setSalario(salario);
        resumo.setTotalInvestimentos(totalInvestimentos);
        resumo.setRendimentoMensal(rendimentoMensal);
        resumo.setTotalGastosFixos(totalFixos);
        resumo.setTotalGastosVariaveis(totalVariaveis);
        resumo.setTotalGastos(totalGastos);
        resumo.setSaldoMensal(saldoMensal);
        resumo.setTempoRestante(calcularTempoRestante(salario, totalInvestimentos, totalGastos));

        LocalDate hoje = LocalDate.now();
        LocalDate semana = hoje.plusDays(7);

        resumo.setContasVencendoHoje(
                contaPagarRepository.findByDataVencimentoAndPagaFalse(hoje).size());
        resumo.setContasVencendoSemana(
                contaPagarRepository.findByDataVencimentoBetweenAndPagaFalse(hoje, semana).size());
        resumo.setContasAtrasadas(
                contaPagarRepository.findByDataVencimentoBeforeAndPagaFalse(hoje).size());

        Map<String, BigDecimal> gastosPorCat = new LinkedHashMap<>();
        for (Object[] row : gastoRepository.somarPorCategoria()) {
            CategoriaGasto cat = (CategoriaGasto) row[0];
            BigDecimal val = (BigDecimal) row[1];
            gastosPorCat.put(cat.getDescricao(), val);
        }
        resumo.setGastosPorCategoria(gastosPorCat);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<DashboardResumo.ContaAlerta> alertas = contaPagarRepository
                .findByPagaFalseOrderByDataVencimentoAsc()
                .stream()
                .limit(10)
                .map(c -> {
                    DashboardResumo.ContaAlerta a = new DashboardResumo.ContaAlerta();
                    a.setId(c.getId());
                    a.setDescricao(c.getDescricao());
                    a.setValor(c.getValor());
                    a.setDataVencimento(c.getDataVencimento().format(fmt));
                    if (c.getDataVencimento().isBefore(hoje)) {
                        a.setStatus("ATRASADA");
                    } else if (c.getDataVencimento().isEqual(hoje)) {
                        a.setStatus("VENCE_HOJE");
                    } else if (c.getDataVencimento().isBefore(semana)) {
                        a.setStatus("PROXIMA");
                    } else {
                        a.setStatus("NORMAL");
                    }
                    return a;
                })
                .collect(Collectors.toList());
        resumo.setProximasContas(alertas);

        return resumo;
    }

    public ProjecaoFinanceira getProjecao(int meses) {
        ConfiguracaoFinanceira config = getConfig();
        ProjecaoFinanceira projecao = new ProjecaoFinanceira();

        BigDecimal salario = config.getSalario();
        BigDecimal totalInvestimentos = investimentoRepository.somarTotal();
        BigDecimal totalFixos = gastoRepository.somarPorTipo(TipoGasto.FIXO);
        BigDecimal totalVariaveis = gastoRepository.somarPorTipo(TipoGasto.VARIAVEL);
        BigDecimal totalGastos = totalFixos.add(totalVariaveis);
        BigDecimal rendimentoMensal = totalInvestimentos.multiply(TAXA_RENDIMENTO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal saldoMensal = salario.add(rendimentoMensal).subtract(totalGastos);

        projecao.setSalario(salario);
        projecao.setTotalInvestimentos(totalInvestimentos);
        projecao.setRendimentoMensal(rendimentoMensal);
        projecao.setTotalGastosFixos(totalFixos);
        projecao.setTotalGastosVariaveis(totalVariaveis);
        projecao.setTotalGastos(totalGastos);
        projecao.setSaldoMensal(saldoMensal);
        projecao.setTempoRestante(calcularTempoRestante(salario, totalInvestimentos, totalGastos));

        List<ProjecaoMes> listaMeses = new ArrayList<>();
        BigDecimal saldo = totalInvestimentos;
        String[] nomeMeses = {"", "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        LocalDate dataAtual = LocalDate.now();

        for (int i = 1; i <= meses; i++) {
            LocalDate mesFuturo = dataAtual.plusMonths(i);
            BigDecimal rendimento = saldo.multiply(TAXA_RENDIMENTO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal saldoInicio = saldo;
            saldo = saldo.add(rendimento).add(salario).subtract(totalGastos);
            String label = nomeMeses[mesFuturo.getMonthValue()] + "/" + mesFuturo.getYear();
            listaMeses.add(new ProjecaoMes(i, label, saldoInicio, rendimento, salario, totalGastos, saldo));
            if (saldo.compareTo(BigDecimal.ZERO) < 0) break;
        }
        projecao.setProjecaoMeses(listaMeses);

        return projecao;
    }

    public ResumoCartao getResumoCartao(Long cartaoId) {
        CartaoCredito cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        ResumoCartao resumo = new ResumoCartao();
        resumo.setId(cartao.getId());
        resumo.setNome(cartao.getNome());
        resumo.setBandeira(cartao.getBandeira());
        resumo.setCor(cartao.getCor());
        resumo.setLimiteTotal(cartao.getLimiteTotal());
        resumo.setLimiteUsado(cartao.getLimiteUsado());
        resumo.setLimiteDisponivel(cartao.getLimiteDisponivel());
        resumo.setDiaFechamento(cartao.getDiaFechamento());
        resumo.setDiaVencimento(cartao.getDiaVencimento());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<ResumoCartao.GastoResumo> gastos = cartao.getGastos().stream()
                .map(g -> {
                    ResumoCartao.GastoResumo gr = new ResumoCartao.GastoResumo();
                    gr.setId(g.getId());
                    gr.setDescricao(g.getDescricao());
                    gr.setValor(g.getValor());
                    gr.setData(g.getDataGasto() != null ? g.getDataGasto().format(fmt) : "");
                    gr.setCategoria(g.getCategoria() != null ? g.getCategoria().getDescricao() : "");
                    gr.setParcelas(g.getParcelas());
                    gr.setParcelaAtual(g.getParcelaAtual());
                    return gr;
                })
                .collect(Collectors.toList());
        resumo.setGastos(gastos);

        return resumo;
    }

    private String calcularTempoRestante(BigDecimal salario, BigDecimal investimentos, BigDecimal totalGastos) {
        BigDecimal rendimentoMensal = investimentos.multiply(TAXA_RENDIMENTO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal saldoMensal = salario.add(rendimentoMensal).subtract(totalGastos);

        if (saldoMensal.compareTo(BigDecimal.ZERO) >= 0) {
            return "Sustentável - Patrimônio crescendo!";
        }

        BigDecimal saldo = investimentos;
        int meses = 0;

        while (saldo.compareTo(BigDecimal.ZERO) > 0 && meses < 1200) {
            BigDecimal rend = saldo.multiply(TAXA_RENDIMENTO).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.add(rend).add(salario).subtract(totalGastos);
            meses++;
        }

        if (meses >= 1200) return "Mais de 100 anos";

        int anos = meses / 12;
        int mesesRest = meses % 12;

        if (anos > 0) {
            return anos + " ano" + (anos > 1 ? "s" : "") + " e " + mesesRest + " mes" + (mesesRest != 1 ? "es" : "");
        }
        return meses + " mes" + (meses != 1 ? "es" : "");
    }

    public ConfiguracaoFinanceira getConfig() {
        List<ConfiguracaoFinanceira> configs = configRepository.findAll();
        if (configs.isEmpty()) {
            ConfiguracaoFinanceira config = new ConfiguracaoFinanceira();
            return configRepository.save(config);
        }
        return configs.get(0);
    }

    public ConfiguracaoFinanceira salvarConfig(ConfiguracaoFinanceira config) {
        ConfiguracaoFinanceira existente = getConfig();
        existente.setSalario(config.getSalario());
        existente.setEmailAlertas(config.getEmailAlertas());
        existente.setTelefoneAlertas(config.getTelefoneAlertas());
        existente.setDiasAntesAlerta(config.getDiasAntesAlerta());
        existente.setAlertasEmailAtivos(config.isAlertasEmailAtivos());
        return configRepository.save(existente);
    }
}
