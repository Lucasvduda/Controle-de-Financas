package com.gastos.controller;

import com.gastos.dto.DashboardResumo;
import com.gastos.dto.ProjecaoFinanceira;
import com.gastos.model.CategoriaGasto;
import com.gastos.model.ConfiguracaoFinanceira;
import com.gastos.service.AlertaService;
import com.gastos.service.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller que expõe as URLs do dashboard, projeção, configuração e alertas.
 * Todas as URLs começam com /api (definido em @RequestMapping).
 */
@RestController  // Indica que esta classe responde a requisições HTTP e devolve dados (em geral JSON)
@RequestMapping("/api")  // Prefixo de todas as URLs: /api/dashboard, /api/projecao, etc.
public class DashboardController {

    // O Spring injeta essas dependências no construtor (injeção de dependência) - não fazemos new FinanceiroService()
    private final FinanceiroService financeiroService;
    private final AlertaService alertaService;

    public DashboardController(FinanceiroService financeiroService, AlertaService alertaService) {
        this.financeiroService = financeiroService;
        this.alertaService = alertaService;
    }

    /** GET /api/dashboard - Retorna o resumo completo para a tela inicial (salário, gastos, contas a vencer, etc.) */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResumo> getDashboard() {
        return ResponseEntity.ok(financeiroService.getDashboard());  // 200 OK + corpo = objeto convertido em JSON
    }

    /** GET /api/projecao?meses=12 - Retorna a projeção financeira para os próximos N meses. Se não passar ?meses=, usa 12 */
    @GetMapping("/projecao")
    public ResponseEntity<ProjecaoFinanceira> getProjecao(
            @RequestParam(defaultValue = "12") int meses) {  // Parâmetro da URL: /api/projecao?meses=24
        return ResponseEntity.ok(financeiroService.getProjecao(meses));
    }

    /** GET /api/configuracao - Retorna a configuração atual (salário, e-mail de alertas, etc.) */
    @GetMapping("/configuracao")
    public ResponseEntity<ConfiguracaoFinanceira> getConfig() {
        return ResponseEntity.ok(financeiroService.getConfig());
    }

    /** PUT /api/configuracao - Atualiza a configuração. O corpo da requisição (JSON) vira o objeto ConfiguracaoFinanceira */
    @PutMapping("/configuracao")
    public ResponseEntity<ConfiguracaoFinanceira> salvarConfig(
            @RequestBody ConfiguracaoFinanceira config) {  // @RequestBody = lê o JSON do corpo e converte para o objeto
        return ResponseEntity.ok(financeiroService.salvarConfig(config));
    }

    /** GET /api/categorias - Retorna a lista de categorias de gasto (enum). Útil para popular dropdown no frontend */
    @GetMapping("/categorias")
    public ResponseEntity<CategoriaGasto[]> getCategorias() {
        return ResponseEntity.ok(CategoriaGasto.values());  // values() retorna array com todos os valores do enum
    }

    /** POST /api/alertas/enviar - Envia na hora os alertas de contas por e-mail (além do envio automático diário às 8h) */
    @PostMapping("/alertas/enviar")
    public ResponseEntity<Map<String, String>> enviarAlertas() {
        try {
            alertaService.enviarAlertaManual();
            return ResponseEntity.ok(Map.of("status", "Alertas enviados com sucesso"));  // Map.of = mapa imutável { "status": "..." }
        } catch (Exception e) {
            // Se der erro (ex: falha no SMTP), devolve 500 com mensagem de erro
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao enviar alertas: " + e.getMessage()));
        }
    }
}
