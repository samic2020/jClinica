/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atendimentos;

import static Atendimentos.jatdMedicos.os;
import Db.DbMain;
import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.newTable;
import com.sun.mail.dsn.Report;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;
import static java.lang.Thread.sleep;

/**
 *
 * @author supervisor
 */
public class jatdMedicos extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.con;
    String[][] iform = new String[0][];
    int tam = 0;
    int n = 0;
    public static Socket clientSocket = null;
    public static PrintStream os = null;
    public static DataInputStream is = null;

    public jatdMedicos() {
        initComponents();

        //new Thread() {public void run() {new MultiThreadChatClient().main(new String[] { VariaveisGlobais.logado, "med", VariaveisGlobais.unidade, "" });}}.start();
        jbtRecSalvar.setVisible(false);

        limpartela();

        String[] aheader = { "Cod Paciente", "Paciente", "C", "I", "P", "M", "iC", "iI", "iP", "iM", "T", "Entrada", "Saida", "Tempo" };
        int[] awidths = { 0, 380, 0, 0, 0, 0, 10, 10, 10, 10, 20, 80, 80, 60 };
        String[] aligns = { "L", "L", "L", "L", "L", "L", "L", "L", "L", "C", "C", "C", "C", "L" };
        newTable.InitTable(clvEspera, aheader, awidths, aligns, true);
        new Thread() {public void run() { initPac(); }}.start();
        addPopupMenu(jtxtSintomasDia);
        jtxtSintomasDia.setLineWrap(true);
    }

    private void addPopupMenu(JTextArea ta) {
        JMenuItem item1 = new JMenuItem("Copiar");
        item1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jtxtSintomasDia.copy();
          }
        });
        JMenuItem item2 = new JMenuItem("Colar");
        item2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jtxtSintomasDia.paste();
          }
        });
        JMenuItem item3 = new JMenuItem("Recortar");
        item3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jtxtSintomasDia.cut();
          }
        });
        JMenuItem item4 = new JMenuItem("Selecionar Tudo");
        item4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            jtxtSintomasDia.selectAll();
          }
        });
        JPopupMenu menu = new JPopupMenu();
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(new JSeparator());
        menu.add(item4);

        ta.add(menu);
        ta.addMouseListener(new jatdMedicos.PopupTriggerMouseListener(menu, this));
    }

    public static class PopupTriggerMouseListener extends MouseAdapter {
        private JPopupMenu popup;
        private JComponent component;

        public PopupTriggerMouseListener(JPopupMenu popup, JComponent component) {
            popup = popup;
            component = component;
        }

        private void showMenuIfPopupTrigger(MouseEvent e) {
            if (e.isPopupTrigger()) {
                MouseEvent me = e;
                Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), component);
                popup.show(component, pt.x, pt.y);
            }
        }

        public void mousePressed(MouseEvent e) {
            showMenuIfPopupTrigger(e);
        }

        public void mouseReleased(MouseEvent e) {
            showMenuIfPopupTrigger(e);
        }
    }
    
    private void initPac() {
        boolean bespera = false;
        String tsql = "select m.*, p.* from marcar as m, pacientes as p where lower(m.ma_origem) = '" + VariaveisGlobais.origem.toLowerCase().trim() + "' AND m.ma_pcnumero = p.pc_numero " + "and to_char(m.ma_data, 'dd-mm-yyyy') = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and m.ma_codmedico = " + VariaveisGlobais.cdmedico + " order by ma_hora;";

        ResultSet tbpac = conn.AbrirTabela(tsql, 1007);
        try {
          tam = DbMain.RecordCount(tbpac);
          n = 1;
          jbarra.setValue(0);
          jbarra.setVisible(true);
          while (tbpac.next()) {
            new Thread() { public void run() {
                int pos = n * 100 / tam;
                try {sleep(100L);} catch (Exception ex) {}
                jbarra.setValue(pos);
                jbarra.repaint();
              }
            }.start();
            String tstatus = tbpac.getString("ma_status");
            String thora = tbpac.getString("ma_hora");
            String tpcnumero = tbpac.getString("ma_pcnumero");
            String tisnc = tbpac.getString("ma_inscricao");
            String tnome = tbpac.getString("ma_nome").toUpperCase();
            String tcvnum = tbpac.getString("ma_convnumero");
            String tconvenio = tbpac.getString("ma_plano");
            String tcategoria = tbpac.getString("ma_categoria");
            String tcdmedico = tbpac.getString("ma_codmedico");
            String tmedico = tbpac.getString("ma_medico").toUpperCase();
            String ttipoatd;
            if (tbpac.getBoolean("ma_consulta")) {
              ttipoatd = "C";
            } else {
              if (tbpac.getBoolean("ma_revisao")) {
                ttipoatd = "R";
              } else {
                ttipoatd = "T";
              }
            }
            String tstatatd = null;ImageIcon istatatd = null;
            if (tbpac.getString("ma_status").equalsIgnoreCase("0")) {
              tstatatd = "W";
              istatatd = new ImageIcon(getClass().getResource("/Figuras/aguardando.png"));
            } else if (tbpac.getString("ma_status").equalsIgnoreCase("1")) {
              tstatatd = "I";
            } else if (tbpac.getString("ma_status").equalsIgnoreCase("2")) {
              tstatatd = "A";
              istatatd = new ImageIcon(getClass().getResource("/Figuras/atendendo.png"));
            } else if (tbpac.getString("ma_status").equalsIgnoreCase("3")) {
              tstatatd = "P";
              istatatd = new ImageIcon(getClass().getResource("/Figuras/pendente.png"));
            } else if (tbpac.getString("ma_status").equalsIgnoreCase("4")) {
              tstatatd = "E";
              istatatd = new ImageIcon(getClass().getResource("/Figuras/encerrado.png"));
            }
            String tstatimg = null;
            String esql = "select st_exame as exame from exames where dt_exame = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_exame desc;";

            ResultSet ttable = conn.AbrirTabela(esql, 1007);
            try {
              if (ttable.next()) {
                if (ttable.getInt("exame") == 1) {
                  bespera = false;
                } else {
                  bespera = true;
                }
                ttable.last();
                tstatimg = ttable.getString("exame");
              } else {
                tstatimg = "";
              }
            } catch (Exception err) {}
            DbMain.FecharTabela(ttable);

            String tstatproc = null;
            String psql = "select st_trata as tratam from trata where dt_trata = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_trata desc";

            ttable = conn.AbrirTabela(psql, 1007);
            try {
              if (ttable.next()) {
                if (ttable.getInt("tratam") == 1) {
                  bespera = false;
                } else {
                  bespera = true;
                }
                ttable.last();
                tstatproc = ttable.getString("tratam");
              } else {
                tstatproc = "";
              }
            } catch (Exception err) {}
            DbMain.FecharTabela(ttable);

            String tstatmed = null;
            String msql = "select st_medica as medic from medica where dt_medica = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_medica desc;";

            ttable = conn.AbrirTabela(msql, 1007);
            try {
              if (ttable.next()) {
                if (ttable.getInt("medic") == 1) {
                  bespera = false;
                } else {
                  bespera = true;
                }
                ttable.last();
                if (!bespera) {
                  tstatmed = "1";
                } else {
                  tstatmed = "0";
                }
              } else {
                tstatmed = "";
              }
            } catch (Exception err) {}
            DbMain.FecharTabela(ttable);

            String thorasai = tbpac.getString("ma_horasai");

            Time ehora = tbpac.getTime("ma_hora");
            Time shora = tbpac.getTime("ma_horasai");
            String fhora = "";
            if (shora != null) {
              long zhora = shora.getTime() - ehora.getTime();
              long r = zhora / 1000L / 60L;
              fhora = String.valueOf(r);
            } else {
              fhora = "";
            }
            Object[] linha = { tpcnumero, tnome, tstatatd, tstatimg, tstatproc, tstatmed, istatatd, null, null, null, ttipoatd, thora, thorasai, fhora };

            newTable.add(clvEspera, linha);

            n += 1;
          }
        } catch (Exception err) {
          err.printStackTrace();
        }
        DbMain.FecharTabela(tbpac);

        int[] tep = contapac();
        mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
        mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
        jbarra.setVisible(false);

        String pp = proximopac();
        if (!pp.equalsIgnoreCase("")) {
          LerDadosPac(pp);
        } else {
          limpartela();
        }
        jtbbMaster.setSelectedIndex(1);

        jtbbLancamentos.setEnabled(false);

        jtxtSintomasDia.setEnabled(false);

        jbtRecSalvar.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecPrint.setEnabled(false);
        jtxtReceita.setEnabled(false);

        cdDoenca.setEnabled(false);
        nmDoenca.setEnabled(false);
        btpesqDoenca.setEnabled(false);

        btAtender.setEnabled(true);
        btEncerrar.setEnabled(false);
        brPender.setEnabled(false);
        btCancelar.setEnabled(false);
        btProximo.setEnabled(true);
        btAnterior.setEnabled(true);
        btSair.setEnabled(true);
    }

    private void limpartela() {
        mNome.setText(" ");mNome.setSize(377, 20);
        mInsc.setText(" ");mInsc.setSize(173, 20);
        mConv.setText(" ");mConv.setSize(157, 20);
        mNasc.setText(" ");mNasc.setSize(80, 20);
        mIdade.setText(" ");mIdade.setSize(40, 20);
        mBairro.setText(" ");mBairro.setSize(160, 20);
        mCidade.setText(" ");mCidade.setSize(178, 20);
        mUf.setText(" ");mUf.setSize(34, 20);
        mCep.setText(" ");mCep.setSize(90, 20);
        mCivil.setText(" ");mCivil.setSize(90, 20);
        mNaciona.setText(" ");mNaciona.setSize(175, 20);
        mNatural.setText(" ");mNatural.setSize(175, 20);
        mProfis.setText(" ");mProfis.setSize(216, 20);
        mRenda.setText(" ");mRenda.setSize(83, 20);
        mPessoas.setText(" ");mPessoas.setSize(46, 20);
        mMae.setText(" ");mMae.setSize(356, 20);
        mPai.setText(" ");mPai.setSize(356, 20);
        mEsperas.setText("000000");
        mPendente.setText("000000");
        jtxtSintomasDia.setText("");
        jtxtReceita.setText("");
    }
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtbbLancamentos = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtxtSintomasDia = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        cdDoenca = new javax.swing.JTextField();
        btpesqDoenca = new javax.swing.JButton();
        nmDoenca = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jbtRecApagar = new javax.swing.JButton();
        jbtRecSalvar = new javax.swing.JButton();
        jbtRecPrint = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtxtReceita = new javax.swing.JTextArea();
        jPanel10 = new javax.swing.JPanel();
        btAtender = new javax.swing.JButton();
        btEncerrar = new javax.swing.JButton();
        brPender = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        btProximo = new javax.swing.JButton();
        btAnterior = new javax.swing.JButton();
        btSair = new javax.swing.JButton();
        jtbbMaster = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clvEspera = new javax.swing.JTable();
        jbarra = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        mNome = new javax.swing.JLabel();
        mInsc = new javax.swing.JLabel();
        mConv = new javax.swing.JLabel();
        mNasc = new javax.swing.JLabel();
        mIdade = new javax.swing.JLabel();
        mBairro = new javax.swing.JLabel();
        mCidade = new javax.swing.JLabel();
        mUf = new javax.swing.JLabel();
        mCep = new javax.swing.JLabel();
        mCivil = new javax.swing.JLabel();
        mNaciona = new javax.swing.JLabel();
        mNatural = new javax.swing.JLabel();
        mProfis = new javax.swing.JLabel();
        mRenda = new javax.swing.JLabel();
        mPessoas = new javax.swing.JLabel();
        mPai = new javax.swing.JLabel();
        mMae = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        mEsperas = new javax.swing.JLabel();
        mPendente = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jbtFichaPac = new javax.swing.JButton();

        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel7.setBackground(new java.awt.Color(125, 214, 234));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(5, 16, 157), null));

        jtxtSintomasDia.setColumns(5);
        jtxtSintomasDia.setLineWrap(true);
        jtxtSintomasDia.setRows(15);
        jtxtSintomasDia.setTabSize(5);
        jtxtSintomasDia.setWrapStyleWord(true);
        jtxtSintomasDia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtxtSintomasDiaMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jtxtSintomasDia);

        jLabel1.setText("Doença:");

        cdDoenca.setEnabled(false);
        cdDoenca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cdDoencaKeyReleased(evt);
            }
        });

        btpesqDoenca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        btpesqDoenca.setEnabled(false);
        btpesqDoenca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpesqDoencaActionPerformed(evt);
            }
        });

        nmDoenca.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cdDoenca, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nmDoenca, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btpesqDoenca)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btpesqDoenca)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cdDoenca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nmDoenca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbbLancamentos.addTab("Sintomas", jPanel5);

        jPanel8.setBackground(new java.awt.Color(148, 234, 125));
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(5, 16, 157), null));

        jbtRecApagar.setText("Excluir Receita");
        jbtRecApagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRecApagarActionPerformed(evt);
            }
        });

        jbtRecSalvar.setText("Salvar");
        jbtRecSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRecSalvarActionPerformed(evt);
            }
        });

        jbtRecPrint.setText("Imprimir");
        jbtRecPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRecPrintActionPerformed(evt);
            }
        });

        jScrollPane5.setAutoscrolls(true);

        jtxtReceita.setColumns(5);
        jtxtReceita.setLineWrap(true);
        jtxtReceita.setRows(5);
        jtxtReceita.setWrapStyleWord(true);
        jScrollPane5.setViewportView(jtxtReceita);

        jScrollPane3.setViewportView(jScrollPane5);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jbtRecApagar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 538, Short.MAX_VALUE)
                        .addComponent(jbtRecSalvar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtRecPrint)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtRecApagar)
                    .addComponent(jbtRecSalvar)
                    .addComponent(jbtRecPrint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbbLancamentos.addTab("Receituário", jPanel6);

        jPanel10.setBackground(new java.awt.Color(198, 255, 180));
        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        btAtender.setText("Atender");
        btAtender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAtenderActionPerformed(evt);
            }
        });

        btEncerrar.setText("Encerrar");
        btEncerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEncerrarActionPerformed(evt);
            }
        });

        brPender.setText("Pender");
        brPender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brPenderActionPerformed(evt);
            }
        });

        btCancelar.setText("Cancelar");
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });

        btProximo.setText("Próximo");
        btProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProximoActionPerformed(evt);
            }
        });

        btAnterior.setText("Anterior");
        btAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnteriorActionPerformed(evt);
            }
        });

        btSair.setText("Sair");
        btSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btAtender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btEncerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(brPender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btProximo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btAnterior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btAtender, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btEncerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(brPender, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btProximo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAnterior)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(btSair)
                .addGap(18, 18, 18))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(248, 255, 0), null));

        clvEspera.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clvEspera.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clvEsperaMouseClicked(evt);
            }
        });
        clvEspera.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                clvEsperaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(clvEspera);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
            .addComponent(jbarra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbbMaster.addTab("Lista de Espera", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(36, 189, 1), null), "Ficha [000000]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel6.setText("Nome:");

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel7.setText("Inscrição:");

        jLabel8.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel8.setText("Convênio:");

        jLabel9.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel9.setText("Dt.Nasc:");

        jLabel10.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel10.setText("Idade:");

        jLabel11.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel11.setText("Bairro:");

        jLabel12.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel12.setText("Cidade:");

        jLabel13.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel13.setText("UF:");

        jLabel14.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel14.setText("Cep:");

        jLabel15.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel15.setText("Est.Civil:");

        jLabel16.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel16.setText("Nacionalidade:");

        jLabel17.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel17.setText("Naturalidade:");

        jLabel18.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel18.setText("Profissão:");

        jLabel19.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel19.setText("Nº Pessoas");

        jLabel20.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel20.setText("Renda:");

        jLabel21.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel21.setText("Mãe:");

        jLabel22.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel22.setText("Pai:");

        mNome.setBackground(new java.awt.Color(236, 248, 244));
        mNome.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mNome.setForeground(new java.awt.Color(68, 4, 179));
        mNome.setText("jLabel23");
        mNome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mNome.setOpaque(true);

        mInsc.setBackground(new java.awt.Color(236, 248, 244));
        mInsc.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mInsc.setForeground(new java.awt.Color(68, 4, 179));
        mInsc.setText("jLabel23");
        mInsc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mInsc.setOpaque(true);

        mConv.setBackground(new java.awt.Color(236, 248, 244));
        mConv.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mConv.setForeground(new java.awt.Color(68, 4, 179));
        mConv.setText("jLabel23");
        mConv.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mConv.setOpaque(true);

        mNasc.setBackground(new java.awt.Color(236, 248, 244));
        mNasc.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mNasc.setForeground(new java.awt.Color(68, 4, 179));
        mNasc.setText("99/99/9999");
        mNasc.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mNasc.setOpaque(true);

        mIdade.setBackground(new java.awt.Color(236, 248, 244));
        mIdade.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mIdade.setForeground(new java.awt.Color(68, 4, 179));
        mIdade.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mIdade.setText("99");
        mIdade.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mIdade.setOpaque(true);

        mBairro.setBackground(new java.awt.Color(236, 248, 244));
        mBairro.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mBairro.setForeground(new java.awt.Color(68, 4, 179));
        mBairro.setText("jLabel23");
        mBairro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mBairro.setOpaque(true);

        mCidade.setBackground(new java.awt.Color(236, 248, 244));
        mCidade.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mCidade.setForeground(new java.awt.Color(68, 4, 179));
        mCidade.setText("jLabel23");
        mCidade.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mCidade.setOpaque(true);

        mUf.setBackground(new java.awt.Color(236, 248, 244));
        mUf.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mUf.setForeground(new java.awt.Color(68, 4, 179));
        mUf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mUf.setText("99");
        mUf.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mUf.setOpaque(true);

        mCep.setBackground(new java.awt.Color(236, 248, 244));
        mCep.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mCep.setForeground(new java.awt.Color(68, 4, 179));
        mCep.setText("99999-999");
        mCep.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mCep.setOpaque(true);

        mCivil.setBackground(new java.awt.Color(236, 248, 244));
        mCivil.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mCivil.setForeground(new java.awt.Color(68, 4, 179));
        mCivil.setText("Divorciado");
        mCivil.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mCivil.setOpaque(true);

        mNaciona.setBackground(new java.awt.Color(236, 248, 244));
        mNaciona.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mNaciona.setForeground(new java.awt.Color(68, 4, 179));
        mNaciona.setText("jLabel23");
        mNaciona.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mNaciona.setOpaque(true);

        mNatural.setBackground(new java.awt.Color(236, 248, 244));
        mNatural.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mNatural.setForeground(new java.awt.Color(68, 4, 179));
        mNatural.setText("jLabel23");
        mNatural.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mNatural.setOpaque(true);

        mProfis.setBackground(new java.awt.Color(236, 248, 244));
        mProfis.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mProfis.setForeground(new java.awt.Color(68, 4, 179));
        mProfis.setText("jLabel23");
        mProfis.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mProfis.setOpaque(true);

        mRenda.setBackground(new java.awt.Color(236, 248, 244));
        mRenda.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mRenda.setForeground(new java.awt.Color(68, 4, 179));
        mRenda.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        mRenda.setText("0,00");
        mRenda.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mRenda.setOpaque(true);

        mPessoas.setBackground(new java.awt.Color(236, 248, 244));
        mPessoas.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mPessoas.setForeground(new java.awt.Color(68, 4, 179));
        mPessoas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mPessoas.setText("99");
        mPessoas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mPessoas.setOpaque(true);

        mPai.setBackground(new java.awt.Color(236, 248, 244));
        mPai.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mPai.setForeground(new java.awt.Color(68, 4, 179));
        mPai.setText("jLabel23");
        mPai.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mPai.setOpaque(true);

        mMae.setBackground(new java.awt.Color(236, 248, 244));
        mMae.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mMae.setForeground(new java.awt.Color(68, 4, 179));
        mMae.setText("jLabel23");
        mMae.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        mMae.setOpaque(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(mMae, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel16)
                                    .addGap(79, 79, 79)
                                    .addComponent(jLabel17)
                                    .addGap(87, 87, 87)
                                    .addComponent(jLabel18))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(mNaciona, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(mNatural, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(mProfis, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(mRenda, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(mPessoas, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel20)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(mNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mIdade, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(35, 35, 35)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11))
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(mCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(mUf, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(mCep, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(mCivil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(mNome, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(mInsc, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(mConv, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mNome)
                    .addComponent(mInsc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mConv))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mNasc)
                    .addComponent(mIdade)
                    .addComponent(mBairro)
                    .addComponent(mCidade)
                    .addComponent(mUf)
                    .addComponent(mCep)
                    .addComponent(mCivil))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(mNatural)
                                .addComponent(mProfis)
                                .addComponent(mRenda)
                                .addComponent(mPessoas))
                            .addComponent(mNaciona)))
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mPai)
                    .addComponent(mMae))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtbbMaster.addTab("Dados do Paciênte em Atendimento", jPanel2);

        jPanel9.setBackground(new java.awt.Color(255, 250, 180));
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel2.setText("<html><b>Paciênte(s) em Espera:</b></html>");
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        mEsperas.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        mEsperas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mEsperas.setText("000000");
        mEsperas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        mPendente.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        mPendente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mPendente.setText("000000");
        mPendente.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setText("<html><b>Pendente(s):</b></html>");
        jLabel5.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(mEsperas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addComponent(mPendente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mEsperas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mPendente, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jbtFichaPac.setText("Ficha Paciênte");
        jbtFichaPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtFichaPacActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtbbMaster)
                    .addComponent(jtbbLancamentos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtFichaPac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(jbtFichaPac))
                    .addComponent(jtbbMaster, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtbbLancamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtSintomasDiaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtxtSintomasDiaMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSintomasDiaMouseReleased

    private void cdDoencaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cdDoencaKeyReleased
        if (evt.getKeyCode() == 10) {
          String[][] dados = (String[][])null;
          try {
            dados = conn.LerCamposTabela(new String[] { "categoria", "descricao" }, "cid10", "lower(categoria) = '" + cdDoenca.getText().trim().toLowerCase() + "'");
          } catch (Exception e) {}
          if (dados != null) {
            cdDoenca.setText(dados[0][3].toUpperCase());
            nmDoenca.setText(dados[1][3].toUpperCase());
            jtxtSintomasDia.requestFocus();
          } else {
            nmDoenca.setText("Codigo nao existe!!!");
            cdDoenca.setText(null);
            cdDoenca.requestFocus();
          }
        }
    }//GEN-LAST:event_cdDoencaKeyReleased

    private void btpesqDoencaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpesqDoencaActionPerformed
        BuscaCid10 oCid = new BuscaCid10(null, true);
        oCid.setVisible(true);

        Object[] dados = oCid.dados;
        oCid = null;
        if (dados != null) {
          cdDoenca.setText(dados[0].toString());
          nmDoenca.setText(dados[1].toString());
          jtxtSintomasDia.requestFocus();
        }
    }//GEN-LAST:event_btpesqDoencaActionPerformed

    private void jbtRecApagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRecApagarActionPerformed
        int row = clvEspera.getSelectedRow();
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        try {
          String dSql = "DELETE FROM receituario WHERE pc_numero = " + cdpac + " AND rc_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' AND Upper(rc_medico) = '" + VariaveisGlobais.nmmedico.toUpperCase() + "';";
          conn.ExecutarComando(dSql);
          jtxtReceita.setText(null);
        } catch (Exception e) {}
    }//GEN-LAST:event_jbtRecApagarActionPerformed

    private void jbtRecSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRecSalvarActionPerformed
        SalvaReceita();
    }//GEN-LAST:event_jbtRecSalvarActionPerformed

    private void jbtRecPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRecPrintActionPerformed
        SalvaReceita();
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
          return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        GerarReceita(cdpac);
    }//GEN-LAST:event_jbtRecPrintActionPerformed

    private void btAtenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAtenderActionPerformed
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
            return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        String nmpac = clvEspera.getModel().getValueAt(row, 1).toString().trim();
        setDefaultCloseOperation(0);
        try {
          os.println("#rec " + cdpac + ";ate");
        } catch (Exception err) {}
        try {
          conn.ExecutarComando("update marcar set ma_status = 2 where upper(ma_origem) = '" + VariaveisGlobais.origem.toUpperCase() + "' and (ma_consulta = 1 or ma_revisao = 1 or ma_cortesia = 1) and ma_codmedico = " + VariaveisGlobais.cdmedico + " and ma_pcnumero = " + cdpac + ";");
        } catch (Exception err) {}
        conn.Auditor("MEDICO:ATENDEU", nmpac.toLowerCase());

        clvEspera.getModel().setValueAt("A", row, 2);
        ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/atendendo.png"));
        clvEspera.getModel().setValueAt(istatatd, row, 6);

        clvEspera.setEnabled(false);
        jtbbMaster.setEnabledAt(0, false);
        jtbbMaster.setSelectedIndex(1);

        btAtender.setEnabled(false);
        btEncerrar.setEnabled(true);
        brPender.setEnabled(true);
        btCancelar.setEnabled(true);
        btProximo.setEnabled(false);
        btAnterior.setEnabled(false);
        btSair.setEnabled(false);

        jtxtReceita.setEnabled(true);
        jbtRecApagar.setEnabled(true);
        jbtRecSalvar.setEnabled(true);
        jbtRecPrint.setEnabled(true);

        jtbbLancamentos.setEnabled(true);

        cdDoenca.setEnabled(true);
        nmDoenca.setEnabled(true);
        btpesqDoenca.setEnabled(true);

        jtxtSintomasDia.setEnabled(true);

        cdDoenca.requestFocus();
    }//GEN-LAST:event_btAtenderActionPerformed

    private void btEncerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEncerrarActionPerformed
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
            return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        String nmpac = clvEspera.getModel().getValueAt(row, 1).toString().trim();
        String tpatd = clvEspera.getModel().getValueAt(row, 10).toString().trim().toUpperCase();
        try {
            os.println("#rec " + cdpac + ";enc;" + Dates.DateFormat("HH:mm:ss", new Date()));
        } catch (Exception err) {}
        try {
            conn.ExecutarComando("update marcar set ma_status = 4, ma_horasai = '" + Dates.DateFormat("HH:mm:ss", new Date()) + "' where upper(ma_origem) = '" + VariaveisGlobais.origem.toUpperCase() + "' and (ma_consulta = 1 or ma_revisao = 1 or ma_cortesia = 1) and ma_codmedico = " + VariaveisGlobais.cdmedico + " and ma_pcnumero = " + cdpac + ";");
        } catch (Exception e) {}
        if (tpatd.equalsIgnoreCase("C")) {
            try {
                conn.ExecutarComando("update pacientes set pc_dataultimaconsulta = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' where upper(pc_origem) = '" + VariaveisGlobais.origem.toUpperCase() + "' and pc_numero = " + cdpac + ";");
            } catch (Exception e) {}
        }
        try {
          conn.ExecutarComando("DELETE FROM sintomas WHERE pc_numero = " + cdpac + " AND st_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "';");

          String iSql = "INSERT INTO sintomas (st_sintoma, pc_numero, st_data, st_hora, st_nmmedico, st_cddoenca) VALUES ('" + jtxtSintomasDia.getText() + "'," + cdpac + ",'" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "',to_timestamp('" + Dates.DateFormat("yyyy-MM-dd hh:mm:ss", new Date()) + "', 'YYYY/MM/DD HH:MI')" + ",'" + VariaveisGlobais.nmmedico.toUpperCase() + "','" + cdDoenca.getText() + "');";

          conn.ExecutarComando(iSql);
        } catch (Exception e) {}
        try {
          conn.ExecutarComando("update marcados set status = 1 where upper(medico) = '" + VariaveisGlobais.nmmedico.toUpperCase() + "' and data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' and pcnumero = " + cdpac + "");
        } catch (Exception e) {}
        try {
          conn.ExecutarComando("update pacientes set pc_atendidopor = '" + VariaveisGlobais.nmmedico.toUpperCase() + "' where pc_numero = " + cdpac);
        } catch (Exception e) {}
        try {
          conn.Auditor("MEDICO:ENCERROU", nmpac.toLowerCase());
        } catch (Exception e) {}
        clvEspera.getModel().setValueAt("E", row, 2);
        ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/encerrado.png"));
        clvEspera.getModel().setValueAt(istatatd, row, 6);

        setDefaultCloseOperation(2);

        int[] tep = contapac();
        mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
        mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
        jbarra.setVisible(false);

        String pp = proximopac();
        if (!pp.equalsIgnoreCase("")) {
          LerDadosPac(pp);
        } else {
          limpartela();
        }
        jtbbMaster.setEnabledAt(0, true);
        jtbbMaster.setSelectedIndex(1);
        clvEspera.setEnabled(true);

        jtbbLancamentos.setEnabled(false);

        jtxtSintomasDia.setEnabled(false);

        jbtRecSalvar.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        jtxtReceita.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecSalvar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        cdDoenca.setEnabled(false);
        nmDoenca.setEnabled(false);
        btpesqDoenca.setEnabled(false);

        btAtender.setEnabled(true);
        btEncerrar.setEnabled(false);
        brPender.setEnabled(false);
        btCancelar.setEnabled(false);
        btProximo.setEnabled(true);
        btAnterior.setEnabled(true);
        btSair.setEnabled(true);
    }//GEN-LAST:event_btEncerrarActionPerformed

    private void brPenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brPenderActionPerformed
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
            return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        String nmpac = clvEspera.getModel().getValueAt(row, 1).toString().trim();
        try { os.println("#rec " + cdpac + ";pen;" + Dates.DateFormat("HH:mm:ss", new Date())); } catch (Exception e) {}
        try {
          conn.ExecutarComando("update marcar set ma_status = 3 where upper(ma_origem) = '" + VariaveisGlobais.origem.toUpperCase() + "' and (ma_consulta = 1 or ma_revisao = 1 or ma_cortesia = 1) and ma_codmedico = " + VariaveisGlobais.cdmedico + " and ma_pcnumero = " + cdpac + ";");
        } catch (Exception e) {}
        try {
          conn.ExecutarComando("DELETE FROM sintomas WHERE pc_numero = " + cdpac + " AND st_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "';");

          String iSql = "INSERT INTO sintomas (st_sintoma, pc_numero, st_data, st_hora, st_nmmedico, st_cddoenca) VALUES ('" + jtxtSintomasDia.getText() + "'," + cdpac + ",'" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "',to_timestamp('" + Dates.DateFormat("yyyy-MM-dd hh:mm:ss", new Date()) + "', 'YYYY/MM/DD HH:MI')" + ",'" + VariaveisGlobais.nmmedico.toUpperCase() + "','" + cdDoenca.getText() + "');";

          conn.ExecutarComando(iSql);
        } catch (Exception e) {}
        try {
          conn.Auditor("MEDICO:PENDEU", nmpac.toLowerCase());
        } catch (Exception e) {}
        clvEspera.getModel().setValueAt("P", row, 2);
        ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/pendente.png"));
        clvEspera.getModel().setValueAt(istatatd, row, 6);

        setDefaultCloseOperation(2);

        int[] tep = contapac();
        mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
        mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
        jbarra.setVisible(false);

        String pp = proximopac();
        if (!pp.equalsIgnoreCase("")) {
          LerDadosPac(pp);
        } else {
          limpartela();
        }
        jtbbMaster.setEnabledAt(0, true);
        jtbbMaster.setSelectedIndex(1);
        clvEspera.setEnabled(true);

        jtbbLancamentos.setEnabled(false);

        jtxtSintomasDia.setEnabled(false);

        jbtRecSalvar.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        jtxtReceita.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecSalvar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        cdDoenca.setEnabled(false);
        nmDoenca.setEnabled(false);
        btpesqDoenca.setEnabled(false);

        btAtender.setEnabled(true);
        btEncerrar.setEnabled(false);
        brPender.setEnabled(false);
        btCancelar.setEnabled(false);
        btProximo.setEnabled(true);
        btAnterior.setEnabled(true);
        btSair.setEnabled(true);
    }//GEN-LAST:event_brPenderActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
            return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        String nmpac = clvEspera.getModel().getValueAt(row, 1).toString().trim();
        try { os.println("#rec " + cdpac + ";can"); } catch (Exception e) {}
        try {
          conn.ExecutarComando("update marcar set ma_status = 0 where upper(ma_origem) = '" + VariaveisGlobais.origem.toUpperCase() + "' and (ma_consulta = 1 or ma_revisao = 1 or ma_cortesia = 1) and ma_codmedico = " + VariaveisGlobais.cdmedico + " and ma_pcnumero = " + cdpac + ";");
        } catch (Exception e) {}
        try {
          conn.Auditor("MEDICO:CANCELAR", nmpac.toLowerCase());
        } catch (Exception e) {}
        clvEspera.getModel().setValueAt("W", row, 2);
        ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/aguardando.png"));
        clvEspera.getModel().setValueAt(istatatd, row, 6);

        setDefaultCloseOperation(2);

        int[] tep = contapac();
        mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
        mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
        jbarra.setVisible(false);

        String pp = proximopac();
        if (!pp.equalsIgnoreCase("")) {
          LerDadosPac(pp);
        } else {
          limpartela();
        }
        jtbbMaster.setEnabledAt(0, true);
        jtbbMaster.setSelectedIndex(1);
        clvEspera.setEnabled(true);

        jtbbLancamentos.setEnabled(false);

        jtxtSintomasDia.setEnabled(false);

        jbtRecSalvar.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        jtxtReceita.setEnabled(false);
        jbtRecApagar.setEnabled(false);
        jbtRecSalvar.setEnabled(false);
        jbtRecPrint.setEnabled(false);

        cdDoenca.setEnabled(false);
        nmDoenca.setEnabled(false);
        btpesqDoenca.setEnabled(false);

        btAtender.setEnabled(true);
        btEncerrar.setEnabled(false);
        brPender.setEnabled(false);
        btCancelar.setEnabled(false);
        btProximo.setEnabled(true);
        btAnterior.setEnabled(true);
        btSair.setEnabled(true);
    }//GEN-LAST:event_btCancelarActionPerformed

    private void btProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btProximoActionPerformed
        nextpac(clvEspera.getSelectedRow());
    }//GEN-LAST:event_btProximoActionPerformed

    private void btAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnteriorActionPerformed
        previouspac(clvEspera.getSelectedRow());
    }//GEN-LAST:event_btAnteriorActionPerformed

    private void btSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSairActionPerformed
        dispose();
    }//GEN-LAST:event_btSairActionPerformed

    private void clvEsperaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clvEsperaMouseClicked
        int row = clvEspera.getSelectedRow();
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();

        LerDadosPac(cdpac);
    }//GEN-LAST:event_clvEsperaMouseClicked

    private void clvEsperaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clvEsperaKeyReleased
        int row = clvEspera.getSelectedRow();
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();

        LerDadosPac(cdpac);
    }//GEN-LAST:event_clvEsperaKeyReleased

    private void jbtFichaPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtFichaPacActionPerformed
        int row = clvEspera.getSelectedRow();
        if (row == -1) {
          return;
        }
        String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
        GerarFichaPac(cdpac);
    }//GEN-LAST:event_jbtFichaPacActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        try { os.println("/quit"); } catch (Exception err) {}
    }//GEN-LAST:event_formInternalFrameClosed

    private void SalvaReceita() {
      int row = clvEspera.getSelectedRow();
      String cdpac = clvEspera.getModel().getValueAt(row, 0).toString().trim();
      try {
        String dSql = "DELETE FROM receituario WHERE pc_numero = " + cdpac + " AND rc_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' AND Upper(rc_medico) = '" + VariaveisGlobais.nmmedico.toUpperCase() + "';";
        conn.ExecutarComando(dSql);

        String iSql = "INSERT INTO receituario (pc_numero, rc_data, rc_medico, rc_receita) VALUES (" + cdpac + ",'" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "','" + VariaveisGlobais.nmmedico.toUpperCase() + "','" + jtxtReceita.getText() + "');";

        conn.ExecutarComando(iSql);
      } catch (Exception e) {}
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brPender;
    private javax.swing.JButton btAnterior;
    private javax.swing.JButton btAtender;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btEncerrar;
    private javax.swing.JButton btProximo;
    private javax.swing.JButton btSair;
    private javax.swing.JButton btpesqDoenca;
    private javax.swing.JTextField cdDoenca;
    private javax.swing.JTable clvEspera;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JProgressBar jbarra;
    private javax.swing.JButton jbtFichaPac;
    private javax.swing.JButton jbtRecApagar;
    private javax.swing.JButton jbtRecPrint;
    private javax.swing.JButton jbtRecSalvar;
    private javax.swing.JTabbedPane jtbbLancamentos;
    private javax.swing.JTabbedPane jtbbMaster;
    private javax.swing.JTextArea jtxtReceita;
    private javax.swing.JTextArea jtxtSintomasDia;
    private javax.swing.JLabel mBairro;
    private javax.swing.JLabel mCep;
    private javax.swing.JLabel mCidade;
    private javax.swing.JLabel mCivil;
    private javax.swing.JLabel mConv;
    private javax.swing.JLabel mEsperas;
    private javax.swing.JLabel mIdade;
    private javax.swing.JLabel mInsc;
    private javax.swing.JLabel mMae;
    private javax.swing.JLabel mNaciona;
    private javax.swing.JLabel mNasc;
    private javax.swing.JLabel mNatural;
    private javax.swing.JLabel mNome;
    private javax.swing.JLabel mPai;
    private javax.swing.JLabel mPendente;
    private javax.swing.JLabel mPessoas;
    private javax.swing.JLabel mProfis;
    private javax.swing.JLabel mRenda;
    private javax.swing.JLabel mUf;
    private javax.swing.JTextField nmDoenca;
    // End of variables declaration//GEN-END:variables

  private void GerarReceita(String cdpac)
  {
    String query = "Select r.*, p.pc_nome from receituario r, pacientes p Where p.pc_numero = r.pc_numero AND r.pc_numero = " + cdpac + " AND r.rc_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' ORDER BY r.rc_data DESC;";
    
    ResultSet rs = conn.AbrirTabela(query, 1007);
    
    Map parametros = new HashMap();
    parametros.put("pcnumero", cdpac);
    parametros.put("datareceita", Dates.DateFormat("yyyy-MM-dd", new Date()));
    
    JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
    
    String fileName = "./reports/rReceituario.jasper";
    try
    {
      JasperPrint print = JasperFillManager.fillReport(fileName, null, jrRS);
      viewReportFrame("Receita", print);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    DbMain.FecharTabela(rs);
  }
  
  private void GerarFichaPac(String cdpac)
  {
    String query = "select p.pc_inscricao, p.pc_nome, p.pc_nascimento, extract(year from age(pc_nascimento)) AS idade, p.pc_nacionalidade, p.pc_profissao, p.pc_numero, p.pc_nome, p.pc_endereco, p.pc_bairro, p.pc_cidade, p.pc_estado, p.pc_cep, p.pc_naturalidade, p.pc_mae, p.pc_pai, p.pc_estadocivil, p.pc_sexo, p.pc_cor, p.pc_convenio, p.pc_renda, p.pc_pessoas, s.st_sintoma, s.st_data, s.st_hora, s.st_nmmedico FROM pacientes p, sintomas s WHERE (p.pc_numero = s.pc_numero) AND p.pc_numero = Cast('" + cdpac + "' AS double precision) " + "ORDER BY s.st_data DESC;";
    
    ResultSet rs = conn.AbrirTabela(query, 1007);
    
    JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
    
    String fileName = "./reports/rFichaPac.jasper";
    
    Map parametros = new HashMap();
    parametros.put("pc_numero", cdpac);
    
    InputStream urlSubReportFaturas = Report.class.getResourceAsStream("reports/rFichaPac_receitas.jasper");
    parametros.put("SUB_REPORT", urlSubReportFaturas);
    try
    {
      JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrRS);
      viewReportFrame("Ficha do Paci��nte", print);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    DbMain.FecharTabela(rs);
  }
  
  private static void viewReportFrame(String titulo, JasperPrint print)
  {
    JRViewer viewer = new JRViewer(print);
    
    JFrame frameRelatorio = new JFrame(titulo);
    
    frameRelatorio.add(viewer, "Center");
    
    frameRelatorio.setSize(500, 500);
    
    frameRelatorio.setExtendedState(6);
    
    frameRelatorio.setDefaultCloseOperation(2);
    
    frameRelatorio.setVisible(true);
  }
  
  private void LerDadosPac(String cdpac)
  {
    String[] aCampos = { "pc_nome", "pc_inscricao", "pc_convenio", "pc_convnumero", "pc_nascimento", "pc_bairro", "pc_cidade", "pc_estado", "pc_cep", "pc_estadocivil", "pc_nacionalidade", "pc_naturalidade", "pc_profissao", "pc_mae", "pc_pai" };
    
    String[][] dados_pac = (String[][])null;
    try
    {
      dados_pac = conn.LerCamposTabela(aCampos, "pacientes", "pc_numero = " + cdpac);
    }
    catch (Exception err) {}
    if (dados_pac != null)
    {
      jPanel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(new Color(36, 189, 1), null), "Ficha [" + FuncoesGlobais.StrZero(cdpac, 6) + "]", 0, 0, new Font("Dialog", 1, 14)));
      mNome.setText(dados_pac[0][3]);mNome.setSize(377, 20);
      mInsc.setText(dados_pac[1][3]);mInsc.setSize(173, 20);
      mConv.setText(dados_pac[2][3]);mConv.setSize(157, 20);mConv.setToolTipText(dados_pac[3][3]);
      
      Date tnasc = Dates.StringtoDate(dados_pac[4][3], "yyyy-MM-dd");
      mNasc.setText(Dates.DateFormat("dd/MM/yyyy", tnasc));mNasc.setSize(90, 20);
      
      int idade = Dates.DateDiff("A", tnasc, new Date());
      mIdade.setText(FuncoesGlobais.nStrZero(idade, 2));mIdade.setSize(40, 20);
      
      mBairro.setText(dados_pac[5][3]);mBairro.setSize(160, 20);
      mCidade.setText(dados_pac[6][3]);mCidade.setSize(178, 20);
      mUf.setText(dados_pac[7][3]);mUf.setSize(34, 20);
      mCep.setText(dados_pac[8][3]);mCep.setSize(90, 20);
      mCivil.setText(dados_pac[9][3]);mCivil.setSize(90, 20);
      mNaciona.setText(dados_pac[10][3]);mNaciona.setSize(175, 20);
      mNatural.setText(dados_pac[11][3]);mNatural.setSize(175, 20);
      mProfis.setText(dados_pac[12][3]);mProfis.setSize(216, 20);
      mRenda.setText(" ");mRenda.setSize(83, 20);
      mPessoas.setText(" ");mPessoas.setSize(46, 20);
      mMae.setText(dados_pac[13][3]);mMae.setSize(356, 20);
      mPai.setText(dados_pac[14][3]);mPai.setSize(356, 20);
      
      jtxtSintomasDia.setText(LerDadosPacSintomas(cdpac));
      
      jtxtReceita.setText(LerDadosPacReceita(cdpac));
      
      int pos = TableControl.seek(clvEspera, 0, cdpac);
      if (pos > -1) {
        clvEspera.changeSelection(pos, 0, false, false);
      }
    }
  }
  
  private String LerDadosPacSintomas(String cdpac)
  {
    String rSintomas = "";
    String sSql = "SELECT st_sintoma FROM sintomas WHERE pc_numero = " + cdpac + " AND st_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' ORDER BY st_data DESC;";
    
    ResultSet srs = conn.AbrirTabela(sSql, 1007);
    try
    {
      while (srs.next()) {
        rSintomas = rSintomas + srs.getString("st_sintoma");
      }
    }
    catch (Exception err) {}
    return rSintomas;
  }
  
  private String LerDadosPacReceita(String cdpac)
  {
    String rReceita = "";
    String sSql = "SELECT rc_receita FROM receituario WHERE pc_numero = " + cdpac + " AND rc_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' ORDER BY rc_data DESC;";
    
    ResultSet srs = conn.AbrirTabela(sSql, 1007);
    try
    {
      while (srs.next()) {
        rReceita = rReceita + srs.getString("rc_receita");
      }
    }
    catch (Exception err) {}
    return rReceita;
  }
  
  private int[] contapac()
  {
    int e = 0;int p = 0;
    for (int i = 0; i < clvEspera.getRowCount(); i++)
    {
      int modelRow = clvEspera.convertRowIndexToModel(i);
      String tep = clvEspera.getModel().getValueAt(modelRow, 2).toString().trim();
      if ((tep.equalsIgnoreCase("W")) || (tep.equalsIgnoreCase(""))) {
        e++;
      }
      if (tep.equalsIgnoreCase("P")) {
        p++;
      }
    }
    return new int[] { e, p };
  }
  
  private String proximopac()
  {
    String cdpac = "";
    for (int i = 0; i < clvEspera.getRowCount(); i++)
    {
      int modelRow = clvEspera.convertRowIndexToModel(i);
      String tep = clvEspera.getModel().getValueAt(modelRow, 2).toString().trim();
      if ((tep.equalsIgnoreCase("W")) || (tep.equalsIgnoreCase("")))
      {
        cdpac = clvEspera.getModel().getValueAt(modelRow, 0).toString().trim();
        break;
      }
    }
    return cdpac;
  }
  
  private void nextpac(int pos)
  {
    String cdpac = "";
    for (int i = pos + 1; i < clvEspera.getRowCount(); i++)
    {
      int modelRow = clvEspera.convertRowIndexToModel(i);
      String tep = clvEspera.getModel().getValueAt(modelRow, 2).toString().trim();
      if ((tep.equalsIgnoreCase("W")) || (tep.equalsIgnoreCase("")))
      {
        cdpac = clvEspera.getModel().getValueAt(modelRow, 0).toString().trim();
        break;
      }
    }
    if (!cdpac.equalsIgnoreCase(""))
    {
      LerDadosPac(cdpac);
      jtbbMaster.setSelectedIndex(1);
      jtbbLancamentos.setEnabled(false);
      
      jtxtSintomasDia.setEnabled(false);
      
      jbtRecSalvar.setEnabled(false);
      jbtRecApagar.setEnabled(false);
      jbtRecPrint.setEnabled(false);
      jtxtReceita.setEnabled(false);
      
      cdDoenca.setEnabled(false);
      nmDoenca.setEnabled(false);
      btpesqDoenca.setEnabled(false);
      
      btAtender.setEnabled(true);
      btEncerrar.setEnabled(false);
      brPender.setEnabled(false);
      btCancelar.setEnabled(false);
      btProximo.setEnabled(true);
      btAnterior.setEnabled(true);
      btSair.setEnabled(true);
    }
  }
  
  private void previouspac(int pos)
  {
    String cdpac = "";
    for (int i = pos - 1; i >= 0; i--)
    {
      int modelRow = clvEspera.convertRowIndexToModel(i);
      String tep = clvEspera.getModel().getValueAt(modelRow, 2).toString().trim();
      if ((tep.equalsIgnoreCase("W")) || (tep.equalsIgnoreCase("")))
      {
        cdpac = clvEspera.getModel().getValueAt(modelRow, 0).toString().trim();
        break;
      }
    }
    if (!cdpac.equalsIgnoreCase(""))
    {
      LerDadosPac(cdpac);
      jtbbMaster.setSelectedIndex(1);
      jtbbLancamentos.setEnabled(false);
      
      jtxtSintomasDia.setEnabled(false);
      
      jbtRecSalvar.setEnabled(false);
      jbtRecApagar.setEnabled(false);
      jbtRecPrint.setEnabled(false);
      jtxtReceita.setEnabled(false);
      
      cdDoenca.setEnabled(false);
      nmDoenca.setEnabled(false);
      btpesqDoenca.setEnabled(false);
      
      btAtender.setEnabled(true);
      btEncerrar.setEnabled(false);
      brPender.setEnabled(false);
      btCancelar.setEnabled(false);
      btProximo.setEnabled(true);
      btAnterior.setEnabled(true);
      btSair.setEnabled(true);
    }
  }
  
  class MultiThreadChatClient
    implements Runnable
  {
    private BufferedReader inputLine = null;
    private boolean closed = false;
    private String user = "";
    private String type = "";
    private int portNumber = 2222;
    private String host = "localhost";
    
    MultiThreadChatClient() {}
    
    public void main(String[] args)
    {
      user = args[0];
      type = args[1];
      if ((!args[2].isEmpty()) && (args[2] != null)) {
        host = args[2];
      }
      if ((!args[3].isEmpty()) && (args[3] != null)) {
        portNumber = Integer.valueOf(args[3]).intValue();
      }
      try
      {
        jatdMedicos.clientSocket = new Socket(host, portNumber);
        inputLine = new BufferedReader(new InputStreamReader(System.in));
        jatdMedicos.os = new PrintStream(jatdMedicos.clientSocket.getOutputStream());
        jatdMedicos.is = new DataInputStream(jatdMedicos.clientSocket.getInputStream());
      }
      catch (UnknownHostException e)
      {
        System.err.println("Don't know about host " + host);
        dispose();
      }
      catch (IOException e)
      {
        System.err.println("Couldn't get I/O for the connection to the host " + host);
        
        dispose();
      }
      if ((jatdMedicos.clientSocket != null) && (jatdMedicos.os != null) && (jatdMedicos.is != null)) {
        try
        {
          new Thread(new MultiThreadChatClient()).start();
          
          jatdMedicos.os.println(user + ";" + type);
          closed = false;
          while (!closed) {
            jatdMedicos.os.println(inputLine.readLine().trim());
          }
          jatdMedicos.os.close();
          jatdMedicos.is.close();
          jatdMedicos.clientSocket.close();
        }
        catch (IOException e)
        {
          System.err.println("IOException:  " + e);
        }
      }
    }
    
    public void run()
    {
      try
      {
        String responseLine;
        while ((responseLine = jatdMedicos.is.readLine()) != null)
        {
          System.out.println(responseLine);
          if (responseLine.indexOf("*** Tchau") != -1) {
            break;
          }
          Protocolo(responseLine);
        }
        closed = true;
      }
      catch (IOException e)
      {
        System.err.println("IOException:  " + e);
      }
    }
  }
  
  private void Protocolo(String msg)
  {
    String[] protocol = msg.split(";", 2);
    String acao = protocol[1];
    String cdpac = protocol[0];
    if (acao.equalsIgnoreCase("ins")) {
      addPac(cdpac);
    } else if (acao.equalsIgnoreCase("des")) {
      Desistiu(cdpac);
    } else if (acao.equalsIgnoreCase("sai")) {
      Saida(cdpac);
    } else if (acao.equalsIgnoreCase("bye")) {
      dispose();
    }
  }
  
  private void addPac(String cdpac)
  {
    boolean bespera = false;
    String tsql = "select m.*, p.* from marcar as m, pacientes as p where lower(m.ma_origem) = '" + VariaveisGlobais.origem.toLowerCase().trim() + "' AND m.ma_pcnumero = p.pc_numero " + "and to_char(m.ma_data, 'dd-mm-yyyy') = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and m.ma_codmedico = '" + VariaveisGlobais.cdmedico + "' AND (ma_pcnumero = '" + cdpac + "') order by ma_hora;";
    
    ResultSet tbpac = conn.AbrirTabela(tsql, 1007);
    try
    {
      while (tbpac.next())
      {
        String tstatus = tbpac.getString("ma_status");
        String thora = tbpac.getString("ma_hora");
        String tpcnumero = tbpac.getString("ma_pcnumero");
        String tisnc = tbpac.getString("ma_inscricao");
        String tnome = tbpac.getString("ma_nome").toUpperCase();
        String tcvnum = tbpac.getString("ma_convnumero");
        String tconvenio = tbpac.getString("ma_plano");
        String tcategoria = tbpac.getString("ma_categoria");
        String tcdmedico = tbpac.getString("ma_codmedico");
        String tmedico = tbpac.getString("ma_medico").toUpperCase();
        String ttipoatd;
        if (tbpac.getBoolean("ma_consulta"))
        {
          ttipoatd = "C";
        }
        else
        {
          if (tbpac.getBoolean("ma_revisao")) {
            ttipoatd = "R";
          } else {
            ttipoatd = "T";
          }
        }
        String tstatatd = null;ImageIcon istatatd = null;
        if (tbpac.getString("ma_status").equalsIgnoreCase("0"))
        {
          tstatatd = "W";
          istatatd = new ImageIcon(getClass().getResource("/Figuras/aguardando.png"));
        }
        else if (tbpac.getString("ma_status").equalsIgnoreCase("1"))
        {
          tstatatd = "I";
        }
        else if (tbpac.getString("ma_status").equalsIgnoreCase("2"))
        {
          tstatatd = "A";
          istatatd = new ImageIcon(getClass().getResource("/Figuras/atendendo.png"));
        }
        else if (tbpac.getString("ma_status").equalsIgnoreCase("3"))
        {
          tstatatd = "P";
          istatatd = new ImageIcon(getClass().getResource("/Figuras/pendente.png"));
        }
        else if (tbpac.getString("ma_status").equalsIgnoreCase("4"))
        {
          tstatatd = "E";
          istatatd = new ImageIcon(getClass().getResource("/Figuras/encerrado.png"));
        }
        String tstatimg = null;
        String esql = "select st_exame as exame from exames where dt_exame = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_exame desc;";
        
        ResultSet ttable = conn.AbrirTabela(esql, 1007);
        try
        {
          if (ttable.next())
          {
            if (ttable.getInt("exame") == 1) {
              bespera = false;
            } else {
              bespera = true;
            }
            ttable.last();
            tstatimg = ttable.getString("exame");
          }
          else
          {
            tstatimg = "";
          }
        }
        catch (Exception err) {}
        DbMain.FecharTabela(ttable);
        
        String tstatproc = null;
        String psql = "select st_trata as tratam from trata where dt_trata = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_trata desc";
        
        ttable = conn.AbrirTabela(psql, 1007);
        try
        {
          if (ttable.next())
          {
            if (ttable.getInt("tratam") == 1) {
              bespera = false;
            } else {
              bespera = true;
            }
            ttable.last();
            tstatproc = ttable.getString("tratam");
          }
          else
          {
            tstatproc = "";
          }
        }
        catch (Exception err) {}
        DbMain.FecharTabela(ttable);
        
        String tstatmed = null;
        String msql = "select st_medica as medic from medica where dt_medica = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' and pc_numero = " + tpcnumero + " order by st_medica desc;";
        
        ttable = conn.AbrirTabela(msql, 1007);
        try
        {
          if (ttable.next())
          {
            if (ttable.getInt("medic") == 1) {
              bespera = false;
            } else {
              bespera = true;
            }
            ttable.last();
            if (!bespera) {
              tstatmed = "1";
            } else {
              tstatmed = "0";
            }
          }
          else
          {
            tstatmed = "";
          }
        }
        catch (Exception err) {}
        DbMain.FecharTabela(ttable);
        
        String thorasai = tbpac.getString("ma_horasai");
        
        Time ehora = tbpac.getTime("ma_hora");
        Time shora = tbpac.getTime("ma_horasai");
        String fhora = "";
        if (shora != null)
        {
          long zhora = shora.getTime() - ehora.getTime();
          long r = zhora / 1000L / 60L / 60L;
          fhora = String.valueOf(r);
        }
        else
        {
          fhora = "";
        }
        Object[] linha = { tpcnumero, tnome, tstatatd, tstatimg, tstatproc, tstatmed, istatatd, null, null, null, ttipoatd, thora, thorasai, fhora };
        
        newTable.add(clvEspera, linha);
      }
    }
    catch (Exception err)
    {
      err.printStackTrace();
    }
    DbMain.FecharTabela(tbpac);
    
    int[] tep = contapac();
    mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
    mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
    jbarra.setVisible(false);
    if ((btAtender.isEnabled()) && (mNome.getText().trim().equalsIgnoreCase("")))
    {
      String pp = proximopac();
      if (!pp.equalsIgnoreCase("")) {
        LerDadosPac(pp);
      } else {
        limpartela();
      }
      jtbbMaster.setSelectedIndex(1);
      
      jtbbLancamentos.setEnabled(false);
      
      jtxtSintomasDia.setEnabled(false);
      
      jbtRecSalvar.setEnabled(false);
      jbtRecApagar.setEnabled(false);
      jbtRecPrint.setEnabled(false);
      jtxtReceita.setEnabled(false);
      
      btAtender.setEnabled(true);
      btEncerrar.setEnabled(false);
      brPender.setEnabled(false);
      btCancelar.setEnabled(false);
      btProximo.setEnabled(true);
      btAnterior.setEnabled(true);
      btSair.setEnabled(true);
    }
  }
  
  private void Desistiu(String cdpac)
  {
    int row = TableControl.seek(clvEspera, 0, cdpac);
    
    TableControl.del(clvEspera, row);
    
    int[] tep = contapac();
    mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
    mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
    jbarra.setVisible(false);
    if ((btAtender.isEnabled()) && (mNome.getText().trim().equalsIgnoreCase("")))
    {
      String pp = proximopac();
      if (!pp.equalsIgnoreCase("")) {
        LerDadosPac(pp);
      } else {
        limpartela();
      }
      jtbbMaster.setSelectedIndex(1);
      
      jtbbLancamentos.setEnabled(false);
      
      jtxtSintomasDia.setEnabled(false);
      
      jbtRecSalvar.setEnabled(false);
      jbtRecApagar.setEnabled(false);
      jbtRecPrint.setEnabled(false);
      jtxtReceita.setEnabled(false);
      
      btAtender.setEnabled(true);
      btEncerrar.setEnabled(false);
      brPender.setEnabled(false);
      btCancelar.setEnabled(false);
      btProximo.setEnabled(true);
      btAnterior.setEnabled(true);
      btSair.setEnabled(true);
    }
  }
  
  private void Saida(String cdpac)
  {
    int mRow = TableControl.seek(clvEspera, 0, cdpac);
    
    TableControl.del(clvEspera, mRow);
    
    int[] tep = contapac();
    mEsperas.setText(FuncoesGlobais.nStrZero(tep[0], 6));
    mPendente.setText(FuncoesGlobais.nStrZero(tep[1], 6));
    jbarra.setVisible(false);
    if ((btAtender.isEnabled()) && (mNome.getText().trim().equalsIgnoreCase("")))
    {
      String pp = proximopac();
      if (!pp.equalsIgnoreCase("")) {
        LerDadosPac(pp);
      } else {
        limpartela();
      }
      jtbbMaster.setSelectedIndex(1);
      
      jtbbLancamentos.setEnabled(false);
      
      jtxtSintomasDia.setEnabled(false);
      
      jbtRecSalvar.setEnabled(false);
      jbtRecApagar.setEnabled(false);
      jbtRecPrint.setEnabled(false);
      jtxtReceita.setEnabled(false);
      
      btAtender.setEnabled(true);
      btEncerrar.setEnabled(false);
      brPender.setEnabled(false);
      btCancelar.setEnabled(false);
      btProximo.setEnabled(true);
      btAnterior.setEnabled(true);
      btSair.setEnabled(true);
    }
  }
}
