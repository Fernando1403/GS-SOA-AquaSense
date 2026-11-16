# 🌊 AquaSense - Serviço de Alertas de Alagamento (SOA)

## 📌 Sobre o Projeto

O **AquaSense** é uma CitizenTech acadêmica da Global Solution 2026.2 (FIAP) que ajuda moradores e trabalhadores de áreas com recorrência de alagamentos em São Paulo a saber, de forma simples, qual é o nível de água em um ponto monitorado.

Este repositório é o **serviço de alertas (SOA)**: ele recebe as leituras de nível de água enviadas pelo sensor (ESP32), guarda o histórico por local e gera alertas classificados em **NORMAL**, **ATENCAO** ou **CRITICO**, que o aplicativo consulta.

> **Observação:** todos os dados de demonstração são fictícios e possuem finalidade exclusivamente acadêmica. O AquaSense **não substitui** alertas oficiais da Defesa Civil ou do CGE, e os limiares de classificação são didáticos.

---

---

## 👨‍💻 Integrantes

| Nome | RM |
|--------|--------|
| Fernando Gonzales Alexandre | RM555045 |
| Gabriel Guerreiro | RM554973 |
| Lucas Dias | RM555450 |
| Luiz Felipe Coelho | RM555074 |
| Vitor Musolino | RM555012 |

---

## 🧾 Ficha da CitizenTech

| Campo | Conteúdo |
|---|---|
| Nome da CitizenTech | AquaSense |
| Cidade e bairro | São Paulo (SP) — ponto piloto a definir |
| Cidadão beneficiado | Moradores, trabalhadores e comerciantes de áreas com recorrência de alagamentos |
| Problema, em uma frase | Pessoas expostas a alagamentos nem sempre têm informações locais, organizadas e compreensíveis para planejar seus deslocamentos |
| Como funciona | O sensor (ESP32) envia leituras de nível de água; este serviço guarda o histórico e gera alertas; o aplicativo consulta |
| O que cada parte faz | SOA (este repositório): leituras e alertas; C#: API de coleta e classificação; IA/ML: padrões históricos; ESP32/Wokwi: sensor; App Expo: consulta |
| Link do repositório | `<colar o link do GitHub>` |
| Indicador de impacto | Alertas gerados corretamente a partir das leituras recebidas |
| ODS conectados | 11, 13 e 3 |

---

## 🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.3
- Spring Data JPA
- MySQL
- Flyway (migrações)
- Swagger/OpenAPI
- JUnit 5 e Mockito
- Maven
- Hibernate
- Lombok

---

## 🏗 Arquitetura do Projeto

```text
Controller
    ↓
Service (interface + implementação)
    ↓
Repository
    ↓
MySQL (esquema criado pelo Flyway)
```

O projeto foi estruturado em camadas para garantir organização, reutilização de código e facilidade de manutenção.

Fluxo da solução:

```text
Sensor (ESP32) → POST /api/leituras → Serviço AquaSense → POST /api/alertas/gerar/{localId} → Alerta → MySQL
```

---

## 🧠 Conceitos Aplicados

- Programação Orientada a Objetos (POO)
- Enum com regra de negócio (`NivelRisco`)
- Interfaces (`ILocalService`, `ILeituraService`, `IAlertaService`)
- Injeção de Dependência (por construtor)
- Tratamento de Exceções (`ResourceNotFoundException` e `GlobalExceptionHandler`)
- DTOs (Data Transfer Objects) com validação
- API REST
- Persistência de Dados com JPA (relacionamento 1:N)
- Migrações de banco de dados com Flyway
- Testes automatizados com JUnit 5 e Mockito

---

## 🗄 Banco de Dados

Banco utilizado:

```sql
aquasense
```

Tabelas criadas pelas migrações:

- locais
- leituras
- alertas
- flyway_schema_history (controle do Flyway)

### Migrações (Flyway)

| Arquivo | O que faz |
|---|---|
| `V1__criar_tabelas.sql` | Cria as tabelas `locais`, `leituras` e `alertas`, com chaves estrangeiras |
| `V2__dados_iniciais.sql` | Insere 1 local e 3 leituras de demonstração (simuladas) |

As migrações rodam sozinhas ao iniciar a aplicação.

---

## ⚙️ Como Executar

### 1. Criar o banco

```sql
CREATE DATABASE aquasense;
```

(A aplicação também cria o banco automaticamente se ele não existir.)

### 2. Configurar a senha do MySQL

O `application.properties` lê a senha da variável de ambiente `DB_PASSWORD`. Para uso local, crie o arquivo
`src/main/resources/application-local.properties` (ele é ignorado pelo Git, então a senha não vai para o GitHub):

```properties
spring.datasource.password=SUA_SENHA
```

O usuário padrão é `root` e o banco fica em `localhost:3306`. Para mudar, edite `src/main/resources/application.properties`.

### 3. Executar o projeto

Pela IDE:

```text
AquaSenseApplication.java
```

Ou pelo terminal:

```bash
mvn spring-boot:run
```

---

## 📡 Endpoints

### Locais

```http
GET  /api/locais
POST /api/locais
GET  /api/locais/{id}/leituras
```

### Leituras

```http
POST /api/leituras
```

Exemplo:

```json
{ "localId": 1, "nivelAgua": 30 }
```

### Alertas

```http
POST /api/alertas/gerar/{localId}
GET  /api/alertas
```

Exemplo:

```http
POST /api/alertas/gerar/1
```

Resposta:

```json
{
  "id": 1,
  "local": "Ponto Piloto (provisório) - São Paulo",
  "nivelAgua": 30.0,
  "nivelRisco": "CRITICO",
  "mensagem": "Nível de água crítico. Risco de alagamento: evite o local e procure um ponto seguro.",
  "dataGeracao": "2026-11-16T14:55:00"
}
```

### Classificação do nível de água

| Nível (cm) | Risco |
|---|---|
| menor que 10 | NORMAL |
| de 10 a menos de 25 | ATENCAO |
| 25 ou mais | CRITICO |

### Códigos de erro

| Código | Quando acontece |
|---|---|
| 400 | Dados inválidos (ex.: nível de água negativo ou `localId` ausente) |
| 404 | Local inexistente, ou local sem leituras ao gerar o alerta |

---

## 📖 Swagger

Após iniciar a aplicação:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Testes Automatizados

```bash
mvn test
```

| Classe de teste | O que verifica |
|---|---|
| `NivelRiscoTest` | Classificação do nível de água nos limites (NORMAL, ATENCAO, CRITICO) e erro para valor negativo |
| `AlertaServiceTest` | Geração do alerta, erro 404 para local inexistente e para local sem leituras, listagem |
| `LeituraServiceTest` | Registro de leitura e erro 404 para local inexistente |

Os testes usam Mockito e **não precisam do MySQL**.

---

## 📸 Evidências de Execução

### Swagger
<img width="1912" height="981" alt="image" src="https://github.com/user-attachments/assets/37c80aca-ce6e-4d37-a496-85e44b1185d2" />

### GET Locais
<img width="1911" height="986" alt="image" src="https://github.com/user-attachments/assets/a68e43a5-d9c3-47d1-be83-86e1815ff6ff" />

### POST Locais
<img width="1908" height="982" alt="image" src="https://github.com/user-attachments/assets/03d8656d-ed74-4e18-9d4e-8c28496e9174" />

### GET Locais Id
<img width="1903" height="985" alt="image" src="https://github.com/user-attachments/assets/2ce19f4b-d244-4a10-aca8-5d74814351a8" />

### POST Leitura
<img width="1890" height="981" alt="image" src="https://github.com/user-attachments/assets/5460b3a2-18e3-4fdf-95a9-ac321d5d9845" />

### POST Gerar Alerta
<img width="1912" height="991" alt="image" src="https://github.com/user-attachments/assets/3cdcaf50-1d92-4009-a8eb-f0641fd03193" />

### GET Alertas
<img width="1907" height="981" alt="image" src="https://github.com/user-attachments/assets/9509cfc7-81c2-4879-9ca7-ff5f553a6266" />

### Erro 404
<img width="1897" height="987" alt="image" src="https://github.com/user-attachments/assets/9657c84d-1dd8-4bd8-a8ed-b2db593926c7" />

### Banco MySQL
<img width="1912" height="1018" alt="image" src="https://github.com/user-attachments/assets/6d419fc3-28ca-4679-bba7-15d7b87a8af6" />

### Testes automatizados
<img width="1911" height="1028" alt="image" src="https://github.com/user-attachments/assets/a845e778-bfd5-4859-ba7f-3423a12798ae" />

## 🤖 Uso de IA

Os prompts utilizados estão registrados em [`prompts.md`](prompts.md).

---

## ✅ Status

Projeto desenvolvido para fins acadêmicos, demonstrando conceitos de APIs REST, estruturação de domínio, integração com banco de dados, migrações e testes automatizados.
