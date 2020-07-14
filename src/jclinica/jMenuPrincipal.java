/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jMenuPrincipal.java
 *
 * Created on 02/02/2012, 12:47:36
 */
package jclinica;

//import Atendimentos.jRecepcao;
//import Atendimentos.jatdMedicos;
import Atendimentos.JAgendar;
import Atendimentos.jRecepcao;
import Atendimentos.jatdMedicos;
import Cadastos.jMedico;
import Cadastos.jUsuarios;
import Db.DbMain;
import Faturamentos.AdmGrupos;
import Gerencia.jPasCaixa;
import Faturamentos.jFarmacia;
import Funcoes.BackGroundDeskTopPane;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import Gerencia.jCaixa;
import Gerencia.jDespesas;
import Login.jLogin;
import Relatorios.iRelEstatisticas;
import Relatorios.nAtendimentos;
import Relatorios.nRelDesp;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import org.easylookandfeel.adapter.MenuLookAndFeelAdapter;
import org.easylookandfeel.core.EasyLookAndFeel;

/**
 *
 * @author supervisor
 */
public class jMenuPrincipal extends javax.swing.JFrame {
    DbMain conn = VariaveisGlobais.con;
    
    /** Creates new form jMenuPrincipal */
    public jMenuPrincipal() {
        initComponents();
        
        EasyLookAndFeel.start(this, new MenuLookAndFeelAdapter(mnuSkin));
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
//        try {
//            Date cxdata = Dates.StringtoDate(conn.LerCamposTabela(new String[] {"cx_data"}, "caixa", "f_cod = " + VariaveisGlobais.cdlogado)[0][3],"yyyy-MM-dd");
//            System.out.println(cxdata + "\n" + new Date());
//            Date hoje = new Date();
//            if (!Dates.DateFormat("dd-MM-yyyy", cxdata).equals(Dates.DateFormat("dd-MM-yyyy", new Date()))) {
//                JOptionPane.showMessageDialog(null, "Caixa anterior não foi fechado!","Atenção",INFORMATION_MESSAGE);
//                fCaixa();
//            }
//        } catch (Exception e) {}            
        
        new Settings();
        VariaveisGlobais.Thermica = System.getProperty("Thermica", null);
        VariaveisGlobais.Printer = System.getProperty("Printer", null);
        VariaveisGlobais.Preview = System.getProperty("Preview", null);
        VariaveisGlobais.Externo = System.getProperty("Externo", null);
        VariaveisGlobais.Externo2 = System.getProperty("Externo2", null);
        

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new BackGroundDeskTopPane("/Figuras/clinica_cs.jpg");
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jmnuUsuarios = new javax.swing.JMenuItem();
        jmnuMedicos = new javax.swing.JMenuItem();
        jmnuGrupos = new javax.swing.JMenuItem();
        jmnuAtdMed = new javax.swing.JMenu();
        jmnuRecepcao = new javax.swing.JMenuItem();
        jmnuAgenda = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jmnuFarmacia = new javax.swing.JMenuItem();
        jmnuRelFatura = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jmnuEstatisticas = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jmnuLogOff = new javax.swing.JMenuItem();
        mnuFim = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuSkin = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: jClinica - Sistema de Gestão de Clinicas...");

        jDesktopPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jMenuBar1.setBackground(new java.awt.Color(202, 193, 207));

        jMenu1.setForeground(new java.awt.Color(1, 1, 1));
        jMenu1.setText("Cadastros");

        jmnuUsuarios.setText("Usuários");
        jmnuUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuUsuariosActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuUsuarios);

        jmnuMedicos.setText("Médicos");
        jmnuMedicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuMedicosActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuMedicos);

        jmnuGrupos.setText("Adm Grupos");
        jmnuGrupos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuGruposActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuGrupos);

        jMenuBar1.add(jMenu1);

        jmnuAtdMed.setForeground(new java.awt.Color(1, 1, 1));
        jmnuAtdMed.setText("Atendimentos");

        jmnuRecepcao.setText("Recepção");
        jmnuRecepcao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuRecepcaoActionPerformed(evt);
            }
        });
        jmnuAtdMed.add(jmnuRecepcao);

        jmnuAgenda.setText("Agenda");
        jmnuAgenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuAgendaActionPerformed(evt);
            }
        });
        jmnuAtdMed.add(jmnuAgenda);

        jMenuItem6.setText("Médico");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jmnuAtdMed.add(jMenuItem6);

        jmnuFarmacia.setText("Farmácia");
        jmnuFarmacia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuFarmaciaActionPerformed(evt);
            }
        });
        jmnuAtdMed.add(jmnuFarmacia);

        jMenuBar1.add(jmnuAtdMed);

        jmnuRelFatura.setForeground(new java.awt.Color(1, 1, 1));
        jmnuRelFatura.setText("Relatórios");

        jMenuItem3.setText("Atendimentos");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jmnuRelFatura.add(jMenuItem3);

        jmnuEstatisticas.setText("Estatisticas");
        jmnuEstatisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuEstatisticasActionPerformed(evt);
            }
        });
        jmnuRelFatura.add(jmnuEstatisticas);

        jMenuBar1.add(jmnuRelFatura);

        jMenu4.setForeground(new java.awt.Color(1, 1, 1));
        jMenu4.setText("Sair");

        jmnuLogOff.setText("LogOff");
        jmnuLogOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuLogOffActionPerformed(evt);
            }
        });
        jMenu4.add(jmnuLogOff);

        mnuFim.setText("Fechar Sistema");
        mnuFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFimActionPerformed(evt);
            }
        });
        jMenu4.add(mnuFim);
        jMenu4.add(jSeparator1);

        mnuSkin.setText("Aparência");
        jMenu4.add(mnuSkin);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmnuLogOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuLogOffActionPerformed
        this.dispose();
        try {
            (new jLogin(null, true)).main(new String[]{""});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jmnuLogOffActionPerformed

    private void mnuFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFimActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuFimActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        nAtendimentos oTela = null;
        try {
            oTela = new nAtendimentos();
            jDesktopPane1.add(oTela);

            jDesktopPane1.getDesktopManager().activateFrame(oTela);
            CentralizaTela.setCentro(oTela, jDesktopPane1, 0, 0);
            oTela.requestFocus();
            oTela.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jmnuUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuUsuariosActionPerformed
        try {
            jUsuarios oTela2 = null;
            oTela2 = new jUsuarios();
            jDesktopPane1.add(oTela2);

            jDesktopPane1.getDesktopManager().activateFrame(oTela2);
            CentralizaTela.setCentro(oTela2, jDesktopPane1, 0, 0);
            oTela2.setVisible(true);
            oTela2.requestFocus();
            oTela2.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuUsuariosActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            jatdMedicos oTela1 = null;
            oTela1 = new jatdMedicos();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jmnuRecepcaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuRecepcaoActionPerformed
        try {
            jRecepcao oRecepção = new jRecepcao();
            jDesktopPane1.add(oRecepção);
            jDesktopPane1.getDesktopManager().activateFrame(oRecepção);
            CentralizaTela.setCentro(oRecepção, jDesktopPane1, 0, 0);
            oRecepção.requestFocus();
            oRecepção.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuRecepcaoActionPerformed

    private void jmnuFarmaciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuFarmaciaActionPerformed
        try {
            jFarmacia oFarmacia = new jFarmacia();
            jDesktopPane1.add(oFarmacia);
            jDesktopPane1.getDesktopManager().activateFrame(oFarmacia);
            CentralizaTela.setCentro(oFarmacia, jDesktopPane1, 0, 0);
            oFarmacia.requestFocus();
            oFarmacia.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuFarmaciaActionPerformed

    private void jmnuEstatisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuEstatisticasActionPerformed
        try {
            iRelEstatisticas oEstatisticas = new iRelEstatisticas();
            jDesktopPane1.add(oEstatisticas);
            jDesktopPane1.getDesktopManager().activateFrame(oEstatisticas);
            CentralizaTela.setCentro(oEstatisticas, jDesktopPane1, 0, 0);
            oEstatisticas.requestFocus();
            oEstatisticas.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuEstatisticasActionPerformed

    private void jmnuMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuMedicosActionPerformed
        try {
            jMedico oTela1 = new jMedico();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuMedicosActionPerformed

    private void jmnuAgendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuAgendaActionPerformed
        try {
            JAgendar oTela1 = new JAgendar();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuAgendaActionPerformed

    private void jmnuGruposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuGruposActionPerformed
        try {
            AdmGrupos admTela = new AdmGrupos();
            jDesktopPane1.add(admTela);

            jDesktopPane1.getDesktopManager().activateFrame(admTela);
            CentralizaTela.setCentro(admTela, jDesktopPane1, 0, 0);
            admTela.requestFocus();
            admTela.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuGruposActionPerformed

    public void fCaixa() {
        try {
            jCaixa oTela1 = new jCaixa();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jMenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new jMenuPrincipal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem jmnuAgenda;
    private javax.swing.JMenu jmnuAtdMed;
    private javax.swing.JMenuItem jmnuEstatisticas;
    private javax.swing.JMenuItem jmnuFarmacia;
    private javax.swing.JMenuItem jmnuGrupos;
    private javax.swing.JMenuItem jmnuLogOff;
    private javax.swing.JMenuItem jmnuMedicos;
    private javax.swing.JMenuItem jmnuRecepcao;
    private javax.swing.JMenu jmnuRelFatura;
    private javax.swing.JMenuItem jmnuUsuarios;
    private javax.swing.JMenuItem mnuFim;
    private javax.swing.JMenu mnuSkin;
    // End of variables declaration//GEN-END:variables
}
