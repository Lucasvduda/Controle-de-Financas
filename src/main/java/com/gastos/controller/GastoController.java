package com.gastos.controller;

import com.gastos.model.Gasto;
import com.gastos.model.TipoGasto;
import com.gastos.repository.GastoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Controller de gastos (fixos e variáveis). URLs: /api/gastos, /api/gastos/fixos, /api/gastos/variaveis */
@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    /** GET /api/gastos - Lista todos os gastos */
    @GetMapping
    public ResponseEntity<List<Gasto>> listar() {
        return ResponseEntity.ok(gastoRepository.findAll());
    }

    /** GET /api/gastos/fixos - Apenas gastos do tipo FIXO (aluguel, contas mensais, etc.) */
    @GetMapping("/fixos")
    public ResponseEntity<List<Gasto>> listarFixos() {
        return ResponseEntity.ok(gastoRepository.findByTipo(TipoGasto.FIXO));
    }

    /** GET /api/gastos/variaveis - Apenas gastos do tipo VARIAVEL (mercado, compras eventuais) */
    @GetMapping("/variaveis")
    public ResponseEntity<List<Gasto>> listarVariaveis() {
        return ResponseEntity.ok(gastoRepository.findByTipo(TipoGasto.VARIAVEL));
    }

    /** POST /api/gastos - Cria novo gasto (pode ser com ou sem cartão vinculado) */
    @PostMapping
    public ResponseEntity<Gasto> criar(@Valid @RequestBody Gasto gasto) {
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }

    /** PUT /api/gastos/1 - Atualiza o gasto. Atualizamos campo a campo e salvamos */
    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizar(@PathVariable Long id, @Valid @RequestBody Gasto gasto) {
        Gasto existente = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        existente.setDescricao(gasto.getDescricao());
        existente.setValor(gasto.getValor());
        existente.setCategoria(gasto.getCategoria());
        existente.setTipo(gasto.getTipo());
        existente.setDataGasto(gasto.getDataGasto());
        existente.setParcelas(gasto.getParcelas());
        existente.setParcelaAtual(gasto.getParcelaAtual());
        existente.setPago(gasto.isPago());
        return ResponseEntity.ok(gastoRepository.save(existente));
    }

    /** DELETE /api/gastos/1 - Remove o gasto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        gastoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
