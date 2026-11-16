CREATE TABLE locais (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE leituras (
    id BIGINT NOT NULL AUTO_INCREMENT,
    local_id BIGINT NOT NULL,
    nivel_agua DOUBLE NOT NULL,
    data_hora DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_leituras_local FOREIGN KEY (local_id) REFERENCES locais (id)
);

CREATE TABLE alertas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    local_id BIGINT NOT NULL,
    nivel_agua DOUBLE NOT NULL,
    nivel_risco VARCHAR(20) NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    data_geracao DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alertas_local FOREIGN KEY (local_id) REFERENCES locais (id)
);
