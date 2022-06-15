/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jclinica;

import Funcoes.VariaveisGlobais;
import Login.jLogin;
import de.hillenbrand.swing.plaf.threeD.ThreeDLookAndFeel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

        try {
            ThreeDLookAndFeel lf = new ThreeDLookAndFeel();
            UIManager.setLookAndFeel((LookAndFeel)lf);
            //UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel");
            //UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
        } catch (UnsupportedLookAndFeelException ux) {
            Logger.getLogger(JClinica.class.getName()).log(Level.SEVERE, null, ux);
        }
        
        //UIManager.setLookAndFeel("de.muntjak.tinylookandfell");
        
        VariaveisGlobais.LerSettings();

        t.cancel();
        (new jLogin(null, true)).main(new String[] {""});        
    }
    
    private static class RemindTask extends TimerTask {
        public void run() {
            System.exit(0);
        }
    }    
}
