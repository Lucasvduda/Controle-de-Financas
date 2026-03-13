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

/**
 * Controller da API de cartões de crédito. Todas as URLs: /api/cartoes, /api/cartoes/1, /api/cartoes/1/gastos, etc.
 */
@RestController
@RequestMapping("/api/cartoes")
public class CartaoCreditoController {

    private final CartaoCreditoRepository cartaoRepository;
    private final GastoRepository gastoRepository;
    private final FinanceiroService financeiroService;

    /** Construtor - o Spring chama automaticamente passando as implementações dos repositórios e do service */
    public CartaoCreditoController(CartaoCreditoRepository cartaoRepository,
                                   GastoRepository gastoRepository,
                                   FinanceiroService financeiroService) {
        this.cartaoRepository = cartaoRepository;
        this.gastoRepository = gastoRepository;
        this.financeiroService = financeiroService;
    }

    /** GET /api/cartoes - Lista todos os cartões cadastrados */
    @GetMapping
    public ResponseEntity<List<CartaoCredito>> listar() {
        return ResponseEntity.ok(cartaoRepository.findAll());
    }

    /** GET /api/cartoes/1 - Retorna o resumo do cartão com ID 1 (dados do cartão + lista de gastos). O {id} vem na URL */
    @GetMapping("/{id}")
    public ResponseEntity<ResumoCartao> detalhe(@PathVariable Long id) {  // @PathVariable = pega o {id} da URL
        return ResponseEntity.ok(financeiroService.getResumoCartao(id));
    }

    /** POST /api/cartoes - Cria um novo cartão. Corpo da requisição = JSON do cartão. @Valid valida os campos (@NotBlank, etc.) */
    @PostMapping
    public ResponseEntity<CartaoCredito> criar(@Valid @RequestBody CartaoCredito cartao) {
        return ResponseEntity.ok(cartaoRepository.save(cartao));  // save = INSERT ou UPDATE (se já tem id)
    }

    /** PUT /api/cartoes/1 - Atualiza o cartão com ID 1. Busca o existente, copia os novos dados e salva */
    @PutMapping("/{id}")
    public ResponseEntity<CartaoCredito> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody CartaoCredito cartao) {
        CartaoCredito existente = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));  // Se não achar, lança exceção
        existente.setNome(cartao.getNome());
        existente.setBandeira(cartao.getBandeira());
        existente.setLimiteTotal(cartao.getLimiteTotal());
        existente.setDiaFechamento(cartao.getDiaFechamento());
        existente.setDiaVencimento(cartao.getDiaVencimento());
        existente.setCor(cartao.getCor());
        return ResponseEntity.ok(cartaoRepository.save(existente));
    }

    /** DELETE /api/cartoes/1 - Remove o cartão. noContent() = resposta 204 sem corpo */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cartaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** POST /api/cartoes/1/gastos - Adiciona um gasto ao cartão 1. O gasto vem no corpo (JSON); associamos ao cartão e salvamos */
    @PostMapping("/{id}/gastos")
    public ResponseEntity<Gasto> adicionarGasto(@PathVariable Long id,
                                                @Valid @RequestBody Gasto gasto) {
        CartaoCredito cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        gasto.setCartao(cartao);  // Liga o gasto ao cartão antes de salvar
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }
}
