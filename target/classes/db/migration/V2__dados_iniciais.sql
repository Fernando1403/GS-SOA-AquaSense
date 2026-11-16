-- Dados de demonstração SIMULADOS (não são medições reais).
INSERT INTO locais (nome, latitude, longitude)
VALUES ('Ponto Piloto (provisório) - São Paulo', -23.5505, -46.6333);

INSERT INTO leituras (local_id, nivel_agua, data_hora)
VALUES
    ((SELECT MIN(id) FROM locais), 6.0,  NOW(6) - INTERVAL 40 MINUTE),
    ((SELECT MIN(id) FROM locais), 15.0, NOW(6) - INTERVAL 20 MINUTE),
    ((SELECT MIN(id) FROM locais), 27.0, NOW(6));
