package com.example.estacionamento.model;

import java.io.Serializable;

public class Veiculo implements Serializable {

    private int id, ano, fkProprietario;

    private double mensalidade;
    private String placa;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public double getMensalidade() {
        return mensalidade;
    }

    public void setMensalidade(double mensalidade) {
        this.mensalidade = mensalidade;
    }

    public int getFkProprietario() {
        return fkProprietario;
    }

    public void setFkProprietario(int fkProprietario) {
        this.fkProprietario = fkProprietario;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
