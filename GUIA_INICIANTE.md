# 📚 Guia do Projeto — Explicação para Iniciantes

Este documento explica **cada parte** do projeto Gerenciador de Gastos como se você estivesse começando em Java e Spring Boot.

---

## 1. Visão geral: o que o projeto faz

O sistema é uma **API REST** (backend) + uma **página web** (frontend) que:

- Cadastra cartões de crédito e gastos em cada cartão
- Controla gastos fixos (aluguel, contas) e variáveis (mercado, compras)
- Registra contas a pagar com data de vencimento
- Calcula investimentos com rendimento mensal
- Faz projeção de quanto tempo seu dinheiro dura
- Envia alertas por e-mail quando contas estão perto do vencimento

O backend é em **Java 17** com **Spring Boot**. O frontend é HTML + CSS + JavaScript que chama a API.

---

## 2. Estrutura de pastas do projeto

```
Gastos/
├── pom.xml                          ← Configuração do Maven (dependências)
├── src/main/
│   ├── java/com/gastos/
│   │   ├── GastosApplication.java   ← Ponto de entrada (main)
│   │   ├── model/                   ← Entidades (tabelas do banco)
│   │   ├── repository/              ← Acesso ao banco (SELECT, INSERT...)
│   │   ├── service/                 ← Regras de negócio e cálculos
│   │   ├── controller/              ← Endpoints da API (URLs)
│   │   └── dto/                     ← Objetos para enviar dados ao frontend
│   └── resources/
│       ├── application.properties   ← Configurações (banco, e-mail, etc.)
│       └── static/                  ← Página HTML, CSS e JS do frontend
└── data/                            ← Banco H2 (arquivos .db) — criado ao rodar
```

Resumindo:

- **model** = formato dos dados (como uma “ficha” de cartão, de gasto, etc.)
- **repository** = “conversa” com o banco (salvar, buscar, apagar)
- **service** = lógica (cálculos, regras, montagem de resumos)
- **controller** = expõe URLs (ex: `GET /api/cartoes`) e devolve JSON
- **dto** = objetos simplificados só para enviar dados na API (ex: resumo do dashboard)

---

## 3. O arquivo `pom.xml` (Maven)

O **pom.xml** diz ao Maven:

- Qual versão do Java usar (17)
- De qual “projeto pai” herdar (Spring Boot 3.2.3)
- Quais **bibliotecas** (dependências) o projeto precisa

Principais dependências:

| Dependência | Para que serve |
|-------------|----------------|
| `spring-boot-starter-web` | Cria o servidor (API REST, URLs, JSON) |
| `spring-boot-starter-data-jpa` | Acesso ao banco com JPA (entities, repositories) |
| `spring-boot-starter-mail` | Envio de e-mails (alertas) |
| `spring-boot-starter-validation` | Validação de campos (@NotBlank, etc.) |
| `h2` | Banco de dados em arquivo (não precisa instalar nada) |

Sem o `pom.xml`, o Maven não saberia o que baixar nem como compilar o projeto.

---

## 4. Ponto de entrada: `GastosApplication.java`

```java
@SpringBootApplication   // "Isso aqui é uma aplicação Spring Boot"
@EnableScheduling        // "Pode executar tarefas agendadas (ex: todo dia às 8h)"
public class GastosApplication {
    public static void main(String[] args) {
        SpringApplication.run(GastosApplication.class, args);  // Sobe o servidor
    }
}
```

- **main** = método que roda quando você executa `mvn spring-boot:run`.
- **@SpringBootApplication** = ativa o Spring Boot (scan de controllers, services, etc.).
- **@EnableScheduling** = permite usar `@Scheduled` (como no `AlertaService`, que roda todo dia às 8h).

É a “porta de entrada” do programa.

---

## 5. Camada Model (entidades / “tabelas”)

Cada classe em **model** representa uma **tabela** no banco de dados. As anotações JPA dizem como mapear a classe para colunas.

### Exemplo: `CartaoCredito.java`

```java
@Entity
@Table(name = "cartoes_credito")
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;           // Chave primária, gerada automaticamente

    private String nome;       // Nome do cartão (ex: Nubank)
    private String bandeira;   // Ex: Visa, Mastercard
    private BigDecimal limiteTotal;
    private int diaFechamento;
    private int diaVencimento;
    private String cor;

    @OneToMany(mappedBy = "cartao", ...)
    private List<Gasto> gastos;   // Um cartão tem VÁRIOS gastos
    // ...
}
```

- **@Entity** = “essa classe é uma tabela”.
- **@Table(name = "cartoes_credito")** = nome da tabela no banco.
- **@Id** + **@GeneratedValue** = coluna de ID, auto-incremento.
- **@OneToMany** = relacionamento: 1 cartão → vários gastos.

### Exemplo: `Gasto.java`

```java
@Entity
@Table(name = "gastos")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private BigDecimal valor;
    private LocalDate dataGasto;

    @Enumerated(EnumType.STRING)
    private CategoriaGasto categoria;   // ALUGUEL, MERCADO, etc.
    private TipoGasto tipo;             // FIXO ou VARIAVEL

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private CartaoCredito cartao;       // Este gasto pertence a qual cartão?
}
```

- **@ManyToOne** = vários gastos podem pertencer a um mesmo cartão.
- **@Enumerated** = salva o enum como texto no banco (ex: "FIXO", "MERCADO").

Outras entidades do projeto:

- **ContaPagar** – contas com vencimento (luz, água, etc.)
- **Investimento** – valor aplicado e taxa de rendimento
- **ObjetivoEconomia** – meta de economia (ex: viagem)
- **ConfiguracaoFinanceira** – salário, e-mail para alertas, etc.

Resumo: **model** = estrutura dos dados que ficam no banco.

---

## 6. Camada Repository (acesso ao banco)

Os **repositories** são interfaces. O Spring Data JPA **implementa sozinho** os métodos comuns (save, findAll, findById, delete…) e ainda permite criar métodos por nome ou com `@Query`.

### Exemplo: `CartaoCreditoRepository.java`

```java
@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {
}
```

- **JpaRepository<CartaoCredito, Long>** = repositório para a entidade `CartaoCredito`, com ID do tipo `Long`.
- Só com isso você já tem: `findAll()`, `findById(id)`, `save(cartao)`, `deleteById(id)`.

Não precisa escrever SQL; o Spring gera as queries.

### Exemplo: `GastoRepository.java` (com métodos extras)

```java
List<Gasto> findByTipo(TipoGasto tipo);                    // Gastos fixos ou variáveis
List<Gasto> findByCartaoId(Long cartaoId);                // Gastos de um cartão
BigDecimal somarPorTipo(@Param("tipo") TipoGasto tipo);    // Soma dos valores por tipo
```

- **findByTipo** = Spring monta: `SELECT * FROM gastos WHERE tipo = ?`
- **somarPorTipo** usa **@Query** para fazer um `SUM(valor)`.

Resumo: **repository** = “quem busca e salva no banco”; você não mexe em SQL na mão na maior parte do tempo.

---

## 7. Camada Service (regras de negócio)

Aqui ficam os **cálculos** e a **lógica** que não é só “buscar um registro”. O controller chama o service; o service chama os repositories.

### Exemplo: `FinanceiroService.java`

Ele usa vários repositories (gastos, investimentos, contas, configuração, cartões) para:

- Montar o **dashboard**: salário, total de investimentos, rendimento, gastos fixos/variáveis, saldo mensal, “tempo até o dinheiro acabar”.
- Montar a **projeção** mês a mês (saldo inicial + rendimento + salário - gastos).
- Montar o **resumo de um cartão** (limite usado, lista de gastos daquele cartão).

Exemplo de trecho:

```java
BigDecimal totalFixos = gastoRepository.somarPorTipo(TipoGasto.FIXO);
BigDecimal totalVariaveis = gastoRepository.somarPorTipo(TipoGasto.VARIAVEL);
BigDecimal totalGastos = totalFixos.add(totalVariaveis);
BigDecimal saldoMensal = salario.add(rendimentoMensal).subtract(totalGastos);
```

Ou seja: busca totais nos repositórios e aplica as regras (somar, subtrair, etc.).

### Exemplo: `AlertaService.java`

- Envia e-mail com contas atrasadas e contas que vencem em breve.
- Usa **@Scheduled(cron = "0 0 8 * * *")** = “execute todo dia às 8h”.
- Lê configuração (e-mail ativo? qual e-mail?) e lista de contas do `ContaPagarRepository`, monta o texto e chama `mailSender.send(...)`.

Resumo: **service** = onde ficam as regras, cálculos e orquestração entre repositories.

---

## 8. Camada Controller (API REST)

Os **controllers** definem as **URLs** da API e o que cada uma retorna (geralmente JSON). O navegador ou o frontend faz requisições HTTP (GET, POST, PUT, DELETE) para essas URLs.

### Exemplo: `CartaoCreditoController.java`

```java
@RestController
@RequestMapping("/api/cartoes")
public class CartaoCreditoController {

    private final CartaoCreditoRepository cartaoRepository;
    private final FinanceiroService financeiroService;
    // ... (Spring injeta esses objetos — injeção de dependência)

    @GetMapping
    public ResponseEntity<List<CartaoCredito>> listar() {
        return ResponseEntity.ok(cartaoRepository.findAll());
    }
    // GET /api/cartoes → lista todos os cartões

    @GetMapping("/{id}")
    public ResponseEntity<ResumoCartao> detalhe(@PathVariable Long id) {
        return ResponseEntity.ok(financeiroService.getResumoCartao(id));
    }
    // GET /api/cartoes/1 → detalhes do cartão 1 (com gastos)

    @PostMapping
    public ResponseEntity<CartaoCredito> criar(@Valid @RequestBody CartaoCredito cartao) {
        return ResponseEntity.ok(cartaoRepository.save(cartao));
    }
    // POST /api/cartoes + JSON no body → cria novo cartão

    @PutMapping("/{id}")
    public ResponseEntity<CartaoCredito> atualizar(...) { ... }
    // PUT /api/cartoes/1 → atualiza o cartão 1

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cartaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // DELETE /api/cartoes/1 → remove o cartão 1

    @PostMapping("/{id}/gastos")
    public ResponseEntity<Gasto> adicionarGasto(@PathVariable Long id, @RequestBody Gasto gasto) {
        // ...
    }
    // POST /api/cartoes/1/gastos → adiciona um gasto ao cartão 1
}
```

- **@RestController** = “esta classe expõe endpoints que devolvem dados (JSON)”.
- **@RequestMapping("/api/cartoes")** = prefixo das URLs.
- **@GetMapping**, **@PostMapping**, etc. = método HTTP + caminho.
- **@PathVariable** = valor que vem na URL (ex: `id` em `/api/cartoes/1`).
- **@RequestBody** = corpo da requisição em JSON (ex: dados do cartão ou do gasto).
- **@Valid** = valida o objeto com as anotações do model (@NotBlank, etc.).

Resumo: **controller** = “quando alguém chama essa URL, executo esse método e devolvo essa resposta”.

---

## 9. DTOs (Data Transfer Objects)

DTOs são classes **só para transporte de dados** na API. Às vezes você não quer devolver a entidade inteira (com relações e campos internos), então monta um objeto mais “limpo”.

Exemplos no projeto:

- **DashboardResumo** – tudo que a tela inicial precisa: salário, totais, contas vencendo, etc.
- **ProjecaoFinanceira** – saldo mensal, lista de meses (ProjecaoMes), tempo restante.
- **ResumoCartao** – dados do cartão + lista de gastos formatados (sem referência circular).

Assim o frontend recebe JSON enxuto e fácil de usar, e as entidades JPA ficam só no backend.

---

## 10. Fluxo de uma requisição (exemplo)

Quando o frontend chama **GET /api/cartoes/1**:

1. **Spring** recebe a requisição e encaminha para o método `detalhe(1)` do `CartaoCreditoController`.
2. O **controller** chama `financeiroService.getResumoCartao(1)`.
3. O **FinanceiroService** usa o `CartaoCreditoRepository` (e indiretamente os gastos) para buscar o cartão e montar um **ResumoCartao** (limite usado, lista de gastos, etc.).
4. O **controller** devolve esse objeto em **JSON** com `ResponseEntity.ok(...)`.
5. O **navegador** recebe o JSON e o JavaScript da página atualiza a tela.

Resumo do fluxo:

**Frontend (navegador)**  
→ **HTTP GET /api/cartoes/1**  
→ **Controller**  
→ **Service**  
→ **Repository**  
→ **Banco H2**  
→ resposta volta na ordem inversa até o frontend.

---

## 11. Frontend (pasta `static/`)

- **index.html** – estrutura da página (menu, áreas onde o conteúdo é carregado).
- **css/styles.css** – cores, layout, botões (tema escuro, cards, etc.).
- **js/app.js** – lógica no navegador:
  - Chama a API com `fetch("/api/...")`.
  - Preenche a tela com os dados (dashboard, cartões, contas, etc.).
  - Abre modais para cadastrar/editar (novo cartão, novo gasto, etc.).

Ou seja: o backend só devolve JSON; quem “desenha” a tela e trata cliques é o HTML/CSS/JS.

---

## 12. Configuração: `application.properties`

- **URL do banco** – `jdbc:h2:file:./data/gastosdb` (arquivo na pasta `data/`).
- **E-mail** – host SMTP, usuário, senha (para alertas).
- **Porta** – `server.port=8080` (servidor sobe em `http://localhost:8080`).

O arquivo `application.properties.example` tem o “modelo” sem dados reais; o `.gitignore` evita subir o `application.properties` com senhas para o Git.

---

## 13. Resumo em uma frase por pasta

| Pasta / arquivo | Em uma frase |
|-----------------|--------------|
| **model** | Define as “tabelas” e os campos que vão pro banco. |
| **repository** | Faz as operações de leitura/gravação no banco (find, save, delete, consultas). |
| **service** | Contém as regras e cálculos; usa os repositories para montar resumos e alertas. |
| **controller** | Expõe as URLs da API e devolve JSON; chama os services. |
| **dto** | Objetos simples só para enviar dados na API (dashboard, projeção, resumo de cartão). |
| **static/** | Página web que consome a API e mostra tudo na tela. |

Se você decorar esse fluxo (Controller → Service → Repository → Banco) e o papel de cada camada, já consegue navegar e alterar o projeto com segurança.
