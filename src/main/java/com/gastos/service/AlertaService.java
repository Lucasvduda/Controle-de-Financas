package com.gastos.service;

import com.gastos.model.ConfiguracaoFinanceira;
import com.gastos.model.ContaPagar;
import com.gastos.repository.ContaPagarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final ContaPagarRepository contaPagarRepository;
    private final FinanceiroService financeiroService;
    private final JavaMailSender mailSender;

    public AlertaService(ContaPagarRepository contaPagarRepository,
                         FinanceiroService financeiroService,
                         JavaMailSender mailSender) {
        this.contaPagarRepository = contaPagarRepository;
        this.financeiroService = financeiroService;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 8 * * *") // todo dia às 8h
    public void verificarContasVencendo() {
        ConfiguracaoFinanceira config = financeiroService.getConfig();
        if (!config.isAlertasEmailAtivos() || config.getEmailAlertas() == null) {
            log.info("Alertas por email desativados ou email não configurado");
            return;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(config.getDiasAntesAlerta());

        List<ContaPagar> contasProximas = contaPagarRepository
                .findByAlertaEnviadoFalseAndPagaFalseAndDataVencimentoBetween(hoje, limite);

        List<ContaPagar> contasAtrasadas = contaPagarRepository
                .findByDataVencimentoBeforeAndPagaFalse(hoje);

        if (contasProximas.isEmpty() && contasAtrasadas.isEmpty()) {
            log.info("Nenhuma conta para alertar");
            return;
        }

        StringBuilder body = new StringBuilder();
        body.append("=== ALERTA DE CONTAS - Gerenciador de Gastos ===\n\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (!contasAtrasadas.isEmpty()) {
            body.append("⚠️ CONTAS ATRASADAS:\n");
            body.append("─".repeat(40)).append("\n");
            for (ContaPagar conta : contasAtrasadas) {
                body.append(String.format("• %s - R$ %.2f (Venceu em %s)\n",
                        conta.getDescricao(),
                        conta.getValor(),
                        conta.getDataVencimento().format(fmt)));
            }
            body.append("\n");
        }

        if (!contasProximas.isEmpty()) {
            body.append("📅 CONTAS PRÓXIMAS DO VENCIMENTO:\n");
            body.append("─".repeat(40)).append("\n");
            for (ContaPagar conta : contasProximas) {
                body.append(String.format("• %s - R$ %.2f (Vence em %s)\n",
                        conta.getDescricao(),
                        conta.getValor(),
                        conta.getDataVencimento().format(fmt)));
                conta.setAlertaEnviado(true);
            }
            body.append("\n");
        }

        BigDecimal totalAtrasado = contasAtrasadas.stream()
                .map(ContaPagar::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProximo = contasProximas.stream()
                .map(ContaPagar::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        body.append("─".repeat(40)).append("\n");
        body.append(String.format("Total atrasado: R$ %.2f\n", totalAtrasado));
        body.append(String.format("Total próximo: R$ %.2f\n", totalProximo));
        body.append("\nAcesse o Gerenciador de Gastos para mais detalhes.");

        try {
            enviarEmail(config.getEmailAlertas(),
                    "Alerta de Contas - Gerenciador de Gastos",
                    body.toString());
            contaPagarRepository.saveAll(contasProximas);
            log.info("Alerta enviado com sucesso para {}", config.getEmailAlertas());
        } catch (Exception e) {
            log.error("Erro ao enviar alerta por email: {}", e.getMessage());
        }
    }

    public void enviarAlertaManual() {
        verificarContasVencendo();
    }

    public void enviarEmail(String para, String assunto, String corpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(corpo);
        mailSender.send(message);
    }
}
