# Registro de Uso de IA — AquaSense SOA (Global Solution 2026.2)

Este arquivo registra a conversa com a IA (Claude) usada como apoio no desenvolvimento do serviço de alertas. A IA foi usada para sugerir a arquitetura, revisar código e gerar testes. As decisões de escopo, a revisão do código e a execução (`mvn spring-boot:run` e `mvn test`) foram feitas pelo grupo.

- **Ferramenta:** Claude (Anthropic)
- **Disciplina:** SOA
- **Projeto:** AquaSense SOA — serviço de leituras e alertas em Java 21 / Spring Boot 3.3 com MySQL

---

## Conversa 1 — Contexto e arquitetura

**👤 Usuário**

> Estou fazendo a Global Solution da FIAP (SOA). Nosso grupo criou uma "CitizenTech" chamada AquaSense: um sensor de nível de água (ESP32) envia leituras, e um app Expo consulta. Outra pessoa do grupo faz a API de coleta em C#. Eu fiz a parte de SOA, um serviço de alertas de alagamento.
>
> Requisitos do professor: Java com Spring Boot, camadas (controller, service e repository), interfaces, injeção de dependência, DTOs com validação, tratamento de exceções, banco de dados com migrações e testes automatizados.
>
> Criei o projeto Maven com o Spring Initializr. Me sugere a arquitetura e as entidades, sem exagerar. É projeto acadêmico e eu preciso explicar na apresentação.

**🤖 Claude**

> Sugestão enxuta que cobre todos os requisitos:
>
> **Entidades**
> - `Local`: ponto monitorado (`id`, `nome`, `latitude`, `longitude`).
> - `Leitura`: medição de nível de água em cm, ligada a um local (`id`, `local`, `nivelAgua`, `dataHora`).
> - `Alerta`: resultado gerado a partir da última leitura de um local (`id`, `local`, `nivelAgua`, `nivelRisco`, `mensagem`, `dataGeracao`).
> - `NivelRisco`: enum `NORMAL`, `ATENCAO`, `CRITICO`.
>
> **Camadas**
> ```
> controller  → endpoints REST (finos)
> interfaces  → contratos dos services (ILocalService, ILeituraService, IAlertaService)
> service     → regras de negócio (implementam as interfaces)
> repository  → Spring Data JPA
> dto         → entrada e saída, com validação
> exception   → exceções de negócio e handler global
> ```
>
> **Fluxo (a parte "SOA")**
> `Sensor → POST /api/leituras → este serviço → POST /api/alertas/gerar/{localId} → Alerta → banco`
>
> **Decisões**
> - A classificação em Normal, Atenção e Crítico fica **no enum**, que carrega a regra e a mensagem de cada nível. Fica simples de testar e de explicar.
> - O esquema do banco é criado por **Flyway**, e não pelo Hibernate (`ddl-auto=none`), para as mudanças de esquema ficarem versionadas.
> - Privacidade: o `Local` guarda só coordenadas aproximadas, nunca endereço residencial. Vale escrever isso no README.

---

## Conversa 2 — Modelos, enum e banco de dados

**👤 Usuário**

> Fechado, vou seguir essa arquitetura. Não precisa escrever todas as classes: me dá o exemplo de uma, a entidade `Local`, bem comentada, pra eu ver o padrão do JPA e fazer as outras (`Leitura`, `Alerta` e o enum `NivelRisco`) por conta própria. Estou usando Lombok.

**🤖 Claude**

> Segue a `Local` como modelo.
>
> ```java
> package com.aquasense.model;
>
> import jakarta.persistence.*;
> import lombok.Data;
>
> @Entity
> @Table(name = "locais")
> @Data
> public class Local {
>
>     @Id
>     @GeneratedValue(strategy = GenerationType.IDENTITY)
>     private Long id;
>
>     private String nome;
>
>     // Coordenadas aproximadas do ponto monitorado (nunca endereço residencial)
>     private Double latitude;
>     private Double longitude;
> }
> ```
>
> O padrão para as demais:
> - `@Entity` + `@Table(name = "...")` com o nome da tabela no plural, igual ao da migração.
> - `@Id` + `@GeneratedValue(IDENTITY)`, que combina com `AUTO_INCREMENT` do MySQL.
> - `@Data` do Lombok gera getters, setters, `equals` e `toString`.
> - Na `Leitura` e no `Alerta`, o lado "N" da relação usa `@ManyToOne(optional = false)` com `@JoinColumn(name = "local_id")`.
> - Os nomes dos campos em camelCase (`nivelAgua`) mapeiam sozinhos para colunas em snake_case (`nivel_agua`) no Spring Boot.
>
> Dicas para o enum: coloque a mensagem de cada nível no próprio construtor (`NORMAL("...")`), os limites como constantes públicas e um método estático `doNivelAgua(double)` que classifica. Verifique o valor negativo **antes** das faixas, e teste primeiro `>= 25`, depois `>= 10`.

**👤 Usuário**

> Fiz o resto seguindo o exemplo. No `NivelRisco` coloquei as constantes `LIMITE_ATENCAO_CM = 10` e `LIMITE_CRITICO_CM = 25`, e o método `doNivelAgua` lança `IllegalArgumentException` se o valor for negativo. Pode revisar?

**🤖 Claude**

> A lógica está correta. Pontos da revisão:
>
> - A ordem (negativo, depois `>= 25`, depois `>= 10`) está certa. Se invertesse, todo valor crítico cairia em Atenção.
> - Guardar a mensagem no enum evita um `switch` no service, e uma regra nova de nível passa a ser mudança em um lugar só.
> - `IllegalArgumentException` é adequada para valor inválido. A API não deixa chegar valor negativo, porque o DTO já valida antes. O enum fica protegido caso seja chamado de outro ponto.
> - No `Alerta`, você guardou `nivelRisco` como `String` (`risco.name()`). Funciona e simplifica a migração. A alternativa é `@Enumerated(EnumType.STRING)`, mas aí a coluna fica acoplada ao enum.
>
> Nos testes, cubra exatamente os limites 10 e 25, onde costuma haver erro de `>` versus `>=`.

**👤 Usuário**

> Agora o banco. Quero MySQL com Flyway: uma migração para criar as tabelas `locais`, `leituras` e `alertas` com chave estrangeira, e outra com dados de demonstração SIMULADOS (1 local e 3 leituras subindo até o nível crítico). A senha do banco não pode ir pro GitHub.

**🤖 Claude**

> **`V1__criar_tabelas.sql`**: três tabelas com `id BIGINT AUTO_INCREMENT`, `nome VARCHAR(100) NOT NULL`, coordenadas `DOUBLE NOT NULL`, e as chaves estrangeiras `fk_leituras_local` e `fk_alertas_local` apontando para `locais(id)`. Use `DATETIME(6)` nas datas, para guardar frações de segundo.
>
> **`V2__dados_iniciais.sql`**: insere o "Ponto Piloto (provisório) - São Paulo" e 3 leituras com níveis 6, 15 e 27 cm (Normal, Atenção e Crítico), com datas relativas (`NOW(6) - INTERVAL 20 MINUTE`). Deixe o comentário "dados SIMULADOS" no topo. Use `(SELECT MIN(id) FROM locais)` para pegar o id do local, em vez de fixar `1`.
>
> **Configuração**
> - `spring.jpa.hibernate.ddl-auto=none`, para o Hibernate não mexer no esquema.
> - `spring.flyway.enabled=true`.
> - URL com `createDatabaseIfNotExist=true`, para o banco ser criado sozinho.
> - A senha lê uma variável de ambiente: `spring.datasource.password=${DB_PASSWORD:SUA_SENHA}`.
> - Para uso local, um `application-local.properties` importado com `spring.config.import=optional:classpath:application-local.properties`.
>
> **Importante:** esse arquivo local só protege a senha se estiver no `.gitignore`. Antes do primeiro commit, confira com `git ls-files` que ele **não** aparece na lista. Se já tiver sido commitado, o `.gitignore` sozinho não resolve: é preciso removê-lo do índice (`git rm --cached`) e **trocar a senha**, porque ela já ficou no histórico.

---

## Conversa 3 — DTOs, repositories, services e controllers

**👤 Usuário**

> Agora a camada de API. Preciso destes endpoints:
> - `GET /api/locais`, `POST /api/locais`, `GET /api/locais/{id}/leituras`
> - `POST /api/leituras` (é o que a API em C#/ESP32 vai chamar)
> - `POST /api/alertas/gerar/{localId}` e `GET /api/alertas`
>
> Não quero devolver a entidade direto. Cria DTOs com validação, as interfaces dos services e os repositories. O `POST /api/leituras` não deve aceitar `id` nem `dataHora` do cliente, a data é a do servidor.

**🤖 Claude**

> **Repositories** (Spring Data JPA, sem SQL manual):
> - `LocalRepository extends JpaRepository<Local, Long>`.
> - `LeituraRepository` com `findByLocalIdOrderByDataHoraDesc(Long)` e `findFirstByLocalIdOrderByDataHoraDesc(Long)` (a última leitura).
> - `AlertaRepository` com `findAllByOrderByDataGeracaoDesc()`.
>
> **DTOs** com Bean Validation e Lombok:
> - `LocalDTO`: `@NotBlank` + `@Size(3..100)` no nome, `@NotNull` + `@DecimalMin/@DecimalMax` na latitude (-90 a 90) e longitude (-180 a 180).
> - `LeituraDTO`: `localId` `@NotNull`, `nivelAgua` `@NotNull` com `@DecimalMin("0.0")` e `@DecimalMax("1000.0")`.
> - `AlertaDTO`: só saída (`id`, `local`, `nivelAgua`, `nivelRisco`, `mensagem`, `dataGeracao`).
>
> Para `id` e `dataHora` não serem aceitos, marque com `@Schema(accessMode = READ_ONLY)`. No service, ignore o que vier do cliente e use `LocalDateTime.now()`.
>
> **Interfaces:** `ILocalService` (`listar`, `criar`), `ILeituraService` (`registrar`, `listarPorLocal`), `IAlertaService` (`gerarAlerta`, `listar`). Os controllers dependem só da **interface**, e a implementação é injetada pelo construtor, sem `@Autowired` em campo.
>
> **Regra do alerta** (`AlertaService.gerarAlerta`):
> 1. Busca o local. Se não existir, lança `ResourceNotFoundException`.
> 2. Busca a última leitura. Se não houver, lança `ResourceNotFoundException`.
> 3. Classifica com `NivelRisco.doNivelAgua(...)`.
> 4. Monta o `Alerta` (nível, `risco.name()`, `risco.getMensagem()`, `LocalDateTime.now()`), salva e devolve o DTO.
>
> **Controllers:** `@RestController`, `ResponseEntity` com `201 Created` nos POSTs, `@Valid @RequestBody` nos DTOs de entrada e `@Operation`/`@Tag` do Swagger.

**👤 Usuário**

> No `GET /api/locais/{id}/leituras` de um id que não existe, quero 404 e não uma lista vazia.

**🤖 Claude**

> No `listarPorLocal`, valide antes de consultar:
>
> ```java
> if (!localRepository.existsById(localId)) {
>     throw new ResourceNotFoundException("Local " + localId + " não encontrado.");
> }
> ```
>
> Assim, lista vazia significa "o local existe, mas ainda não tem leituras", e 404 significa "o local não existe". O `existsById` evita carregar a entidade inteira só para checar.

---

## Conversa 4 — Tratamento de exceções

**👤 Usuário**

> Preciso tratar os erros. Quero 404 quando o local não existe e 400 quando o DTO é inválido, com uma mensagem legível. Sem stack trace pro cliente.

**🤖 Claude**

> Crie uma exceção de negócio e um handler global:
>
> - `ResourceNotFoundException extends RuntimeException`, só com a mensagem.
> - `GlobalExceptionHandler` com `@RestControllerAdvice` e dois métodos:
>   - `@ExceptionHandler(ResourceNotFoundException.class)` devolve `404` com a mensagem da exceção.
>   - `@ExceptionHandler(MethodArgumentNotValidException.class)` devolve `400`, juntando os erros de campo no formato `campo: mensagem`, separados por `; `.
>
> Assim, os services só lançam a exceção e os controllers ficam limpos, sem `try/catch`.
>
> Para evoluir depois, dá para devolver um objeto JSON (`timestamp`, `status`, `mensagem`) em vez de texto simples. Para a entrega acadêmica, texto é suficiente.

**👤 Usuário**

> Testei no Swagger com `nivelAgua: -5` e voltou 400 com a mensagem certa. E o `localId` inexistente voltou 404. Funcionou.

---

## Conversa 5 — Testes automatizados

**👤 Usuário**

> Agora os testes com JUnit 5 e Mockito. Não quero depender do MySQL nos testes. Quero:
> 1. Testar o `NivelRisco` nos limites (0, 9.9, 10, 24.9, 25, 80) e o erro com valor negativo.
> 2. Testar o `AlertaService`: local inexistente, local sem leituras, alerta crítico gerado e a listagem.
> 3. Testar o `LeituraService`: local inexistente e leitura registrada com sucesso.

**🤖 Claude**

> Criei três classes de teste.
>
> **`NivelRiscoTest`**: um `@ParameterizedTest` com `@CsvSource` para as fronteiras (6 casos) e um `@Test` com `assertThrows(IllegalArgumentException.class, ...)` para `-1`. Total: 7 execuções.
>
> **`AlertaServiceTest`** (`@ExtendWith(MockitoExtension.class)`, com `@Mock` nos três repositories e `@InjectMocks` no service):
> - local inexistente → `ResourceNotFoundException` e `verifyNoInteractions(alertaRepository)`;
> - local sem leituras → `ResourceNotFoundException` e `verify(alertaRepository, never()).save(any())`;
> - nível 30 → alerta `CRITICO`, com o nome do local e o nível preservados;
> - `listar` → converte a entidade em DTO.
>
> **`LeituraServiceTest`**: local inexistente lança exceção e **não** salva nada; local existente salva, e o DTO devolvido tem `localId`, `nivelAgua` e `dataHora` preenchida.
>
> Detalhe técnico: o `save` do repository mockado devolve o próprio argumento (`thenAnswer(inv -> inv.getArgument(0))`), simulando o comportamento do JPA.
>
> Total: 13 testes (7 + 4 + 2). Rode com `mvn test`.

**👤 Usuário**

> Rodei `mvn test`: 13 testes, 0 falhas, 0 erros. Tirei o print para as evidências.

---

## Conversa 6 — Revisão final e README

**👤 Usuário**

> Revisa o projeto como se fosse o professor. O que está fraco ou faltando? Depois monta o README no padrão da Global Solution.

**🤖 Claude**

> O projeto cobre os requisitos (interfaces, DI por construtor, DTOs com validação, exceções tratadas, JPA 1:N, Flyway e testes com Mockito). Pontos que eu registraria ou conferiria:
>
> 1. **Segredos:** confirme que nenhuma senha está no repositório (`git ls-files` e `git log -p`). Se algum arquivo com senha já foi commitado, remova-o do índice e troque a senha.
> 2. **Arquivos gerados:** a pasta `target/` não deve ir para o GitHub. Adicione `target/` ao `.gitignore`.
> 3. **Sem autenticação:** qualquer cliente chama os endpoints. É aceitável na entrega acadêmica, mas deixe escrito como limitação.
> 4. **Alerta sob demanda:** o alerta só é gerado quando alguém chama `POST /api/alertas/gerar/{localId}`. Uma evolução seria gerar automaticamente ao registrar uma leitura crítica.
> 5. **Limiares fixos e didáticos**, e dados de demonstração **simulados**. Avise isso no README, junto com o aviso de que o AquaSense **não substitui** a Defesa Civil nem o CGE.
>
> O README ficou com: sobre o projeto, ficha da CitizenTech, tecnologias, arquitetura e fluxo, conceitos aplicados, banco e migrações, como executar (incluindo a senha por variável de ambiente), endpoints com exemplos, tabela de classificação, códigos de erro, Swagger, testes e evidências.
>
> Antes de entregar, confira o que eu não consigo verificar por você: o link do repositório, os RMs e nomes do grupo, a porta e a URL do Swagger e os prints.

---

## Como a IA foi usada

| Etapa | Uso da IA | O que o grupo fez |
|---|---|---|
| Arquitetura | Sugestão de camadas e entidades | Escolheu o escopo e o fluxo sensor → leitura → alerta |
| Modelos e enum | Exemplo de uma entidade (`Local`) e revisão do restante | Escreveu `Leitura`, `Alerta` e `NivelRisco`, e definiu os limiares (10 e 25 cm) |
| Banco e Flyway | Estrutura das migrações e da configuração | Executou as migrações no MySQL e cuidou da senha do banco |
| DTOs, services e controllers | Geração e explicação | Executou, testou no Swagger e pediu o 404 para local inexistente |
| Exceções | Sugestão do handler global | Validou os códigos 400 e 404 no Swagger |
| Testes | Geração dos casos | Rodou `mvn test` e registrou o resultado |
| README | Estrutura do documento | Preencheu os dados do grupo e as evidências |

Todo o código foi lido, executado e testado pelo grupo antes da entrega.
