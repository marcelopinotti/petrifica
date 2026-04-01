#  Petrifica

> Plataforma de concessão de empréstimos com análise de fraude em tempo real, construída com arquitetura de microserviços.

---

## 📌 Sobre o Projeto

O **Petrifica** é um sistema backend de empréstimos que simula um fluxo real de crédito: o cliente solicita um empréstimo, o sistema analisa o risco de fraude automaticamente via mensageria assíncrona e retorna uma decisão de aprovação ou rejeição — tudo de forma segura, escalável e desacoplada.

---

## 🏗️ Arquitetura

```
┌─────────────────┐        Kafka         ┌──────────────────────────┐
│   loan-service  │ ──── loan-topic ───► │  fraud-analysis-service  │
│   (porta 8081)  │ ◄─── fraud-topic ─── │       (porta 8082)       │
└─────────────────┘                      └──────────────────────────┘
        │                                            │
        └──────────────┬─────────────────────────────┘
                       │
              ┌────────▼────────┐
              │    MongoDB      │
              │   (porta 27017) │
              └─────────────────┘
                       │
              ┌────────▼────────┐
              │    Keycloak     │
              │   (porta 8080)  │
              └─────────────────┘
```

### Fluxo de um Empréstimo

```
Cliente autenticado
      │
      ▼
POST /loans  ──► PENDING ──► UNDER_ANALYSIS ──► (Kafka) ──► Fraud Service
                                                                    │
                                                              Analisa risco
                                                                    │
                                                         ┌──────────▼──────────┐
                                                         │  APPROVED / REJECTED │
                                                         └─────────────────────┘
```

---

## 🧩 Microserviços

### 🏦 loan-service
Responsável pelo ciclo de vida completo do empréstimo.

| Endpoint | Método | Acesso | Descrição |
|---|---|---|---|
| `/auth/register` | POST | Autenticado | Registra o cliente no sistema |
| `/loans` | POST | Autenticado | Solicita um novo empréstimo |
| `/loans/details/{id}` | GET | Autenticado | Busca empréstimo por ID |
| `/loans/my-loans` | GET | Autenticado | Lista empréstimos do usuário |
| `/loans/update/{id}` | PUT | Autenticado | Atualiza empréstimo pendente |
| `/loans/cancel/{id}` | DELETE | Autenticado | Cancela empréstimo |
| `/loans/pending` | GET | `ROLE_ANALYST` | Lista todos os empréstimos pendentes |

**Estados do empréstimo (State Machine):**
```
PENDING ──► UNDER_ANALYSIS ──► APPROVED
                           └──► REJECTED
PENDING ──► CANCELLED
```

### 🔍 fraud-analysis-service
Responsável pela análise de risco e detecção de fraude.

- Consome eventos do `loan-topic`
- Aplica regras de risco configuráveis
- Publica resultado no `fraud-topic`

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.4.5 | Framework base |
| Spring Security + OAuth2 | - | Autenticação e autorização |
| Spring State Machine | 4.0.0 | Ciclo de vida do empréstimo |
| Spring Kafka | - | Mensageria assíncrona |
| MongoDB | 5.0 | Banco de dados |
| Keycloak | 25.0.2 | Identity Provider (JWT) |
| Apache Kafka | 7.5.0 | Broker de mensagens |
| Lombok | - | Redução de boilerplate |
| Docker + Docker Compose | - | Containerização |

---

## 🚀 Como Rodar

### Pré-requisitos
- Docker e Docker Compose instalados

### 1. Subir a infraestrutura e os serviços

```bash
docker compose up -d --build
```

### 2. Configurar o Keycloak

Acesse `http://localhost:8080` com `admin / admin` e:

1. Crie o realm **petrifica**
2. Crie o client **petrifica-client** com _Direct Access Grants_ habilitado
3. Crie as roles: `ROLE_USER`, `ROLE_ANALYST`
4. Crie os usuários e atribua as roles

### 3. Obter token JWT

```bash
curl -X POST http://localhost:8080/realms/petrifica/protocol/openid-connect/token \
  -d "grant_type=password" \
  -d "client_id=petrifica-client" \
  -d "username=SEU_USUARIO" \
  -d "password=SUA_SENHA"
```

### 4. Usar a API

```bash
# Registrar cliente
curl -X POST http://localhost:8081/auth/register \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"João Silva","email":"joao@email.com","cpf":"12345678900","monthlyIncome":5000}'

# Solicitar empréstimo
curl -X POST http://localhost:8081/loans \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"requestedAmount":10000,"installments":12,"reason":"HOME_RENOVATION"}'
```

---

## 📁 Estrutura do Projeto

```
petrifica/
├── loan-service/
│   └── src/main/java/com/marcelo/loan/
│       ├── config/          # Security, StateMachine, Mongo
│       ├── controller/      # AuthController, LoanController
│       ├── entity/          # Loan, Customer, enums
│       ├── exception/       # Handlers e exceções customizadas
│       ├── repository/      # LoanRepository, CustomerRepository
│       └── service/         # LoanService, CustomerService, LoanStateService
│
├── fraud-analysis-service/
│   └── src/main/java/com/marcelo/fraud/
│       ├── config/          # Security, Mongo
│       ├── controller/      # FraudController
│       ├── entity/          # Analysis, RiskRule
│       ├── event/           # LoanRequestedEvent
│       ├── exception/       # Handlers e exceções customizadas
│       ├── repository/      # AnalysisRepository, RiskRuleRepository
│       └── service/         # FraudAnalysisService
│
└── docker-compose.yaml
```

---

## 🔐 Segurança

- Autenticação via **JWT** emitido pelo Keycloak
- Autorização por **roles** extraídas do claim `realm_access`
- Endpoints protegidos com `@Secured` por role
- Validação de input com Bean Validation (`@Valid`)
- Sessão **stateless** — sem cookies ou estado no servidor

---

## 👤 Autor

Feito por **Marcelo** 🚀
