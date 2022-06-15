package Faturamentos.Tiss;

import Atendimentos.jFichaPaciente;
import Db.DbMain;
import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import static Funcoes.FuncoesGlobais.myLetra;
import Funcoes.Md5;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author YOGA 510
 */
public class Tiss extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.con;
    TableRowSorter<TableModel> sorterConsulta;

    jTableControl tabela = new jTableControl(true);
    TableRowSorter<TableModel> sorterPacientes;

    /**
     * Creates new form Tiss
     */
    public Tiss() {
        initComponents();

        jdtinic.setText(Dates.DateFormat("dd/MM/yyyy", new Date()));
        jdtfim.setText(Dates.DateFormat("dd/MM/yyyy", new Date()));

        {
            jConvenios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            String[][] aheader = { { "codigo", "convênio", "numero", "ans", "ope"}, { "30", "380", "0", "50", "50" } };
            TableControl.header(jConvenios, aheader);
        }
        
        jTxtConvBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if ("".equals(jTxtConvBuscar.getText().trim())) {
                    sorterConsulta.setRowFilter(null);
                } else {
                    try {
                        sorterConsulta.setRowFilter(
                               RowFilter.regexFilter(jTxtConvBuscar.getText().trim()));
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

        jTxtPacBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if ("".equals(jTxtPacBuscar.getText().trim())) {
                    sorterPacientes.setRowFilter(null);
                } else {
                    try {
                        sorterPacientes.setRowFilter(
                               RowFilter.regexFilter(jTxtPacBuscar.getText().trim()));
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

        jTxtConvBuscar.requestFocus();        

    }

    private void GridConvenios(String dtInic, String dtFin) {
        String tsql = "SELECT DISTINCT fat.ma_convnumero codigo, con.cv_apelido nome, " + 
                      "con.cv_numerador numero, con.cv_ans ans, con.cv_codoperadora codoper FROM " + 
                      "faturar fat, convenios con WHERE (fat.ma_convnumero = con.cv_numero) " + 
                      "AND fat.ma_data::date BETWEEN ? AND ? ORDER BY con.cv_apelido; ";
        Object[][] param = {{"date", dtInic}, {"date", dtFin}};
        ResultSet trs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY, param);
        
        TableControl.delall(jConvenios);
        TableControl.Clear(jConvenios);
        
        String mcod = null, mnome = null, mnumero = null, mans = null, mcdoper = null;
        try {
            while (trs.next()) {
                try { mcod = trs.getString("codigo"); } catch (SQLException e) {}
                try { mnome = trs.getString("nome"); } catch (SQLException e) {}
                try { mnumero = trs.getString("numero"); } catch (SQLException e) {}
                try { mans = trs.getString("ans"); } catch (SQLException e) {}
                try { mcdoper = trs.getString("codoper"); } catch (SQLException e) {}
                TableControl.add(jConvenios, new String[][] { { mcod, mnome, mnumero, mans, mcdoper }, { "C", "L", "L", "C", "C" } }, true);
                sorterConsulta = new TableRowSorter(jConvenios.getModel());
                jConvenios.setRowSorter(sorterConsulta);
            }
        } catch (SQLException e) {}
        try {trs.close();} catch (SQLException e) {}
    }

    private void GridPacientes(String dtInic, String dtFin, int codplano, boolean isConsulta) {
        Integer[] tam = {30,30,20,170,160,10,0,0,0,30,10};
        String[] col = {"data","hora","codigo","nome","medico","t","cdmed","nrguia","vrcon","lote","f"};
        Boolean[] edt = {false,false,false,false,false,true,false,false,false,false,false};
        String[] aln = {"C","C","C","L","L","","C","C","C","C","C"};
        Object[][] data = {};

        String tsql = "SELECT ma_data::date as data, ma_hora as hora, ma_pcnumero as codigo, ma_nome as nome, " + 
                      "ma_medico as medico, ma_codmedico, ma_nrguia, cv_consultavr, cv_numerador, ma_faturado " + 
                      "FROM faturar WHERE (" + (isConsulta ? "ma_consulta = 1" : "ma_procedimento") + 
                      " AND ma_status = 4 AND ma_convnumero = ?) AND " + 
                      "ma_data BETWEEN ? AND ? ORDER BY Lower(ma_nome), ma_data, ma_hora;";
        Object[][] param = {{"int", codplano}, {"date", dtInic}, {"date", dtFin}};
        ResultSet trs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY, param);
        
        TableControl.delall(jPacientes);
        
        String mdata = null, mhora = null, mcod = null, mnome = null, mmedico = null;
        String mcdmed = null, mnrguia = null, mvrconsulta = null; 
        String mnumerador = null; boolean mfaturado = false;
        try {
            while (trs.next()) {
                try { mdata = Dates.DateFormat("dd-MM-yyyy", trs.getDate("data")); } catch (SQLException e) {}
                try { mhora = trs.getString("hora"); } catch (SQLException e) {}
                try { mcod = String.valueOf(trs.getInt("codigo")); } catch (SQLException e) {}
                try { mnome = trs.getString("nome"); } catch (SQLException e) {}
                try { mmedico = trs.getString("medico"); } catch (SQLException e) {}
                try { mcdmed = trs.getString("ma_codmedico"); } catch (SQLException e) {}
                try { mnrguia = trs.getString("ma_nrguia"); } catch (SQLException e) {}
                try { mvrconsulta = trs.getString("cv_consultavr"); } catch (SQLException e) {}
                
                try { mnumerador = String.valueOf(trs.getInt("cv_numerador")); } catch (SQLException e) {mnumerador = "";}
                //if (mnumerador.equalsIgnoreCase("0")) mnumerador = "";
                try { mfaturado = trs.getBoolean("ma_faturado"); } catch (SQLException e) {mfaturado = false;}

                Object[] dado = {mdata, mhora, mcod, mnome, mmedico, false, mcdmed, mnrguia, mvrconsulta, mnumerador, (mfaturado ? "*" : "")};
                data = tabela.insert(data, dado);

            }
        } catch (SQLException e) {}
        try {trs.close();} catch (SQLException e) {}

        tabela.Show(jPacientes, data, tam, aln, col, edt);
        sorterPacientes = new TableRowSorter(jPacientes.getModel());
        jPacientes.setRowSorter(sorterPacientes);
        
        jPacientes.getModel().addTableModelListener(new CheckBoxModelListener());
    }

    public class CheckBoxModelListener implements TableModelListener {

        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 5) {
                TableModel model = (TableModel) e.getSource();
                String columnName = model.getColumnName(column);
                Boolean checked = (Boolean) model.getValueAt(row, column);
                int nSel = 0;
                try { nSel = Integer.parseInt(tSelect.getText()); } catch (NumberFormatException ex) {}
                if (checked) {
                    nSel += 1;
                } else {
                    nSel -= 1;
                }
                if (nSel < 0) nSel = 0;
                tSelect.setText(FuncoesGlobais.StrZero(String.valueOf(nSel),4));
            }                
        }
    }    
    
    private void SelectAll(int zera) {
        for (int i=0; i< jPacientes.getRowCount(); i++) {
            int modelRow = jPacientes.convertRowIndexToModel(i);
            jPacientes.getModel().setValueAt(false, modelRow, 5);
        }
            
        if (zera != 0) {
            for (int i=0; i< jPacientes.getRowCount(); i++) {
                int modelRow = jPacientes.convertRowIndexToModel(i);
                boolean isSel = jChbSelTodosPacientes.isSelected();
                if (Integer.parseInt((String)jPacientes.getModel().getValueAt(modelRow, 9)) == 0) {
                    jPacientes.getModel().setValueAt(isSel, modelRow, 5);
                }
            }
        }
        
        jPacientes.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConsultaSadt = new javax.swing.ButtonGroup();
        Versao = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jConvenios = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPacientes = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jTxtConvBuscar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTxtPacBuscar = new javax.swing.JTextField();
        jBtnListarPacientes = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jChbSelTodosPacientes = new javax.swing.JCheckBox();
        jRbxTiss30200 = new javax.swing.JRadioButton();
        jRbxTiss30500 = new javax.swing.JRadioButton();
        jBtnGerarXml = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        SelLote = new javax.swing.JSpinner();
        btnSelLote = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRbtConsulta = new javax.swing.JRadioButton();
        jRbtSadt = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jdtinic = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jdtfim = new javax.swing.JFormattedTextField();
        jBtnConveniosListar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        tSelect = new javax.swing.JLabel();

        setBackground(new java.awt.Color(101, 227, 255));
        setClosable(true);
        setTitle(".:: Faturamento - Geração do XML");
        setDoubleBuffered(true);
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/money.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(669, 519));
        setMinimumSize(new java.awt.Dimension(669, 519));
        setOpaque(true);
        setVisible(true);

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(253, 251, 251));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Selecione o Convênio");
        jLabel3.setOpaque(true);

        jConvenios.setBackground(new java.awt.Color(102, 255, 140));
        jConvenios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Convênio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jConvenios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jConvenios.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jConvenios);
        if (jConvenios.getColumnModel().getColumnCount() > 0) {
            jConvenios.getColumnModel().getColumn(0).setResizable(false);
            jConvenios.getColumnModel().getColumn(0).setPreferredWidth(10);
        }

        jLabel4.setBackground(new java.awt.Color(153, 153, 255));
        jLabel4.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(253, 251, 251));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Selecione os Paciêntes");
        jLabel4.setOpaque(true);

        jPacientes.setBackground(new java.awt.Color(102, 255, 140));
        jPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jPacientes.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jPacientes.getTableHeader().setReorderingAllowed(false);
        jPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPacientesMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jPacientes);

        jLabel5.setText("Buscar:");

        jTxtConvBuscar.setMaximumSize(new java.awt.Dimension(6, 20));

        jLabel6.setText("Buscar:");

        jBtnListarPacientes.setText("Listar");
        jBtnListarPacientes.setPreferredSize(new java.awt.Dimension(73, 20));
        jBtnListarPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnListarPacientesActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(101, 227, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jChbSelTodosPacientes.setBackground(new java.awt.Color(101, 227, 255));
        jChbSelTodosPacientes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jChbSelTodosPacientes.setText("Selecionar Todos");
        jChbSelTodosPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChbSelTodosPacientesActionPerformed(evt);
            }
        });

        jRbxTiss30200.setBackground(new java.awt.Color(101, 227, 255));
        Versao.add(jRbxTiss30200);
        jRbxTiss30200.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRbxTiss30200.setForeground(new java.awt.Color(0, 0, 255));
        jRbxTiss30200.setText("3.02.00");

        jRbxTiss30500.setBackground(new java.awt.Color(101, 227, 255));
        Versao.add(jRbxTiss30500);
        jRbxTiss30500.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRbxTiss30500.setForeground(new java.awt.Color(0, 153, 0));
        jRbxTiss30500.setSelected(true);
        jRbxTiss30500.setText("3.05.00");

        jBtnGerarXml.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jBtnGerarXml.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/save.png"))); // NOI18N
        jBtnGerarXml.setMnemonic('G');
        jBtnGerarXml.setIconTextGap(7);
        jBtnGerarXml.setLabel("Gerar XML");
        jBtnGerarXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnGerarXmlActionPerformed(evt);
            }
        });

        jLabel8.setText("Selecionar Lote:");

        SelLote.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        btnSelLote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/plus.png"))); // NOI18N
        btnSelLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelLoteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jChbSelTodosPacientes)
                .addGap(14, 14, 14)
                .addComponent(jRbxTiss30200)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRbxTiss30500)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SelLote)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelLote, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jBtnGerarXml)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChbSelTodosPacientes)
                    .addComponent(jRbxTiss30200)
                    .addComponent(jRbxTiss30500)
                    .addComponent(jBtnGerarXml)
                    .addComponent(jLabel8)
                    .addComponent(SelLote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelLote, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(101, 227, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), " [ Selecione uma opção ] ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jRbtConsulta.setBackground(new java.awt.Color(101, 227, 255));
        ConsultaSadt.add(jRbtConsulta);
        jRbtConsulta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRbtConsulta.setSelected(true);
        jRbtConsulta.setText("Guias de Consulta");

        jRbtSadt.setBackground(new java.awt.Color(101, 227, 255));
        ConsultaSadt.add(jRbtSadt);
        jRbtSadt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRbtSadt.setText("Guias de SADT");
        jRbtSadt.setEnabled(false);
        jRbtSadt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRbtSadtActionPerformed(evt);
            }
        });

        jLabel1.setText("Período:");

        jdtinic.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        jdtinic.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setText("até");

        jdtfim.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        jdtfim.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jBtnConveniosListar.setText("Convênios");
        jBtnConveniosListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnConveniosListarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRbtConsulta)
                .addGap(18, 18, 18)
                .addComponent(jRbtSadt)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdtinic, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdtfim, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnConveniosListar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRbtConsulta)
                        .addComponent(jRbtSadt))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jdtinic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jdtfim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnConveniosListar)))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jLabel7.setText("Total Selecionado:");

        tSelect.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tSelect.setText("0000");
        tSelect.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtConvBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnListarPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtPacBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTxtConvBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnListarPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTxtPacBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tSelect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnListarPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnListarPacientesActionPerformed
        ListarPacientes();
        if (jChbSelTodosPacientes.isSelected()) SelectAll(0);
        tSelect.setText("0000");
    }//GEN-LAST:event_jBtnListarPacientesActionPerformed

    private void ListarPacientes() {
        int trow = jConvenios.getSelectedRow();
        if (trow == -1) return;
        int modelRow = jConvenios.convertRowIndexToModel(trow);
        int cdcon = Integer.valueOf(jConvenios.getModel().getValueAt(modelRow, 0).toString());

        String dtInic = Dates.StringtoString(jdtinic.getText(), "dd-MM-yyyy", "yyyy-MM-dd");
        String dtFin = Dates.StringtoString(jdtfim.getText(), "dd-MM-yyyy", "yyyy-MM-dd");

       GridPacientes(dtInic, dtFin, cdcon, jRbtConsulta.isSelected());
    }
    
    private void jRbtSadtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRbtSadtActionPerformed
        
    }//GEN-LAST:event_jRbtSadtActionPerformed

    private void jBtnConveniosListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnConveniosListarActionPerformed
        Date dInic = Dates.StringtoDate(jdtinic.getText(), "dd-MM-yyyy");
        Date dFin = Dates.StringtoDate(jdtfim.getText(), "dd-MM-yyyy");
        
        if (dInic.compareTo(dFin) > 0) {
            JOptionPane.showMessageDialog(this, "Data inicial não pode ser maior que Data Final!");
            jdtinic.selectAll();
            jdtinic.requestFocus();
            return;
        }
        
        if (dInic.getYear() < 121) {
            JOptionPane.showMessageDialog(this, "Não pode gerar faturamentos antes de 2021!");
            jdtinic.selectAll();
            jdtinic.requestFocus();
            return;
        }
        
        if (dInic.getMonth() < 03) {
            JOptionPane.showMessageDialog(this, "Não pode gerar faturamentos antes de Abril/2021!");
            jdtinic.selectAll();
            jdtinic.requestFocus();
            return;
        }
                
        String dtInic = Dates.StringtoString(jdtinic.getText(), "dd-MM-yyyy", "yyyy-MM-dd");
        String dtFin = Dates.StringtoString(jdtfim.getText(), "dd-MM-yyyy", "yyyy-MM-dd");
        
        if (jRbtConsulta.isSelected()) {
            GridConvenios(dtInic, dtFin);
        } else {
            JOptionPane.showMessageDialog(this, "Não implantado ainda...");
        }
    }//GEN-LAST:event_jBtnConveniosListarActionPerformed

    private void jChbSelTodosPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChbSelTodosPacientesActionPerformed
        SelectAll(1);
    }//GEN-LAST:event_jChbSelTodosPacientesActionPerformed

    private void jBtnGerarXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnGerarXmlActionPerformed
        int trow = jConvenios.getSelectedRow();
        if (trow == -1) return;
        int modelRow = jConvenios.convertRowIndexToModel(trow);
        int cdcon = Integer.valueOf(jConvenios.getModel().getValueAt(modelRow, 0).toString());
        String cdans = "";
        try { cdans = jConvenios.getModel().getValueAt(modelRow, 3).toString(); } catch (Exception e) {}
        String cdoper = "";
        try { cdoper = jConvenios.getModel().getValueAt(modelRow, 4).toString(); } catch (Exception e) {}

        // Pega Numero do Lote
        String lote = PegaNumerador(cdcon);
        if (lote == null) {
            JOptionPane.showMessageDialog(this, "Convenio sem numerador!\n\nSera assumido o valor 0(Zero).");
            lote = "0";
        }
        
        new jDirectory("reports/tiss/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/tiss/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
        String sLote = "";
        Object[] string_XML = xml_tiss(lote, cdans, cdoper);
        try {
            for (Object item : string_XML) {
                sLote = FuncoesGlobais.StrZero(String.valueOf(((Object[])item)[2]), 20);
                FileWriter arq = new FileWriter(pathName + sLote + "_" + ((Object[])item)[1].toString() +".xml");
                PrintWriter gravarArq = new PrintWriter(arq);       
                gravarArq.printf(((Object[])item)[0].toString());
                arq.close();
            }
        } catch (Exception ex) {ex.printStackTrace();}
        
        if (!AtualizaNumerador(cdcon, sLote)) {
            JOptionPane.showMessageDialog(this, "Não foi possivel atualizar o numero do LOTE.!");
        } else {
            JOptionPane.showMessageDialog(this, "(" + string_XML.length + ") LOTE(S) foram gerados com sucesso.");
            ListarPacientes();
        }
    }//GEN-LAST:event_jBtnGerarXmlActionPerformed

    private void btnSelLoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelLoteActionPerformed
        // Limpa Seleções
        for (int i=0; i< jPacientes.getRowCount(); i++) {
            int modelRow = jPacientes.convertRowIndexToModel(i);
            jPacientes.getModel().setValueAt(false, modelRow, 5);
        }        

        // Seleciona o Lote
        for (int i=0; i< jPacientes.getRowCount(); i++) {
            int modelRow = jPacientes.convertRowIndexToModel(i);
            if (Integer.parseInt(jPacientes.getModel().getValueAt(modelRow, 9).toString()) == (int)SelLote.getValue()) jPacientes.getModel().setValueAt(true, modelRow, 5);
        }                
    }//GEN-LAST:event_btnSelLoteActionPerformed

    private void jPacientesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPacientesMouseReleased
        if (evt.getClickCount() == 2) {
            int row = jPacientes.convertRowIndexToModel(jPacientes.getSelectedRow());
            String tficha = jPacientes.getModel().getValueAt(row, 2).toString();
            jFichaPaciente oFichaPac = new jFichaPaciente(null, true);
            oFichaPac.ReadFields("WHERE pc_numero = '" + tficha + "';");
            oFichaPac.setVisible(true);
        }
    }//GEN-LAST:event_jPacientesMouseReleased

    private String PegaNumerador(int cdconv) {
        String retorno = null;
        String selectSQL = "SELECT cv_numerador FROM convenios WHERE cv_numero = ? LIMIT 1";
        ResultSet rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int",cdconv}});
        try {
            while (rs.next()) {
                retorno = String.valueOf(rs.getInt("cv_numerador"));
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
        return retorno;
    }
    
    private boolean AtualizaNumerador(int cdconv, String lote) {
        boolean retorno = false;
        
        String updateSQL = "UPDATE convenios SET cv_numerador = ? WHERE cv_numero = ?;";
        retorno = conn.ExecutarComando(updateSQL, new Object[][] {
            {"int", Integer.parseInt(lote) + 1},
            {"int", cdconv}
        }) > 0;
        return retorno;
    }
    
    private Object[] xml_tiss(String numeroLote, String registroANS, String codigoPrestadorNaOperadora) {
        List selPacientes = new ArrayList<Integer>();
        int tPacientes = jPacientes.getRowCount();
        for (int p = 0; p < tPacientes; p++) {
            int row = jPacientes.convertRowIndexToModel(p);
            if ((boolean)jPacientes.getModel().getValueAt(row, 5)) {
                selPacientes.add(row);
            }
        }
        tPacientes = selPacientes.size();
        
        int iPacientes = 0; int fPacientes = 0;
        
        // Dividir em lotes de 100 pacientes
        int Mod100 = tPacientes / 100;
        int iCounter = Math.abs(Mod100) - 1;
        if (tPacientes <= 100) {
            iCounter = 0;
        } else {
            if ( Math.floorMod(tPacientes, 100) > 0) iCounter += 1;
        }
    
        String xml = ""; String xml_calculo = ""; String hash = "";
        int inumeroLote = Integer.parseInt(numeroLote) - 1;
        List xmls = new ArrayList();
        
        for (int l = 0; l <= iCounter; l++) {
            xml = ""; xml_calculo = "";
            
            // Variaveis de Pacientes
            iPacientes = (l * 100);
            fPacientes = ((l * 99) + l) + 99;
            if (fPacientes >= (tPacientes - 1)) fPacientes = tPacientes - 1;
            
            // Atualiza numero do Lote
            inumeroLote += 1;

            xml += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
            xml += "<ans:mensagemTISS xmlns:ans=\"http://www.ans.gov.br/padroes/tiss/schemas\">";
            xml += "<ans:cabecalho>";
            xml += "<ans:identificacaoTransacao>";

            xml += "<ans:tipoTransacao>ENVIO_LOTE_GUIAS</ans:tipoTransacao>";
            xml_calculo += "ENVIO_LOTE_GUIAS";

            xml += "<ans:sequencialTransacao>" + inumeroLote + "</ans:sequencialTransacao>";
            xml_calculo += inumeroLote;

            Date hoje = new Date();
            xml += "<ans:dataRegistroTransacao>" + Dates.DateFormat("yyyy-MM-dd", hoje) + "</ans:dataRegistroTransacao>";
            xml_calculo += Dates.DateFormat("yyyy-MM-dd", hoje);

            xml += "<ans:horaRegistroTransacao>" + Dates.DateFormat("HH:mm:ss", hoje) + "</ans:horaRegistroTransacao>";
            xml_calculo += Dates.DateFormat("HH:mm:ss", hoje);

            xml += "</ans:identificacaoTransacao>";
            xml += "<ans:origem>";
            xml += "<ans:identificacaoPrestador>";

            xml += "<ans:codigoPrestadorNaOperadora>" + codigoPrestadorNaOperadora + "</ans:codigoPrestadorNaOperadora>";
            xml_calculo += codigoPrestadorNaOperadora;

            xml += "</ans:identificacaoPrestador>";
            xml += "</ans:origem>";
            xml += "<ans:destino>";

            xml += "<ans:registroANS>" + registroANS + "</ans:registroANS>";
            xml_calculo += registroANS;

            xml += "</ans:destino>";

            xml += "<ans:Padrao>" + (jRbxTiss30500.isSelected() ? "3.05.00" : "3.02.00") + "</ans:Padrao>";
            xml_calculo += (jRbxTiss30500.isSelected() ? "3.05.00" : "3.02.00");

            xml += "</ans:cabecalho>";
            xml += "<ans:prestadorParaOperadora>";
            xml += "<ans:loteGuias>";

            xml += "<ans:numeroLote>" + inumeroLote + "</ans:numeroLote>";
            xml_calculo += inumeroLote;

            xml += "<ans:guiasTISS>";

            if (jRbtConsulta.isSelected()) {

                // para cada guia
                for (int i = iPacientes; i <= fPacientes; i++) {
                    int row = jPacientes.convertRowIndexToModel((int)selPacientes.get(i));
                    if ((Boolean)jPacientes.getModel().getValueAt(row, 5)) {
                        String mdata = Dates.StringtoString(jPacientes.getModel().getValueAt(row, 0).toString(),"dd-MM-yyyy","yyyy-MM-dd");
                        String mhora = jPacientes.getModel().getValueAt(row, 1).toString();
                        String mguia = null; try {mguia = jPacientes.getModel().getValueAt(row, 7).toString();} catch (NullPointerException gex) {mguia = "001";};
                        String mpcnumero = jPacientes.getModel().getValueAt(row, 2).toString();
                        String mnome = jPacientes.getModel().getValueAt(row, 3).toString();
                        String mcdmed = jPacientes.getModel().getValueAt(row, 6).toString();
                        String mvrcon = jPacientes.getModel().getValueAt(row, 8).toString();
                        
                        Object[][] dadospac = null;
                        try { dadospac = conn.LerCamposTabela(new String[] {"pc_inscricao", "pc_nome"}, "pacientes", "pc_numero = ?", new Object[][]{{"double", Double.valueOf(mpcnumero)}}); } catch (SQLException pex) {}
                        if (dadospac == null) {
                            System.out.println("Lote: " + inumeroLote + " > PCNUMERO = " + mpcnumero + " - " + mnome + " < ignorado.");
                            continue;
                        }

                        // Atualizar faturar com numero do lote no paciente faturado e true para ma_faturado
                        //AtualizaDadosFaturar(mdata, mhora, mguia, numeroLote);
                        AtualizaDadosFaturar2(mdata, mhora, mpcnumero, inumeroLote);

                        Object[][] dadosmed = null;
                        try { dadosmed = conn.LerCamposTabela(new String[] {"md_cpf", "md_nome", "md_crm", "md_crmuf", "md_cbo"}, "medicos", "md_codigo = ?", new Object[][]{{"int", Integer.valueOf(mcdmed)}}); } catch (SQLException pex) {}
                        if (dadosmed == null) {
                            System.out.println("Lote: " + inumeroLote + " > PCNUMERO = " + mpcnumero + " - " + mnome + " < ignorado.");
                            continue;
                        }

                        xml += "<ans:guiaConsulta>";
                        xml += "<ans:cabecalhoConsulta>";
                        xml += "<ans:registroANS>" + registroANS + "</ans:registroANS>";
                        xml_calculo += registroANS;
                        xml += "<ans:numeroGuiaPrestador>" + mguia + "</ans:numeroGuiaPrestador>";  // Verificar
                        xml_calculo += mguia;
                        xml += "</ans:cabecalhoConsulta>";
                        xml += "<ans:numeroGuiaOperadora>" + mguia + "</ans:numeroGuiaOperadora>";
                        xml_calculo +=  mguia;
                        xml += "<ans:dadosBeneficiario>";
                        xml += "<ans:numeroCarteira>" + dadospac[0][3].toString() + "</ans:numeroCarteira>";
                        xml_calculo += dadospac[0][3].toString();
                        xml += "<ans:atendimentoRN>" + "N" + "</ans:atendimentoRN>";
                        xml_calculo += "N";
                        xml += "<ans:nomeBeneficiario>" + myLetra(dadospac[1][3].toString()).toUpperCase() + "</ans:nomeBeneficiario>";
                        xml_calculo += myLetra(dadospac[1][3].toString()).toUpperCase();
                        xml += "</ans:dadosBeneficiario>";
                        xml += "<ans:contratadoExecutante>";
                        xml += "<ans:cnpjContratado>" + contratadoExecutante()[0][1].toString() + "</ans:cnpjContratado>";
                        xml_calculo += contratadoExecutante()[0][1].toString();
                        xml += "<ans:nomeContratado>" + myLetra(contratadoExecutante()[1][1].toString()) + "</ans:nomeContratado>";
                        xml_calculo += myLetra(contratadoExecutante()[1][1].toString());
                        xml += "<ans:CNES>" + contratadoExecutante()[2][1].toString() + "</ans:CNES>";
                        xml_calculo += contratadoExecutante()[2][1].toString();
                        xml += "</ans:contratadoExecutante>";
                        xml += "<ans:profissionalExecutante>";
                        xml += "<ans:nomeProfissional>" + myLetra(dadosmed[1][3].toString()) + "</ans:nomeProfissional>";
                        xml_calculo += myLetra(dadosmed[1][3].toString());
                        xml += "<ans:conselhoProfissional>" + CodigoTipoConselho() + "</ans:conselhoProfissional>";
                        xml_calculo += CodigoTipoConselho();
                        xml += "<ans:numeroConselhoProfissional>" + dadosmed[2][3].toString() + "</ans:numeroConselhoProfissional>";
                        xml_calculo += dadosmed[2][3].toString();
                        xml += "<ans:UF>" + CodigosUF(dadosmed[3][3].toString(),2,0) + "</ans:UF>";
                        xml_calculo += CodigosUF(dadosmed[3][3].toString(),2,0);
                        xml += "<ans:CBOS>" + dadosmed[4][3].toString() + "</ans:CBOS>";
                        xml_calculo += dadosmed[4][3].toString();
                        xml += "</ans:profissionalExecutante>";
                        xml += "<ans:indicacaoAcidente>" + CodigoTipoAcidente() + "</ans:indicacaoAcidente>";
                        xml_calculo += CodigoTipoAcidente();
                        xml += "<ans:dadosAtendimento>";
                        xml += "<ans:dataAtendimento>" + mdata + "</ans:dataAtendimento>";
                        xml_calculo += mdata;
                        xml += "<ans:tipoConsulta>" + CodigoTipoConsulta() + "</ans:tipoConsulta>";
                        xml_calculo += CodigoTipoConsulta();
                        xml += "<ans:procedimento>";
                        xml += "<ans:codigoTabela>" + CodigoTabelas() + "</ans:codigoTabela>";
                        xml_calculo += CodigoTabelas();
                        xml += "<ans:codigoProcedimento>" + CodigoConsulta() + "</ans:codigoProcedimento>";
                        xml_calculo += CodigoConsulta();
                        xml += "<ans:valorProcedimento>" + mvrcon + "</ans:valorProcedimento>";
                        xml_calculo += mvrcon;
                        xml += "</ans:procedimento>";
                        xml += "</ans:dadosAtendimento>";
                        //if (guia.getObservacoes() != null) {
                        //        xml += "<ans:observacao>" + guia.getObservacoes() + "</ans:observacao>";
                        //}
                        xml += "</ans:guiaConsulta>";
                    }
                }
            } else {

    //                // para cada guia
    //                for (GuiaSADT guia : guiasSADT) {
    //
    //                        xml += "<ans:guiaSP-SADT>";
    //                        xml += "<ans:cabecalhoGuia>";
    //                        xml += "<ans:registroANS>" + this.planoSaude.getRegistroANS() + "</ans:registroANS>";
    //                        xml += "<ans:numeroGuiaPrestador>" + guia.getNumeroGuia() + "</ans:numeroGuiaPrestador>";
    //                        xml += "</ans:cabecalhoGuia>";
    //
    //                        xml += "<ans:dadosAutorizacao>";
    //                        xml += "<ans:dataAutorizacao>" + guia.getDataAutorizacaoBR() + "</ans:dataAutorizacao>";
    //                        xml += "<ans:senha>" + guia.getSenha() + "</ans:senha>";
    //                        xml += "<ans:dataValidadeSenha>" + guia.getDataValidadeSenhaBR() + "</ans:dataValidadeSenha>";
    //                        xml += "</ans:dadosAutorizacao>";
    //
    //                        xml += "<ans:dadosBeneficiario>";
    //                        xml += "<ans:numeroCarteira>" + guia.getCarteira() + "</ans:numeroCarteira>";
    //                        xml += "<ans:atendimentoRN>" + guia.getAtendimentoRN().name() + "</ans:atendimentoRN>";
    //                        xml += "<ans:nomeBeneficiario>" + guia.getCliente().getNome() + "</ans:nomeBeneficiario>";
    //                        xml += "</ans:dadosBeneficiario>";
    //                        xml += "<ans:dadosSolicitante>";
    //                        xml += "<ans:contratadoSolicitante>";
    //                        xml += "<ans:cpfContratado>" + guia.getMedico().getCpf() + "</ans:cpfContratado>";
    //                        xml += "<ans:nomeContratado>" + guia.getMedico().getNome() + "</ans:nomeContratado>";
    //                        xml += "</ans:contratadoSolicitante>";
    //                        xml += "<ans:profissionalSolicitante>";
    //                        xml += "<ans:nomeProfissional>" + guia.getMedico().getNome() + "</ans:nomeProfissional>";
    //                        xml += "<ans:conselhoProfissional>" + guia.getMedico().getConselhoProfissional().getCodigo() + "</ans:conselhoProfissional>";
    //                        xml += "<ans:numeroConselhoProfissional>" + guia.getMedico().getNumeroConselho() + "</ans:numeroConselhoProfissional>";
    //                        xml += "<ans:UF>" + guia.getMedico().getEstadoConselho().getCodigo() + "</ans:UF>";
    //                        xml += "<ans:CBOS>" + guia.getMedico().getCbos() + "</ans:CBOS>";
    //                        xml += "</ans:profissionalSolicitante>";
    //                        xml += "</ans:dadosSolicitante>";
    //                        xml += "<ans:dadosSolicitacao>";
    //                        xml += "<ans:dataSolicitacao>" + guia.getDataSolicitacaoYMD() + "</ans:dataSolicitacao>";
    //                        xml += "<ans:caraterAtendimento>" + guia.getCaraterSolicitacao().getCodigo() + "</ans:caraterAtendimento>";
    //                        xml += "</ans:dadosSolicitacao>";
    //                        xml += "<ans:dadosExecutante>";
    //                        xml += "<ans:contratadoExecutante>";
    //                        xml += "<ans:cpfContratado>" + guia.getMedico().getCpf() + "</ans:cpfContratado>";
    //                        xml += "<ans:nomeContratado>" + guia.getMedico().getNome() + "</ans:nomeContratado>";
    //                        xml += "</ans:contratadoExecutante>";
    //                        xml += "<ans:CNES>" + guia.getMedico().getNumeroCNES() + "</ans:CNES>";
    //                        xml += "</ans:dadosExecutante>";
    //                        xml += "<ans:dadosAtendimento>";
    //                        xml += "<ans:tipoAtendimento>" + guia.getTipoAtendimento().getCodigo() + "</ans:tipoAtendimento>";
    //                        xml += "<ans:indicacaoAcidente>" + guia.getIndicacaoAcidente().getCodigo() + "</ans:indicacaoAcidente>";
    //                        xml += "</ans:dadosAtendimento>";
    //                        xml += "<ans:procedimentosExecutados>";
    //
    //                        for (ProcedimentoExecutado proc : guia.getProcedimentosExecutados()) {
    //                                xml += "<ans:procedimentoExecutado>";
    //                                xml += "<ans:dataExecucao>" + proc.getDataYMD() + "</ans:dataExecucao>";
    //                                xml += "<ans:procedimento>";
    //                                xml += "<ans:codigoTabela>" + proc.getTabelaConsulta().getCodigo() + "</ans:codigoTabela>";
    //                                xml += "<ans:codigoProcedimento>" + proc.getCodigoProcedimento() + "</ans:codigoProcedimento>";
    //                                xml += "<ans:descricaoProcedimento>" + proc.getDescricao() + "</ans:descricaoProcedimento>";
    //                                xml += "</ans:procedimento>";
    //                                xml += "<ans:quantidadeExecutada>" + proc.getQuantidade() + "</ans:quantidadeExecutada>";
    //                                xml += "<ans:reducaoAcrescimo>" + proc.getFatorReducaoAcrescimo() + "</ans:reducaoAcrescimo>";
    //                                xml += "<ans:valorUnitario>" + proc.getValorUnitario() + "</ans:valorUnitario>";
    //                                xml += "<ans:valorTotal>" + proc.getValorTotal() + "</ans:valorTotal>";
    //                                xml += "<ans:equipeSadt>";
    //                                xml += "<ans:codProfissional>";
    //                                xml += "<ans:codigoPrestadorNaOperadora>" + guia.getMedico().getCpf() + "</ans:codigoPrestadorNaOperadora>";
    //                                xml += "</ans:codProfissional>";
    //                                xml += "<ans:nomeProf>" + guia.getMedico().getNome() + "</ans:nomeProf>";
    //                                xml += "<ans:conselho>" + guia.getMedico().getConselhoProfissional().getCodigo() + "</ans:conselho>";
    //                                xml += "<ans:numeroConselhoProfissional>" + guia.getMedico().getNumeroConselho() + "</ans:numeroConselhoProfissional>";
    //                                xml += "<ans:UF>" + guia.getMedico().getEstadoConselho().getCodigo() + "</ans:UF>";
    //                                xml += "<ans:CBOS>" + guia.getMedico().getCbos() + "</ans:CBOS>";
    //                                xml += "</ans:equipeSadt>";
    //                                xml += "</ans:procedimentoExecutado>";
    //                        }
    //                        xml += "</ans:procedimentosExecutados>";
    //
    //                        xml += "<ans:outrasDespesas>";
    //                        for (OutraDespesa outra : guia.getOutrasDespesas()) {
    //                                xml += "<ans:despesa>";
    //                                xml += "<ans:codigoDespesa>" + outra.getCd().getCodigo() + "</ans:codigoDespesa>";
    //                                xml += "<ans:servicosExecutados>";
    //                                xml += "<ans:dataExecucao>" + outra.getDataYMD() + "</ans:dataExecucao>";
    //                                xml += "<ans:codigoTabela>" + outra.getTabelaConsulta().getCodigo() + "</ans:codigoTabela>";
    //                                xml += "<ans:codigoProcedimento>" + outra.getCodigoItem() + "</ans:codigoProcedimento>";
    //                                xml += "<ans:quantidadeExecutada>" + outra.getQuantidade() + "</ans:quantidadeExecutada>";
    //                                xml += "<ans:unidadeMedida>" + outra.getUnidadeMedida().getCodigo() + "</ans:unidadeMedida>";
    //                                xml += "<ans:reducaoAcrescimo>" + outra.getFatorReducaoAcrescimo() + "</ans:reducaoAcrescimo>";
    //                                xml += "<ans:valorUnitario>" + outra.getValorUnitario() + "</ans:valorUnitario>";
    //                                xml += "<ans:valorTotal>" + outra.getValorTotal() + "</ans:valorTotal>";
    //                                xml += "<ans:descricaoProcedimento>" + outra.getDescricao() + "</ans:descricaoProcedimento>";
    //                                xml += "</ans:servicosExecutados>";
    //                                xml += "</ans:despesa>";
    //                        }
    //                        xml += "</ans:outrasDespesas>";
    //
    //                        if (guia.getObservacoes() != null) {
    //                                xml += "<ans:observacao>" + guia.getObservacoes() + "</ans:observacao>";
    //                        }
    //                        xml += "<ans:valorTotal>";
    //                        xml += "<ans:valorProcedimentos>"+guia.getValorTotalProcedimentosExecutados()+"</ans:valorProcedimentos>";
    //                        xml += "<ans:valorDiarias>0.00</ans:valorDiarias>";
    //                        xml += "<ans:valorTaxasAlugueis>"+guia.getValorTotalTaxasAlugueis()+"</ans:valorTaxasAlugueis>";
    //                        xml += "<ans:valorMateriais>"+guia.getValorTotalMateriais()+"</ans:valorMateriais>";
    //                        xml += "<ans:valorMedicamentos>"+guia.getValorTotalMedicamentos()+"</ans:valorMedicamentos>";
    //                        xml += "<ans:valorOPME>"+guia.getValorTotalOPME()+"</ans:valorOPME>";
    //                        xml += "<ans:valorGasesMedicinais>"+guia.getValorTotalGasesMedicinais()+"</ans:valorGasesMedicinais>";
    //                        xml += "<ans:valorTotalGeral>"+guia.getValorTotalGeral()+"</ans:valorTotalGeral>";
    //                        xml += "</ans:valorTotal>";
    //                        xml += "</ans:guiaSP-SADT>";
    //
    //                }

            }

            xml += "</ans:guiasTISS>";
            xml += "</ans:loteGuias>";
            xml += "</ans:prestadorParaOperadora>";
            xml += "<ans:epilogo>";

            hash = new Md5(xml_calculo).getHash();
            xml += "<ans:hash>" + hash + "</ans:hash>";

            xml += "</ans:epilogo>";
            xml += "</ans:mensagemTISS>";
            
            xmls.add(new Object[] {xml, hash, inumeroLote});
        }
                
        return xmls.toArray();
    }    
    
    private boolean AtualizaDadosFaturar(String mdata, String mhora, String nrguia, int lote) {
        boolean retorno = false;
        
        String updateSQL = "UPDATE faturar SET cv_numerador = ?, ma_faturado = TRUE WHERE ma_data::date = ? AND " +
                "ma_hora = ? AND ma_nrguia = ?;";
        retorno = conn.ExecutarComando(updateSQL, new Object[][] {
            {"int", lote},
            {"date", Dates.toSqlDate(Dates.StringtoDate(mdata,"yyyy-MM-dd"))},
            {"string", mhora},
            {"int", nrguia}
        }) > 0;
        return retorno;
    }

    private boolean AtualizaDadosFaturar2(String mdata, String mhora, String pcnumero, int lote) {
        boolean retorno = false;
        
        String updateSQL = "UPDATE faturar SET cv_numerador = ?, ma_faturado = TRUE WHERE ma_data::date = ? AND "+
                "ma_hora = ? AND ma_pcnumero = ?;";
        retorno = conn.ExecutarComando(updateSQL, new Object[][] {
            {"int", lote},
            {"date", Dates.toSqlDate(Dates.StringtoDate(mdata,"yyyy-MM-dd"))},
            {"string", mhora},
            {"int", Integer.parseInt(pcnumero)}
        }) > 0;
        return retorno;
    }

    private String EncodeStringtoUTF8(String value, String type) {
        String EncodedString;        
        if (type.equalsIgnoreCase("UTF8")) {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(value); 
            EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        } else {
            byte[] englishBytes = value.getBytes();
            EncodedString = new String(englishBytes, StandardCharsets.US_ASCII);
        }
        return EncodedString;
    }
    
    private String[][] contratadoExecutante() {
        String[][] Dados = new String[][] {
            {"CNPJ", "35832542000105"},
            {"NOME", "Clinica Medica Associada Ltda"},
            {"CNES", "3370453"}
        };
        return Dados;
    }

    private String CodigosUF(String value, int posPesq, int posRet) {
        String[][] cUF = new String[][] {
            {"11","Rondônia","RO"},
            {"12","Acre","AC"},
            {"13","Amazonas","AM"},
            {"14","Roraima","RR"},
            {"15","Pará","PA"},
            {"16","Amapá","AP"},
            {"17","Tocantins","TO"},
            {"21","Maranhão","MA"},
            {"22","Piauí","PI"},
            {"23","Ceará","CE"},
            {"24","Rio Grande do Norte","RN"},
            {"25","Paraíba","PB"},
            {"26","Pernambuco","PE"},
            {"27","Alagoas","AL"},
            {"28","Sergipe","SE"},
            {"29","Bahia","BA"},
            {"31","Minas Gerais","MG"},
            {"32","Espírito Santo","ES"},
            {"33","Rio de Janeiro","RJ"},
            {"35","São Paulo","SP"},
            {"41","Paraná","PR"},
            {"42","Santa Catarina","SC"},
            {"43","Rio Grande do Sul","RS"},
            {"50","Mato Grosso do Sul","MS"},
            {"51","Mato Grosso","MT"},
            {"52","Goiás","GO"},
            {"53","Distrito Federal","DF"}
        };
        
        String retorno= null;
        for (String[] item : cUF) {
            if (item[posPesq].equalsIgnoreCase(value)) {
                retorno = item[posRet].toString();
                break;
            }
        }
        return retorno;
    }

    private String CodigoConsulta() { return "10101012"; }

    private String CodigoTipoAcidente() {
        String[][] cAcidente = new String[][] {
            {"0","Trabalho"},
            {"1", "Transito"},
            {"2", "Outros"},
            {"9", "Nao Acidente"}
        };
        return cAcidente[3][0].toString();
    }

    private String CodigoTipoConsulta() {
        String[][] cConsulta = new String[][] {
            {"1","Primeira Consulta"},
            {"2","Retorno"},
            {"3","Pre-Natal"},
            {"4","Por Encaminhamento"}
        };
        return cConsulta[0][0].toString();
    }

    private String CodigoTipoConselho() {
        String[][] cTipoCon = new String[][] {
            {"01","Conselho Regional de Assistência Social", "CRAS"},
            {"02","Conselho Regional de Enfermagem","COREN"},
            {"03","Conselho Regional de Farmácia","CRF"},
            {"04","Conselho Regional de Fonoaudiologia","CRFA"},
            {"05","Conselho Regional de Fisioterapia e Terapia Ocupacional","CREFITO"},
            {"06","Conselho Regional de Medicina","CRM"},
            {"07","Conselho Regional de Nutrição","CRN"},
            {"08","Conselho Regional de Odontologia","CRO"},
            {"09","Conselho Regional de Psicologia","CRP"},
            {"10","Outros Conselhos",""}
        };
        return cTipoCon[5][0].toString();
    }

    private String CodigoTabelas() {
        String[][] cTabela = new String[][] {
            {"00","Tabela própria das operadoras"},
            {"18","Diárias, taxas e gases medicinais"},
            {"19","Materiais e Órteses, Próteses e Materiais Especiais (OPME)"},
            {"20","Medicamentos"},
            {"22","Procedimentos e eventos em saúde"},
            {"90","Tabela Própria Pacote Odontológico"},
            {"98","Tabela Própria de Pacotes"}
        };
        return cTabela[4][0].toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup ConsultaSadt;
    private javax.swing.JSpinner SelLote;
    private javax.swing.ButtonGroup Versao;
    private javax.swing.JButton btnSelLote;
    private javax.swing.JButton jBtnConveniosListar;
    private javax.swing.JButton jBtnGerarXml;
    private javax.swing.JButton jBtnListarPacientes;
    private javax.swing.JCheckBox jChbSelTodosPacientes;
    private javax.swing.JTable jConvenios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTable jPacientes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRbtConsulta;
    private javax.swing.JRadioButton jRbtSadt;
    private javax.swing.JRadioButton jRbxTiss30200;
    private javax.swing.JRadioButton jRbxTiss30500;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTxtConvBuscar;
    private javax.swing.JTextField jTxtPacBuscar;
    private javax.swing.JFormattedTextField jdtfim;
    private javax.swing.JFormattedTextField jdtinic;
    private javax.swing.JLabel tSelect;
    // End of variables declaration//GEN-END:variables
}
