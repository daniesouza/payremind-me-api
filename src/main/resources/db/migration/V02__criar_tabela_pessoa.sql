CREATE TABLE pessoa (
  codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(50) NOT NULL,
  ativo BOOLEAN NOT NULL,
  logradouro VARCHAR(200) NOT NULL,
  numero VARCHAR(5) NOT NULL,
  complemento VARCHAR(15) NOT NULL,
  bairro VARCHAR(50) NOT NULL,
  cep VARCHAR(10) NOT NULL,
  cidade VARCHAR(50) NOT NULL,
  estado VARCHAR(50) NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado)
VALUES ('JOAO',true ,'RUA JOAO','123','CASA 5','JARDIM SANTO','06984123','SAO PAULO','SP');

INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado)
VALUES ('DANIEL',true ,'RUA DANIEL','11','CASA 15','JARDIM AA','06984123','MINAS GERAIS','MG');

INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado)
VALUES ('TESTE',true ,'RUA TESTE','22','CASA 522','JARDIM BB','06984123','RIO DE JANEIRO','RJ');

INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado)
VALUES ('AAAAA',true ,'RUA JOAAAAaAO','33','CASA 5','JARDIM SANTO','06984123','SAO PAULO','SP');
