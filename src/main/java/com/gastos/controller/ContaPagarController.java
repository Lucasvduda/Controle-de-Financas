package com.gastos.controller;

import com.gastos.model.ContaPagar;
import com.gastos.repository.ContaPagarRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Controller de contas a pagar. URLs: /api/contas, /api/contas/pendentes, /api/contas/1/pagar */
@RestController
@RequestMapping("/api/contas")
public class ContaPagarController {

    private final ContaPagarRepository contaPagarRepository;

    public ContaPagarController(ContaPagarRepository contaPagarRepository) {
        this.contaPagarRepository = contaPagarRepository;
    }

    /** GET /api/contas - Lista todas as contas (pagas e pendentes) */
    @GetMapping
    public ResponseEntity<List<ContaPagar>> listar() {
        return ResponseEntity.ok(contaPagarRepository.findAll());
    }

    /** GET /api/contas/pendentes - Apenas contas não pagas, ordenadas por vencimento */
    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaPagar>> listarPendentes() {
        return ResponseEntity.ok(contaPagarRepository.findByPagaFalseOrderByDataVencimentoAsc());
    }

    /** GET /api/contas/atrasadas - Contas cujo vencimento já passou e ainda não foram pagas */
    @GetMapping("/atrasadas")
    public ResponseEntity<List<ContaPagar>> listarAtrasadas() {
        return ResponseEntity.ok(contaPagarRepository
                .findByDataVencimentoBeforeAndPagaFalse(LocalDate.now()));
    }

    /** POST /api/contas - Cria nova conta a pagar */
    @PostMapping
    public ResponseEntity<ContaPagar> criar(@Valid @RequestBody ContaPagar conta) {
        return ResponseEntity.ok(contaPagarRepository.save(conta));
    }

    /** PUT /api/contas/1 - Atualiza dados da conta (descrição, valor, vencimento, etc.) */
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

    /**
     * PATCH /api/contas/1/pagar - Marca a conta como paga (data de hoje).
     * Se a conta for recorrente, cria automaticamente outra conta com vencimento no mês seguinte.
     */
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<ContaPagar> marcarComoPaga(@PathVariable Long id) {
        ContaPagar conta = contaPagarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        conta.setPaga(true);
        conta.setDataPagamento(LocalDate.now());

        if (conta.isRecorrente()) {
            // Cria nova conta para o próximo mês (ex: luz de abril vira luz de maio)
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

    /** DELETE /api/contas/1 - Remove a conta */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contaPagarRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
