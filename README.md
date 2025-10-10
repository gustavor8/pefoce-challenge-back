# Sistema de Cadeia de Custódia de Vestígios Forenses

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)
![Status](https://img.shields.io/badge/Status-Finalizado-success.svg)

## 📜 Sobre o Projeto

Este projeto é a implementação de um **Sistema de Cadeia de Custódia de Vestígios Forenses**, desenvolvido como parte de um desafio técnico. O objetivo principal é garantir a **integridade**, **rastreabilidade** e **validade jurídica** de evidências em investigações criminais através de uma API RESTful robusta e segura.

O sistema documenta cronologicamente cada etapa do ciclo de vida de uma evidência, desde sua coleta até sua análise, utilizando uma blockchain simulada para garantir a imutabilidade de cada transação na cadeia de custódia.

## ✨ Funcionalidades Implementadas

-   [x] **Gestão de Vestígios:** CRUD completo para o cadastro de evidências, com metadados detalhados.
-   [x] **Controle de Transferências:** Registro de todas as transferências de custódia entre responsáveis.
-   [x] **Imutabilidade com Blockchain Simulada:** Cada transferência é registrada em um bloco com hash SHA-256, encadeado ao bloco anterior, garantindo a integridade dos dados.
-   [x] **Segurança e Autenticação:** Sistema de autenticação via JWT (Access Token + Refresh Token em cookie HttpOnly) com Spring Security.

-   [x] **Relatórios e Auditoria:**
    -   Geração de um relatório completo da cadeia de custódia para qualquer vestígio.
    -   Validação em tempo real da integridade da cadeia através da verificação dos hashes da blockchain.
    -   **(Bônus)** Exportação do relatório completo em formato **PDF**.
-   [x] **Documentação de API:** Geração automática de documentação interativa com Swagger (OpenAPI 3).
-   [x] **Containerização:** Ambiente de desenvolvimento e produção totalmente containerizado com Docker e Docker Compose.
-   [x] **Testes Automatizados:** Suíte de testes unitários com JUnit 5 e Mockito, com relatório de cobertura gerado e servido automaticamente.
-   [x] **Íncicies:** Foi implementado índicies estratégicos para melhor o desempenho da aplicação.


## 🚀 Tecnologias e Arquitetura

Este projeto foi construído utilizando tecnologias modernas e seguindo as melhores práticas de desenvolvimento de software.

#### Stack Principal
* **Java 21**
* **Spring Boot 3.3.4**
* **Spring Security 6** (Autenticação JWT)
* **Spring Data JPA / Hibernate** (Persistência de dados)
* **PostgreSQL 15** (Banco de dados relacional)
* **Flyway** (Gerenciamento de migrations do banco de dados)

#### Ferramentas e Outros
* **Maven** (Gerenciador de dependências)
* **Docker & Docker Compose** (Containerização e orquestração)
* **Nginx** (Servidor web para expor o relatório de testes)
* **iTextPDF** (Geração de relatórios em PDF)
* **JUnit 5 & Mockito** (Testes unitários)
* **JaCoCo** (Relatório de cobertura de testes)
* **Swagger (OpenAPI 3)** (Documentação da API)

#### Padrões e Arquitetura
O sistema foi projetado com uma arquitetura em camadas bem definida para garantir a separação de responsabilidades e a manutenibilidade. A estrutura em camadas foi baseado na arquitetura MVC com à adaptação da camada view para se tornar uma APIRest. Além de ser baseada nos princípios SOLID.
* **Arquitetura em Camadas:** `Controller` (API), `Service` (Lógica de Negócio) e `Repository` (Acesso a Dados).
* **Padrão CQRS (Command Query Responsibility Segregation):** A camada de serviço foi dividida em `Commands` (operações de escrita, como `Create`, `Update`, `Delete`) e `Queries` (operações de leitura), resultando em classes mais coesas e especializadas.
* **DTOs (Data Transfer Objects):** Utilizados para desacoplar a camada da API do modelo de persistência, garantindo segurança e flexibilidade no contrato da API.
* **Padrões de Design:** Dentre os padrões utilizados podem ser citados o Builder, Singleton, Facade, Decorator e Adapter.
### 🧪 Estratégia de Testes
A qualidade do código é garantida por uma estratégia focada em testes unitários para validar a lógica de negócio de forma isolada.
* **Ferramentas:** A suíte de testes utiliza **JUnit 5** para a estrutura e **Mockito** para mockar as dependências (como Repositórios).
* **Foco:** O principal alvo dos testes é a **camada de Serviço (`Service`)**, onde as regras de negócio críticas são implementadas. Mockar as dependências permite testar cada cenário (sucesso e falha) de forma rápida e determinística, sem depender de banco de dados ou outros componentes externos.

#### Acesso ao Relatório de Cobertura (JaCoCo)
Como parte do pipeline automatizado no `docker-compose.yml`, a suíte de testes é executada a cada `build`. Se todos os testes passarem, o relatório de cobertura de código gerado pelo **JaCoCo** é automaticamente publicado e fica acessível no seguinte endereço:

* **URL do Relatório:** `http://localhost:8082`

Ao acessar o relatório, é possível navegar pelos pacotes e classes do projeto, observando visualmente as linhas de código que foram cobertas pelos testes. O objetivo é garantir uma alta porcentagem de cobertura especialmente nas classes da camada de serviço, onde a lógica de negócio é implementada.

### ⚡ Otimização de performance com índicies
#### 1. Índices na Tabela 'vestigios'
- **A) Índice: idx_vestigios_status**
    - **Coluna:** status
    - **Função:** Acelerar qualquer busca ou filtro de vestígios pelo seu status (ex: "EM ANÁLISE", "ARQUIVADO").
    - **Uso Prático:** Dashboards gerenciais que mostram contagens por status e filtros em telas de listagem de vestígios.
    - **Impacto:** Garante que a interface do usuário seja rápida e responsiva ao filtrar dados, evitando a leitura completa da tabela.

- **B) Índice: idx_vestigios_responsavel_atual**
    - **Coluna:** responsavel_atual_id
    - **Função:** Encontrar rapidamente todos os vestígios que estão sob a custódia de um usuário específico.
    - **Uso Prático:** Tela principal do perito logado ("Meus Vestígios") e relatórios de carga de trabalho por usuário.
    - **Impacto:** Permite o carregamento instantâneo de telas personalizadas para o usuário, mesmo com um grande volume de dados.

#### 2. Índices na Tabela 'transferencias'
- **A) Índice: idx_transferencias_responsavel_origem**
    - **Coluna:** responsavel_origem_id
    - **Função:** Rastrear de forma eficiente todas as transferências       INICIADAS por um usuário.
    - **Uso Prático:** Geração de relatórios de auditoria e tela de "Histórico de Envios" para um usuário.
    - **Impacto:** Essencial para funcionalidades de auditoria, tornando a geração de relatórios de atividade rápida.

- **B) Índice: idx_transferencias_responsavel_destino**
    - **Coluna:** responsavel_destino_id
    - **Função:** Rastrear de forma eficiente todas as transferências RECEBIDAS por um usuário.
    - **Uso Prático:** Geração de relatórios de auditoria e tela de "Histórico de Recebimentos" para um usuário.
    - **Impacto:** Complementa a capacidade de auditoria, permitindo uma visão 360º das atividades de um usuário.

#### **3. Índice na Tabela de Junção 'transferencia_vestigios'**
- **A) Índice: idx_transferencia_vestigios_vestigio_id (CRÍTICO)**
    - **Coluna:** vestigio_id
    - **Função:** Reconstruir a cadeia de custódia completa de um único vestígio de forma quase instantânea. É o índice mais importante do sistema.
    - **Uso Prático:** Tela de "Detalhes do Vestígio", onde a timeline/histórico completo de transferências é exibida.
    - **Impacto:** Garante que a funcionalidade principal do sistema (gerar o relatório de rastreabilidade de um vestígio) seja extremamente rápida, evitando a leitura completa da massiva tabela de junção. Sem ele, a funcionalidade seria inviável em um ambiente com muitos dados.

Em resumo, esses índices são otimizações que transformam operações de busca potencialmente lentas (que leem tabelas inteiras) em operações de alta velocidade (que usam atalhos), garantindo a performance e a boa experiência de uso do sistema.


## ⚙️ Pré-requisitos

Para executar este projeto, você precisará ter instalado em sua máquina:
* [Git](https://git-scm.com/)
* [Docker](https://www.docker.com/get-started)
* [Docker Compose](https://docs.docker.com/compose/install/)

#### Opcional 
Para rodar direto no ambiente de desenvolvimento (Pode exigir configurações do banco baseado no application.properties)
* [Java](https://www.oracle.com/java/technologies/downloads/)
* [Postgres](https://www.postgresql.org/download/)

## 🚀 Instruções de Execução e Avaliação

Para clonar e executar o projeto, siga os passos abaixo:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/gustavor8/pefoce-challenge-back.git
    ```

2.  **Navegue até a pasta do projeto:**
    ```bash
    cd pefoce-challenge-back
    ```

3.  **Execute o Docker Compose:**
    O comando a seguir irá orquestrar um pipeline completo e automatizado, executando os contêineres em segundo plano (`-d`):
    ```bash
    docker-compose up -d --build
    ```

Este comando irá:

* Construir a imagem da aplicação.
* Iniciar os serviços de banco de dados (PostgreSQL).
* **Executar a suíte completa de testes automatizados.**
* Iniciar a API principal **apenas se os testes passarem**.
* **Disponibilizar o relatório de cobertura de testes.**

Após a inicialização, os seguintes serviços estarão disponíveis:

* **API Principal:** `http://localhost:8081` (Redireciona para a documentação quando acessado)
* **Documentação (Swagger UI):** `http://localhost:8081/swagger-ui.html`
* **Relatório de Cobertura de Testes (JaCoCo):** `http://localhost:8082`

### 🧪 Testando com o Insomnia

Uma coleção pré-configurada para o Insomnia está disponível na raiz do projeto: `Insomnia_Challenge_Pefoce.json`. Ela contém todas as requisições prontas para uso.

**Para usar:**
1.  Importe o arquivo `Insomnia_Challenge_Pefoce.json` no seu Insomnia.
2.  O `DataSeeder` da aplicação já populou o banco com um estado inicial completo, incluindo: **2 usuários** (`enio.perito` e `emanuel.perito`, ambos com senha `senha123`), **5 vestígios** e **5 transferências** iniciais para simular um histórico.
3.  Execute a requisição `Login (Ênio)` para obter um `accessToken`.
4.  Copie o token e cole-o no header `Authorization` das requisições protegidas (no lugar de `COLE_SEU_TOKEN_AQUI`).
5.  Para rotas que exigem UUIDs, substitua os placeholders (ex: `ID_DO_VESTIGIO_CELULAR_AQUI`) por IDs reais obtidos do seu banco ou da resposta de outras requisições.

## 📋 Casos de Uso Documentados

A seguir estão os principais fluxos de interação com o sistema.

### 1. Registro de um Novo Vestígio
* **Ator:** Perito Criminal (usuário autenticado).
* **Objetivo:** Cadastrar uma nova evidência encontrada em uma cena de crime.
* **Fluxo:**
    1.  O perito se autentica na API para obter um token de acesso.
    2.  Envia uma requisição `POST /api/vestigios` com os detalhes da evidência (tipo, descrição, local/data da coleta e o ID do responsável inicial).
    3.  O sistema valida os dados recebidos.
    4.  O sistema cria um novo registro para o vestígio, atribuindo um UUID único e o status inicial **`COLETADO`**.
    5.  O sistema retorna uma resposta `201 Created` com os dados completos do vestígio recém-criado.

### 2. Transferência de Custódia
* **Ator:** Perito Criminal (responsável atual pela custódia).
* **Objetivo:** Transferir a posse de um ou mais vestígios para outro perito (ex: para análise em laboratório).
* **Fluxo:**
    1.  O perito de origem se autentica na API.
    2.  Envia uma requisição `POST /api/transferencias` contendo os IDs dos vestígios, o ID do perito de destino e o motivo da transferência.
    3.  O sistema **valida a posse**, confirmando que o perito de origem é de fato o responsável atual por todos os vestígios listados.
    4.  O sistema cria um registro de `Transferencia`, calcula seu hash (SHA-256) e o salva.
    5.  O sistema atualiza o `responsavelAtual` de cada vestígio para o perito de destino e altera seu status para **`EM_ANALISE`**.
    6.  O sistema invoca o `BlockchainService` para **criar um novo bloco**, "selando" a transação de transferência de forma imutável na cadeia.
    7.  O sistema retorna uma resposta `200 OK` com os detalhes da transferência.

### 3. Auditoria da Cadeia de Custódia
* **Ator:** Auditor ou Autoridade (usuário autenticado).
* **Objetivo:** Gerar um relatório completo para verificar a integridade e o histórico de um vestígio.
* **Fluxo:**
    1.  O auditor se autentica na API.
    2.  Envia uma requisição `GET /api/relatorios/cadeia-custodia/{vestigioId}`.
    3.  O sistema busca o vestígio e todo o seu histórico de transferências, ordenado por data.
    4.  Para cada transferência no histórico, o sistema **recalcula o hash da transação e do bloco associado**, comparando com os valores armazenados para detectar adulterações.
    5.  O sistema retorna uma resposta `200 OK` com um relatório completo em JSON, contendo os dados do vestígio, o histórico de transferências e um status de validação (`valid: true` ou `valid: false` com a mensagem de erro).
    6.  *(Variação)* O auditor pode fazer a mesma requisição ao endpoint `/pdf` para receber o mesmo relatório em formato PDF.

### 4. Verificação da Integridade da Blockchain
* **Ator:** Administrador do Sistema (usuário autenticado).
* **Objetivo:** Realizar uma auditoria completa em toda a blockchain para garantir que nenhum dado histórico foi corrompido.
* **Fluxo:**
    1.  O administrador se autentica na API.
    2.  Envia uma requisição `GET /api/blockchain/validar`.
    3.  O sistema percorre toda a cadeia, do Bloco Gênese ao mais recente.
    4.  Para cada bloco, o sistema realiza duas verificações:
        * **Validação de Encadeamento:** Confirma se o `hashAnterior` do bloco atual corresponde ao `hashAtual` do bloco anterior.
        * **Validação de Conteúdo:** Recalcula o hash do bloco atual com base em seus dados e o compara com o `hashAtual` armazenado.
    5.  O sistema retorna uma resposta `200 OK` com uma mensagem de sucesso se a cadeia estiver íntegra, ou uma mensagem de erro detalhando exatamente onde a integridade foi quebrada.

<details>
<summary><strong>Clique para ver a Explicação Detalhada da Implementação da Blockchain</strong></summary>

### Explicação da Implementação da Blockchain Simulada

A funcionalidade de blockchain neste projeto, embora simulada (não é uma rede distribuída), implementa os princípios criptográficos fundamentais de uma blockchain para cumprir um requisito central do desafio: garantir a **imutabilidade** e a **integridade** da cadeia de custódia.

#### 1. Objetivo

O objetivo é criar um "livro-razão" digital à prova de adulteração. Uma vez que uma transferência de custódia é registrada, deve ser computacionalmente inviável alterá-la sem que a fraude seja detectada. Isso é alcançado através do encadeamento de blocos por meio de hashes criptográficos.

#### 2. Componentes Principais

A implementação é sustentada por quatro componentes principais:

* **Entidade `Blockchain`:** Representa um bloco na cadeia.
* **Entidade `Transferencia`:** Representa a "transação" registrada na blockchain.
* **Utilitário `HashUtils`:** Fornece o algoritmo `SHA-256` e garante hashes determinísticos.
* **Serviço `BlockchainService`:** Orquestra a criação de blocos e a validação da cadeia.

#### 3. Fluxo de Funcionamento e Validação

Quando uma transferência é criada, seu hash é calculado e ela é salva. Em seguida, um novo bloco é criado, contendo o hash do bloco anterior e o hash da nova transação, "selando-a" na cadeia. O processo de validação (`GET /api/blockchain/validar`) percorre a cadeia e recalcula todos os hashes para garantir que o encadeamento e o conteúdo de cada bloco permaneçam inalterados, provando a integridade dos dados.

</details>