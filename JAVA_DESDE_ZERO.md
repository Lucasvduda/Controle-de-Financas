# ☕ Java desde zero — Explicando o código como se você nunca tivesse visto

Este guia explica **cada conceito de Java** usando só exemplos do próprio projeto. Não assume que você já sabe programar.

---

## 1. O que é um arquivo Java

Em Java, cada arquivo `.java` normalmente contém **uma classe**. O nome do arquivo tem que ser **igual ao nome da classe**.

Exemplo: o arquivo `CartaoCredito.java` contém a classe `CartaoCredito`.

---

## 2. `package` e `import` — Onde a classe mora e o que ela usa

No topo do arquivo você vê:

```java
package com.gastos.model;

import java.math.BigDecimal;
import java.util.List;
```

- **package** = “endereço” da classe no projeto.  
  `com.gastos.model` significa: pasta `com/gastos/model`.  
  Assim evitamos conflito de nomes (duas classes chamadas `CartaoCredito` em pacotes diferentes).

- **import** = “quero usar coisas que estão em outro pacote”.  
  Sem o import, você teria que escrever o nome completo toda hora (por exemplo `java.math.BigDecimal`).  
  Com o import, você pode escrever só `BigDecimal`.

---

## 3. O que é uma **classe**

Uma classe é um “molde” que descreve um tipo de dado: que informações guarda (atributos) e que ações faz (métodos).

```java
public class CartaoCredito {

    private Long id;
    private String nome;
    private String bandeira;
    // ...

}
```

- **public** = qualquer outro código do projeto pode usar essa classe.
- **class** = palavra que define uma classe.
- **CartaoCredito** = nome da classe.
- **{ }** = tudo que faz parte da classe fica entre as chaves.

Cada “cartão” que você criar no sistema será **um objeto** (uma “cópia”) desse molde. Por exemplo: um objeto com nome "Nubank", outro com nome "Itaú".

---

## 4. Atributos (variáveis dentro da classe)

Atributos são os “dados” que cada objeto guarda.

```java
private Long id;
private String nome;
private String bandeira;
private BigDecimal limiteTotal = BigDecimal.ZERO;
private List<Gasto> gastos = new ArrayList<>();
```

Forma geral:

- **private** = só o próprio objeto (e a própria classe) pode acessar. De fora não dá para fazer `cartao.nome` direto; por isso existem getters e setters.
- **Tipo** = que tipo de valor é (Long, String, BigDecimal, List<Gasto>, etc.).
- **nome** = nome do atributo.
- **= valor** = valor inicial (opcional).  
  Ex.: `BigDecimal.ZERO` é “zero” para dinheiro; `new ArrayList<>()` é uma lista vazia.

Tipos que aparecem no projeto:

| Tipo        | Significado                         | Exemplo de valor      |
|------------|--------------------------------------|------------------------|
| `Long`     | Número inteiro grande (ID)           | `1L`, `42L`            |
| `int`      | Número inteiro                      | `1`, `31`              |
| `String`   | Texto                               | `"Nubank"`, `"Visa"`   |
| `BigDecimal` | Número decimal para dinheiro      | `new BigDecimal("1500.00")` |
| `boolean`  | Verdadeiro ou falso                 | `true`, `false`        |
| `List<Gasto>` | Lista de objetos do tipo Gasto   | `new ArrayList<>()`    |

---

## 5. Métodos — As “ações” da classe

Um método é um bloco de código com nome, que pode receber dados (parâmetros) e pode devolver um resultado (retorno).

Forma geral:

```text
modificador tipoDeRetorno nomeDoMetodo (tipoParametro nomeParametro, ...) {
    // código
    return valor;   // se o tipo não for void
}
```

Exemplo simples (getter):

```java
public Long getId() {
    return id;
}
```

- **public** = pode ser chamado de fora da classe.
- **Long** = o método **devolve** um número Long (o id).
- **getId** = nome do método. Por convenção, getters começam com `get`.
- **()** = não recebe parâmetros.
- **return id;** = devolve o valor do atributo `id`.

Exemplo com parâmetro (setter):

```java
public void setNome(String nome) {
    this.nome = nome;
}
```

- **void** = não devolve nada.
- **String nome** = recebe um parâmetro chamado `nome`, do tipo String.
- **this.nome** = “o atributo `nome` **deste** objeto”. O `this` evita confusão com o parâmetro `nome`.

Por que getters e setters?  
Porque os atributos são `private`. Quem está “fora” da classe não pode fazer `cartao.nome = "Nubank"`. Em vez disso, chama `cartao.setNome("Nubank")` e `cartao.getNome()`. Assim a classe controla como seus dados são lidos e alterados.

---

## 6. Construtor

Construtor é um método **especial** que roda quando você cria um objeto com `new CartaoCredito()`.

```java
public CartaoCredito() {
}
```

Esse é o **construtor vazio**: não recebe parâmetros e não faz nada. O Spring e o JPA usam ele para criar objetos vazios e depois preencher com os dados do banco.

Nome do construtor = sempre o **nome da classe**. Não tem tipo de retorno (nem `void`).

---

## 7. Métodos que fazem conta ou lógica

No `CartaoCredito` temos:

```java
@Transient
public BigDecimal getLimiteUsado() {
    return gastos.stream()
            .map(Gasto::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

O que está acontecendo, em português:

- **gastos** = lista de gastos do cartão.
- **.stream()** = “transforma a lista em um fluxo para processar item por item”.
- **.map(Gasto::getValor)** = “de cada gasto, pega só o valor”. Fica um fluxo de valores.
- **.reduce(BigDecimal.ZERO, BigDecimal::add)** = “começa em zero e vai somando cada valor”. Resultado = soma de todos os valores.

Ou seja: **getLimiteUsado()** = soma dos valores dos gastos (quanto já foi usado do limite).

**@Transient** = “isso não é uma coluna do banco”. O limite usado não fica salvo na tabela; é calculado na hora.

---

## 8. O que são as “@” (anotações)

Tudo que começa com **@** em Java é uma **anotação**. Elas não são o “código que roda”; são **instruções para o framework** (Spring, JPA) sobre o que fazer com a classe, o método ou o atributo.

Exemplos no `CartaoCredito`:

| Anotação      | Significado (em palavras) |
|---------------|----------------------------|
| `@Entity`     | “Esta classe é uma tabela no banco.” |
| `@Table(name = "cartoes_credito")` | “O nome da tabela é cartoes_credito.” |
| `@Id`         | “Este atributo é a chave primária.” |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | “O banco gera o ID automaticamente.” |
| `@Column(name = "limite_total", ...)` | “Este atributo vira a coluna limite_total, com precisão para dinheiro.” |
| `@OneToMany(mappedBy = "cartao")` | “Um cartão tem muitos gastos; o relacionamento é mapeado pelo atributo cartao na classe Gasto.” |
| `@NotBlank`   | “Este campo não pode ser vazio (validação).” |
| `@Transient`  | “Não salvar no banco; só usar em memória.” |

Ou seja: **você escreve a classe; as anotações dizem ao Spring/JPA como usar essa classe** (tabela, colunas, validação, etc.).

---

## 9. Enum — Lista fixa de opções

`CategoriaGasto` é um **enum**: um tipo com um conjunto **fixo** de valores.

```java
public enum CategoriaGasto {
    ALUGUEL("Aluguel"),
    MERCADO("Mercado"),
    TRANSPORTE("Transporte"),
    // ...
    OUTROS("Outros");

    private final String descricao;

    CategoriaGasto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
```

- **enum** = em vez de usar números ou strings soltas (1, 2, "mercado"), você usa constantes com nome: `CategoriaGasto.MERCADO`, `CategoriaGasto.ALUGUEL`.
- Cada valor pode ter dados extras. Aqui, cada constante tem uma **descricao** (texto para mostrar na tela).
- **private final String descricao** = cada constante guarda uma String que não muda (`final`).
- O “construtor” `CategoriaGasto(String descricao)` é usado na declaração: `MERCADO("Mercado")` chama esse construtor com `"Mercado"`.

Assim evitamos typos e deixamos o código mais claro do que usar só String ou int.

---

## 10. Interface — Contrato sem implementação

No projeto, os repositórios são **interfaces**, não classes:

```java
@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByTipo(TipoGasto tipo);

    BigDecimal somarPorTipo(@Param("tipo") TipoGasto tipo);
}
```

- **interface** = lista de **assinaturas** de métodos (nome, parâmetros, tipo de retorno). Não tem o “corpo” do método (não tem `{ ... }` com a lógica).
- **extends JpaRepository<Gasto, Long>** = “este repositório segue o contrato do JpaRepository para a entidade Gasto com ID Long”. O JPA já implementa `findAll()`, `save()`, `findById()`, etc.
- **findByTipo(TipoGasto tipo)** = “quem implementar essa interface precisa ter um método com esse nome e esse parâmetro”. No caso, **o Spring implementa sozinho** a partir do nome: ele gera a query “buscar gastos onde tipo = ?”.
- **@Query("SELECT ...")** = quando o método não pode ser “adivinhado” pelo nome, você escreve a consulta (em JPQL, uma espécie de SQL das entidades).

Em resumo: **interface** = “o que” tem que existir (contrato); **quem** faz o “como” aqui é o Spring Data JPA.

---

## 11. Controller — Classe que “atende” as URLs

No `DashboardController`:

```java
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final FinanceiroService financeiroService;
    private final AlertaService alertaService;

    public DashboardController(FinanceiroService financeiroService, AlertaService alertaService) {
        this.financeiroService = financeiroService;
        this.alertaService = alertaService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResumo> getDashboard() {
        return ResponseEntity.ok(financeiroService.getDashboard());
    }
}
```

- **@RestController** = “esta classe tem métodos que respondem a requisições HTTP e devolvem dados (em geral JSON)”.
- **@RequestMapping("/api")** = “todas as URLs que essa classe atende começam com /api”.
- **private final FinanceiroService ...** = atributos que guardam os **services**. O **final** significa que, depois de receber no construtor, não trocamos mais.
- O **construtor** recebe `FinanceiroService` e `AlertaService`. O **Spring** é quem chama esse construtor e **injeta** as implementações. Isso se chama **injeção de dependência**: você não faz `new FinanceiroService()`, o framework cria e entrega.
- **@GetMapping("/dashboard")** = “quando alguém fizer GET em /api/dashboard, chame este método”.
- **ResponseEntity<DashboardResumo>** = resposta HTTP que pode carregar um corpo (o objeto `DashboardResumo`) e um status (200 OK, etc.).
- **return ResponseEntity.ok(...)** = “resposta com status 200 e corpo = resultado de financeiroService.getDashboard()”.

Fluxo: **GET /api/dashboard** → Spring chama `getDashboard()` → o método chama `financeiroService.getDashboard()` → o service monta o resumo e devolve → o controller coloca isso em `ResponseEntity.ok(...)` e devolve como JSON para o cliente.

---

## 12. Parâmetros vindos da requisição

```java
@GetMapping("/projecao")
public ResponseEntity<ProjecaoFinanceira> getProjecao(
        @RequestParam(defaultValue = "12") int meses) {
    return ResponseEntity.ok(financeiroService.getProjecao(meses));
}
```

- **@RequestParam(defaultValue = "12")** = “pegue da URL um parâmetro chamado `meses` (ex.: `/api/projecao?meses=24`). Se não vier, use 12”.
- **int meses** = esse valor é passado para o método como variável `meses` do tipo inteiro.

```java
@PutMapping("/configuracao")
public ResponseEntity<ConfiguracaoFinanceira> salvarConfig(
        @RequestBody ConfiguracaoFinanceira config) {
    return ResponseEntity.ok(financeiroService.salvarConfig(config));
}
```

- **@RequestBody** = “o corpo da requisição (em geral JSON) deve ser convertido em um objeto `ConfiguracaoFinanceira`”. O Spring faz essa conversão sozinho.

---

## 13. Tratando erro (try e catch)

```java
@PostMapping("/alertas/enviar")
public ResponseEntity<Map<String, String>> enviarAlertas() {
    try {
        alertaService.enviarAlertaManual();
        return ResponseEntity.ok(Map.of("status", "Alertas enviados com sucesso"));
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erro ao enviar alertas: " + e.getMessage()));
    }
}
```

- **try { ... }** = “tente executar este bloco”.
- **catch (Exception e) { ... }** = “se der qualquer erro (Exception), execute este bloco em vez de quebrar o programa”. O `e` guarda dados do erro; `e.getMessage()` é a mensagem.
- **Map.of("status", "Alertas enviados com sucesso")** = um mapa (chave → valor) com uma entrada. Usado para devolver um JSON simples como `{ "status": "Alertas enviados com sucesso" }`.
- **ResponseEntity.internalServerError()** = status 500. **.body(...)** = corpo da resposta.

Assim, se o envio do e-mail falhar, a API devolve 500 e uma mensagem de erro em vez de cair.

---

## 14. O método `main` — Por onde tudo começa

No `GastosApplication.java`:

```java
public static void main(String[] args) {
    SpringApplication.run(GastosApplication.class, args);
}
```

- **public** = acessível de fora.
- **static** = “este método é da **classe**; não precisa de um objeto. A JVM chama assim: GastosApplication.main(...)”.
- **void** = não retorna nada.
- **String[] args** = lista de argumentos que podem ser passados pela linha de comando (no nosso projeto quase não usamos).
- **SpringApplication.run(...)** = “inicie a aplicação Spring Boot com esta classe”. O Spring sobe o servidor, carrega os controllers, os services, os repositories, etc.

Ou seja: quando você roda a aplicação, a JVM chama esse `main`, e o Spring toma conta do resto.

---

## 15. Resumo: “traduzindo” trechos do projeto

| Código | Em português |
|--------|----------------|
| `private Long id;` | “Cada cartão tem um número de identificação (Long), só acessível dentro da classe.” |
| `public Long getId() { return id; }` | “Quem precisar do id de fora chama getId() e recebe o valor.” |
| `public void setNome(String nome) { this.nome = nome; }` | “Quem quiser mudar o nome chama setNome(...); o nome deste objeto passa a ser o valor recebido.” |
| `@Entity` | “Spring/JPA: trate esta classe como uma tabela.” |
| `List<Gasto> gastos = new ArrayList<>();` | “Lista que vai guardar objetos do tipo Gasto; começa vazia.” |
| `return ResponseEntity.ok(objeto);` | “Devolva uma resposta HTTP 200 com esse objeto em JSON.” |
| `financeiroService.getDashboard()` | “Peça ao objeto financeiroService o resultado do método getDashboard().” |
| `cartaoRepository.findAll()` | “Peça ao repositório de cartões todos os registros (todos os cartões).” |

---

## 16. Ordem sugerida para estudar o código

1. **GastosApplication.java** — Só o `main` e as anotações.
2. **CategoriaGasto.java** — Ver um enum simples.
3. **CartaoCredito.java** — Atributos, getters/setters, um método que calcula algo (`getLimiteUsado`).
4. **CartaoCreditoRepository.java** — Ver uma interface e o que ela “promete”.
5. **CartaoCreditoController.java** — Ver como uma URL vira chamada de método e uso do service/repository.
6. **FinanceiroService.java** — Ver como a lógica usa vários repositórios e monta DTOs.

Assim você passa do “onde começa” até “como os dados fluem da URL até o banco e de volta”.

Se quiser, na próxima vez podemos pegar **um único fluxo** (por exemplo “criar um cartão”) e percorrer linha por linha em cima do código real.
