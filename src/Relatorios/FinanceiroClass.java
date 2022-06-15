/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Relatorios;

import java.math.BigDecimal;

/**
 *
 * @author Samic
 */
public class FinanceiroClass {
    private int id;
    private String data;
    private int codmedico;
    private String nmmedico;
    private int cdconvenio;
    private String nmconvenio;
    private int quantidade;
    
    // Comissionado
    private double vrconsulta;
    private double vrmedico;      // valor pago ao medico por consulta
    
    // Plantao
    private int dias;                 // Dias trabalhado
    private double vrplantao;     // Valor pago por plantão

    public FinanceiroClass(int id, String data, int codmedico, String nmmedico, 
            int cdconvenio, String nmconvenio, int quantidade, double vrconsulta, 
            double vrmedico, int dias, double vrplantao) {
        this.id = id;
        this.data = data;
        this.codmedico = codmedico;
        this.nmmedico = nmmedico;
        this.cdconvenio = cdconvenio;
        this.nmconvenio = nmconvenio;
        this.quantidade = quantidade;
        this.vrconsulta = vrconsulta;
        this.vrmedico = vrmedico;
        this.dias = dias;
        this.vrplantao = vrplantao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public int getCodmedico() { return codmedico; }
    public void setCodmedico(int codmedico) { this.codmedico = codmedico; }

    public String getNmmedico() { return nmmedico; }
    public void setNmmedico(String nmmedico) { this.nmmedico = nmmedico; }

    public int getCdconvenio() { return cdconvenio; }
    public void setCdconvenio(int cdconvenio) { this.cdconvenio = cdconvenio; }

    public String getNmconvenio() { return nmconvenio; }
    public void setNmconvenio(String nmconvenio) { this.nmconvenio = nmconvenio; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getVrconsulta() { return vrconsulta; }
    public void setVrconsulta(double vrconsulta) { this.vrconsulta = vrconsulta; }

    public double getVrmedico() { return vrmedico; }
    public void setVrmedico(double vrmedico) { this.vrmedico = vrmedico; }

    public int getDias() { return dias; }
    public void setDias(int dias) { this.dias = dias; }

    public double getVrplantao() { return vrplantao; }
    public void setVrplantao(double vrplantao) { this.vrplantao = vrplantao; }
}
