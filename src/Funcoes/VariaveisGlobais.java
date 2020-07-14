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
    
    public static String sWhere = "";
    
}
