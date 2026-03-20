CREATE TABLE IF NOT EXISTS cliente (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nome VARCHAR(255),
                         documento VARCHAR(20),
                         telefone VARCHAR(20),
                         email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS veiculo (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         cliente_id BIGINT,
                         placa VARCHAR(20),
                         marca VARCHAR(50),
                         modelo VARCHAR(50),
                         ano INT);

CREATE TABLE IF NOT EXISTS servico (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         codigo VARCHAR(50),
                         descricao VARCHAR(255),
                         preco DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS peca (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      sku VARCHAR(50),
                      nome VARCHAR(255),
                      preco DECIMAL(10,2),
                      quantidade_estoque INT
);

CREATE TABLE IF NOT EXISTS ordem_servico (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               cliente_id BIGINT,
                               veiculo_id BIGINT,
                               data_atualizacao TIMESTAMP NULL DEFAULT NULL,
                               data_criacao TIMESTAMP NULL DEFAULT NULL,
                               total_orcamento DECIMAL(10,2),
                               status VARCHAR(50),
                               FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                               FOREIGN KEY (veiculo_id) REFERENCES veiculo(id)
);

CREATE TABLE IF NOT EXISTS os_item_servico (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 ordem_id BIGINT,
                                 servico_id BIGINT,
                                 descricao VARCHAR(255),
                                 preco DECIMAL(10,2),
                                 FOREIGN KEY (ordem_id) REFERENCES ordem_servico(id),
                                 FOREIGN KEY (servico_id) REFERENCES servico(id)
);

CREATE TABLE IF NOT EXISTS os_item_peca (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              nome VARCHAR(255),
                              peca_id BIGINT,
                              preco_unitario DECIMAL(10,2),
                              quantidade INT,
                              ordem_id BIGINT,
                              FOREIGN KEY (ordem_id) REFERENCES ordem_servico(id),
                              FOREIGN KEY (peca_id) REFERENCES peca(id)
);
