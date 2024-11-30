CREATE database estacionamento;

use estacionamento;

CREATE TABLE
  proprietario (
    Id_proprietario INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NULL,
    cpf VARCHAR(255) NULL,
    PRIMARY KEY (Id_proprietario)
  );

CREATE TABLE
  veiculo (
    Id_veiculo INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    fk_proprietario INTEGER UNSIGNED NOT NULL,
    placa VARCHAR(255) NULL,
    ano INTEGER UNSIGNED NULL,
    mensalidade DECIMAL(10, 2) NULL,
    PRIMARY KEY (Id_veiculo),
    INDEX veiculo_FKIndex1 (fk_proprietario),
    FOREIGN KEY (fk_proprietario) REFERENCES Proprietario (Id_proprietario) ON DELETE NO ACTION ON UPDATE NO ACTION
  );

insert into proprietario(nome, cpf) values("Mario", "332323"); 
insert into veiculo(placa, ano, mensalidade, fk_proprietario) values("gav-99", 2017, "1423.58",1); 
