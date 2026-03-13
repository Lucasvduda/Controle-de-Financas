package com.gastos.controller;

import com.gastos.model.Investimento;
import com.gastos.repository.InvestimentoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controller de investimentos. CRUD completo: listar, criar, atualizar, deletar */
@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {

    private final InvestimentoRepository investimentoRepository;

    public InvestimentoController(InvestimentoRepository investimentoRepository) {
        this.investimentoRepository = investimentoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Investimento>> listar() {
        return ResponseEntity.ok(investimentoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Investimento> criar(@Valid @RequestBody Investimento investimento) {
        return ResponseEntity.ok(investimentoRepository.save(investimento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investimento> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody Investimento investimento) {
        Investimento existente = investimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));
        existente.setNome(investimento.getNome());
        existente.setValorAplicado(investimento.getValorAplicado());
        existente.setTaxaRendimentoMensal(investimento.getTaxaRendimentoMensal());
        existente.setDataAplicacao(investimento.getDataAplicacao());
        existente.setTipo(investimento.getTipo());
        return ResponseEntity.ok(investimentoRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        investimentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
