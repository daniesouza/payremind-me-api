CREATE TABLE categoria (
  codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT into categoria (nome) VALUES ('Lazer');
INSERT into categoria (nome) VALUES ('ALimentacao');
INSERT into categoria (nome) VALUES ('SuperMercado');
INSERT into categoria (nome) VALUES ('FArmacia');
INSERT into categoria (nome) VALUES ('Outros');