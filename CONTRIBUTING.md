# Contribuindo para o Petrifica 🗿

Obrigado por considerar contribuir com o **Petrifica**! Este documento descreve as diretrizes para contribuir com o projeto.

## Código de Conduta

Ao participar deste projeto, você concorda em manter um ambiente respeitoso e colaborativo. Trate todos os contribuidores com respeito e profissionalismo.

## Como Contribuir

### Reportando Bugs

Antes de abrir uma issue, verifique se o bug já foi reportado. Ao criar uma nova issue de bug, inclua:

- **Título claro e descritivo**
- **Passos para reproduzir** o problema
- **Comportamento esperado** vs. **comportamento atual**
- **Versão do Java, Spring Boot e demais dependências**
- **Logs relevantes** (remova informações sensíveis)

### Sugerindo Melhorias

Para sugerir novas funcionalidades ou melhorias:

1. Abra uma issue com o prefixo `[FEATURE]` no título
2. Descreva claramente o problema que a funcionalidade resolve
3. Proponha uma solução ou abordagem, se possível

### Enviando Pull Requests

1. **Fork** o repositório
2. Crie uma branch a partir de `main`:
   ```bash
   git checkout -b feat/minha-feature
   # ou
   git checkout -b fix/meu-fix
   ```
3. Faça suas alterações seguindo os padrões do projeto
4. Certifique-se de que os testes passam:
   ```bash
   mvn clean test
   ```
5. Faça o commit das suas alterações (veja [Convenção de Commits](#convenção-de-commits))
6. Faça o push da branch e abra um **Pull Request**

## Convenção de Commits

Este projeto segue o padrão [Conventional Commits](https://www.conventionalcommits.org/):

| Tipo       | Descrição                                               |
|------------|---------------------------------------------------------|
| `feat`     | Nova funcionalidade                                     |
| `fix`      | Correção de bug                                         |
| `docs`     | Alterações na documentação                              |
| `chore`    | Tarefas de manutenção (build, configs, etc.)            |
| `refactor` | Refatoração de código sem mudança de comportamento      |
| `test`     | Adição ou correção de testes                            |
| `perf`     | Melhoria de performance                                 |

**Exemplos:**
```
feat: add endpoint for loan cancellation
fix: correct risk score calculation for high-value loans
docs: update README with Docker Compose instructions
```

## Padrões de Código

- **Java 17+** com boas práticas de Clean Code e Clean Architecture
- Use **Lombok** para reduzir boilerplate
- Siga a estrutura de camadas existente: `controller`, `service`, `entity`, `repository`, `messaging`, `config`, `exception`
- Escreva testes unitários para novas funcionalidades
- Mantenha a cobertura de testes adequada

## Configuração do Ambiente de Desenvolvimento

1. Clone o repositório:
   ```bash
   git clone https://github.com/marcelopinotti/petrifica.git
   cd petrifica
   ```
2. Suba a infraestrutura com Docker Compose:
   ```bash
   mvn clean package -DskipTests
   docker compose up -d --build
   ```
3. Acesse o Swagger UI em: `http://localhost:8081/swagger-ui.html`

## Dúvidas?

Abra uma [issue](https://github.com/marcelopinotti/petrifica/issues) ou entre em contato com o mantenedor.

---

> Este projeto é licenciado sob a [MIT License](LICENSE).
