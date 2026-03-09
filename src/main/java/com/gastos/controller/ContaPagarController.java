package com.gastos.controller;

import com.gastos.model.ContaPagar;
import com.gastos.repository.ContaPagarRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaPagarController {

    private final ContaPagarRepository contaPagarRepository;

    public ContaPagarController(ContaPagarRepository contaPagarRepository) {
        this.contaPagarRepository = contaPagarRepository;
    }

    @GetMapping
    public ResponseEntity<List<ContaPagar>> listar() {
        return ResponseEntity.ok(contaPagarRepository.findAll());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaPagar>> listarPendentes() {
        return ResponseEntity.ok(contaPagarRepository.findByPagaFalseOrderByDataVencimentoAsc());
    }

    @GetMapping("/atrasadas")
    public ResponseEntity<List<ContaPagar>> listarAtrasadas() {
        return ResponseEntity.ok(contaPagarRepository
                .findByDataVencimentoBeforeAndPagaFalse(LocalDate.now()));
    }

    @PostMapping
    public ResponseEntity<ContaPagar> criar(@Valid @RequestBody ContaPagar conta) {
        return ResponseEntity.ok(contaPagarRepository.save(conta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaPagar> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody ContaPagar conta) {
        ContaPagar existente = contaPagarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        existente.setDescricao(conta.getDescricao());
        existente.setValor(conta.getValor());
        existente.setDataVencimento(conta.getDataVencimento());
        existente.setCategoria(conta.getCategoria());
        existente.setRecorrente(conta.isRecorrente());
        existente.setObservacao(conta.getObservacao());
        return ResponseEntity.ok(contaPagarRepository.save(existente));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<ContaPagar> marcarComoPaga(@PathVariable Long id) {
        ContaPagar conta = contaPagarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        conta.setPaga(true);
        conta.setDataPagamento(LocalDate.now());

        if (conta.isRecorrente()) {
            ContaPagar novaConta = new ContaPagar();
            novaConta.setDescricao(conta.getDescricao());
            novaConta.setValor(conta.getValor());
            novaConta.setDataVencimento(conta.getDataVencimento().plusMonths(1));
            novaConta.setCategoria(conta.getCategoria());
            novaConta.setRecorrente(true);
            novaConta.setObservacao(conta.getObservacao());
            contaPagarRepository.save(novaConta);
        }

        return ResponseEntity.ok(contaPagarRepository.save(conta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contaPagarRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
