/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.io.IOException;

/**
 *
 * @author mariana
 */
public class ComandoExterno {

        public static void ComandoExterno(String cmd) {
            Process p;
             try {
                 //executar rotina de backup
                 p = Runtime.getRuntime().exec(cmd);
                 p.waitFor(); // espera pelo processo terminar
              } catch (InterruptedException ex) {
                 ex.printStackTrace();
              } catch (IOException ex) {
                 ex.printStackTrace();
              }
        }
}
