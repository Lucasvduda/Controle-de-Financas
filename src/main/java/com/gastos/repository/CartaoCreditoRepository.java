package com.gastos.repository;

import com.gastos.model.CartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de cartões de crédito - faz o acesso ao banco (tabela cartoes_credito).
 * É uma INTERFACE: não escrevemos o código dos métodos; o Spring Data JPA implementa automaticamente.
 * JpaRepository<CartaoCredito, Long> já fornece: save(), findAll(), findById(id), deleteById(id), etc.
 */
@Repository  // Marca como componente Spring que acessa dados; o Spring cria uma implementação em tempo de execução
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {
    // Vazio aqui = só usamos os métodos herdados de JpaRepository
}
