# FinControl - Gerenciador Financeiro Completo

Sistema completo de gerenciamento financeiro pessoal construído com **Java Spring Boot** + **H2 Database** + **Frontend moderno**.

## Funcionalidades

### Dashboard
- Resumo financeiro completo (salário, investimentos, gastos, saldo)
- Alertas de contas atrasadas e próximas do vencimento
- Gastos por categoria com barras de progresso
- Cálculo automático de quanto tempo o dinheiro vai durar

### Cartões de Crédito
- Cadastro de múltiplos cartões com visual de cartão real
- Controle de limite (usado/disponível)
- Adição de gastos por cartão com suporte a parcelamento
- Dias de fechamento e vencimento

### Gastos Fixos e Variáveis
- Adicionar e remover gastos fixos (aluguel, contas, assinaturas)
- Adicionar e remover gastos variáveis (mercado, compras)
- Categorização automática
- Totais por tipo

### Contas a Pagar
- Cadastro com data de vencimento
- Status automático (atrasada, vence hoje, próxima, normal)
- Marcar como paga com um clique
- Contas recorrentes (gera automaticamente o próximo mês)
- Histórico de contas pagas

### Investimentos
- Cadastro com taxa de rendimento personalizável (padrão: 0,9%/mês)
- Cálculo automático de rendimento mensal e anual
- Suporte a múltiplos investimentos com taxas diferentes

### Projeção Financeira
- Projeção de 24 meses com gráfico de barras
- Simulação mês a mês com rendimentos compostos
- Tabela detalhada: saldo início, rendimento, entrada, saída, saldo final
- Cálculo de quanto tempo o dinheiro vai durar

### Objetivos de Economia
- Defina metas com valor alvo e economia mensal
- Barra de progresso visual
- Faça depósitos diretamente pelo sistema
- Cálculo de meses restantes

### Alertas por Email
- Envio automático diário às 8h (via scheduler)
- Lista de contas atrasadas e próximas do vencimento
- Envio manual com botão na tela
- Configurável: dias de antecedência, ativar/desativar

## Requisitos

- **Java 17+** (JDK)
- **Maven 3.6+**

## Como Executar

### 1. Clone ou baixe o projeto

### 2. Configure o email (opcional - para alertas)

Edite `src/main/resources/application.properties`:

```properties
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_SENHA_DE_APP
app.alerta.email-destino=SEU_EMAIL@gmail.com
```

Para Gmail: ative verificação em 2 etapas e gere uma "Senha de App" em:
https://myaccount.google.com/apppasswords

### 3. Execute o projeto

```bash
mvn spring-boot:run
```

Ou gere o JAR:

```bash
mvn clean package
java -jar target/gerenciador-gastos-1.0.0.jar
```

### 4. Acesse no navegador

```
http://localhost:8080
```

## Banco de Dados

O sistema usa **H2 Database** com persistência em arquivo:
- Os dados são salvos em `./data/gastosdb`
- Não perde dados ao reiniciar o servidor
- Console H2 disponível em: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/gastosdb`
  - User: `sa`
  - Password: (vazio)

## API REST

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/dashboard` | Resumo financeiro completo |
| GET | `/api/projecao?meses=24` | Projeção financeira |
| GET/PUT | `/api/configuracao` | Configurações (salário, alertas) |
| GET/POST/DELETE | `/api/cartoes` | Cartões de crédito |
| GET | `/api/cartoes/{id}` | Resumo detalhado do cartão |
| POST | `/api/cartoes/{id}/gastos` | Adicionar gasto ao cartão |
| GET/POST/PUT/DELETE | `/api/gastos` | Gastos fixos e variáveis |
| GET | `/api/gastos/fixos` | Apenas gastos fixos |
| GET | `/api/gastos/variaveis` | Apenas gastos variáveis |
| GET/POST/PUT/DELETE | `/api/contas` | Contas a pagar |
| PATCH | `/api/contas/{id}/pagar` | Marcar conta como paga |
| GET/POST/PUT/DELETE | `/api/investimentos` | Investimentos |
| GET/POST/PUT/DELETE | `/api/objetivos` | Objetivos de economia |
| PATCH | `/api/objetivos/{id}/depositar?valor=X` | Depositar em objetivo |
| POST | `/api/alertas/enviar` | Enviar alertas por email manualmente |
| GET | `/api/categorias` | Lista de categorias disponíveis |

## Tecnologias

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring Mail
- **Banco**: H2 Database (arquivo persistente)
- **Frontend**: HTML5, CSS3 (dark theme), JavaScript ES6+
- **Build**: Maven
