/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jclinica;

import Funcoes.FuncoesGlobais;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import Login.jLogin;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author supervisor
 */
public class JClinica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Timer t = new Timer();
        t.schedule(new RemindTask(), 60 * 1000);

        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        //UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel");
        //UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
        
        //UIManager.setLookAndFeel("de.muntjak.tinylookandfell");
        
        LerSettings();

        t.cancel();
        (new jLogin(null, true)).main(new String[] {""});        
    }
    
    private static class RemindTask extends TimerTask {
        public void run() {
            System.exit(0);
        }
    }

    private static void LerSettings() {
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

    }
    
}
