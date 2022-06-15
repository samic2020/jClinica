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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
                 "md_intervs, md_vrconsulta, md_vrmedico, md_plantao, md_plantaovr, md_cbo, md_perc FROM medicos;";
    ResultSet urs = conn.AbrirTabela(sql, ResultSet.TYPE_SCROLL_INSENSITIVE);
    TableRowSorter<TableModel> sorter;
    boolean bInc = false;
    boolean bAlt = false;
    
    /**
     * Creates new form jMedico
     */
    public jMedico() {
        initComponents();
        
        // Cor dos campos inativos
        //UIManager.put("TextField.inactiveBackground", new ColorUIResource(new Color(255, 255, 255)));

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

        try { 
            if (urs.first()) {
                LerMedicos();                
            } else {
                EnDispnlMedTaxas(false);
            }
        } catch (SQLException e) {}        
        
        mtbtnIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incMedConv();
            }
        });
        
        mtbtnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altMedConv();
            }
        });
        
        mtbtnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excMedConv();
            }
        });
        
        mtbtnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gvaMedConv();
            }
        });
        
        mtbtnRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retMedConv();
            }
        });
        
        mtcbxConvenios.removeAllItems();
        
        //this.setBackground(new Color(240, 240, 240));
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
        mCBO.setText("");
        mVRCONSULTA.setText("0,00");
        mVRMEDICO.setText("0,00");
        mINTERVALO.setText("15");
        mPlantao.setSelected(false);
        mttxbValorPlantao.setText("0,00");
        mttxbPercMed.setText("0,00");
    }
    
    private void LerMedicos() {
        try { mCODIGO.setText(urs.getString("md_codigo")); } catch (SQLException e) { mCODIGO.setText("0"); }
        try { mNOME.setText(urs.getString("md_nome")); } catch (SQLException e) { mNOME.setText(""); }
        try { mFUNCAO.setSelectedItem(urs.getString("md_categoria")); } catch (SQLException e) { mFUNCAO.setSelectedIndex( -1); }
        try { mCPF.setText(urs.getString("md_cpf")); } catch (SQLException e) { mCPF.setText(""); }
        try { mRG.setText(urs.getString("md_identidade")); } catch (SQLException e) { mRG.setText(""); }
        try { mCRM.setText(urs.getString("md_crm")); } catch (SQLException e) { mCRM.setText(""); }
        try { mESTADO.setSelectedItem(urs.getString("md_crmuf")); } catch (SQLException e) { mESTADO.setToolTipText(""); }
        try { mCBO.setText(urs.getString("md_cbo")); } catch (SQLException e) { mCBO.setText(""); }
        try { mVRCONSULTA.setText(urs.getBigDecimal("md_vrconsulta").toPlainString().toString().replace(".", ",")); } catch (SQLException e) { mVRCONSULTA.setText("0,00"); }
        try { mVRMEDICO.setText(urs.getBigDecimal("md_vrmedico").toPlainString().toString().replace(".", ",")); } catch (SQLException e) { mVRMEDICO.setText("0,00"); }
        try { mINTERVALO.setText(urs.getString("md_intervs")); } catch (SQLException e) { mINTERVALO.setText("0"); }
        try { mPlantao.setSelected(urs.getBoolean("md_plantao")); } catch (SQLException e) { mPlantao.setSelected(false); }
        try { mttxbValorPlantao.setText(urs.getString("md_plantaovr").replace(".", ",")); } catch (SQLException e) { mttxbValorPlantao.setText("0,00"); }
        try { mttxbPercMed.setText(urs.getString("md_perc").replace(".", ",")); } catch (SQLException e) { mttxbPercMed.setText("0,00"); }
        
        OnpnlMedTaxas();
        LerConvenios(Integer.valueOf(mCODIGO.getText()));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btIncluir = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btPesquisar = new javax.swing.JButton();
        btRetornar = new javax.swing.JButton();
        btGravar = new javax.swing.JButton();
        mCODIGO = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        mVRCONSULTA = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        mVRMEDICO = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        mINTERVALO = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        pnlMedTaxas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mtbvConvenios = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        mtcbxConvenios = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        mttxbValor = new javax.swing.JTextField();
        mttxbValorMed = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        mtbtnIncluir = new javax.swing.JButton();
        mtbtnAlterar = new javax.swing.JButton();
        mtbtnExcluir = new javax.swing.JButton();
        mtbtnGravar = new javax.swing.JButton();
        mtbtnRetornar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        mPlantao = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        mttxbValorPlantao = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mNOME = new LimitedTextField(60);
        jLabel2 = new javax.swing.JLabel();
        mFUNCAO = new javax.swing.JComboBox();
        mCBO = new LimitedTextField(14);
        jLabel15 = new javax.swing.JLabel();
        mESTADO = new javax.swing.JComboBox();
        mCRM = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        mRG = new LimitedTextField(20);
        jLabel4 = new javax.swing.JLabel();
        mCPF = new LimitedTextField(14);
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        mttxbPercMed = new javax.swing.JTextField();

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(101, 227, 255));
        setForeground(java.awt.Color.black);
        setTitle("Cadastro de Médicos");
        setMaximumSize(new java.awt.Dimension(656, 496));
        setMinimumSize(new java.awt.Dimension(656, 496));
        setOpaque(true);
        setPreferredSize(new java.awt.Dimension(656, 496));
        setVisible(true);

        jPanel1.setBackground(new java.awt.Color(101, 227, 255));
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

        mCODIGO.setBackground(new java.awt.Color(101, 227, 255));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
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

        jPanel2.setBackground(new java.awt.Color(101, 227, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "[ Particular ]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel2.setEnabled(false);

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

        jLabel7.setText("Valor do Médico:");

        mVRMEDICO.setForeground(new java.awt.Color(1, 35, 213));
        mVRMEDICO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mVRMEDICO.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mVRMEDICO.setText("0,00");
        mVRMEDICO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mVRMEDICOKeyReleased(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mVRCONSULTA, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mVRMEDICO, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mINTERVALO, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(mVRCONSULTA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7)
                .addComponent(mVRMEDICO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel8)
                .addComponent(mINTERVALO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel9))
        );

        pnlMedTaxas.setBackground(new java.awt.Color(101, 227, 255));
        pnlMedTaxas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "[ Taxas Medico/Convenios ]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(102, 102, 255))); // NOI18N

        mtbvConvenios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "cdmedico", "cdconvenio", "nmconvenio", "valor", "valormed"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(mtbvConvenios);

        jLabel10.setText("Convenio:");

        mtcbxConvenios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel11.setText("Valor:");

        mttxbValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mttxbValor.setText("0,00");

        mttxbValorMed.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mttxbValorMed.setText("0,00");

        jLabel12.setText("Valor Medico:");

        mtbtnIncluir.setText("Incluir");

        mtbtnAlterar.setText("Alterar");

        mtbtnExcluir.setText("Excluir");

        mtbtnGravar.setText("Gravar");
        mtbtnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtbtnGravarActionPerformed(evt);
            }
        });

        mtbtnRetornar.setText("Cancelar");

        jLabel13.setBackground(java.awt.SystemColor.activeCaption);
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("CONVENIOS");
        jLabel13.setOpaque(true);

        javax.swing.GroupLayout pnlMedTaxasLayout = new javax.swing.GroupLayout(pnlMedTaxas);
        pnlMedTaxas.setLayout(pnlMedTaxasLayout);
        pnlMedTaxasLayout.setHorizontalGroup(
            pnlMedTaxasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMedTaxasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMedTaxasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlMedTaxasLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mtcbxConvenios, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mttxbValor, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mttxbValorMed, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMedTaxasLayout.createSequentialGroup()
                        .addComponent(mtbtnIncluir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mtbtnAlterar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mtbtnExcluir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mtbtnGravar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mtbtnRetornar)))
                .addContainerGap())
        );
        pnlMedTaxasLayout.setVerticalGroup(
            pnlMedTaxasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMedTaxasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlMedTaxasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(mtcbxConvenios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(mttxbValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(mttxbValorMed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMedTaxasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mtbtnIncluir)
                    .addComponent(mtbtnAlterar)
                    .addComponent(mtbtnExcluir)
                    .addComponent(mtbtnGravar)
                    .addComponent(mtbtnRetornar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(101, 227, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("[ Plantonista ]"));

        mPlantao.setBackground(new java.awt.Color(101, 227, 255));
        mPlantao.setText("Este médico é Plantonista");

        jLabel14.setText("Valor do Plantão:");

        mttxbValorPlantao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mttxbValorPlantao.setText("0,00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mPlantao)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mttxbValorPlantao, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mPlantao)
                    .addComponent(jLabel14)
                    .addComponent(mttxbValorPlantao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBackground(new java.awt.Color(101, 227, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nome:");

        mNOME.setBackground(new java.awt.Color(254, 254, 254));
        mNOME.setForeground(new java.awt.Color(1, 35, 213));
        mNOME.setDisabledTextColor(new java.awt.Color(254, 254, 254));

        jLabel2.setText("Função:");

        mFUNCAO.setEditable(true);
        mFUNCAO.setForeground(new java.awt.Color(1, 35, 213));

        mCBO.setForeground(new java.awt.Color(1, 35, 213));
        mCBO.setDisabledTextColor(new java.awt.Color(1, 1, 1));

        jLabel15.setText("CBO:");

        mESTADO.setForeground(new java.awt.Color(1, 35, 213));
        mESTADO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mESTADOKeyReleased(evt);
            }
        });

        try {
            mCRM.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#######-#")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel5.setText("CRM:");

        mRG.setForeground(new java.awt.Color(1, 35, 213));

        jLabel4.setText("RG:");

        mCPF.setForeground(new java.awt.Color(1, 35, 213));
        mCPF.setDisabledTextColor(new java.awt.Color(1, 1, 1));

        jLabel3.setText("CPF:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mNOME)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mFUNCAO, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCPF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mRG, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCRM, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(mESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCBO, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(mNOME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mFUNCAO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(mCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mRG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mESTADO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCRM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCBO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(101, 227, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("[ Percentual Faturados ]"));

        jLabel16.setText("Valor do Médico:");

        mttxbPercMed.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mttxbPercMed.setText("0,00");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(mttxbPercMed, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mttxbPercMed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMedTaxas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMedTaxas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed
        String tsql = ""; 
        Object[][] param = {};
        if (isnew) {
//            tsql = "INSERT INTO medicos(md_nome, md_categoria, md_cpf, md_identidade, " + 
//                   "md_crm, md_crmuf, md_codigo, md_intervs, md_vrconsulta, md_vrmedico) " + 
//                   "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
            tsql = "INSERT INTO medicos(md_nome, md_categoria, md_cpf, md_identidade, " + 
                   "md_crm, md_crmuf, md_codigo, md_intervs, md_vrconsulta, md_vrmedico, " + 
                   "md_plantao, md_plantaovr, md_cbo, md_perc) " + 
                   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            param = new Object[][] {
                {"string",mNOME.getText()},
                {"string",mFUNCAO.getSelectedItem().toString()},
                {"string",mCPF.getText()},
                {"string",mRG.getText()},
                {"string", mCRM.getText()},
                {"string",mESTADO.getSelectedItem().toString()},
                {"int",Integer.valueOf(mCODIGO.getText())},
                {"int",Integer.valueOf(mINTERVALO.getText())},
                {"decimal", new BigDecimal(mVRCONSULTA.getText().replace(".", "").replace(",", "."))},
                {"decimal", new BigDecimal(mVRMEDICO.getText().replace(".", "").replace(",", "."))},
                {"boolean", mPlantao.isSelected()},
                {"decimal", new BigDecimal(mttxbValorPlantao.getText().replace(".", "").replace(",", "."))},
                {"string", mCBO.getText()},
                {"decimal", new BigDecimal(mttxbPercMed.getText().replace(".", "").replace(",", "."))}
            };
//            tsql = String.format(tsql, 
//                    mNOME.getText(), 
//                    mFUNCAO.getSelectedItem().toString(),
//                    mCPF.getText(), 
//                    mRG.getText(),
//                    mCRM.getText(),
//                    mESTADO.getSelectedItem().toString(),
//                    mCODIGO.getText(),
//                    mINTERVALO.getText(),
//                    LerValor.StringToFloat(mVRCONSULTA.getText()),
//                    LerValor.StringToFloat(mVRMEDICO.getText())
//            );
        } else {
//            tsql = "UPDATE medicos SET md_categoria = '%s', md_crm = '%s', md_crmuf = '%s', " +
//                   "md_intervs = '%s', md_vrconsulta = '%s', md_vrmedico = '%s' WHERE md_codigo = '%s';";
            tsql = "UPDATE medicos SET md_categoria = ?, md_crm = ?, md_crmuf = ?, " +
                   "md_intervs = ?, md_vrconsulta = ?, md_vrmedico = ?, " + 
                   "md_plantao = ?, md_plantaovr = ?, md_cbo = ?, md_perc = ? WHERE md_codigo = ?;";
            param = new Object[][] {
                {"string",mFUNCAO.getSelectedItem().toString()},
                {"string",mCRM.getText()},
                {"string","" + mESTADO.getSelectedItem().toString()},
                {"int",Integer.valueOf(mINTERVALO.getText())},
                {"decimal", new BigDecimal(mVRCONSULTA.getText().replace(".", "").replace(",", "."))},
                {"decimal", new BigDecimal(mVRMEDICO.getText().replace(".", "").replace(",", "."))},
                {"boolean", mPlantao.isSelected()},
                {"decimal", new BigDecimal(mttxbValorPlantao.getText().replace(".", "").replace(",", "."))},
                {"string", mCBO.getText()},
                {"decimal", new BigDecimal(mttxbPercMed.getText().replace(".", "").replace(",", "."))},
                {"int",Integer.valueOf(mCODIGO.getText())}
            };
//            tsql = String.format(tsql, 
//                    mFUNCAO.getSelectedItem().toString(),
//                    mCRM.getText(),
//                    "" + mESTADO.getSelectedItem().toString(),
//                    mINTERVALO.getText(),
//                    LerValor.StringToFloat(mVRCONSULTA.getText()),
//                    LerValor.StringToFloat(mVRMEDICO.getText()),
//                    mCODIGO.getText()
//            );
        }
        try { conn.ExecutarComando(tsql, param); } catch (Exception e) {}
        
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
        try {urs.refreshRow();} catch (Exception e) {}
        
        isnew = false;
        btIncluir.setEnabled(true);
        btExcluir.setEnabled(true);
        btPesquisar.setEnabled(true);
        btGravar.setEnabled(true);
        btRetornar.setEnabled(true);

        mtbtnIncluir.setEnabled(true);
        mtbtnAlterar.setEnabled(false);
        mtbtnExcluir.setEnabled(false);
        mtbtnGravar.setEnabled(false);
        mtbtnRetornar.setEnabled(false);
    }//GEN-LAST:event_btGravarActionPerformed

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        mtbtnIncluir.setEnabled(false);
        mtbtnAlterar.setEnabled(false);
        mtbtnExcluir.setEnabled(false);
        mtbtnGravar.setEnabled(false);
        mtbtnRetornar.setEnabled(false);
        
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
                
                mtbtnIncluir.setEnabled(true);
                mtbtnAlterar.setEnabled(false);
                mtbtnExcluir.setEnabled(false);
                mtbtnGravar.setEnabled(false);
                mtbtnRetornar.setEnabled(false);
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

    private void mtbtnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mtbtnGravarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mtbtnGravarActionPerformed

    private void BuscarMedico(String mdcodigo) {
        try {urs.beforeFirst();} catch (SQLException e) {}
        
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField mCBO;
    private javax.swing.JLabel mCODIGO;
    private javax.swing.JTextField mCPF;
    private javax.swing.JFormattedTextField mCRM;
    private javax.swing.JComboBox mESTADO;
    private javax.swing.JComboBox mFUNCAO;
    private javax.swing.JFormattedTextField mINTERVALO;
    private javax.swing.JTextField mNOME;
    private javax.swing.JCheckBox mPlantao;
    private javax.swing.JTextField mRG;
    private javax.swing.JFormattedTextField mVRCONSULTA;
    private javax.swing.JFormattedTextField mVRMEDICO;
    private javax.swing.JButton mtbtnAlterar;
    private javax.swing.JButton mtbtnExcluir;
    private javax.swing.JButton mtbtnGravar;
    private javax.swing.JButton mtbtnIncluir;
    private javax.swing.JButton mtbtnRetornar;
    private javax.swing.JTable mtbvConvenios;
    private javax.swing.JComboBox<String> mtcbxConvenios;
    private javax.swing.JTextField mttxbPercMed;
    private javax.swing.JTextField mttxbValor;
    private javax.swing.JTextField mttxbValorMed;
    private javax.swing.JTextField mttxbValorPlantao;
    private javax.swing.JPanel pnlMedTaxas;
    // End of variables declaration//GEN-END:variables

    private void EnDispnlMedTaxas(boolean value) {
        pnlMedTaxas.setEnabled(value);
                
        mtcbxConvenios.setEnabled(value);
        mttxbValor.setEnabled(value);
        mttxbValorMed.setEnabled(value);
        
        mtbtnIncluir.setEnabled(value);
        mtbtnAlterar.setEnabled(value);
        mtbtnExcluir.setEnabled(value);
        mtbtnGravar.setEnabled(value);
        mtbtnRetornar.setEnabled(value);
        
        mtbvConvenios.setEnabled(value);
    }
    
    private void OnpnlMedTaxas() {
        pnlMedTaxas.setEnabled(true);
        
        mtcbxConvenios.setEnabled(false);
        mttxbValor.setEnabled(false); mttxbValor.setText("0,00");
        mttxbValorMed.setEnabled(false); mttxbValorMed.setText("0,00");
        
        mtbtnIncluir.setEnabled(true);
        mtbtnAlterar.setEnabled(false);
        mtbtnExcluir.setEnabled(false);
        mtbtnGravar.setEnabled(false);
        mtbtnRetornar.setEnabled(false);
        
        mtbvConvenios.setEnabled(true);
    }
    
    private void LerConvenios(int cdMedico) {
        TableControl.Clear(mtbvConvenios);
        boolean bTemReg = false;
        
        String[][] aheader3 = { { "id", "cdmedico", "cdconvenio", "nmconvenio", "valor", "valormed" }, { "0", "0", "0", "200", "80", "80" } };
        TableControl.header(mtbvConvenios, aheader3, new boolean[] {false, false, false, false, false, false});
        
        String selectSQL = "select mt_id, mt_cdmedico, mt_cdconvenio, (select cv_apelido from convenios where cv_numero = mt_cdconvenio) mt_nmconvenio, mt_valor, mt_valormed from medtaxas where mt_cdmedico = ?;";
        ResultSet rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY,new Object[][] {{"int", cdMedico}});
        try {
            while (rs.next()) {
                bTemReg = true;
                
                String qid = null; try { qid = rs.getString("mt_id"); } catch (SQLException e) {}
                String qcdmedico = null; try { qcdmedico = rs.getString("mt_cdmedico"); } catch (SQLException e) {}
                String qcdconvenio = null; try { qcdconvenio = rs.getString("mt_cdconvenio"); } catch (SQLException e) {}
                String qnmconvenio = null; try { qnmconvenio = rs.getString("mt_nmconvenio"); } catch (SQLException e) {}
                String qvalor = null; try { qvalor = String.valueOf(rs.getBigDecimal("mt_valor")); } catch (SQLException e) {}
                String qvalormed = null; try { qvalormed = String.valueOf(rs.getBigDecimal("mt_valormed")); } catch (SQLException e) {}
                
                TableControl.add(mtbvConvenios, new String[][] { {qid, qcdmedico, qcdconvenio, qnmconvenio, qvalor, qvalormed  }, { "C", "C", "C", "L", "R", "R" } }, true);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}        

        mtbvConvenios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                mtbtnIncluir.setEnabled(true);
                mtbtnAlterar.setEnabled(true);
                mtbtnExcluir.setEnabled(true);
                mtbtnGravar.setEnabled(false);
                mtbtnRetornar.setEnabled(false);
            }
        });
        OnpnlMedTaxas();
    }
    
    private void fillConvenios() {
        mtcbxConvenios.removeAllItems();
        String selectSQL = "SELECT cv_numero, cv_apelido FROM convenios ORDER BY Upper(Trim(cv_apelido));";
        ResultSet rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String qcdconvenio = null; try { qcdconvenio = rs.getString("cv_numero"); } catch (SQLException e) {}
                String qnmconvenio = null; try { qnmconvenio = rs.getString("cv_apelido"); } catch (SQLException e) {}
                
                mtcbxConvenios.addItem(qcdconvenio + " - " + qnmconvenio);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
    }

    private void incMedConv() {
        bInc = true; bAlt = false;
        
        // Botões painel principal
        btIncluir.setEnabled(false);
        btExcluir.setEnabled(false);
        btPesquisar.setEnabled(false);
        btGravar.setEnabled(false);
        btRetornar.setEnabled(false);
        
        // Botões painel MedTaxas
        mtbtnIncluir.setEnabled(false);
        mtbtnAlterar.setEnabled(false);
        mtbtnExcluir.setEnabled(false);
        mtbtnGravar.setEnabled(true);
        mtbtnRetornar.setEnabled(true);
        mtbvConvenios.setEnabled(false);
        
        fillConvenios();
        mtcbxConvenios.setEnabled(true);
        mtcbxConvenios.setSelectedIndex(0);
        
        mttxbValor.setEnabled(true);
        mttxbValor.setText("0,00");
        
        mttxbValorMed.setEnabled(true);
        mttxbValorMed.setText("0,00");

        mttxbPercMed.setEnabled(true);
        mttxbPercMed.setText("0,00");
                
        mtcbxConvenios.requestFocus();
    }

    private void altMedConv() {
        bInc = false; bAlt = true;
        
        // Botões painel principal
        btIncluir.setEnabled(false);
        btExcluir.setEnabled(false);
        btPesquisar.setEnabled(false);
        btGravar.setEnabled(false);
        btRetornar.setEnabled(false);
        
        // Botões painel MedTaxas
        mtbtnIncluir.setEnabled(false);
        mtbtnAlterar.setEnabled(false);
        mtbtnExcluir.setEnabled(false);
        mtbtnGravar.setEnabled(true);
        mtbtnRetornar.setEnabled(true);
        mtbvConvenios.setEnabled(false);
        
        // Pega itens no tableview
        //String qid = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 0).toString();
        //String qcdmedico = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 1).toString();
        String qcdconvenio = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 2).toString();
        String qnmconvenio = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 3).toString();
        String qvalor = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 4).toString();
        String qvalormed = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 5).toString();
        
        mtcbxConvenios.removeAllItems();
        mtcbxConvenios.addItem(qcdconvenio + " - " + qnmconvenio);
        mtcbxConvenios.setEnabled(true);
        
        mttxbValor.setEnabled(true);
        mttxbValor.setText(qvalor);
        
        mttxbValorMed.setEnabled(true);
        mttxbValorMed.setText(qvalormed);
        
        mttxbValor.selectAll();
        mttxbValor.requestFocus();
    }

    private void excMedConv() {
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(this, "Exclui a a ligação Medico x Convenio selecionada?", "Atenção", 0, 3, null, options, options[0]);
        if (n == 0) {
            // Pega id no tableview
            String qid = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 0).toString();
            if (conn.ExecutarComando("DELETE FROM medtaxas WHERE mt_id = ?;", new Object[][] {{"int", Integer.valueOf(qid)}}) > 0) {
                JOptionPane.showMessageDialog(null, "Item excluido com sucesso!","Atenção",INFORMATION_MESSAGE);
                LerConvenios(Integer.valueOf(mCODIGO.getText()));
            }
        }
    }
    
    private void gvaMedConv() {
        Object[][] param = {};
        String querySQL = "";
        
        // Pega itens no tableview
        String qid = "";
        String qcdmedico = "";
        String qcdconvenio = "";
        String qnmconvenio = "";
        
        // Itens da tela
        BigDecimal qvalor = null;
        BigDecimal qvalormed = null;

        if (bInc) {
            qcdconvenio = mtcbxConvenios.getSelectedItem().toString().substring(0, mtcbxConvenios.getSelectedItem().toString().indexOf("-") - 1);
            int pos = -1;
            pos = TableControl.seek(mtbvConvenios, 2, qcdconvenio);
            if (pos > -1) {
                JOptionPane.showMessageDialog(null, "Item já lançado!","Atenção",INFORMATION_MESSAGE);
                return;
            }
        }
        
        if (bInc || bAlt) {
            qcdmedico = mCODIGO.getText();
            qvalor = new BigDecimal(mttxbValor.getText().replace(",", "."));
            qvalormed = new BigDecimal(mttxbValorMed.getText().replace(",", "."));

            if (bInc) {
                qcdconvenio = mtcbxConvenios.getSelectedItem().toString().substring(0, mtcbxConvenios.getSelectedItem().toString().indexOf("-") - 1);
                querySQL = "INSERT INTO medtaxas(mt_cdmedico, mt_cdconvenio, mt_valor, mt_valormed) VALUES (?, ?, ?, ?);";
                param = new Object[][] {
                    {"int", Integer.valueOf(qcdmedico)},
                    {"int", Integer.valueOf(qcdconvenio)},
                    {"decimal", qvalor},
                    {"decimal", qvalormed}
                };
            }
            
            if (bAlt) {
                qid = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 0).toString();
                qcdconvenio = mtbvConvenios.getValueAt(mtbvConvenios.getSelectedRow(), 2).toString();
                querySQL = "UPDATE medtaxas SET mt_valor=?, mt_valormed=? WHERE mt_id = ?;";
                param = new Object[][] {
                    {"decimal", qvalor},
                    {"decimal", qvalormed},
                    {"int", Integer.valueOf(qid)}
                };
            }
            
            if (conn.ExecutarComando(querySQL, param) > 0) {
                JOptionPane.showMessageDialog(null, "Item salvo com sucesso!","Atenção",INFORMATION_MESSAGE);
                LerConvenios(Integer.valueOf(mCODIGO.getText()));
            }
            
            bInc = false;
            bAlt = false;

            // Botões painel principal
            btIncluir.setEnabled(true);
            btExcluir.setEnabled(true);
            btPesquisar.setEnabled(true);
            btGravar.setEnabled(true);
            btRetornar.setEnabled(true);
        
            // Botões painel MedTaxas
            mtbtnIncluir.setEnabled(true);
            mtbtnAlterar.setEnabled(mtbvConvenios.getRowCount() > 0);
            mtbtnExcluir.setEnabled(mtbvConvenios.getRowCount() > 0);
            mtbtnGravar.setEnabled(false);
            mtbtnRetornar.setEnabled(false);
            mtbvConvenios.setEnabled(true);
            
            mttxbValor.setEnabled(false);
            mttxbValor.setText("0,00");       
            mttxbValorMed.setEnabled(false);
            mttxbValorMed.setText("0,00");            
            mtcbxConvenios.removeAllItems();
            mtcbxConvenios.setEnabled(false);
        }        
    }
    
    private void retMedConv() {
        if (bInc || bAlt) {
            Object[] options = { "Sim", "Não" };
            int n = JOptionPane.showOptionDialog(this, "MUITO CUIDADO POIS TODOS OS DADOS DESTE PACIENTE SERÃO PERDIDOS!!!\n\nCancelar operação?", "Atenção", 0, 3, null, options, options[0]);
            if (n == 1) {
                return;
            }
            
            bInc = false;
            bAlt = false;

            // Botões painel principal
            btIncluir.setEnabled(true);
            btExcluir.setEnabled(true);
            btPesquisar.setEnabled(true);
            btGravar.setEnabled(true);
            btRetornar.setEnabled(true);
        
            // Botões painel MedTaxas
            mtbtnIncluir.setEnabled(true);
            mtbtnAlterar.setEnabled(mtbvConvenios.getRowCount() > 0);
            mtbtnExcluir.setEnabled(mtbvConvenios.getRowCount() > 0);
            mtbtnGravar.setEnabled(false);
            mtbtnRetornar.setEnabled(false);
            mtbvConvenios.setEnabled(true);
            
            mttxbValor.setEnabled(false);
            mttxbValor.setText("0,00");       
            mttxbValorMed.setEnabled(false);
            mttxbValorMed.setText("0,00");            
            mtcbxConvenios.removeAllItems();
            mtcbxConvenios.setEnabled(false);
        }                        
    }
    
}
