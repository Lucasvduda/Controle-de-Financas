package com.gastos.controller;

import com.gastos.model.Gasto;
import com.gastos.model.TipoGasto;
import com.gastos.repository.GastoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> listar() {
        return ResponseEntity.ok(gastoRepository.findAll());
    }

    @GetMapping("/fixos")
    public ResponseEntity<List<Gasto>> listarFixos() {
        return ResponseEntity.ok(gastoRepository.findByTipo(TipoGasto.FIXO));
    }

    @GetMapping("/variaveis")
    public ResponseEntity<List<Gasto>> listarVariaveis() {
        return ResponseEntity.ok(gastoRepository.findByTipo(TipoGasto.VARIAVEL));
    }

    @PostMapping
    public ResponseEntity<Gasto> criar(@Valid @RequestBody Gasto gasto) {
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        gastoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
