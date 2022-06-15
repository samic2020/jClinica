/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import Db.DbMain;
import java.sql.Connection;

/**
 *
 * @author supervisor
 */
public class VariaveisGlobais {
    static public Connection conn = null;
    static public DbMain con = null;
    static public String reader = "";
    static public String dbnome = "";

    static public String unidade = "";
    static public String munidade = "";
    static public String remoto1 = "";
    static public String und_rmt1 = "";
    static public String remoto2 = "";
    static public String und_rmt2 = "";
    static public String remoto3 = "";
    static public String und_rmt3 = "";
    static public String remoto4 = "";
    static public String und_rmt4 = "";
    static public String remoto5 = "";
    static public String und_rmt5 = "";
    static public String[][] unidades = {};
    
    static public String path = "";
    static public String local = "";
    
    static public String origem = "";
    static public String logado = "";
    static public String cdlogado = "";

    static public boolean emedico = false;
    static public String cdmedico = "";
    static public String nmmedico = "";
    static public String lgmedico = "";
    
    static public boolean iswork = false;
    
    // Printers
    public static String PrinterMode = "NORMAL";  // NORMAL | EXTERNA
    public static String Thermica = null;
    public static String ThermicaMode = "NORMAL";
    public static String Printer = null;
    public static String Preview = null;
    public static String Externo = null;
    public static String Externo2 = null;
    public static String PrinterType = "PDF";
    public static Boolean statPrinter = true;
    
    public static String DefaultFilePath = "";
    public static float[] bobinaSize = {215f, 730f, 12, 2, -2, 2};    
    static public Collections dCliente = new Collections();
    static public String logo = "";
    static public int nviasRecibo = 1;
    
    public static String Recibo    = "";
    public static String Caixa     = "";
    public static String PassCaixa = "";
    
    public static String sWhere = "";
    
    public static void LerSettings() {
        // Settings
        Settings settings = new Settings();

        // informações de ambiente
        VariaveisGlobais.reader = System.getProperty("Reader", "evince");
        VariaveisGlobais.path = System.getProperty("Path", "dist/");
        VariaveisGlobais.local = System.getProperty("Local", "SGONCALO");
        VariaveisGlobais.origem = VariaveisGlobais.local;
        
        VariaveisGlobais.dbnome = System.getProperty("dbNome", "jClinica");
        VariaveisGlobais.unidade = System.getProperty("Unidade", "127.0.0.1");
        VariaveisGlobais.munidade = System.getProperty("MUnidade", "");
        VariaveisGlobais.remoto1 = System.getProperty("remoto1", "");
        VariaveisGlobais.und_rmt1 = System.getProperty("undRmt1", "");
        VariaveisGlobais.remoto2 = System.getProperty("remoto2", "");
        VariaveisGlobais.und_rmt2 = System.getProperty("undRmt2", "");
        VariaveisGlobais.remoto3 = System.getProperty("remoto3", "");
        VariaveisGlobais.und_rmt3 = System.getProperty("undRmt3", "");
        VariaveisGlobais.remoto4 = System.getProperty("remoto4", "");
        VariaveisGlobais.und_rmt4 = System.getProperty("undRmt4", "");
        VariaveisGlobais.remoto5 = System.getProperty("remoto5", "");
        VariaveisGlobais.und_rmt5 = System.getProperty("undRmt5", "");

        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.unidade, VariaveisGlobais.munidade});
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.remoto1, VariaveisGlobais.und_rmt1});
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.remoto2, VariaveisGlobais.und_rmt2});
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.remoto3, VariaveisGlobais.und_rmt3});
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.remoto4, VariaveisGlobais.und_rmt4});
        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ArraysAdd(VariaveisGlobais.unidades, new String[] {VariaveisGlobais.remoto5, VariaveisGlobais.und_rmt5});
        
        // Printers
        VariaveisGlobais.Thermica = System.getProperty("Thermica", null);
        VariaveisGlobais.Printer = System.getProperty("Printer", null);
        VariaveisGlobais.Preview = System.getProperty("Preview", null);
        VariaveisGlobais.Externo = System.getProperty("Externo", null);
        VariaveisGlobais.Externo2 = System.getProperty("Externo2", null);
        
        // Controle de Impressão
        VariaveisGlobais.PassCaixa  = System.getProperty("PassCaixa", "THERMICA,PS/PDF,INTERNA");
        VariaveisGlobais.Recibo        = System.getProperty("Recibo", "THERMICA,PS/PDF,INTERNA");
        VariaveisGlobais.Caixa         = System.getProperty("Caixa", "THERMICA,TXT,EXTERNA");
        
        VariaveisGlobais.nviasRecibo = Integer.valueOf(System.getProperty("nviasRecibo", "2"));
        
        String BobSize[] = System.getProperty("bobinaSize", "215, 730, 12, 10, 0, 2").split(",");
        VariaveisGlobais.bobinaSize = new float[] {Float.valueOf(BobSize[0]),Float.valueOf(BobSize[1]),Float.valueOf(BobSize[2]),
                                                   Float.valueOf(BobSize[3]),Float.valueOf(BobSize[4]),Float.valueOf(BobSize[5])};
        
        // Atualiza Variaveis
        VariaveisGlobais.dCliente.add("empresa", "Clima - Clinica Medica Assossiada");
        VariaveisGlobais.dCliente.add("endereco", "Rua Dr. Nilo Peçanha");
        VariaveisGlobais.dCliente.add("numero", "1076");
        VariaveisGlobais.dCliente.add("complemento", "");
        VariaveisGlobais.dCliente.add("bairro", "Mutondo");
        VariaveisGlobais.dCliente.add("cidade", "São Gonçalo");
        VariaveisGlobais.dCliente.add("estado", "RJ");
        VariaveisGlobais.dCliente.add("cep", "24450-000");
        VariaveisGlobais.dCliente.add("cnpj", "35.832.542/0001-05");
        VariaveisGlobais.dCliente.add("inscricao", "");
        VariaveisGlobais.dCliente.add("marca", "clima");
        VariaveisGlobais.dCliente.add("telefone", "(21) 2602-5675");
        VariaveisGlobais.dCliente.add("hpage", "http://climaclinicamedica.com.br");
        VariaveisGlobais.dCliente.add("email", "clima@climaclinicamedica.com.br");

        VariaveisGlobais.dCliente.add("recibo", "R E C I B O");
       
        VariaveisGlobais.logo = System.getProperty("Logo", "resources/clima.png");
    }

}
