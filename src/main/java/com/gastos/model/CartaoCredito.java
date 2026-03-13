package com.gastos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um cartão de crédito no banco de dados.
 * Cada objeto CartaoCredito vira uma linha na tabela "cartoes_credito".
 */
@Entity  // JPA: esta classe é uma tabela no banco
@Table(name = "cartoes_credito")  // Nome da tabela no banco (se não especificar, usaria "cartao_credito")
public class CartaoCredito {

    @Id  // Este campo é a chave primária (identificador único de cada cartão)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // O banco gera o ID automaticamente (1, 2, 3...) ao inserir
    private Long id;

    @NotBlank(message = "Nome do cartão é obrigatório")  // Validação: não pode ser vazio ou só espaços
    private String nome;

    private String bandeira;  // Ex: Visa, Mastercard, Elo

    @Column(name = "limite_total", precision = 12, scale = 2)  // Coluna no banco: 12 dígitos no total, 2 decimais (ex: 9999999999.99)
    private BigDecimal limiteTotal = BigDecimal.ZERO;  // BigDecimal é usado para dinheiro (evita erro de arredondamento)

    @Column(name = "dia_fechamento")  // Dia do mês em que a fatura fecha (ex: 15)
    private int diaFechamento;

    @Column(name = "dia_vencimento")  // Dia do mês em que a fatura vence (ex: 25)
    private int diaVencimento;

    private String cor;  // Cor do cartão na interface (ex: "#6366f1")

    // Um cartão tem MUITOS gastos. mappedBy = "cartao" significa que a classe Gasto tem um atributo "cartao" que faz a ligação
    // cascade = ALL: ao salvar/deletar o cartão, salva/deleta os gastos junto. orphanRemoval: ao remover um gasto da lista, deleta do banco
    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cartao-gastos")  // Evita loop infinito ao converter para JSON: aqui é o "lado dono" da relação
    private List<Gasto> gastos = new ArrayList<>();  // Lista vazia no início; novos gastos são adicionados aqui

    /** Construtor vazio - obrigatório para o JPA criar objetos ao ler do banco. O Spring também usa para instanciar a classe. */
    public CartaoCredito() {}

    // ========== Getters e Setters ==========
    // Getter: retorna o valor do atributo. Setter: altera o valor. Como os atributos são private, o acesso externo é só por aqui.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
    public BigDecimal getLimiteTotal() { return limiteTotal; }
    public void setLimiteTotal(BigDecimal limiteTotal) { this.limiteTotal = limiteTotal; }
    public int getDiaFechamento() { return diaFechamento; }
    public void setDiaFechamento(int diaFechamento) { this.diaFechamento = diaFechamento; }
    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public List<Gasto> getGastos() { return gastos; }
    public void setGastos(List<Gasto> gastos) { this.gastos = gastos; }

    /**
     * Calcula quanto do limite já foi usado (soma do valor de todos os gastos deste cartão).
     * @Transient = este valor NÃO é salvo no banco; é calculado em tempo de execução sempre que chamado.
     */
    @Transient
    public BigDecimal getLimiteUsado() {
        // gastos.stream() = percorre a lista. map(Gasto::getValor) = de cada Gasto, pega o valor. reduce = soma tudo começando de ZERO
        return gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula quanto ainda pode gastar no cartão (limite total - limite usado).
     * Também @Transient pois é calculado, não armazenado.
     */
    @Transient
    public BigDecimal getLimiteDisponivel() {
        return limiteTotal.subtract(getLimiteUsado());
    }
}
