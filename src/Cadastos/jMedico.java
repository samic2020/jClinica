/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cadastos;

import Db.DbMain;
import Funcoes.LerValor;
import Funcoes.LimitedTextField;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author supervisor
 */
public class jMedico extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.con;
    boolean isnew = false;
    String sql = "SELECT md_nome, md_categoria, md_cpf, md_identidade, " + 
                 "md_crm, md_crmuf, md_codigo, " + 
                 "md_intervs, md_vrconsulta, md_vrmedico FROM medicos;";
    ResultSet urs = conn.AbrirTabela(sql, ResultSet.TYPE_SCROLL_INSENSITIVE);
    TableRowSorter<TableModel> sorter;
    
    /**
     * Creates new form jMedico
     */
    public jMedico() {
        initComponents();
        
        // Cor dos campos inativos
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(new Color(255, 255, 255)));

        mCODIGO.setVisible(false);
        
        DefaultComboBoxModel cbm = new DefaultComboBoxModel(Estados());
        mESTADO.setModel(cbm);
        
        // ComboBox
        {
            fillFuncoes(mFUNCAO);
            
            JTextField editor = (JTextField)mFUNCAO.getEditor().getEditorComponent();
            editor.setHorizontalAlignment(JTextField.CENTER);
            editor.addKeyListener( new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER && !mFUNCAO.getEditor().getItem().toString().trim().equalsIgnoreCase("")) {
                        mCRM.requestFocus();
                    }
                }
            });
            mFUNCAO.getEditor().getEditorComponent().addFocusListener( new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent evt) {
                    if (mFUNCAO.getSelectedIndex() == -1 && !((JTextField)mFUNCAO.getEditor().getEditorComponent()).getText().trim().equals("")) {
                        mFUNCAO.addItem(mFUNCAO.getEditor().getItem().toString().toUpperCase().trim());
                    }
                }
            });
        }

        try { urs.first(); } catch (SQLException e) {}
        LerMedicos();
    }

    private void fillFuncoes(JComboBox box) {
        String sql = "SELECT DISTINCT md_categoria FROM medicos WHERE Trim(md_categoria) != '' ORDER BY md_categoria;";
        ResultSet crs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                box.addItem(crs.getString("md_categoria"));
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(crs);
        
        box.setSelectedIndex(-1);
    }

    private String[] Estados() {
        String[] list = {"AC","AL","AP","AM","BA","CE","CE","DF","ES","GO",
                         "MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ",
                         "RN","RS","RO","RR","SC","SP","SE","TO"};
        return list;
    }
    
    private void LimpaTela() {
        mCODIGO.setText("");
        mNOME.setText(""); mNOME.setEditable(false); 
        mFUNCAO.setSelectedIndex(-1);
        mCPF.setText("");
        mRG.setText("");
        mCRM.setText("");
        mESTADO.setSelectedIndex(-1);
        mVRCONSULTA.setText("0,00");
        mVRMEDICO.setText("0,00");
        mINTERVALO.setText("15");
    }
    
    private void LerMedicos() {
        try { mCODIGO.setText(urs.getString("md_codigo")); } catch (SQLException e) { mCODIGO.setText("0"); }
        try {  mNOME.setText(urs.getString("md_nome")); } catch (SQLException e) { mNOME.setText(""); }
        try { mFUNCAO.setSelectedItem(urs.getString("md_categoria")); } catch (SQLException e) { mFUNCAO.setSelectedIndex( -1); }
        try { mCPF.setText(urs.getString("md_cpf")); } catch (SQLException e) { mCPF.setText(""); }
        try { mRG.setText(urs.getString("md_identidade")); } catch (SQLException e) { mRG.setText(""); }
        try { mCRM.setText(urs.getString("md_crm")); } catch (SQLException e) { mCRM.setText(""); }
        try { mESTADO.setSelectedItem(urs.getString("md_crmuf")); } catch (SQLException e) { mESTADO.setToolTipText(""); }
        try { mVRCONSULTA.setText(urs.getBigDecimal("md_vrconsulta").toString()); } catch (SQLException e) { mVRCONSULTA.setText("0,00"); }
        try { mVRMEDICO.setText(urs.getBigDecimal("md_vrmedico").toString()); } catch (SQLException e) { mVRMEDICO.setText("0,00"); }
        try { mINTERVALO.setText(urs.getString("md_intervs")); } catch (SQLException e) { mINTERVALO.setText("0"); }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        mNOME = new LimitedTextField(60);
        mFUNCAO = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        mCPF = new LimitedTextField(14);
        mRG = new LimitedTextField(20);
        mESTADO = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        mVRCONSULTA = new javax.swing.JFormattedTextField();
        mVRMEDICO = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        mINTERVALO = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btIncluir = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btPesquisar = new javax.swing.JButton();
        btRetornar = new javax.swing.JButton();
        btGravar = new javax.swing.JButton();
        mCODIGO = new javax.swing.JLabel();
        mCRM = new javax.swing.JFormattedTextField();

        setTitle("de Médicos");
        setVisible(true);

        jLabel1.setText("Nome:");

        jLabel2.setText("Função:");

        mNOME.setEditable(false);
        mNOME.setBackground(new java.awt.Color(254, 254, 254));
        mNOME.setForeground(new java.awt.Color(1, 35, 213));
        mNOME.setDisabledTextColor(new java.awt.Color(254, 254, 254));

        mFUNCAO.setEditable(true);
        mFUNCAO.setForeground(new java.awt.Color(1, 35, 213));

        jLabel3.setText("CPF:");

        jLabel4.setText("RG:");

        jLabel5.setText("CRM:");

        mCPF.setEditable(false);
        mCPF.setBackground(new java.awt.Color(255, 255, 255));
        mCPF.setForeground(new java.awt.Color(1, 35, 213));
        mCPF.setDisabledTextColor(new java.awt.Color(1, 1, 1));

        mRG.setEditable(false);
        mRG.setForeground(new java.awt.Color(1, 35, 213));

        mESTADO.setForeground(new java.awt.Color(1, 35, 213));
        mESTADO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mESTADOKeyReleased(evt);
            }
        });

        jLabel6.setText("Valor da Consulta:");

        mVRCONSULTA.setForeground(new java.awt.Color(1, 35, 213));
        mVRCONSULTA.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mVRCONSULTA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mVRCONSULTA.setText("0,00");
        mVRCONSULTA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mVRCONSULTAKeyReleased(evt);
            }
        });

        mVRMEDICO.setForeground(new java.awt.Color(1, 35, 213));
        mVRMEDICO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mVRMEDICO.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mVRMEDICO.setText("0,00");
        mVRMEDICO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mVRMEDICOKeyReleased(evt);
            }
        });

        jLabel7.setText("Valor do Médico:");

        jLabel8.setText("Intervalo de Atendimento:");

        mINTERVALO.setForeground(new java.awt.Color(1, 35, 213));
        mINTERVALO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("00"))));
        mINTERVALO.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mINTERVALO.setText("10");
        mINTERVALO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mINTERVALOKeyReleased(evt);
            }
        });

        jLabel9.setText("minutos");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        btIncluir.setText("Incluir");
        btIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIncluirActionPerformed(evt);
            }
        });

        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        btPesquisar.setText("Pesquisar");
        btPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarActionPerformed(evt);
            }
        });

        btRetornar.setText("Retornar");
        btRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRetornarActionPerformed(evt);
            }
        });

        btGravar.setText("Gravar");
        btGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGravarActionPerformed(evt);
            }
        });

        mCODIGO.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72)
                .addComponent(mCODIGO, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRetornar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mCODIGO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btIncluir)
                            .addComponent(btExcluir)
                            .addComponent(btPesquisar)
                            .addComponent(btRetornar)
                            .addComponent(btGravar))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        try {
            mCRM.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#######-#")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mNOME)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFUNCAO, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mRG)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCRM, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(13, 13, 13)
                        .addComponent(mVRCONSULTA, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mVRMEDICO, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mINTERVALO, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(mNOME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mFUNCAO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(mCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mRG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCRM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(mVRCONSULTA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(mVRMEDICO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(mINTERVALO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed
        String tsql = "";
        if (isnew) {
            tsql = "INSERT INTO medicos(md_nome, md_categoria, md_cpf, md_identidade, " + 
                   "md_crm, md_crmuf, md_codigo, md_intervs, md_vrconsulta, md_vrmedico) " + 
                   "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
            tsql = String.format(tsql, 
                    mNOME.getText(), 
                    mFUNCAO.getSelectedItem().toString(),
                    mCPF.getText(), 
                    mRG.getText(),
                    mCRM.getText(),
                    mESTADO.getSelectedItem().toString(),
                    mCODIGO.getText(),
                    mINTERVALO.getText(),
                    LerValor.StringToFloat(mVRCONSULTA.getText()),
                    LerValor.StringToFloat(mVRMEDICO.getText())
            );
        } else {
            tsql = "UPDATE medicos SET md_categoria = '%s', md_crm = '%s', md_crmuf = '%s', " +
                   "md_intervs = '%s', md_vrconsulta = '%s', md_vrmedico = '%s' WHERE md_codigo = '%s';";
            tsql = String.format(tsql, 
                    mFUNCAO.getSelectedItem().toString(),
                    mCRM.getText(),
                    mESTADO.getSelectedItem().toString(),
                    mINTERVALO.getText(),
                    LerValor.StringToFloat(mVRCONSULTA.getText()),
                    LerValor.StringToFloat(mVRMEDICO.getText()),
                    mCODIGO.getText()
            );
        }
        try { conn.ExecutarComando(tsql); } catch (Exception e) {}
        
        if (isnew) {
            tsql = "INSERT INTO medconv(mc_crm, mc_matricula, mc_plano, " +
                   "mc_segunda, mc_terca, mc_quarta, mc_quinta, mc_sexta, mc_sabado, mc_domingo, " +
                   "mc_valper, mc_medico, mc_medcod, mc_comissao) " + 
                    "VALUES ('%s','%s','PARTUCULAR','1','1','1','1','1','1','1','1','%s','%s','10')";
            tsql = String.format(tsql, 
                    mCRM.getText(),
                    mCRM.getText(),
                    mNOME.getText(),
                    mCODIGO.getText()
            );
        } else {
            tsql = "UPDATE medconv SET mc_crm = '%s', mc_matricula = '%s' WHERE mc_medcod = '%s';";
            tsql = String.format(tsql,
                    mCRM.getText(),
                    mCRM.getText(),
                    mCODIGO.getText()
            );
        }
        try { conn.ExecutarComando(tsql); } catch (Exception e) {}
        
        isnew = false;
        btIncluir.setEnabled(true);
        btExcluir.setEnabled(true);
        btPesquisar.setEnabled(true);
        btGravar.setEnabled(true);
        btRetornar.setEnabled(true);
    }//GEN-LAST:event_btGravarActionPerformed

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        String tsql = "SELECT f_cod, f_nome from cadfun WHERE lower(f_funcao) LIKE '%medico%' ORDER BY LOWER(f_nome);";
        ResultSet rs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
        ArrayList<String> cdMed = new ArrayList();
        ArrayList<String> nmMed = new ArrayList();
        try {
            while (rs.next()) {
                cdMed.add(rs.getString("f_cod"));
                nmMed.add(rs.getString("f_nome").toUpperCase());
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
        
        JPanel pn = new JPanel();
        final JComboBox jcbcdMed = new JComboBox(cdMed.toArray());
        jcbcdMed.setEditable(false); jcbcdMed.setSize(67, 25);
        final JComboBox jcbnmMedico = new JComboBox(nmMed.toArray());
        jcbnmMedico.setEditable(false); jcbnmMedico.setSize(200,25);

        jcbcdMed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!VariaveisGlobais.iswork) {
                    VariaveisGlobais.iswork = true;
                    jcbnmMedico.setSelectedIndex(jcbcdMed.getSelectedIndex());
                } else  VariaveisGlobais.iswork = false;
            }
        });
        jcbnmMedico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!VariaveisGlobais.iswork) {
                    VariaveisGlobais.iswork = true;
                    jcbcdMed.setSelectedIndex(jcbnmMedico.getSelectedIndex());
                } else VariaveisGlobais.iswork = false;
            }
        });
        pn.add(jcbcdMed); pn.add(jcbnmMedico);
        
        if (JOptionPane.showConfirmDialog(null, pn, "Selecione o Médico abaixo:", YES_NO_OPTION) == YES_OPTION) {
            //System.out.println(jcbcdMed.getSelectedItem());
            isnew = true;
            btIncluir.setEnabled(false);
            btExcluir.setEnabled(false);
            btPesquisar.setEnabled(false);
            btGravar.setEnabled(true);
            btRetornar.setEnabled(true);
                    
            LimpaTela();
            String[][] campos = null;
            try {
                campos = conn.LerCamposTabela(new String[] {"f_cod","f_nome","f_cpf","f_rg"}, "cadfun", "f_cod = '" + jcbcdMed.getSelectedItem().toString() + "'");
            } catch (SQLException e) {}
            if (campos != null) {
                mCODIGO.setText(campos[0][3]);
                mNOME.setText(campos[1][3]);
                mCPF.setText(campos[2][3]);
                mRG.setText(campos[3][3]);
            }
            mFUNCAO.requestFocus();
        } else {
            isnew = false;
            btIncluir.setEnabled(true);
            btExcluir.setEnabled(true);
            btPesquisar.setEnabled(true);
            btGravar.setEnabled(true);
            btRetornar.setEnabled(true);
        }
    }//GEN-LAST:event_btIncluirActionPerformed

    private void mESTADOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mESTADOKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && mESTADO.getSelectedIndex() != -1) {
            mVRCONSULTA.requestFocus();
        }
    }//GEN-LAST:event_mESTADOKeyReleased

    private void mVRCONSULTAKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mVRCONSULTAKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && !(mVRCONSULTA.getText().equalsIgnoreCase("") || mVRCONSULTA.getText().equalsIgnoreCase("0,00"))) {
            mVRMEDICO.requestFocus();
        }
    }//GEN-LAST:event_mVRCONSULTAKeyReleased

    private void mVRMEDICOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mVRMEDICOKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && !(mVRMEDICO.getText().equalsIgnoreCase("") || mVRMEDICO.getText().equalsIgnoreCase("0,00"))) {
            mINTERVALO.requestFocus();
        }
    }//GEN-LAST:event_mVRMEDICOKeyReleased

    private void mINTERVALOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mINTERVALOKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && LerValor.StringToFloat(mINTERVALO.getText()) >= 10) {
            btGravar.requestFocus();
        }
    }//GEN-LAST:event_mINTERVALOKeyReleased

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        if (!mCODIGO.getText().trim().equalsIgnoreCase("")) {
            if (JOptionPane.showConfirmDialog(this, "Este registro vai ser excluido!\n\nConfirma exclusão?", "Excluir", YES_NO_OPTION) == YES_OPTION) {
                try {
                    conn.ExecutarComando("DELETE FROM medicos WHERE md_codigo = '" + mCODIGO.getText() + "';");
                    conn.ExecutarComando("DELETE FROM medconv WHERE mc_medcod = '" + mCODIGO.getText() + "';");
                } catch (Exception e) {}
                try {urs.first();} catch (SQLException e) {}
                LerMedicos();
            }
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void btRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRetornarActionPerformed
        if (isnew) {
            if (JOptionPane.showConfirmDialog(this, "Você esta inserindo dados!\n\nCancela operação?","Cancelar",YES_NO_OPTION) == YES_OPTION) {
                LimpaTela();
                LerMedicos();

                isnew = false;
                btIncluir.setEnabled(true);
                btExcluir.setEnabled(true);
                btPesquisar.setEnabled(true);
                btGravar.setEnabled(true);
                btRetornar.setEnabled(true);
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_btRetornarActionPerformed

    private void btPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarActionPerformed
        String tsql = "SELECT md_codigo, md_nome, md_categoria FROM medicos ORDER BY Lower(md_nome);";
        ResultSet trs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
        final JTable tbl = new JTable();
        tbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    int trow = tbl.getSelectedRow();
                    int modelRow = tbl.convertRowIndexToModel(trow);
                    String tficha = tbl.getModel().getValueAt(modelRow, 0).toString();
                    BuscarMedico(tficha);
                }
            }
        });
        
        String[][] aheader = { { "codigo", "Medico", "Especialidade" }, { "60", "380", "150" } };
        TableControl.header(tbl, aheader);
        TableControl.delall(tbl);
        
        String mcod = null, mnome = null, mfuncao = null;
        try {
            while (trs.next()) {
                try { mcod = trs.getString("md_codigo"); } catch (SQLException e) {}
                try { mnome = trs.getString("md_nome"); } catch (SQLException e) {}
                try { mfuncao = trs.getString("md_categoria"); } catch (SQLException e) {}
                TableControl.add(tbl, new String[][] { { mcod, mnome, mfuncao }, { "C", "L", "L" } }, true);
                sorter = new TableRowSorter(tbl.getModel());
                tbl.setRowSorter(sorter);
            }
        } catch (SQLException e) {}
        try {trs.close();} catch (SQLException e) {}
        tbl.setSize(300,200);
        
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(tbl);
        jLabel1 = new JLabel();
        jLabel1.setText("Buscar:");
        final JTextField jbuscar = new JTextField();
        jbuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if ("".equals(jbuscar.getText().trim())) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(
                               RowFilter.regexFilter(jbuscar.getText().trim()));
                    } catch (PatternSyntaxException pse) {
                       System.err.println("Bad regex pattern");
                    }
                }
            }

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }
        });
        jbuscar.requestFocus();
        
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        //getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbuscar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jbuscar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel.setLayout(layout);
        Object[] options = { "Ok", "Cancelar" };
        int opc = JOptionPane.showOptionDialog(this, panel,"Selecione o médico!",JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,options, options[0]);
        if (opc == JOptionPane.YES_OPTION) {
            int trow = tbl.getSelectedRow();
            int modelRow = tbl.convertRowIndexToModel(trow);
            String tficha = tbl.getModel().getValueAt(modelRow, 0).toString();
            BuscarMedico(tficha);
        }
    }//GEN-LAST:event_btPesquisarActionPerformed

    private void BuscarMedico(String mdcodigo) {
        try {urs.first();} catch (SQLException e) {}
        try {
            while (urs.next()) {
                if (urs.getString("md_codigo").equalsIgnoreCase(mdcodigo)) {
                    LerMedicos();
                    break;
                }
            }
        } catch (SQLException e) {}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btGravar;
    private javax.swing.JButton btIncluir;
    private javax.swing.JButton btPesquisar;
    private javax.swing.JButton btRetornar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel mCODIGO;
    private javax.swing.JTextField mCPF;
    private javax.swing.JFormattedTextField mCRM;
    private javax.swing.JComboBox mESTADO;
    private javax.swing.JComboBox mFUNCAO;
    private javax.swing.JFormattedTextField mINTERVALO;
    private javax.swing.JTextField mNOME;
    private javax.swing.JTextField mRG;
    private javax.swing.JFormattedTextField mVRCONSULTA;
    private javax.swing.JFormattedTextField mVRMEDICO;
    // End of variables declaration//GEN-END:variables
}
