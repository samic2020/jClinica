/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 *
 * @author supervisor
 */
public class Settings {
    static private String mFile = System.getProperty("user.dir") + "/jClinica.conf";
    private Properties p;
    FileInputStream propFile;
    
    public Settings() {
        try {
            //System.out.println(System.getProperty("user.dir"));
            propFile = new FileInputStream(mFile);
            p = new Properties(System.getProperties());
            p.load(propFile);

            System.setProperties(p);
        } catch (Exception ex) {ex.printStackTrace();}
        
    }
    
    public void Set(String propriedade, String Valor) {
        try {
            propFile = new FileInputStream(mFile);
            p = new Properties(System.getProperties());
            p.load(propFile);
            p.setProperty(propriedade, Valor);
            p.store(new FileOutputStream(mFile),null);
        } catch (Exception err) {}
    }
    
    public void Del(String propriedade) {
        try {
            Properties prop = System.getProperties();
            propFile = new FileInputStream(mFile);
            p = new Properties(prop);
            p.load(propFile);
            p.remove(propriedade);
            p.store(new FileOutputStream(mFile),null);
        } catch (Exception err) {}
        
    }
}
