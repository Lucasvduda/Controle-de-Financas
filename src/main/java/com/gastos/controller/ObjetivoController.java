package com.gastos.controller;

import com.gastos.model.ObjetivoEconomia;
import com.gastos.repository.ObjetivoEconomiaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/objetivos")
public class ObjetivoController {

    private final ObjetivoEconomiaRepository objetivoRepository;

    public ObjetivoController(ObjetivoEconomiaRepository objetivoRepository) {
        this.objetivoRepository = objetivoRepository;
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoEconomia>> listar() {
        return ResponseEntity.ok(objetivoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ObjetivoEconomia> criar(@Valid @RequestBody ObjetivoEconomia objetivo) {
        return ResponseEntity.ok(objetivoRepository.save(objetivo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoEconomia> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ObjetivoEconomia objetivo) {
        ObjetivoEconomia existente = objetivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Objetivo não encontrado"));
        existente.setNome(objetivo.getNome());
        existente.setValorMeta(objetivo.getValorMeta());
        existente.setValorAtual(objetivo.getValorAtual());
        existente.setEconomiaMensal(objetivo.getEconomiaMensal());
        existente.setIcone(objetivo.getIcone());
        return ResponseEntity.ok(objetivoRepository.save(existente));
    }

    @PatchMapping("/{id}/depositar")
    public ResponseEntity<ObjetivoEconomia> depositar(@PathVariable Long id,
                                                      @RequestParam BigDecimal valor) {
        ObjetivoEconomia objetivo = objetivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Objetivo não encontrado"));
        objetivo.setValorAtual(objetivo.getValorAtual().add(valor));
        return ResponseEntity.ok(objetivoRepository.save(objetivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        objetivoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
