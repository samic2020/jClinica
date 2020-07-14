/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Faturamentos;

/**
 *
 * @author supervisor
 */
public class EntradasSaidasEstoque {
        public String codigo;
        public String descricao;
        public String unidade;
        public double entrada;
        public double saida;
        public int grupoid;
        public String gruponm;

        public EntradasSaidasEstoque(String codigo, String descricao, String unidade, double entrada, double saida, int grupoid, String gruponm) {
            this.codigo = codigo;
            this.descricao = descricao;
            this.unidade = unidade;
            this.entrada = entrada;
            this.saida = saida;
            this.grupoid = grupoid;
            this.gruponm = gruponm;
        }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getEntrada() {
        return entrada;
    }

    public void setEntrada(double entrada) {
        this.entrada = entrada;
    }

    public double getSaida() {
        return saida;
    }

    public void setSaida(double saida) {
        this.saida = saida;
    }

    public int getGrupoid() {
        return this.grupoid;
    }
    
    public void setGrupoid(int grupoid) {
        this.grupoid = grupoid;
    }
    
    public String getGruponm() {
        return this.gruponm;
    }
    
    public void setGruponm(String gruponm) {
        this.gruponm = gruponm;
    }
    
    public double SomaEntrada(double value) {
        this.entrada += value;
        return this.entrada;
    }
    
    public double SomaSaida(double value) {
        this.saida += value;
        return this.saida;
    }
    
    @Override
    public String toString() {
        return "EntradasSaidasEstoque{" + "descricao=" + descricao + ", unidade=" + unidade + ", entrada=" + entrada + ", saida=" + saida + '}';
    }        
}
