package com.gastos.controller;

import com.gastos.dto.ResumoCartao;
import com.gastos.model.CartaoCredito;
import com.gastos.model.Gasto;
import com.gastos.repository.CartaoCreditoRepository;
import com.gastos.repository.GastoRepository;
import com.gastos.service.FinanceiroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoCreditoController {

    private final CartaoCreditoRepository cartaoRepository;
    private final GastoRepository gastoRepository;
    private final FinanceiroService financeiroService;

    public CartaoCreditoController(CartaoCreditoRepository cartaoRepository,
                                   GastoRepository gastoRepository,
                                   FinanceiroService financeiroService) {
        this.cartaoRepository = cartaoRepository;
        this.gastoRepository = gastoRepository;
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public ResponseEntity<List<CartaoCredito>> listar() {
        return ResponseEntity.ok(cartaoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumoCartao> detalhe(@PathVariable Long id) {
        return ResponseEntity.ok(financeiroService.getResumoCartao(id));
    }

    @PostMapping
    public ResponseEntity<CartaoCredito> criar(@Valid @RequestBody CartaoCredito cartao) {
        return ResponseEntity.ok(cartaoRepository.save(cartao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartaoCredito> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody CartaoCredito cartao) {
        CartaoCredito existente = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        existente.setNome(cartao.getNome());
        existente.setBandeira(cartao.getBandeira());
        existente.setLimiteTotal(cartao.getLimiteTotal());
        existente.setDiaFechamento(cartao.getDiaFechamento());
        existente.setDiaVencimento(cartao.getDiaVencimento());
        existente.setCor(cartao.getCor());
        return ResponseEntity.ok(cartaoRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cartaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/gastos")
    public ResponseEntity<Gasto> adicionarGasto(@PathVariable Long id,
                                                @Valid @RequestBody Gasto gasto) {
        CartaoCredito cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        gasto.setCartao(cartao);
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }
}
