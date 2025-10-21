# 🧰 Oficina Mecânica — Tech Challenge (Fase 1 - SOAT)

# 🧭 Sobre o Projeto

# 

# Este projeto é o resultado da Fase 1 do Tech Challenge da pós-graduação em Arquitetura de Software (SOAT - FIAP/Alura).

# Trata-se de uma API REST desenvolvida em Spring Boot, estruturada com DDD (Domain-Driven Design), que implementa o domínio de uma oficina mecânica.

# 

# O sistema permite gerenciar:

# 

# Clientes

# 

# Veículos

# 

# Serviços

# 

# Peças e insumos (com controle de estoque)

# 

# Ordens de Serviço (OS) com todo o fluxo de atendimento, diagnóstico, orçamento e execução.

# 

# 🏗️ Arquitetura Utilizada

# 

# O projeto foi desenvolvido com base em DDD (Domain-Driven Design) e arquitetura hexagonal, separando claramente as responsabilidades por camadas:

# 

# src/main/java/com/exemplo/oficina

# ├── domain          -> Entidades e interfaces do domínio (regras de negócio)

# ├── application     -> Casos de uso (serviços de aplicação)

# ├── infrastructure  -> Implementações técnicas (JPA, banco de dados)

# └── interfaces

# └── rest        -> Controladores REST e DTOs

# 

# 

# Cada agregado tem seu próprio módulo:

# 

# cliente

# 

# veiculo

# 

# servico

# 

# peca

# 

# ordemservico

# 

# ⚙️ Tecnologias Utilizadas

# 

# Java 21

# 

# Spring Boot 3

# 

# Spring Data JPA

# 

# MySQL 8

# 

# Maven

# 

# Lombok (opcional)

# 

# Swagger / OpenAPI (documentação automática)

# 

# JUnit 5 (testes)

# 

# 🧩 Modelagem de Domínio

# Entidades principais

# Agregado	Responsabilidade

# Cliente	Identificação de clientes (CPF/CNPJ)

# Veículo	Associado ao cliente

# Serviço	Catálogo de serviços prestados

# Peça/Insumo	Controle de estoque e preços

# Ordem de Serviço	Integra os demais agregados, controla status e fluxo

# Fluxo da Ordem de Serviço

# 

# Identificar Cliente

# 

# Cadastrar Veículo (se necessário)

# 

# Criar Ordem de Serviço

# 

# Incluir Serviços e Peças

# 

# Gerar Orçamento

# 

# Aprovar ou Rejeitar Orçamento

# 

# Executar e Finalizar

# 

# Entregar o veículo

# 

# Estados possíveis da OS:

# 

# RECEBIDA → EM\_DIAGNOSTICO → AGUARDANDO\_APROVACAO →

# EM\_EXECUCAO → FINALIZADA → ENTREGUE

# 

# 🗃️ Banco de Dados (MySQL)

# 

# Crie o banco antes de rodar a aplicação:

# 

# CREATE DATABASE oficina\_db CHARACTER SET utf8mb4 COLLATE utf8mb4\_unicode\_ci;

# 

# ⚙️ Configuração (application.properties)

# spring.datasource.url=jdbc:mysql://localhost:3306/db\_db?useSSL=false\&serverTimezone=America/Sao\_Paulo

# spring.datasource.username={SEU_USER}

# spring.datasource.password={SUA_SENHA}

# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 

# spring.jpa.hibernate.ddl-auto=update

# spring.jpa.show-sql=true

# spring.jpa.properties.hibernate.format\_sql=true

# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 

# spring.jackson.time-zone=America/Sao\_Paulo

# spring.jpa.properties.hibernate.jdbc.time\_zone=America/Sao\_Paulo

# 

# server.port=8080

# spring.application.name=oficina-api

# 

# 🚀 Como Rodar o Projeto

# Pré-requisitos:

# 

# Java 21+

# 

# Maven 3.9+

# 

# MySQL 8

# 

# Passos:

# 

# Clone o repositório

# 

# git clone https://github.com/rafaellarosa07/tech-challenge.git

# cd tech-challange

# 

# 

# Configure o banco de dados MySQL conforme acima.

# 

# Rode o projeto:

# 

# mvn spring-boot:run

# 

# 

# Acesse:

# 

# API: http://localhost:8080/api..

# 

# Swagger: http://localhost:8080/swagger-ui.html

# 

# 📚 Endpoints Principais

# 🧍 Cliente

# POST   /api/clientes

# GET    /api/clientes

# GET    /api/clientes/{id}

# PUT    /api/clientes/{id}

# DELETE /api/clientes/{id}

# 

# 🚘 Veículo

# POST   /api/veiculos

# GET    /api/veiculos

# GET    /api/veiculos/{id}

# PUT    /api/veiculos/{id}

# DELETE /api/veiculos/{id}

# 

# 🧰 Serviço

# POST   /api/servicos

# GET    /api/servicos

# GET    /api/servicos/{id}

# PUT    /api/servicos/{id}

# DELETE /api/servicos/{id}

# 

# ⚙️ Peça/Insumo

# POST   /api/pecas

# GET    /api/pecas

# GET    /api/pecas/{id}

# PUT    /api/pecas/{id}

# DELETE /api/pecas/{id}

# 

# 🧾 Ordem de Serviço

# POST   /api/os                   -> cria nova OS

# POST   /api/os/{id}/servicos     -> adiciona serviços

# POST   /api/os/{id}/pecas        -> adiciona peças

# POST   /api/os/{id}/orcamento    -> gera orçamento

# POST   /api/os/{id}/aprovar      -> aprova orçamento

# POST   /api/os/{id}/finalizar    -> finaliza execução

# POST   /api/os/{id}/entregar     -> registra entrega

# GET    /api/os/{id}              -> busca por ID

# GET    /api/os                   -> lista todas

# DELETE /api/os/{id}              -> remove OS

# 

# 🧪 Testes Unitários

# 

# Os testes podem ser executados com:

# 

# mvn test

# 

# 

# Exemplo de teste:

# 

# @Test

# void deveGerarOrcamentoCorreto() {

# OrdemServico os = new OrdemServico(1L, 1L);

# os.incluirServico(new ItemServico(1L, "Troca de óleo", new BigDecimal("100")));

# os.incluirPeca(new ItemPeca(1L, "Filtro", new BigDecimal("50"), 2));

# 

# &nbsp;   os.gerarOrcamento();

# 

# &nbsp;   assertEquals(new BigDecimal("200"), os.getTotalOrcamento());

# &nbsp;   assertEquals(StatusOS.AGUARDANDO\_APROVACAO, os.getStatus());

# }

# 


# 👩‍💻 Autora



# Rafaella Aparecida Rosa Lima Torres

# 📚 Pós-graduação FIAP — Software Architecture (SOAT)

# 📆 Tech Challenge — Fase 1

