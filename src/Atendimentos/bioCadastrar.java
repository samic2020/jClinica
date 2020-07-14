/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Atendimentos;

import Db.DbMain;
import static Db.DbMain.AddTemplate;
import Funcoes.VariaveisGlobais;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.JOptionPane;
import veridis.biometric.BiometricException;
import veridis.biometric.BiometricImage;
import veridis.biometric.BiometricSDK;
import veridis.biometric.BiometricScanner;
import veridis.biometric.BiometricTemplate;
import veridis.biometric.CaptureEventListener;
import veridis.biometric.samples.util.LicenseHelper;
import veridis.sample.util.Logger;
import veridis.sample.util.TextComponentLogger;

/**
 *
 * @author Pc
 */
public class bioCadastrar extends javax.swing.JDialog implements CaptureEventListener {
    DbMain conn = VariaveisGlobais.con;
    
    public final int maos_direita   = 60;
    public final int maos_esquerda  = 50;
    public final int dedo_minimo    = 4;
    public final int dedo_anelar    = 3;
    public final int dedo_medio     = 2;
    public final int dedo_indicador = 1;
    public final int dedo_polegar   = 0;
    
    public final Color DESLIGADO    = new Color(153,153,153);
    public final Color LIGADO       = new Color(204,255,204);
    
    private CaptureEventListener listener = this;
    private boolean isCaptureOn = false;
    private static BiometricImage image = null;

    Logger logPanel;
    String arch;

    int dedo = -1;
    String nID = "";
    
    public void put(String _id) { nID = _id; }
    
     /**
     * Creates new form bioVerificar
     */
    public bioCadastrar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        UUID id = UUID.randomUUID();
        String tempFolderName = System.getProperty("java.io.tmpdir")+"/"+id;
        copyToTempFolder(tempFolderName);

        /*
         * You have to set the directory where the .dll (windows) or .so (linux)
         * are found 
         * Don't forget that they must be on the system_path, or on
         * the java library path or in temporary path
         */
        LicenseHelper.setLibrariesDirectory(tempFolderName);

        String key = "0000-0054-7B2B-7A2E-C618"; 
        BiometricSDK.InstallLicense(key);
        
        this.setSize(511, 390);
        setLocationRelativeTo(null);
        
        TextComponentLogger logger = new TextComponentLogger(logView);
        logView.setPreferredSize(new Dimension(100, 150));
        logPanel = new Logger(logger);
        logView.setText("");
        
        ResetDedo();
    }

    private void AcharCadastro() {
        String sql = "SELECT pc_numero, e_polegar, e_indicador, e_medio, e_anelar, e_minimo, " + 
                     "d_polegar, d_indicador, d_medio, d_anelar, d_minimo " + 
                     "FORM pacientes WHERE pc_numero = '" + nID + "' ORDER BY pc_numero;";
        ResultSet cursor = VariaveisGlobais.con.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        
        try {
            while (cursor.next()) {
                String e_polegar = cursor.getString("e_polegar");
                String e_indicador = cursor.getString("e_indicador");
                String e_medio = cursor.getString("e_medio");
                String e_anelar = cursor.getString("e_anelar");
                String e_minimo = cursor.getString("e_minimo");

                String d_polegar = cursor.getString("d_polegar");
                String d_indicador = cursor.getString("d_indicador");
                String d_medio = cursor.getString("d_medio");
                String d_anelar = cursor.getString("d_anelar");
                String d_minimo = cursor.getString("d_minimo");
                
                if (e_polegar != null) SetarDedo(maos_esquerda + dedo_polegar, LIGADO);
                if (e_indicador != null) SetarDedo(maos_esquerda + dedo_indicador, LIGADO);
                if (e_medio != null) SetarDedo(maos_esquerda + dedo_medio, LIGADO);
                if (e_anelar != null) SetarDedo(maos_esquerda + dedo_anelar, LIGADO);
                if (e_minimo != null) SetarDedo(maos_esquerda + dedo_minimo, LIGADO);

                if (d_polegar != null) SetarDedo(maos_direita + dedo_polegar, LIGADO);
                if (d_indicador != null) SetarDedo(maos_direita + dedo_indicador, LIGADO);
                if (d_medio != null) SetarDedo(maos_direita + dedo_medio, LIGADO);
                if (d_anelar != null) SetarDedo(maos_direita + dedo_anelar, LIGADO);
                if (d_minimo != null) SetarDedo(maos_direita + dedo_minimo, LIGADO);
            }
        } catch (Exception e) {                            
            System.out.println(e.getLocalizedMessage());
        }
        try {cursor.close();} catch (Exception e) {}; cursor = null; 
    }
    
    public void SetarDedo(int id, Color cor) {
        if (id == maos_esquerda + dedo_minimo) {
            if (jmesq_minimo.isEnabled()) {jmesq_minimo.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_esquerda + dedo_anelar) {
            if (jmesq_anelar.isEnabled()) {jmesq_anelar.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_esquerda + dedo_medio) {
            if (jmesq_medio.isEnabled()) {jmesq_medio.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_esquerda + dedo_indicador) {
            if (jmesq_indicador.isEnabled()) {jmesq_indicador.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_esquerda + dedo_polegar) {
            if (jmesq_polegar.isEnabled()) {jmesq_polegar.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_direita + dedo_polegar) {
            if (jmdir_polegar.isEnabled()) {jmdir_polegar.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_direita + dedo_indicador) {
            if(jmdir_indicador.isEnabled()) {jmdir_indicador.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_direita + dedo_medio) {
            if (jmdir_medio.isEnabled()) {jmdir_medio.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_direita + dedo_anelar) {
            if (jmdir_anelar.isEnabled()) {jmdir_anelar.setBackground(cor); dedo = -1;}
        } 
        if (id == maos_direita + dedo_minimo) {
            if (jmdir_minimo.isEnabled()) {jmdir_minimo.setBackground(cor); dedo = -1;}
        }        
    }
    
    public void ResetDedo() {
        Color cor = new Color(153, 153, 153);
        jmesq_minimo.setBackground(cor);
        jmesq_anelar.setBackground(cor);
        jmesq_medio.setBackground(cor);
        jmesq_indicador.setBackground(cor);
        jmesq_polegar.setBackground(cor);
        jmdir_polegar.setBackground(cor);
        jmdir_indicador.setBackground(cor);
        jmdir_medio.setBackground(cor);
        jmdir_anelar.setBackground(cor);
        jmdir_minimo.setBackground(cor);
    }

    public void EnableDisableDedos(Object[][] dedos) {
        for (int i=0;i<dedos.length;i++) {
            if (Integer.valueOf(dedos[i][0].toString()) == maos_esquerda + dedo_minimo) {
                jmesq_minimo.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_esquerda + dedo_anelar) {
                jmesq_anelar.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_esquerda + dedo_medio) {
                jmesq_medio.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_esquerda + dedo_indicador) {
                jmesq_indicador.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_esquerda + dedo_polegar) {
                jmesq_polegar.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_direita + dedo_polegar) {
                jmdir_polegar.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_direita + dedo_indicador) {
                jmdir_indicador.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_direita + dedo_medio) {
                jmdir_medio.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_direita + dedo_anelar) {
                jmdir_anelar.setEnabled((Boolean)dedos[i][1]);
            } 
            if (Integer.valueOf(dedos[i][0].toString()) == maos_direita + dedo_minimo) {
                jmdir_minimo.setEnabled((Boolean)dedos[i][1]);
            }        
        }
    }
    
    private void EnableDisableReader(Boolean endis) {
        if (endis.equals(true)) {
            imagePanel.setBackground(LIGADO);
        } else {
            imagePanel.setBackground(DESLIGADO);
        }
        btnSalvar.setEnabled(endis);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new veridis.biometric.JBiometricPanel();
        jmdir_minimo = new javax.swing.JLabel();
        jmdir_anelar = new javax.swing.JLabel();
        jmdir_medio = new javax.swing.JLabel();
        jmdir_indicador = new javax.swing.JLabel();
        jmdir_polegar = new javax.swing.JLabel();
        jmdir = new javax.swing.JLabel();
        jmesq_minimo = new javax.swing.JLabel();
        jmesq_anelar = new javax.swing.JLabel();
        jmesq_medio = new javax.swing.JLabel();
        jmesq_indicador = new javax.swing.JLabel();
        jmesq_polegar = new javax.swing.JLabel();
        jmesq = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logView = new javax.swing.JTextArea();
        btnSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Identificação Biométrica");
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(null);

        imagePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(imagePanel);
        imagePanel.setBounds(180, 20, 160, 280);

        jmdir_minimo.setBackground(new java.awt.Color(153, 153, 153));
        jmdir_minimo.setOpaque(true);
        jmdir_minimo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmdir_minimoMouseReleased(evt);
            }
        });
        getContentPane().add(jmdir_minimo);
        jmdir_minimo.setBounds(462, 30, 18, 100);

        jmdir_anelar.setBackground(new java.awt.Color(153, 153, 153));
        jmdir_anelar.setOpaque(true);
        jmdir_anelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmdir_anelarMouseReleased(evt);
            }
        });
        getContentPane().add(jmdir_anelar);
        jmdir_anelar.setBounds(437, 30, 18, 60);

        jmdir_medio.setBackground(new java.awt.Color(153, 153, 153));
        jmdir_medio.setOpaque(true);
        jmdir_medio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmdir_medioMouseReleased(evt);
            }
        });
        getContentPane().add(jmdir_medio);
        jmdir_medio.setBounds(410, 30, 18, 50);

        jmdir_indicador.setBackground(new java.awt.Color(153, 153, 153));
        jmdir_indicador.setOpaque(true);
        jmdir_indicador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmdir_indicadorMouseReleased(evt);
            }
        });
        getContentPane().add(jmdir_indicador);
        jmdir_indicador.setBounds(382, 30, 18, 50);

        jmdir_polegar.setBackground(new java.awt.Color(153, 153, 153));
        jmdir_polegar.setOpaque(true);
        jmdir_polegar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmdir_polegarMouseReleased(evt);
            }
        });
        getContentPane().add(jmdir_polegar);
        jmdir_polegar.setBounds(357, 30, 18, 115);

        jmdir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/mao_dir.png"))); // NOI18N
        getContentPane().add(jmdir);
        jmdir.setBounds(347, 11, 147, 292);

        jmesq_minimo.setBackground(new java.awt.Color(153, 153, 153));
        jmesq_minimo.setOpaque(true);
        jmesq_minimo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmesq_minimoMouseReleased(evt);
            }
        });
        getContentPane().add(jmesq_minimo);
        jmesq_minimo.setBounds(35, 30, 18, 100);

        jmesq_anelar.setBackground(new java.awt.Color(153, 153, 153));
        jmesq_anelar.setOpaque(true);
        jmesq_anelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmesq_anelarMouseReleased(evt);
            }
        });
        getContentPane().add(jmesq_anelar);
        jmesq_anelar.setBounds(60, 30, 18, 60);

        jmesq_medio.setBackground(new java.awt.Color(153, 153, 153));
        jmesq_medio.setOpaque(true);
        jmesq_medio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmesq_medioMouseReleased(evt);
            }
        });
        getContentPane().add(jmesq_medio);
        jmesq_medio.setBounds(85, 30, 18, 50);

        jmesq_indicador.setBackground(new java.awt.Color(153, 153, 153));
        jmesq_indicador.setOpaque(true);
        jmesq_indicador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmesq_indicadorMouseReleased(evt);
            }
        });
        getContentPane().add(jmesq_indicador);
        jmesq_indicador.setBounds(112, 30, 18, 50);

        jmesq_polegar.setBackground(new java.awt.Color(153, 153, 153));
        jmesq_polegar.setOpaque(true);
        jmesq_polegar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jmesq_polegarMouseReleased(evt);
            }
        });
        getContentPane().add(jmesq_polegar);
        jmesq_polegar.setBounds(140, 30, 18, 115);

        jmesq.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/mao_esq.png"))); // NOI18N
        getContentPane().add(jmesq);
        jmesq.setBounds(20, 11, 147, 292);

        logView.setColumns(20);
        logView.setRows(5);
        jScrollPane1.setViewportView(logView);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 310, 370, 50);

        btnSalvar.setMnemonic('S');
        btnSalvar.setText("Salvar");
        btnSalvar.setEnabled(false);
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });
        getContentPane().add(btnSalvar);
        btnSalvar.setBounds(400, 310, 100, 50);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmesq_minimoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmesq_minimoMouseReleased
        if (jmesq_minimo.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_esquerda + dedo_minimo, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_esquerda + dedo_minimo;
            startCapture();
        } else {
            SetarDedo(maos_esquerda + dedo_minimo, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmesq_minimoMouseReleased

    private void jmesq_anelarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmesq_anelarMouseReleased
        if (jmesq_anelar.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_esquerda + dedo_anelar, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_esquerda + dedo_anelar;
            startCapture();
        } else {
            SetarDedo(maos_esquerda + dedo_anelar, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmesq_anelarMouseReleased

    private void jmesq_medioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmesq_medioMouseReleased
        if (jmesq_medio.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_esquerda + dedo_medio, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_esquerda + dedo_medio;
            startCapture();
        } else {
            SetarDedo(maos_esquerda + dedo_medio, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmesq_medioMouseReleased

    private void jmesq_indicadorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmesq_indicadorMouseReleased
        if (jmesq_indicador.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_esquerda + dedo_indicador, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_esquerda + dedo_indicador;
            startCapture();
        } else {
            SetarDedo(maos_esquerda + dedo_indicador, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmesq_indicadorMouseReleased

    private void jmesq_polegarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmesq_polegarMouseReleased
        if (jmesq_polegar.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_esquerda + dedo_polegar, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_esquerda + dedo_polegar;
            startCapture();
        } else {
            SetarDedo(maos_esquerda + dedo_polegar, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmesq_polegarMouseReleased

    private void jmdir_polegarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmdir_polegarMouseReleased
        if (jmdir_polegar.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_direita + dedo_polegar, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_direita + dedo_polegar;
            startCapture();
        } else {
            SetarDedo(maos_direita + dedo_polegar, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmdir_polegarMouseReleased

    private void jmdir_indicadorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmdir_indicadorMouseReleased
        if (jmdir_indicador.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_direita + dedo_indicador, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_direita + dedo_indicador;
            startCapture();
        } else {
            SetarDedo(maos_direita + dedo_indicador, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmdir_indicadorMouseReleased

    private void jmdir_medioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmdir_medioMouseReleased
        if (jmdir_medio.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_direita + dedo_medio, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_direita + dedo_medio;
            startCapture();
        } else {
            SetarDedo(maos_direita + dedo_medio, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmdir_medioMouseReleased

    private void jmdir_anelarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmdir_anelarMouseReleased
        if (jmdir_anelar.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_direita + dedo_anelar, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, false},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_direita + dedo_anelar;
            startCapture();
        } else {
            SetarDedo(maos_direita + dedo_anelar, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmdir_anelarMouseReleased

    private void jmdir_minimoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmdir_minimoMouseReleased
        if (jmdir_minimo.getBackground().equals(DESLIGADO)) {
            SetarDedo(maos_direita + dedo_minimo, LIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, false},
                                               {maos_esquerda + dedo_anelar, false},
                                               {maos_esquerda + dedo_medio, false},
                                               {maos_esquerda + dedo_indicador, false},
                                               {maos_esquerda + dedo_polegar, false},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, false},
                                               {maos_direita + dedo_medio, false},
                                               {maos_direita + dedo_indicador, false},
                                               {maos_direita + dedo_polegar, false}});
            EnableDisableReader(true);           
            dedo = maos_direita + dedo_minimo;
            startCapture();
        } else {
            SetarDedo(maos_direita + dedo_minimo, DESLIGADO);
            EnableDisableDedos(new Object[][] {{maos_esquerda + dedo_minimo, true},
                                               {maos_esquerda + dedo_anelar, true},
                                               {maos_esquerda + dedo_medio, true},
                                               {maos_esquerda + dedo_indicador, true},
                                               {maos_esquerda + dedo_polegar, true},
                                               {maos_direita + dedo_minimo, true},
                                               {maos_direita + dedo_anelar, true},
                                               {maos_direita + dedo_medio, true},
                                               {maos_direita + dedo_indicador, true},
                                               {maos_direita + dedo_polegar, true}});
            EnableDisableReader(false);
            stopCapture();
        }
    }//GEN-LAST:event_jmdir_minimoMouseReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (isCaptureOn) BiometricSDK.StopSDK(listener);
    }//GEN-LAST:event_formWindowClosing

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if(image != null){
                try{
                        try{
                                new BiometricTemplate(image);
                        }catch(Exception e){
                                System.out.println(e.getMessage());
                        }
                        if (AddTemplate(new BiometricTemplate(image),nID,dedo)) {
//                                JOptionPane.showMessageDialog(null, "Usuario " + nID
//                                                + " cadastrado");
                        } else {
//                                JOptionPane.showMessageDialog(null, "Usuario " + nID
//                                                + " não pode ser cadastrado");
                        }
                } catch (Exception e) {
//                        JOptionPane.showMessageDialog(null, "Usuario " + nID
//                                        + " não pode ser cadastrado");
                }
        }
        else JOptionPane.showMessageDialog(null, "Nenhuma imagem está disponível.");
        stopCapture(); btnSalvar.setEnabled(false); dedo = -1;
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        AcharCadastro();
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(bioCadastrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(bioCadastrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(bioCadastrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(bioCadastrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                bioCadastrar dialog = new bioCadastrar(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvar;
    private veridis.biometric.JBiometricPanel imagePanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jmdir;
    private javax.swing.JLabel jmdir_anelar;
    private javax.swing.JLabel jmdir_indicador;
    private javax.swing.JLabel jmdir_medio;
    private javax.swing.JLabel jmdir_minimo;
    private javax.swing.JLabel jmdir_polegar;
    private javax.swing.JLabel jmesq;
    private javax.swing.JLabel jmesq_anelar;
    private javax.swing.JLabel jmesq_indicador;
    private javax.swing.JLabel jmesq_medio;
    private javax.swing.JLabel jmesq_minimo;
    private javax.swing.JLabel jmesq_polegar;
    private javax.swing.JTextArea logView;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onCaptureEvent(CaptureEventType eventType, BiometricScanner reader, BiometricImage image) {
        if (image != null)
                logPanel.log(reader + ": " + eventType + "  => " + image);
        else
                logPanel.log(reader + ": " + eventType);

        switch (eventType) {
        // Device plugged. I want to receive images from it.
        case PLUG: {
                try {
                        reader.addCaptureEventListener(this);

                } catch (BiometricException e) {
                        logPanel.log("Cannot start device " + reader + ": " + e);
                }
                break;
        }
        // Device unplugged.
        // It might be nice to display it on your UI, but no action is required.
        case UNPLUG:
                break;

        // The biometric feature has been placed on the scanner.
        // It might be nice to display it on your UI, but no action is required.
        case PLACED:
                break;
        // The biometric feature has been removed from the scanner.
        // It might be nice to display it on your UI, but no action is required.
        case REMOVED:
                break;

        // A image frame has been received.
        // It might be nice to display it on your UI, but no action is required.
        case IMAGE_FRAME:
                imagePanel.setImage(image);
                // pack(); //Refresh layout
                break;
        // A 'final' image has been captured. THIS is the image that we must
        // handle!
        case IMAGE_CAPTURED: {

                imagePanel.setImage(image);

                this.image = image;

                break;
        }

        }        
    }

    public void copyToTempFolder(String tempFolderName){
        /*finds out if you are running on 32 or 64bits*/
        if( System.getProperty("sun.arch.data.model").equals("64")){
                arch = "x64";
        }
        else arch = "x86";

        ArrayList <String> dllNames = new ArrayList<String> ();

        /*dlls to be loaded*/
        dllNames.add("pthreadVC2.dll");
        dllNames.add("VrBio.dll");
        dllNames.add("VrModuleFutronic.dll");
        dllNames.add("VrModuleDigitalPersona.dll");
        dllNames.add("VrModuleNitgen.dll");
        dllNames.add("VrModuleSuprema.dll");

        dllNames.add("libusb0.dll");
        dllNames.add("ftrScanAPI.dll");
        dllNames.add("NBioBSP.dll");
        dllNames.add("UFScanner.dll");
        dllNames.add("UFLicense.dat");

        String dllName;

        for(int i = 0; i < dllNames.size(); i++){
            dllName = dllNames.get(i);
            /*makes new folder*/
            new File(tempFolderName).mkdirs();
            File tmpDir = new File(tempFolderName);
            File tmpFile = new File(tmpDir, dllName);

            if(!new File(tmpDir+"/"+dllName).isFile()){
                try {
                    InputStream in = getClass().getResourceAsStream("/dlls/"+arch+"/"+dllName);
                    OutputStream out = new FileOutputStream(tmpFile);

                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                } catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                    // deal with exception
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }    
    
    public void startCapture() {
            if (!isCaptureOn) BiometricSDK.StartSDK(this);
            isCaptureOn = true;
    }

    public void stopCapture() {

            if (isCaptureOn) {
                    BiometricSDK.StopSDK(this);
                    System.out.println("parou captura");
            }
            isCaptureOn = false;
    }

    public static BiometricImage getImage() {
            return image;
    }
}
