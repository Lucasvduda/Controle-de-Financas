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

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final FinanceiroService financeiroService;
    private final AlertaService alertaService;

    public DashboardController(FinanceiroService financeiroService, AlertaService alertaService) {
        this.financeiroService = financeiroService;
        this.alertaService = alertaService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResumo> getDashboard() {
        return ResponseEntity.ok(financeiroService.getDashboard());
    }

    @GetMapping("/projecao")
    public ResponseEntity<ProjecaoFinanceira> getProjecao(
            @RequestParam(defaultValue = "12") int meses) {
        return ResponseEntity.ok(financeiroService.getProjecao(meses));
    }

    @GetMapping("/configuracao")
    public ResponseEntity<ConfiguracaoFinanceira> getConfig() {
        return ResponseEntity.ok(financeiroService.getConfig());
    }

    @PutMapping("/configuracao")
    public ResponseEntity<ConfiguracaoFinanceira> salvarConfig(
            @RequestBody ConfiguracaoFinanceira config) {
        return ResponseEntity.ok(financeiroService.salvarConfig(config));
    }

    @GetMapping("/categorias")
    public ResponseEntity<CategoriaGasto[]> getCategorias() {
        return ResponseEntity.ok(CategoriaGasto.values());
    }

    @PostMapping("/alertas/enviar")
    public ResponseEntity<Map<String, String>> enviarAlertas() {
        try {
            alertaService.enviarAlertaManual();
            return ResponseEntity.ok(Map.of("status", "Alertas enviados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao enviar alertas: " + e.getMessage()));
        }
    }
}
