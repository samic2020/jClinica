/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atendimentos;

import Caixa.jPagRec;
import Db.DbMain;
import Funcoes.Collections;
import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.ResizeImageIcon;
import Funcoes.Settings;
import Funcoes.StringManager;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.jPDF;
import Funcoes.newTable;
import Funcoes.toPrint;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lowagie.text.Element;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;

/**
 *
 * @author supervisor
 */
public class jRecepcao extends javax.swing.JInternalFrame {
    public static Socket clientSocket = null;
    public static PrintStream os = null;
    public static DataInputStream is = null;
    String[][] listaespera = new String[0][];
    String[][] listaencerrados = new String[0][];
    TableRowSorter<TableModel> sorter;
    TableRowSorter<TableModel> sorter2;
    TableRowSorter<TableModel> sorter3;
    TableRowSorter<TableModel> sorter4;
    DbMain conn = VariaveisGlobais.con;
    Thread digitacao;
    int tam = 0;
    int n = 0;
    
    private static SwingWorker w = null;
            
    public jRecepcao() {
        initComponents();

        try {
            Date cxdata = Dates.StringtoDate(conn.LerCamposTabela(new String[] {"data"}, "ncaixa", "f_cod = " + VariaveisGlobais.cdlogado)[0][3],"yyyy-MM-dd");
            Date hoje = new Date();
            if (!Dates.DateFormat("dd-MM-yyyy", cxdata).equals(Dates.DateFormat("dd-MM-yyyy", new Date()))) {
                JOptionPane.showMessageDialog(null, "Caixa anterior não foi fechado!","Atenção",INFORMATION_MESSAGE);
                this.dispose();
            }
        } catch (Exception e) {}            
        
        setSize(756, 610);

        new Thread() {
            public void run() {
                new MultiThreadChatClient().main(new String[] { VariaveisGlobais.logado, "rec", VariaveisGlobais.unidade, "" });
            }
        }.start();
        
        HashSet conj = new HashSet(getFocusTraversalKeys(0));
        conj.add(AWTKeyStroke.getAWTKeyStroke(10, 0));
        setFocusTraversalKeys(0, conj);

        jpnMedicosOnOff.setVisible(false);
        jpnMedicos.setVisible(false);

        String[][] aheader3 = { { "codigo", "Medico", "Especialidade", "esperas", "pendentes", "total", "autorizar", "m" }, { "0", "380", "30", "0", "0", "0", "0", "0" } };
        TableControl.header(clvMedicos, aheader3);

        TableControl.add(clvMedicos, new String[][] { { "0", "TODOS", "TODAS", "0", "0", "0", "0", "0" }, { "L", "L", "L", "L", "L", "L", "L", "L" } }, true);

        String[][] aheader = { { "codigo", "apelido", "especialidade", "nome do medico", "m" }, { "0", "0", "80", "380", "10" } };
        TableControl.header(medicoson, aheader);
        Fillmedonoff(medicoson);

        String[][] aheader2 = { { "codigo", "apelido", "especialidade", "nome do medico" }, { "0", "0", "80", "380" } };
        TableControl.header(clvSeek, aheader2);
        fillmedativos(clvSeek);

        String[] aheaders = { "Visivel/Invisivel", "hora", "Cod Paciente", "Inscricao", "Paciente", "Codigo do Convenio", "ConVenio", "Categoria", "Codigo do Medico", "Nome Medico", "T", "C", "I", "P", "M", "iC", "iI", "iP", "iM", "Hora Saida", "nAut" };

        int[] widths = { 0, 80, 0, 0, 300, 0, 170, 0, 0, 0, 20, 0, 0, 0, 0, 16, 16, 16, 16, 0, 0 };
        String[] aligns = { "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "C", "C", "C", "C", "L", "C" };
        newTable.InitTable(clvEspera, aheaders, widths, aligns, true);

        new Thread() { public void run() { initPac(); }}.start();
        new Thread() { public void run() { initencerrados(); }}.start();
        jpnMedicosemAtendimento.setEnabledAt(1, false);

        FillIndicados(jIndicacao);
        AutoCompleteDecorator.decorate(jIndicacao);

        limpatela();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tbmatricula.requestFocus();
            }
        });
    }

    private void FillIndicados(JComboBox table) {
      String tsql = "select DISTINCT UPPER(ma_indicado) AS INDICACAO from faturar where trim(ma_indicado) <> '' ORDER BY UPPER(ma_indicado);";
      ResultSet tbmedicos = conn.AbrirTabela(tsql, 1007);
      table.removeAllItems();
      try {
        while (tbmedicos.next()) {
          String tindica = tbmedicos.getString("INDICACAO");
          table.addItem(tindica);
        }
      }
      catch (Exception err) {}
      DbMain.FecharTabela(tbmedicos);
    }

    private void fillmedativos(JTable table) {
        TableControl.delall(clvSeek);
        if (medicoson.getRowCount() > 0) {
            for (int i = 0; i < medicoson.getRowCount(); i++) {
                int row = medicoson.convertRowIndexToModel(i);
                if (medicoson.getModel().getValueAt(row, 4).toString().equalsIgnoreCase("X")) {
                    String col0 = medicoson.getModel().getValueAt(row, 0).toString();
                    String col1 = medicoson.getModel().getValueAt(row, 1).toString();
                    String col2 = medicoson.getModel().getValueAt(row, 2).toString();
                    String col3 = medicoson.getModel().getValueAt(row, 3).toString();
                    TableControl.add(clvSeek, new String[][] { { col0, col1, col2, col3 }, { "L", "L", "L", "L" } }, true);
                    sorter2 = new TableRowSorter(clvSeek.getModel());
                    clvSeek.setRowSorter(sorter2);

                    TableControl.add(clvMedicos, new String[][] { { col0, col3, col2, "0", "0", "0", "0", "0" }, { "L", "L", "L", "L", "L", "L", "L", "L" } }, true);                    
                }
            }
        }
    }

     private void ChamaAgenda(String codMed) {
        String tsql = "SELECT m.md_nome, a.especialidade, a.datahora, a.paciente " + 
                "FROM nagenda a, medicos m WHERE a.cdmedico = m.md_codigo AND " + 
                "m.md_codigo = '" + codMed + "' AND " + 
                "datahora >= '" + Dates.DateFormat("yyyy/MM/dd", new Date()) + " :00:00:00'::timestamp" +
                " AND datahora <= '" + Dates.DateFormat("yyyy/MM/dd", new Date()) + " :23:59:59'::timestamp " +
                "ORDER BY Lower(a.paciente);";
        ResultSet trs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
        JTable tbl = new JTable();
        tbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        String[][] aheader = { { "Medico", "Especialidade", "Data", "Horario", "Paciênte" }, { "0", "0", "0", "10", "300" } };
        TableControl.header(tbl, aheader);
        TableControl.delall(tbl);
        
        String nmmedico = null, nmespecialidade = null, mdata = null, mhora = null, nmpaciente = null;
        try {
            while (trs.next()) {
                try { nmmedico = trs.getString("md_nome"); } catch (SQLException e) {}
                try { nmespecialidade = trs.getString("especialidade"); } catch (SQLException e) {}
                try { mdata = Dates.DateFormat("dd-MM-yyyy", trs.getTimestamp("datahora")); } catch (SQLException e) {}
                try { mhora = Dates.DateFormat("HH:mm", trs.getTimestamp("datahora")); } catch (SQLException e) {}
                try { nmpaciente = trs.getString("paciente"); } catch (SQLException e) {}
                TableControl.add(tbl, new String[][] { { nmmedico, nmespecialidade, mdata, mhora, nmpaciente }, { "L", "L", "C", "C", "L" } }, true);
                sorter4 = new TableRowSorter(tbl.getModel());
                tbl.setRowSorter(sorter4);
            }
        } catch (SQLException e) {}
        try {trs.close();} catch (SQLException e) {}
        tbl.setSize(300,200);
        
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(tbl);
        JLabel jLabel1000 = new JLabel();
        jLabel1000.setText("Buscar:");
        final JTextField jbuscar = new JTextField();
        jbuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if ("".equals(jbuscar.getText().trim())) {
                    sorter4.setRowFilter(null);
                } else {
                    try {
                        sorter4.setRowFilter(RowFilter.regexFilter("(?i)" + jbuscar.getText().trim()));
                    } catch (PatternSyntaxException pse) {System.err.println("Bad regex pattern");}
                }
            }

            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {}
        });
        jbuscar.requestFocus();
        
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1000)
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
                    .addComponent(jLabel1000)
                    .addComponent(jbuscar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel.setLayout(layout);
        
        while (true) {
            int op = JOptionPane.showConfirmDialog(this, panel,"Selecione o paciênte!",JOptionPane.YES_NO_OPTION); 
            if (op == JOptionPane.YES_OPTION) {
                if  (tbl.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(this, "Você tem que selecionar um paciênte na lista!", "Atenção", JOptionPane.INFORMATION_MESSAGE);          
                } else {
                    int selRow = tbl.getSelectedRow();
                    int modelRow = tbl.convertRowIndexToModel(selRow);
                    String nmpac = tbl.getModel().getValueAt(modelRow, 4).toString().toUpperCase().trim();
                    
                    int pos = nmpac.indexOf("/");
                    if (pos == -1) {
                        tbnomepac.setText(nmpac);
                    } else {
                        tbnomepac.setText(nmpac.substring(0,pos - 1));
                    }
                    tbnomepac.requestFocus();
                    break;
                }
            } else break;
        }
    }
    
    private void Fillmedonoff(JTable table) {
        new Settings();
        String tmedicos = System.getProperty("medicoson", "");
        String[] mtmed = tmedicos.split(";");

        String tsql = "select m.md_codigo, m.md_categoria, f.usuario, m.md_nome, m.md_codigo from medicos as m, cadfun as f where m.md_cpf = f.f_cpf order by m.md_nome;";
        ResultSet tbmedicos = conn.AbrirTabela(tsql, 1007);
        try {
            while (tbmedicos.next()) {
                String tcodigo = tbmedicos.getString("md_codigo");
                String tcatgor = tbmedicos.getString("usuario");
                String tespeci = tbmedicos.getString("md_categoria").toUpperCase();
                String tmedico = tbmedicos.getString("md_nome").toUpperCase();
                String tmarca = FuncoesGlobais.IndexOf(mtmed, tcodigo.trim()) > -1 ? "X" : "";

                TableControl.add(table, new String[][] { { tcodigo, tcatgor, tespeci, tmedico, tmarca }, { "L", "L", "L", "L", "C" } }, true);
            }
        } catch (Exception err) {}
        DbMain.FecharTabela(tbmedicos);

        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jBiometria = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tbmatricula = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbnomepac = new javax.swing.JTextField();
        jtpBusca = new javax.swing.JComboBox();
        btnBuscar = new javax.swing.JButton();
        rbtNovoPaciente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        clvPacientes = new javax.swing.JTable();
        pbBuscar = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        tgbconsulta = new javax.swing.JToggleButton();
        tgbrevisao = new javax.swing.JToggleButton();
        tgbcortesia = new javax.swing.JToggleButton();
        lbespecialidade = new javax.swing.JLabel();
        lbmedico = new javax.swing.JLabel();
        btSeekMed = new javax.swing.JButton();
        btMedicos = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jIndicacao = new javax.swing.JComboBox();
        jpnMedicosOnOff = new javax.swing.JPanel();
        jtxBusca = new javax.swing.JTextField();
        try{
            javax.swing.text.MaskFormatter tdtrecto= new javax.swing.text.MaskFormatter("UUUUUUUUUUUUUUUUUUUUUUUUU");
            jtxBusca = new javax.swing.JFormattedTextField(tdtrecto);
        }
        catch (Exception e){
        }
        jScrollPane3 = new javax.swing.JScrollPane();
        medicoson = new javax.swing.JTable();
        jpnMedicos = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        clvSeek = new javax.swing.JTable();
        tbSeekMed = new javax.swing.JTextField();
        try{
            javax.swing.text.MaskFormatter tdtrecto= new javax.swing.text.MaskFormatter("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
            tbSeekMed = new javax.swing.JFormattedTextField(tdtrecto);
        }
        catch (Exception e){
        }
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        clvEspera = new javax.swing.JTable();
        jbarra = new javax.swing.JProgressBar();
        jTooTips = new javax.swing.JLabel();
        lbProcurar = new javax.swing.JLabel();
        jtxtProcurar = new javax.swing.JTextField();
        try{
            javax.swing.text.MaskFormatter tdtrecto= new javax.swing.text.MaskFormatter("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
            jtxtProcurar = new javax.swing.JFormattedTextField(tdtrecto);
        }
        catch (Exception e){
        }
        jPanel6 = new javax.swing.JPanel();
        rbtEsperar = new javax.swing.JButton();
        rbtDesistiu = new javax.swing.JButton();
        rbtSaidaPaciente = new javax.swing.JButton();
        rbtLimpar = new javax.swing.JButton();
        rbtListaEncerrados = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jpnMedicosemAtendimento = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        clvMedicos = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        exExames = new javax.swing.JTable();

        setBackground(new java.awt.Color(101, 227, 255));
        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Recepção");
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(101, 227, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jBiometria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/dedo.jpg"))); // NOI18N
        jBiometria.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jBiometria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jBiometriaMouseReleased(evt);
            }
        });

        jLabel1.setText("Inscrição:");

        tbmatricula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbmatriculaKeyReleased(evt);
            }
        });

        jLabel2.setText("Nome do Paciênte:");

        tbnomepac.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbnomepacKeyTyped(evt);
            }
        });

        jtpBusca.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Começado por", "Contendo", "Igual" }));
        jtpBusca.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jtpBuscaItemStateChanged(evt);
            }
        });
        jtpBusca.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jtpBuscaPropertyChange(evt);
            }
        });

        btnBuscar.setBackground(new java.awt.Color(204, 255, 204));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.setBorder(null);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        rbtNovoPaciente.setText("P");
        rbtNovoPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtNovoPacienteActionPerformed(evt);
            }
        });

        clvPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        clvPacientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clvPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clvPacientesMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(clvPacientes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jBiometria, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(tbmatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(tbnomepac, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(jtpBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(rbtNovoPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(pbBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jBiometria))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(tbmatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel2)
                        .addGap(1, 1, 1)
                        .addComponent(tbnomepac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jtpBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(rbtNovoPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pbBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 11, 720, 170);

        jPanel2.setBackground(new java.awt.Color(101, 227, 255));

        tgbconsulta.setText("Consulta");
        tgbconsulta.setOpaque(true);

        tgbrevisao.setText("Revisão");
        tgbrevisao.setOpaque(true);

        tgbcortesia.setText("Cortesia");

        lbespecialidade.setBackground(new java.awt.Color(254, 254, 254));
        lbespecialidade.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbespecialidade.setOpaque(true);

        lbmedico.setBackground(new java.awt.Color(254, 254, 254));
        lbmedico.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbmedico.setOpaque(true);

        btSeekMed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        btSeekMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSeekMedActionPerformed(evt);
            }
        });

        btMedicos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/personal.png"))); // NOI18N
        btMedicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMedicosActionPerformed(evt);
            }
        });

        jLabel3.setText("Indicado Por:");

        jIndicacao.setEditable(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jIndicacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(tgbconsulta)
                        .addGap(6, 6, 6)
                        .addComponent(tgbrevisao)
                        .addGap(6, 6, 6)
                        .addComponent(tgbcortesia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(lbespecialidade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lbmedico, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btSeekMed, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(btMedicos, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbespecialidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbmedico, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSeekMed, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btMedicos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tgbconsulta)
                    .addComponent(tgbrevisao)
                    .addComponent(tgbcortesia))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jIndicacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(10, 190, 720, 50);

        jpnMedicosOnOff.setBackground(new java.awt.Color(0, 180, 255));
        jpnMedicosOnOff.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpnMedicosOnOff.setLayout(null);

        jtxBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxBuscaKeyReleased(evt);
            }
        });
        jpnMedicosOnOff.add(jtxBusca);
        jtxBusca.setBounds(2, 2, 546, 20);

        medicoson.setAutoCreateRowSorter(true);
        medicoson.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        medicoson.setGridColor(new java.awt.Color(1, 1, 1));
        medicoson.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        medicoson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                medicosonMouseReleased(evt);
            }
        });
        medicoson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                medicosonKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(medicoson);

        jpnMedicosOnOff.add(jScrollPane3);
        jScrollPane3.setBounds(2, 28, 546, 113);

        getContentPane().add(jpnMedicosOnOff);
        jpnMedicosOnOff.setBounds(-10, 20, 0, 156);

        jpnMedicos.setBackground(new java.awt.Color(8, 213, 90));
        jpnMedicos.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpnMedicos.setLayout(null);

        clvSeek.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        clvSeek.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clvSeekMouseReleased(evt);
            }
        });
        clvSeek.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                clvSeekKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(clvSeek);

        jpnMedicos.add(jScrollPane5);
        jScrollPane5.setBounds(2, 28, 570, 110);

        tbSeekMed.setText("jTextField1");
        tbSeekMed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbSeekMedKeyReleased(evt);
            }
        });
        jpnMedicos.add(tbSeekMed);
        tbSeekMed.setBounds(2, 2, 570, 20);

        getContentPane().add(jpnMedicos);
        jpnMedicos.setBounds(150, -30, 0, 150);

        jPanel4.setBackground(new java.awt.Color(101, 227, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        clvEspera.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        clvEspera.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clvEspera.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clvEsperaMouseReleased(evt);
            }
        });
        clvEspera.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                clvEsperaKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(clvEspera);

        jTooTips.setBackground(new java.awt.Color(253, 254, 136));
        jTooTips.setFont(new java.awt.Font("Ubuntu", 1, 10)); // NOI18N
        jTooTips.setForeground(new java.awt.Color(255, 0, 0));
        jTooTips.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jTooTips.setOpaque(true);

        lbProcurar.setText("Procurar:");

        jtxtProcurar.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jtxtProcurar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtProcurarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jTooTips, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbProcurar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jtxtProcurar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jbarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTooTips, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbProcurar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtProcurar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39))
        );

        getContentPane().add(jPanel4);
        jPanel4.setBounds(10, 240, 720, 150);

        jPanel6.setBackground(new java.awt.Color(101, 227, 255));

        rbtEsperar.setBackground(new java.awt.Color(239, 242, 55));
        rbtEsperar.setText("Esperar");
        rbtEsperar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtEsperarActionPerformed(evt);
            }
        });

        rbtDesistiu.setBackground(new java.awt.Color(255, 6, 0));
        rbtDesistiu.setText("Desistiu");
        rbtDesistiu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtDesistiuActionPerformed(evt);
            }
        });

        rbtSaidaPaciente.setBackground(new java.awt.Color(92, 193, 1));
        rbtSaidaPaciente.setText("Saida do Paciênte");
        rbtSaidaPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtSaidaPacienteActionPerformed(evt);
            }
        });

        rbtLimpar.setBackground(new java.awt.Color(182, 97, 178));
        rbtLimpar.setText("Limpar");
        rbtLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtLimparActionPerformed(evt);
            }
        });

        rbtListaEncerrados.setBackground(new java.awt.Color(131, 144, 232));
        rbtListaEncerrados.setText("Paciêntes Encerrados");
        rbtListaEncerrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtListaEncerradosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtEsperar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(rbtDesistiu, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(rbtSaidaPaciente)
                .addGap(10, 10, 10)
                .addComponent(rbtLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(112, 112, 112)
                .addComponent(rbtListaEncerrados)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rbtDesistiu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtSaidaPaciente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtLimpar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtListaEncerrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtEsperar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel6);
        jPanel6.setBounds(10, 390, 710, 40);

        jPanel7.setBackground(new java.awt.Color(101, 227, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jpnMedicosemAtendimento.setBackground(new java.awt.Color(101, 227, 255));

        clvMedicos.setAutoCreateRowSorter(true);
        clvMedicos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clvMedicos.setSurrendersFocusOnKeystroke(true);
        clvMedicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clvMedicosMouseReleased(evt);
            }
        });
        clvMedicos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                clvMedicosKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(clvMedicos);

        jpnMedicosemAtendimento.addTab("<html><b>Médicos em Atendimento</b> <font color=red>(Para ver Agenda selecione o Médico e pressione a letra 'A'.)</font></html>", jScrollPane2);

        jTabbedPane2.setBackground(new java.awt.Color(101, 227, 255));

        exExames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        exExames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(exExames);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Exames", jPanel9);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        jpnMedicosemAtendimento.addTab("Autorizações", jPanel8);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpnMedicosemAtendimento, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpnMedicosemAtendimento, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpnMedicosemAtendimento.getAccessibleContext().setAccessibleDescription("");

        getContentPane().add(jPanel7);
        jPanel7.setBounds(10, 430, 720, 140);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        try { os.println("/quit"); } catch (Exception e) {}
    }//GEN-LAST:event_formInternalFrameClosed

    private void jBiometriaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBiometriaMouseReleased
        bioVerificar oTela = new bioVerificar(null, true);
        oTela.setVisible(true);
        String bID = oTela.get();
        oTela = null;
        if (!bID.isEmpty()) {
            int row = clvPacientes.getSelectedRow();
            jFichaPaciente oFichaPac = new jFichaPaciente(null, true);
            oFichaPac.ReadFields("WHERE pc_numero = '" + bID + "';");
            oFichaPac.setVisible(true);
        }
    }//GEN-LAST:event_jBiometriaMouseReleased

    private void tbmatriculaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbmatriculaKeyReleased
        String tinsc = tbmatricula.getText().trim();
        if (tinsc.length() >= 3) {
            String txtBusca = "";String nrPac = tbmatricula.getText().trim();
            if (jtpBusca.getSelectedIndex() == 0) {
                txtBusca = "WHERE Upper(pc_inscricao) LIKE '" + nrPac + "%'";
            }
            if (jtpBusca.getSelectedIndex() == 1) {
                txtBusca = "WHERE Upper(pc_inscricao) LIKE '%" + nrPac + "%'";
            } else {
                txtBusca = "WHERE Upper(pc_inscricao) LIKE '" + nrPac + "'";
            }
            PegarPacientesCadastro(txtBusca);
        }
    }//GEN-LAST:event_tbmatriculaKeyReleased

    private void tbnomepacKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbnomepacKeyTyped
        try {
            w.cancel(true);
            w = null;
        } catch (Exception e) {}
        btnBuscar.setEnabled(true);
        //        String tnome = tbnomepac.getText().trim();
        //        try {
            //          TableControl.Clear(clvPacientes);
            //        } catch (Exception err) {}
        //        if (tnome.length() >= 3) {
            //          try {
                //            digitacao.stop();
                //          } catch (Exception err) {}
            //          digitacao = null;
            //          digitacao = new Thread() {
                //            public void run() {
                    //              try {
                        //                sleep(100L);
                        //              } catch (Exception ex) {}
                    //              Cursor cursor = Cursor.getPredefinedCursor(3);
                    //              setCursor(cursor);
                    //
                    //              String txtBusca = "";String nmPac = tbnomepac.getText().trim().toUpperCase();
                    //              int ntpbusca = jtpBusca.getSelectedIndex();
                    //              if (ntpbusca == 0) {
                        //                txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "%'";
                        //              }
                    //              if (ntpbusca == 1) {
                        //                txtBusca = "WHERE Upper(pc_nome) LIKE '%" + nmPac + "%'";
                        //              } else if (ntpbusca == 2) {
                        //                txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "'";
                        //              }
                    //              PegarPacientesCadastro(txtBusca);
                    //
                    //              cursor = Cursor.getDefaultCursor();
                    //              setCursor(cursor);
                    //            }
                //          };
            //          digitacao.start();
            //        }
    }//GEN-LAST:event_tbnomepacKeyTyped

    private void jtpBuscaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtpBuscaItemStateChanged
        buscaNomesPac();
    }//GEN-LAST:event_jtpBuscaItemStateChanged

    private void jtpBuscaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jtpBuscaPropertyChange

    }//GEN-LAST:event_jtpBuscaPropertyChange

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String tnome = tbnomepac.getText().trim();
        try {
            TableControl.Clear(clvPacientes);
        } catch (Exception err) {}
        if (tnome.length() >= 3) {
            Cursor cursor = Cursor.getPredefinedCursor(3);
            setCursor(cursor);

            String txtBusca = "";String nmPac = "";
            if (tbnomepac.getText().trim().length() != 0) {
                nmPac = tbnomepac.getText().trim().toUpperCase();
                int ntpbusca = jtpBusca.getSelectedIndex();
                if (ntpbusca == 0) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "%'";
                }
                if (ntpbusca == 1) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '%" + nmPac + "%'";
                } else if (ntpbusca == 2) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "'";
                }
            } else {
                if (tbmatricula.getText().trim().length() != 0) {
                    nmPac = tbmatricula.getText().trim();
                    int ntpbusca = jtpBusca.getSelectedIndex();
                    if (ntpbusca == 0) {
                        txtBusca = "WHERE Upper(pc_matricula) LIKE '" + nmPac + "%'";
                    }
                    if (ntpbusca == 1) {
                        txtBusca = "WHERE Upper(pc_matricula) LIKE '%" + nmPac + "%'";
                    } else if (ntpbusca == 2) {
                        txtBusca = "WHERE Upper(pc_matricula) LIKE '" + nmPac + "'";
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Você deve Digitar uma matricula ou nome a pesquisar!","Atenção",INFORMATION_MESSAGE);
                    cursor = Cursor.getDefaultCursor();
                    setCursor(cursor);
                    return;
                }
            }
            PegarPacientesCadastro(txtBusca);

            cursor = Cursor.getDefaultCursor();
            setCursor(cursor);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void rbtNovoPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtNovoPacienteActionPerformed
        jFichaPaciente oFichaPac = new jFichaPaciente(null, true);
        oFichaPac.setNovo(true);
        oFichaPac.setVisible(true);
        String newNamePac = oFichaPac.getNomePac();
        if (!newNamePac.trim().equalsIgnoreCase("")) {
            tbnomepac.setText(newNamePac);buscaNomesPac();
        }
    }//GEN-LAST:event_rbtNovoPacienteActionPerformed

    private void clvPacientesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clvPacientesMouseReleased
        try {
            w.cancel(true);
            w = null;
        } catch (Exception e) {}

        int row = clvPacientes.getSelectedRow();
        String tficha = clvPacientes.getModel().getValueAt(row, 1).toString();
        jFichaPaciente oFichaPac = new jFichaPaciente(null, true);
        oFichaPac.ReadFields("WHERE pc_numero = '" + tficha + "';");
        oFichaPac.setVisible(true);
    }//GEN-LAST:event_clvPacientesMouseReleased

    private void btSeekMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSeekMedActionPerformed
        jpnMedicos.setVisible(!jpnMedicos.isVisible());
        jpnMedicos.setBounds(160, 240, 550, 150);
        jpnMedicosOnOff.setVisible(false);
        tbSeekMed.setText("");
        tbSeekMed.requestFocus();
    }//GEN-LAST:event_btSeekMedActionPerformed

    private void btMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMedicosActionPerformed
        if (!jpnMedicosOnOff.isVisible()) {
            jpnMedicosOnOff.setVisible(true);
            jpnMedicosOnOff.setBounds(160, 240, 550, 150);
            jpnMedicos.setVisible(false);
            jtxBusca.setText("");
            jtxBusca.requestFocus();
        } else {
            jpnMedicosOnOff.setVisible(false);

            String tmedon = "";
            for (int i = 0; i < medicoson.getRowCount(); i++) {
                int row = medicoson.convertRowIndexToModel(i);
                if (medicoson.getModel().getValueAt(row, 4).toString().equalsIgnoreCase("X")) {
                    String col0 = medicoson.getModel().getValueAt(row, 0).toString();
                    tmedon = tmedon + col0 + ";";
                }
            }
            new Settings().Set("medicoson", tmedon);
        }
    }//GEN-LAST:event_btMedicosActionPerformed

    private void clvEsperaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clvEsperaMouseReleased
        ShowjTooTips();
    }//GEN-LAST:event_clvEsperaMouseReleased

    private void clvEsperaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clvEsperaKeyReleased
        ShowjTooTips();
    }//GEN-LAST:event_clvEsperaKeyReleased

    private void jtxtProcurarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtProcurarKeyReleased
        if ("".equals(jtxtProcurar.getText().trim())) {
            sorter3.setRowFilter(null);
        } else {
            try {
                sorter3.setRowFilter(RowFilter.regexFilter(jtxtProcurar.getText().trim(), new int[0]));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_jtxtProcurarKeyReleased

    private void rbtEsperarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtEsperarActionPerformed
        int sRow = clvPacientes.getSelectedRow();
        if (sRow < 0) return;

        String thora = Dates.DateFormat("HH:mm:ss", new Date());
        boolean batend = true;
        int mRow = clvPacientes.convertRowIndexToModel(sRow);
        String cdpac = ""; String cdmed = "";
        try {cdpac = clvPacientes.getModel().getValueAt(mRow, 1).toString().trim();} catch (Exception err)  { cdpac = ""; }
        try {cdmed = lbespecialidade.getToolTipText();} catch (Exception err)  { cdmed = ""; }
        int cpos = FuncoesGlobais.FindinArrays(listaespera, new int[] {2,8}, new String[]{cdpac,cdmed});
        if (cpos != -1) {
            JOptionPane.showMessageDialog(null, "Paciente já esta na lista de espera!!!", "Atencao!!!", 1);
        } else {
            if (cdpac.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(null, "Não há um paciente selecionado!!!", "Atencao!!!", 1);
                batend = false;
            }
            if ((!tgbconsulta.isSelected()) && (!tgbrevisao.isSelected()) && (!tgbcortesia.isSelected())) {
                JOptionPane.showMessageDialog(null, "Voce deve selecionar um tipo de antendimento!!!", "Atencao!!!", 1);
                batend = false;
            }
            if (lbmedico.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "selecione um medico na lista!!!", "Atencao!!!", 1);
                batend = false;
            }
            if (jIndicacao.getModel().getSelectedItem().toString().trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "Voce deve informar a indicacao!!!", "Atencao!!!", 1);
                batend = false;
            }
            if (jIndicacao.getSelectedIndex() < 0) {
                String newIndic = jIndicacao.getModel().getSelectedItem().toString().toUpperCase().trim();
                jIndicacao.addItem(newIndic);
            }
            if (batend) {
                String[][] dadospac = new String[0][];
                try {
                    dadospac = conn.LerCamposTabela(new String[] { "pc_convnumero", "pc_convenio", "pc_inscricao", "pc_nome" }, "pacientes", "pc_numero = '" + cdpac + "'");
                } catch (Exception err) {}

                String[][] dadosconv = new String[][] {};
                try {
                    //dadosconv = conn.LerCamposTabela(new String[] { "cv_tabelas", "cv_txconsulta", "cv_txproced", "cv_vrpconmed", "cv_vrppromed" }, "convenios", "cv_numero = '" + dadospac[0][3] + "'");
                    dadosconv = conn.LerCamposTabela(new String[] { "cv_tabelas", "cv_txconsulta", "cv_txproced", "cv_consultavr", "cv_medicovr"}, "convenios", "cv_numero = '" + dadospac[0][3] + "'");
                } catch (Exception err) {}
           
                // Checa se Medico x Convenio
                Object[][] ddMedTaxas = null;
                try {
                    ddMedTaxas = conn.LerCamposTabela(new String[] {"mt_valor", "mt_valormed"}, "medtaxas", "mt_cdmedico = ? and mt_cdconvenio = ?",new Object[][] {
                        {"int", Integer.valueOf(lbespecialidade.getToolTipText().trim())},
                        {"int", Integer.valueOf(dadospac[0][3])}
                    });
                } catch (Exception e) {}
                
                if (ddMedTaxas != null) {
                    if (ddMedTaxas[0][3] == null && ddMedTaxas[1][3] == null) ddMedTaxas = null;
                }
                
                boolean cbTaxa = false;
                int tpTaxa = 0;
                if (ddMedTaxas == null) {
                    if (tgbconsulta.isSelected()) {
                        if (LerValor.StringToFloat(dadosconv[1][3]) != 0f || LerValor.StringToFloat(dadosconv[2][3]) != 0f) {
                            cbTaxa = true;

                            Object[] options = { "Consulta R$ " + LerValor.floatToCurrency(LerValor.StringToFloat(dadosconv[1][3]), 2), "Procedimento R$ " + LerValor.floatToCurrency(LerValor.StringToFloat(dadosconv[2][3]), 2) };
                            tpTaxa = JOptionPane.showOptionDialog(this, "Cobrar taxa?", "Atenção", 0, 3, null, options, options[0]);
                        }
                    }
                } else {
                    if (Float.valueOf(ddMedTaxas[0][3].toString()) != 0f || Float.valueOf(ddMedTaxas[1][3].toString()) != 0f) {
                        dadosconv[1][3] = ddMedTaxas[0][3].toString().replace(".", ",");
                        dadosconv[2][3] = ddMedTaxas[1][3].toString().replace(".", ",");
                    }
                    if (LerValor.StringToFloat(dadosconv[1][3]) != 0f || LerValor.StringToFloat(dadosconv[2][3]) != 0f) {
                        cbTaxa = true;

                        Object[] options = { "Consulta R$ " + LerValor.floatToCurrency(LerValor.StringToFloat(dadosconv[1][3]), 2), "Procedimento R$ " + LerValor.floatToCurrency(LerValor.StringToFloat(dadosconv[2][3]), 2) };
                        tpTaxa = JOptionPane.showOptionDialog(this, "Cobrar taxa?", "Atenção", 0, 3, null, options, options[0]);
                    } else {
                        cbTaxa = true;                        
                    }
                }
                
                int aut = 0; String nvrconsulta = "0,00"; String nvrmedico = "0,00"; 
                String[] tipopag = new String[] {"ND","DN","CH","CT"}; String pagtipo = "";
                if (tgbconsulta.isSelected() && (dadospac[1][3].trim().toUpperCase().equalsIgnoreCase("PARTICULAR") || cbTaxa)) {
                    jPagRec tpg = new jPagRec(null, true);
                    try {
                        String[][] vrconsulta = null;
                        if (!cbTaxa) {
                            vrconsulta = conn.LerCamposTabela(new String[] {"md_vrconsulta", "md_vrmedico"}, "medicos", "md_codigo = '" + lbespecialidade.getToolTipText().trim() + "'");
                        } else {
                            String tvrconsulta = "";
                            String tvrconmed = "";
                            if (dadosconv[0][3].toString().equalsIgnoreCase("0")) {
                                // valor
                                if (tpTaxa == 0) {
                                    tvrconsulta = dadosconv[1][3].toString();
                                    tvrconmed = dadosconv[3][3].toString();
                                } else {
                                    tvrconsulta = dadosconv[2][3].toString();
                                    tvrconmed = dadosconv[4][3].toString();
                                }
                            } else {
                                if (ddMedTaxas == null) {
                                    // percentual
                                    if (tpTaxa == 0) {
                                        tvrconsulta = dadosconv[1][3].toString();
                                        tvrconmed = LerValor.FloatToString(LerValor.StringToFloat(dadosconv[1][3].toString()) * (LerValor.StringToFloat(dadosconv[3][3].toString()) / 100));
                                    } else {
                                        tvrconsulta = dadosconv[2][3].toString();
                                        tvrconmed = LerValor.FloatToString(LerValor.StringToFloat(dadosconv[2][3].toString()) * (LerValor.StringToFloat(dadosconv[4][3].toString()) / 100));
                                    }                        
                                } else {
                                        tvrconsulta = ddMedTaxas[0][3].toString();
                                        tvrconmed = ddMedTaxas[1][3].toString();
                                }
                            }  
                            vrconsulta = new String[][] {{"0","0","0",tvrconsulta}, {"0","0","0",tvrconmed}};                            
                        }
                        if (vrconsulta != null) {
                            tpg.setValor(vrconsulta[0][3].replace(".", ","));
                            nvrconsulta = vrconsulta[0][3];
                            nvrmedico = vrconsulta[1][3];
                        } else {
                            tpg.setValor("0,00");
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    tpg.setVisible(true);
                    Object[] retorno = tpg.getPagRec();
                    // System.out.println("tipo: " + retorno[0]);
                    // System.out.println("CT: " + retorno[1]);
                    // System.out.println("BC: " + ((Object[])retorno[2])[0]);
                    // System.out.println("AG: " + ((Object[])retorno[2])[1]);
                    // System.out.println("NR: " + ((Object[])retorno[2])[2]);
                    // System.out.println("DT: " + ((Object[])retorno[2])[3]);
                    tpg = null;
                    
                    if (Integer.valueOf(retorno[0].toString()) == 0) return;
                    if (ddMedTaxas == null) {
                        java.util.Date datap = Dates.StringtoDate((String)((Object[])retorno[2])[3],"dd-MM-yyyy");
                        String pbanco = (String)((Object[])retorno[2])[0];
                        String pagencia = (String)((Object[])retorno[2])[1];
                        String pncheque = (String)((Object[])retorno[2])[2];
                        String pnrcartao = (String)retorno[1];
                        pagtipo = tipopag[Integer.valueOf(retorno[0].toString())];
                        aut = conn.LancarCaixa(
                            "CRE",
                            pagtipo,
                            "REC",
                            Integer.valueOf(cdpac),
                            datap,
                            pbanco,
                            pagencia,
                            pncheque,
                            pnrcartao,
                            new BigDecimal(nvrconsulta),
                            Integer.valueOf(VariaveisGlobais.cdlogado)
                        );
                        if (aut == 0) return;                        
                        //ddMedTaxas = null;
                    }
                } else {
                    if (ddMedTaxas != null) {
                        String[][] vrconsulta = {{"0","0","0",(String)ddMedTaxas[0][3]}, {"0","0","0",(String)ddMedTaxas[1][3]}};
                        nvrconsulta = vrconsulta[0][3];
                        nvrmedico = vrconsulta[1][3];
                        aut = -1;
                    } else {
                        nvrconsulta = dadosconv[3][3];
                        nvrmedico = dadosconv[4][3];
                    }                   
                }

                if (dadospac[1][3].trim().toUpperCase().equalsIgnoreCase("PARTICULAR")) {
                    if (nvrconsulta.contains(",")) nvrconsulta = nvrconsulta.replace(",", ".");
                    if (nvrmedico.contains(",")) nvrmedico = nvrmedico.replace(",", ".");
                    String msql = "insert into nparticular (" +
                    "ma_data, ma_codmedico, ma_medico, ma_categoria, " +
                    "ma_nome, ma_tpatd, ma_pcnumero, aut, valor, valormed) values ('" +
                    Dates.DateFormat("yyyy-MM-dd", new Date()) + "','" +
                    lbespecialidade.getToolTipText().trim() + "','" +
                    lbmedico.getText() + "','" +
                    lbespecialidade.getText() + "','" +
                    dadospac[3][3].toUpperCase() + "','" +
                    (tgbconsulta.isSelected() ? "C" : tgbrevisao.isSelected() ? "R" : "G") + "','" +
                    cdpac + "','" +
                    aut + "','" +
                    nvrconsulta + "','" +
                    nvrmedico + "')";
                    try {conn.ExecutarComando(msql);} catch (Exception e) {e.printStackTrace();}
                    
                    // Impressão do recibo
                    ImprimeReciboParticularPDF(aut,new String[][] {{"","","","",nvrconsulta,pagtipo,"RC","",""}},nvrconsulta,"F",VariaveisGlobais.nviasRecibo,VariaveisGlobais.nviasRecibo);
                } else if (cbTaxa && ddMedTaxas == null) {
                    if (nvrconsulta.contains(",")) nvrconsulta = nvrconsulta.replace(",", ".");
                    if (nvrmedico.contains(",")) nvrmedico = nvrmedico.replace(",", ".");
                    String ctSQL = "insert into convtaxas (ct_data, ct_conv, ct_medico, ct_pcnumero, ct_valor, ct_vrmed, ct_conproc, ct_tppagto, ct_aut) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    Object[][] param = new Object[][] {
                        {"date", Dates.toSqlDate(new Date())},
                        {"int", Integer.valueOf(dadospac[0][3].trim())},
                        {"int", Integer.valueOf(lbespecialidade.getToolTipText().trim())},
                        {"int", Integer.valueOf(cdpac)},
                        {"decimal", new BigDecimal(nvrconsulta)},
                        {"decimal", new BigDecimal(nvrmedico)},
                        {"int",Integer.valueOf(dadosconv[0][3].toString())},
                        {"string",pagtipo},
                        {"int", aut}
                    };
                    try {conn.ExecutarComando(ctSQL, param);} catch (Exception e) {e.printStackTrace();}
                } else if (cbTaxa && ddMedTaxas != null) {
                    if (nvrconsulta.contains(",")) nvrconsulta = nvrconsulta.replace(",", ".");
                    if (nvrmedico.contains(",")) nvrmedico = nvrmedico.replace(",", ".");

                    // Salva no caixa como TX
                    java.util.Date datap = new java.util.Date();
                    aut = conn.LancarCaixa(
                        "CRE",
                        "TX",
                        "REC",
                        Integer.valueOf(cdpac),
                        datap,
                        "",
                        "",
                        "",
                        "",
                        new BigDecimal(nvrconsulta),
                        Integer.valueOf(VariaveisGlobais.cdlogado)
                    );
                    if (aut == 0) return;
                    
                    String ctSQL = "insert into convtaxas (ct_data, ct_conv, ct_medico, ct_pcnumero, ct_valor, ct_vrmed, ct_conproc, ct_tppagto, ct_aut) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    Object[][] param = new Object[][] {
                        {"date", Dates.toSqlDate(new Date())},
                        {"int", Integer.valueOf(dadospac[0][3].trim())},
                        {"int", Integer.valueOf(lbespecialidade.getToolTipText().trim())},
                        {"int", Integer.valueOf(cdpac)},
                        {"decimal", new BigDecimal(nvrconsulta)},
                        {"decimal", new BigDecimal(nvrmedico)},
                        {"int",Integer.valueOf("0")},
                        {"string","TX"},
                        {"int", aut}
                    };
                    try {conn.ExecutarComando(ctSQL, param);} catch (Exception e) {e.printStackTrace();}
                    
                    // Impressao do Recibo
                    ImprimeReciboTaxasPDF(aut,new String[][] {{"","","","",nvrconsulta,pagtipo,"RC","",""}},nvrconsulta,"F",VariaveisGlobais.nviasRecibo,VariaveisGlobais.nviasRecibo);
                }

                // Atualiza marcar
                if (nvrconsulta.contains(",")) nvrconsulta = nvrconsulta.replace(",", ".");
                if (nvrmedico.contains(",")) nvrmedico = nvrmedico.replace(",", ".");
                String isql = "insert into marcar (ma_data, ma_hora, ma_codmedico, ma_medico, ma_categoria, ma_nome, ma_consulta, ma_revisao, ma_cortesia, ma_status, ma_plano, ma_inscricao, ma_origem, ma_convnumero,ma_pcnumero,ma_indicado,autenticacao,cv_consultavr,cv_medicovr) values ('" + 
                        Dates.DateFormat("yyyy-MM-dd", new Date()) + "','" + thora + "','" + lbespecialidade.getToolTipText().trim() + "','" + lbmedico.getText() + "','" +
                        lbespecialidade.getText() + "','" + dadospac[3][3].toUpperCase() + "','" + (tgbconsulta.isSelected() ? 1 : 0) + "','" + 
                        (tgbrevisao.isSelected() ? 1 : 0) + "','" + (tgbcortesia.isSelected() ? 1 : 0) + "','0','" + dadospac[1][3].toUpperCase().trim() + "','" + 
                        dadospac[2][3].trim() + "','" + VariaveisGlobais.origem + "','" + dadospac[0][3].trim() + "','" + cdpac + "','" + 
                        jIndicacao.getModel().getSelectedItem().toString().toUpperCase().trim() + "','" + aut + "'," + 
                        new BigDecimal(nvrconsulta) + "," + new BigDecimal(nvrmedico) + ")";
                conn.ExecutarComando(isql);

                conn.Auditor("CONSULTA: INSERIU:" + lbmedico.getText().toUpperCase().trim(), dadospac[3][3].toUpperCase().trim());
                try {
                    os.println("#rec " + cdpac + ";ins");
                } catch (Exception err) {}
                int mdpos = TableControl.seek(medicoson, 0, lbespecialidade.getToolTipText().trim());
                int mdRow = medicoson.convertRowIndexToModel(mdpos);
                String lgmedico = medicoson.getModel().getValueAt(mdRow, 1).toString().trim();
                try {
                    os.println("@" + lgmedico.toLowerCase() + " " + cdpac + ";ins");
                } catch (Exception err) {}
                addespera(thora, String.valueOf(aut));
                limpatela();
            }
        }
    }//GEN-LAST:event_rbtEsperarActionPerformed

   public void ImprimeReciboParticularPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper,int via, float nRecibos) {
       System.out.println("ImprimeReciboParticularPDF");

        int sRow = clvPacientes.getSelectedRow();
        if (sRow < 0) return;
        int mRow = clvPacientes.convertRowIndexToModel(sRow);
        String cdpac = ""; String cdmed = ""; String nmmed = ""; String nmesp = ""; String nmpac = null;
        try {cdpac = clvPacientes.getModel().getValueAt(mRow, 1).toString().trim();} catch (Exception err)  { cdpac = ""; }
        try {nmpac = clvPacientes.getModel().getValueAt(mRow, 3).toString().trim();} catch (Exception err)  { nmpac = ""; }
        try {cdmed = lbespecialidade.getToolTipText();} catch (Exception err)  { cdmed = ""; }
        nmmed = lbmedico.getText().toUpperCase();
        try {nmesp = lbespecialidade.getText();} catch (Exception err)  { cdmed = ""; }
       
       float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();
        pdf.setPathName("reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String docID = "_" + cdpac + ".pdf";
        String docName = "RC_" + Dates.DateFormat("ddMMyyyy", new Date()) + "_" + 
                FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" + 
                FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + docID;
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            Image t_img = new ResizeImageIcon("E", VariaveisGlobais.logo, 80, 30).getImg().getImage();
            img = com.itextpdf.text.Image.getInstance(t_img, Color.BLACK);
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("cnpj"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print((nAut >0 ? gVar.get("recibo") : "D E M O N S T R A T I V O"), pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.logado,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormat("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        cell1 = new PdfPCell(new Phrase("Paciente: " + cdpac,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(nmpac.toString()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);        
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        PdfPCell cell3 = new PdfPCell(new Phrase("Médico: " + cdmed,font));
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell3.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell3);
        PdfPCell cell4 = new PdfPCell(new Phrase(StringManager.ConvStr(nmmed.toString()),font));
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell4);        

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        PdfPCell cell5 = new PdfPCell(new Phrase("",font));
        cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell5.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell5);
        PdfPCell cell6 = new PdfPCell(new Phrase(StringManager.ConvStr(nmesp.toString()),font));
        cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell6.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell6);        
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        // Dados do recibo
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase((String) "Consulta",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        
        cell3 = new PdfPCell(new Phrase(LerValor.floatToCurrency(Float.valueOf(ValorRec),2), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase(LerValor.floatToCurrency(Float.valueOf(ValorRec),2), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else {
                    bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();
                }

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "RC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" +
                          FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "/" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + 
                          Dates.DateFormat("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec.replace(".", ","), 2) + VariaveisGlobais.logado, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pdf.getPathName() + docName, VariaveisGlobais.Recibo.split(",")[0],VariaveisGlobais.Recibo.split(",")[1],VariaveisGlobais.Recibo.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
   }
    
   public void ImprimeReciboTaxasPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper,int via, float nRecibos) {
       System.out.println("ImprimeReciboTaxasPDF");

        int sRow = clvPacientes.getSelectedRow();
        if (sRow < 0) return;
        int mRow = clvPacientes.convertRowIndexToModel(sRow);
        String cdpac = ""; String cdmed = ""; String nmmed = ""; String nmesp = ""; String nmpac = null;
        try {cdpac = clvPacientes.getModel().getValueAt(mRow, 1).toString().trim();} catch (Exception err)  { cdpac = ""; }
        try {nmpac = clvPacientes.getModel().getValueAt(mRow, 3).toString().trim();} catch (Exception err)  { nmpac = ""; }
        try {cdmed = lbespecialidade.getToolTipText();} catch (Exception err)  { cdmed = ""; }
        nmmed = lbmedico.getText().toUpperCase();
        try {nmesp = lbespecialidade.getText();} catch (Exception err)  { cdmed = ""; }
       
       float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();
        pdf.setPathName("reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String docID = "_" + cdpac + ".pdf";
        String docName = "RC_" + Dates.DateFormat("ddMMyyyy", new Date()) + "_" + 
                FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" + 
                FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + docID;
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            Image t_img = new ResizeImageIcon("E", VariaveisGlobais.logo, 80, 30).getImg().getImage();
            img = com.itextpdf.text.Image.getInstance(t_img, Color.BLACK);
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
            p = pdf.print(gVar.get("cnpj"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print((nAut >0 ? gVar.get("recibo") : "D E M O N S T R A T I V O"), pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.logado,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormat("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        cell1 = new PdfPCell(new Phrase("Paciente: " + cdpac,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(nmpac.toString()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);        
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        PdfPCell cell3 = new PdfPCell(new Phrase("Médico: " + cdmed,font));
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell3.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell3);
        PdfPCell cell4 = new PdfPCell(new Phrase(StringManager.ConvStr(nmmed.toString()),font));
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell4);        

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        PdfPCell cell5 = new PdfPCell(new Phrase("",font));
        cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell5.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell5);
        PdfPCell cell6 = new PdfPCell(new Phrase(StringManager.ConvStr(nmesp.toString()),font));
        cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell6.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell6);        
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        columnWidths = new float[] {70, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        // Dados do recibo
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase((String) "Taxa",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        
        cell3 = new PdfPCell(new Phrase(LerValor.floatToCurrency(Float.valueOf(ValorRec),2), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell3 = new PdfPCell(new Phrase(LerValor.floatToCurrency(Float.valueOf(ValorRec),2), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else {
                    bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(LerValor.floatToCurrency(Float.valueOf(Valores[i][4]),2),10).LPad();
                }

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "RC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" +
                          FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "/" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + 
                          Dates.DateFormat("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec.replace(".", ","), 2) + VariaveisGlobais.logado, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pdf.getPathName() + docName, VariaveisGlobais.Recibo.split(",")[0],VariaveisGlobais.Recibo.split(",")[1],VariaveisGlobais.Recibo.split(",")[2]);
        pdf.setPathName("");
        pdf.setDocName("");
   }
    
    private void rbtDesistiuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtDesistiuActionPerformed
        if (clvEspera.getSelectedRow() == -1) {
            return;
        }
        int mRow = clvEspera.convertRowIndexToModel(clvEspera.getSelectedRow());
        String stat1 = "";String stat2 = "";String stat3 = "";
        try {
            stat1 = clvEspera.getModel().getValueAt(mRow, 11).toString().trim();
        } catch (Exception err) { stat1 = ""; }
        try {
            stat2 = clvEspera.getModel().getValueAt(mRow, 12).toString().trim();
        } catch (Exception err) { stat2 = ""; }
        try {
            stat3 = clvEspera.getModel().getValueAt(mRow, 13).toString().trim();
        } catch (Exception err) { stat3 = ""; }
        String cdmedico = clvEspera.getModel().getValueAt(mRow, 8).toString().trim();
        if ((!stat1.equalsIgnoreCase("W")) && ((stat2.equalsIgnoreCase("")) || (stat2.equalsIgnoreCase("0"))) && ((stat3.equalsIgnoreCase("")) || (stat3.equalsIgnoreCase("0")))) {
            JOptionPane.showMessageDialog(null, "Voce so pode desistir se o paciente nao foi atendido ainda!!!", "Atencao!!!", 1);
        } else {
            jDesistir oDes = new jDesistir(null, true);
            oDes.setVisible(true);
            String dMotivo = oDes.getMotivo();
            oDes = null;
            if (!dMotivo.trim().equalsIgnoreCase("")) {
                int row = clvEspera.convertRowIndexToModel(clvEspera.getSelectedRow());
                String cdpac = clvEspera.getModel().getValueAt(row, 2).toString().trim();
                String dsql = "delete from marcar where ma_data = '" + Dates.DateFormat("yyyy-MM-dd", new Date()) + "' and ma_pcnumero = '" + cdpac + "' and upper(ma_origem) = '" + VariaveisGlobais.origem.trim().toUpperCase() + "' and ma_status = 0";
                try {
                    conn.ExecutarComando(dsql);
                } catch (Exception err) {
                    err.printStackTrace();
                }
                
                // Deleta particular
                dsql = "delete from particular where upper(pa_nome) = '" + clvEspera.getModel().getValueAt(row, 4).toString().trim().toUpperCase() + "' and lower(pa_tipo) = 'consulta'";
                try {
                    conn.ExecutarComando(dsql);
                } catch (Exception err) {  err.printStackTrace(); }

                // Auditor
                conn.Auditor("CONSULTA:DESISTIU:" + dMotivo + ":" + clvEspera.getModel().getValueAt(row, 8).toString().trim(), clvEspera.getModel().getValueAt(row, 4).toString().trim());

                // Extorna caixa
                String naut = clvEspera.getModel().getValueAt(row, 20).toString().trim();
                try {
                    if (!naut.trim().equalsIgnoreCase("")) conn.ExecutarComando("UPDATE ncaixa SET oper = 'RECX' WHERE autenticacao = '" + naut + "';");
                    if (!naut.trim().equalsIgnoreCase("")) conn.ExecutarComando("DELETE FROM nparticular WHERE aut = '" + naut + "';");
                    if (!naut.trim().equalsIgnoreCase("")) conn.ExecutarComando("DELETE FROM convtaxas WHERE ct_aut = '" + naut + "';");
                } catch (Exception e) {}

                TableControl.del(clvEspera, row);

                int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
                if (pos > -1) {
                    listaespera = FuncoesGlobais.ArraysDel(listaespera, pos);
                }
                try {os.println("#rec " + cdpac + ";des");} catch (Exception e) {}

                int mdpos = TableControl.seek(medicoson, 0, cdmedico);
                int mdRow = medicoson.convertRowIndexToModel(mdpos);
                String lgmedico = medicoson.getModel().getValueAt(mdRow, 1).toString().trim();
                try {os.println("@" + lgmedico.toLowerCase() + " " + cdpac + ";des");} catch (Exception e) {}
            }
        }
    }//GEN-LAST:event_rbtDesistiuActionPerformed

    private void rbtSaidaPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtSaidaPacienteActionPerformed
        if (clvEspera.getSelectedRow() == -1) {
            return;
        }
        int mRow = clvEspera.convertRowIndexToModel(clvEspera.getSelectedRow());
        String cdpac = clvEspera.getModel().getValueAt(mRow, 2).toString().trim();
        String tstatus = clvEspera.getModel().getValueAt(mRow, 11).toString().trim();
        String torigem = VariaveisGlobais.origem;
        String thora = clvEspera.getModel().getValueAt(mRow, 1).toString().trim();
        String tdata = Dates.DateFormat("yyyy-MM-dd", new Date());
        String tcdmedico = clvEspera.getModel().getValueAt(mRow, 8).toString().trim();
        if (!tstatus.equalsIgnoreCase("E")) {
            JOptionPane.showMessageDialog(null, "Paciente ainda nao encerrado pelo medico!!!", "Atencao!!!", 1);
        }
        else
        {
            Object[] options = { "Sim", "Naoo" };
            int n = JOptionPane.showOptionDialog(null, "Deseja dar saida deste paciente ? ", "Atencao", 0, 3, null, options, options[0]);
            if (n == 0) {
                String[][] dadospac = new String[0][];
                try {
                    dadospac = conn.LerCamposTabela(new String[] { "pc_convnumero", "pc_convenio", "pc_inscricao", "pc_nome" }, "pacientes", "pc_numero = '" + cdpac + "'");
                } catch (Exception err) {}
                
                String dsql = "";
                String guia = "";
                if (!dadospac[1][3].trim().toUpperCase().equalsIgnoreCase("PARTICULAR")) {   
                    while (guia.isEmpty()) {
                        guia = JOptionPane.showInputDialog("Digite o número da guia:");
                        if (guia == null) guia = "";
                    }
                            
                } else {
                    guia = "";
                }

                dsql = "INSERT INTO faturar SELECT ma_data, ma_hora, ma_horasai, ma_medico, ma_indicado, ma_categoria, " +
                       "ma_nome, ma_consulta, ma_revisao, ma_procedimento, ma_medicamento, " +
                       "ma_status, ma_plano, ma_inscricao, ma_origem, ma_convnumero, " +
                       "ma_pcnumero, ma_cortesia, '" + guia + "', cv_numerador, ma_codmedico, " +
                       "autenticacao, cv_consultavr, cv_medicovr FROM marcar " +
                       "where Lower(ma_origem) = '" + torigem.toLowerCase() + "' AND ma_data = '" + tdata + "' AND ma_codmedico = " + tcdmedico + " AND ma_status = 4 AND ma_pcnumero = " + cdpac;

                try {
                    conn.ExecutarComando(dsql);
                } catch (Exception err) {}
                dsql = "delete from marcar where Lower(ma_origem) = '" + torigem.toLowerCase() + "' AND ma_data = '" + tdata + "' AND ma_codmedico = " + tcdmedico + " AND ma_status = 4 AND ma_pcnumero = " + cdpac + "";
                try {
                    conn.ExecutarComando(dsql);
                } catch (Exception err) {}
                conn.Auditor("CONSULTA:SAICLINICA", clvEspera.getModel().getValueAt(mRow, 4).toString().trim());

                TableControl.del(clvEspera, mRow);
                sorter3 = new TableRowSorter(clvEspera.getModel());
                clvEspera.setRowSorter(sorter3);

                int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
                if (pos > -1) {
                    String mhora = listaespera[pos][1];
                    String thsai = listaespera[pos][15];
                    String tnome = listaespera[pos][4];
                    String tmedico = listaespera[pos][9];

                    listaencerrados = FuncoesGlobais.ArraysAdd(listaencerrados, new String[] { mhora, thsai, tnome, tmedico });

                    listaespera = FuncoesGlobais.ArraysDel(listaespera, pos);
                }
                try {
                    os.println("#rec " + cdpac + ";sai");
                } catch (Exception e) {}
                int mdpos = TableControl.seek(medicoson, 0, tcdmedico);
                int mdRow = medicoson.convertRowIndexToModel(mdpos);
                String lgmedico = medicoson.getModel().getValueAt(mdRow, 1).toString().trim();
                try {
                    os.println("@" + lgmedico.toLowerCase() + " " + cdpac + ";sai");
                } catch (Exception e) {}
            }
        }
    }//GEN-LAST:event_rbtSaidaPacienteActionPerformed

    private void rbtLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtLimparActionPerformed
        limpatela();
    }//GEN-LAST:event_rbtLimparActionPerformed

    private void rbtListaEncerradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtListaEncerradosActionPerformed
        jEncerrados oEnc = new jEncerrados(null, true);
        oEnc.setLista(listaencerrados);
        oEnc.ShowLista();
        oEnc.setVisible(true);
        oEnc = null;
    }//GEN-LAST:event_rbtListaEncerradosActionPerformed

    private void clvMedicosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clvMedicosMouseReleased
        FiltraEspera();
    }//GEN-LAST:event_clvMedicosMouseReleased

    private void clvMedicosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clvMedicosKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_A) {
            int selRow = clvMedicos.getSelectedRow();
            int modelRow = clvMedicos.convertRowIndexToModel(selRow);
            String tcdmed = clvMedicos.getModel().getValueAt(modelRow, 0).toString().toUpperCase().trim();
            if (!tcdmed.equalsIgnoreCase("TODOS")) {                                  
                ChamaAgenda(tcdmed);
            }
        } else FiltraEspera();
        
    }//GEN-LAST:event_clvMedicosKeyReleased

    private void clvSeekMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clvSeekMouseReleased
        int selRow = clvSeek.getSelectedRow();
        int modelRow = clvSeek.convertRowIndexToModel(selRow);
        ShowDadosMedicoSelecionado(modelRow, true, evt.getButton());
    }//GEN-LAST:event_clvSeekMouseReleased

    private void clvSeekKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clvSeekKeyReleased
        int selRow = clvSeek.getSelectedRow();
        int modelRow = clvSeek.convertRowIndexToModel(selRow);
        ShowDadosMedicoSelecionado(modelRow, false, evt.getKeyCode());
    }//GEN-LAST:event_clvSeekKeyReleased

    private void tbSeekMedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbSeekMedKeyReleased
        if (evt.getKeyCode() == 40) {
            clvSeek.requestFocus();return;
        }
        if ("".equals(tbSeekMed.getText().trim())) {
            sorter2.setRowFilter(null);
        } else {
            try {
                sorter2.setRowFilter(RowFilter.regexFilter(tbSeekMed.getText().trim(), new int[0]));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_tbSeekMedKeyReleased

    private void jtxBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxBuscaKeyReleased
        if (evt.getKeyCode() == 40) {
            medicoson.requestFocus();return;
        }
        if ("".equals(jtxBusca.getText().trim())) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(jtxBusca.getText().trim(), new int[0]));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_jtxBuscaKeyReleased

    private void medicosonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medicosonMouseReleased
        int selRow = medicoson.getSelectedRow();
        int modelRow = medicoson.convertRowIndexToModel(selRow);
        ChangeMedicoOnOff(modelRow, true, evt.getButton());
    }//GEN-LAST:event_medicosonMouseReleased

    private void medicosonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_medicosonKeyReleased
        int selRow = medicoson.getSelectedRow();
        int modelRow = medicoson.convertRowIndexToModel(selRow);
        ChangeMedicoOnOff(modelRow, false, evt.getKeyCode());
    }//GEN-LAST:event_medicosonKeyReleased

    private void PegarPacientesCadastro(String sWHERE) {
        VariaveisGlobais.sWhere = sWHERE;
        pbBuscar.setStringPainted(true);
        pbBuscar.setValue(0);
        pbBuscar.setVisible(true);
       
        try { TableControl.Clear(clvPacientes); } catch (Exception err) {}
        String[][] cab = { { "origem", "ficha", "inscricao", "nome", "dias" }, { "100", "80", "200", "400", "50" } };
        TableControl.header(clvPacientes, cab);

        w = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                btnBuscar.setEnabled(false);
                String sql = "SELECT pc_numero, pc_inscricao, pc_nome, pc_origem FROM pacientes " + VariaveisGlobais.sWhere + " ORDER BY Upper(pc_nome);";
                ResultSet rs = conn.AbrirTabela(sql, 1007);
                int tam = DbMain.RecordCount(rs);
                int n = 0;
                try {
                    while (rs.next()) {
                        if (isCancelled()) break;
                        
                        int pos = n * 100 / tam;
                        pbBuscar.setValue(pos);
                        pbBuscar.repaint();
                        try { sleep(1); } catch (Exception ex) {}

                        String tficha = rs.getString("pc_numero");
                        String tinsc = rs.getString("pc_inscricao");
                        String tnome = rs.getString("pc_nome").toUpperCase();
                        String torigem = rs.getString("pc_origem").toLowerCase().trim();

                        Date tdata = null;
                        String[][] tultima = conn.LerCamposTabela(new String[] { "ma_data" }, "faturar", "ma_pcnumero = '" + tficha.trim() + "' AND ma_consulta = 1 ORDER BY ma_data DESC LIMIT 1");
                        if (tultima != null) {
                            tdata = Dates.StringtoDate(tultima[0][3], "yyyy-MM-dd");
                        } 
                        if (tdata != null) {
                        int iDias = Dates.DateDiff("D", tdata, new Date());
                        String tdias = FuncoesGlobais.StrZero(String.valueOf(iDias), 5);
                        TableControl.add(clvPacientes, new String[][] { { torigem, tficha, tinsc, tnome, tdias }, { "C", "L", "L", "L", "C" } }, true);
                        } else {
                            TableControl.add(clvPacientes, new String[][] { { torigem, tficha, tinsc, tnome, "NUNCA" }, { "C", "L", "L", "L", "C" } }, true);
                        }
                        clvPacientes.repaint();

                        n += 1;
                    }
                }
                catch (Exception err) {}
                DbMain.FecharTabela(rs);
                
                pbBuscar.setValue(100);
                pbBuscar.repaint();
                try { sleep(1); } catch (Exception ex) {}
                
                clvPacientes.setRowSelectionInterval(0, 0);
                btnBuscar.setEnabled(true);
                return 0;
            }
            
//            @Override
//            protected void done() {
//                
//            }
        };
        w.execute();
    }

    private void initencerrados() {
        String sql = "select m.ma_medico, p.pc_nome, m.ma_hora, m.ma_horasai from faturar as m, pacientes as p where (m.ma_pcnumero = p.pc_numero) AND (ma_data = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "') and lower(ma_origem) = '" + VariaveisGlobais.origem.toLowerCase().trim() + "' and ma_status = 4 and (ma_consulta = 1 or ma_revisao = 1 " + "or ma_cortesia = 1 or ma_procedimento = 1) order by Lower(p.pc_nome);";

        ResultSet ers = conn.AbrirTabela(sql, 1007);
        try {
            while (ers.next()) {
                String thora = Dates.DateFormat("HH:mm:ss", ers.getTime("ma_hora"));
                String thsai = Dates.DateFormat("HH:mm:ss", ers.getTime("ma_horasai"));
                String tnome = ers.getString("pc_nome").toUpperCase().trim();
                String tmedico = ers.getString("ma_medico").toUpperCase().trim();

                listaencerrados = FuncoesGlobais.ArraysAdd(listaencerrados, new String[] { thora, thsai, tnome, tmedico });
            }
        }
        catch (Exception err) {}
        DbMain.FecharTabela(ers);
    }

    private void buscaNomesPac() {
        try {
            TableControl.Clear(clvPacientes);
        } catch (Exception err) {}
        try {
            digitacao.stop();
        } catch (Exception err) {}
        digitacao = null;
        digitacao = new Thread() { 
            public void run() { 
                try { sleep(100L); } catch (Exception ex) {}
                Cursor cursor = Cursor.getPredefinedCursor(3);
                setCursor(cursor);

                String txtBusca = "";String nmPac = tbnomepac.getText().trim().toUpperCase();
                int ntpbusca = jtpBusca.getSelectedIndex();
                if (ntpbusca == 0) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "%'";
                }
                if (ntpbusca == 1) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '%" + nmPac + "%'";
                } else if (ntpbusca == 2) {
                    txtBusca = "WHERE Upper(pc_nome) LIKE '" + nmPac + "'";
                }
                PegarPacientesCadastro(txtBusca);

                cursor = Cursor.getDefaultCursor();
                setCursor(cursor);
            }
        };
        digitacao.start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btMedicos;
    private javax.swing.JButton btSeekMed;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JTable clvEspera;
    private javax.swing.JTable clvMedicos;
    private javax.swing.JTable clvPacientes;
    private javax.swing.JTable clvSeek;
    private javax.swing.JTable exExames;
    private javax.swing.JLabel jBiometria;
    private javax.swing.JComboBox jIndicacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel jTooTips;
    private javax.swing.JProgressBar jbarra;
    private javax.swing.JPanel jpnMedicos;
    private javax.swing.JPanel jpnMedicosOnOff;
    private javax.swing.JTabbedPane jpnMedicosemAtendimento;
    private javax.swing.JComboBox jtpBusca;
    private javax.swing.JTextField jtxBusca;
    private javax.swing.JTextField jtxtProcurar;
    private javax.swing.JLabel lbProcurar;
    private javax.swing.JLabel lbespecialidade;
    private javax.swing.JLabel lbmedico;
    private javax.swing.JTable medicoson;
    private javax.swing.JProgressBar pbBuscar;
    private javax.swing.JButton rbtDesistiu;
    private javax.swing.JButton rbtEsperar;
    private javax.swing.JButton rbtLimpar;
    private javax.swing.JButton rbtListaEncerrados;
    private javax.swing.JButton rbtNovoPaciente;
    private javax.swing.JButton rbtSaidaPaciente;
    private javax.swing.JTextField tbSeekMed;
    private javax.swing.JTextField tbmatricula;
    private javax.swing.JTextField tbnomepac;
    private javax.swing.JToggleButton tgbconsulta;
    private javax.swing.JToggleButton tgbcortesia;
    private javax.swing.JToggleButton tgbrevisao;
    // End of variables declaration//GEN-END:variables

  private void addespera(String thora, String naut)
  {
    int mRow = clvPacientes.convertRowIndexToModel(clvPacientes.getSelectedRow());
    
    String tstatus = "0";
    
    String cdpac = "";
    try
    {
      cdpac = clvPacientes.getModel().getValueAt(mRow, 1).toString().trim();
    }
    catch (Exception err)
    {
      cdpac = "";
    }
    String tpcnumero = cdpac;
    
    String[][] dadospac = new String[0][];
    try
    {
      dadospac = conn.LerCamposTabela(new String[] { "pc_convnumero", "pc_convenio", "pc_inscricao", "pc_nome" }, "pacientes", "pc_numero = '" + cdpac + "'");
    }
    catch (Exception err) {}
    String tisnc = dadospac[2][3];
    String tnome = dadospac[3][3].toUpperCase();
    String tcvnum = dadospac[0][3];
    String tconvenio = dadospac[1][3];
    String tcategoria = lbespecialidade.getText();
    String tcdmedico = lbespecialidade.getToolTipText().trim();
    String tmedico = lbmedico.getText();
    
    String ttipoatd = "";
    if (tgbconsulta.isSelected()) {
      ttipoatd = "C";
    }
    if (tgbrevisao.isSelected()) {
      ttipoatd = "R";
    }
    if (tgbcortesia.isSelected()) {
      ttipoatd = "T";
    }
    String tstatatd = "W";
    ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/aguardando.png"));
    
    String tstatimg = "0";
    String tstatproc = "";
    String tstatmed = "";
    
    String thorasai = "";
    
    Object[] linha = { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, istatatd, null, null, null, thorasai, naut };
    
    newTable.add(clvEspera, linha);
    
    sorter3 = new TableRowSorter(clvEspera.getModel());
    clvEspera.setRowSorter(sorter3);
    
    listaespera = FuncoesGlobais.ArraysAdd(listaespera, new String[] { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, thorasai });
  }
  
  private void limpatela()
  {
    tgbconsulta.setSelected(false);
    tgbrevisao.setSelected(false);
    tgbcortesia.setSelected(false);
    lbespecialidade.setText("");
    lbespecialidade.setToolTipText("");
    lbmedico.setText("");
    lbmedico.setToolTipText("");
    tbmatricula.setText("");
    tbnomepac.setText("");
    tbnomepac.setToolTipText("");
    TableControl.Clear(clvPacientes);
    tbmatricula.requestFocus();
  }
  
  private void ShowjTooTips()
  {
    int sRow = clvEspera.getSelectedRow();
    if (sRow < 0) {
      return;
    }
    int[] tpacs = contapac();
    int mRow = clvEspera.convertRowIndexToModel(sRow);
    if (mRow < 0) {
      return;
    }
    String nmmed = clvEspera.getModel().getValueAt(mRow, 9).toString().trim();
    
    String tooltext = "<html><font color=red><b>M��dico: </b></font><font color=black>" + nmmed + " - </font><font color=red><b>Em espera: </b></font><font color=black>" + FuncoesGlobais.nStrZero(tpacs[0], 3) + " - </font><font color=red><b>Pendentes: </b></font><font color=black>" + FuncoesGlobais.nStrZero(tpacs[1], 3) + "</font></html>";
    
    jTooTips.setText(tooltext);
  }
  
  private void FiltraEspera() {
    int selRow = clvMedicos.getSelectedRow();
    int modelRow = clvMedicos.convertRowIndexToModel(selRow);
    String tcdmed = clvMedicos.getModel().getValueAt(modelRow, 0).toString().toUpperCase().trim();
    if (tcdmed.equalsIgnoreCase("TODOS")) {
      sorter3.setRowFilter(null);
    } else {
      try
      {
        RowFilter<Object, Object> startsWithAFilter = new RowFilter<Object,Object>() {
            public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
                for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                    if (entry.getStringValue(i).equalsIgnoreCase(clvMedicos.getModel().getValueAt(clvMedicos.convertRowIndexToModel(clvMedicos.getSelectedRow()), 0).toString().toUpperCase().trim())) {
                        return true;
                    }
                }
                return false;
            }
        };
        sorter3.setRowFilter(startsWithAFilter);
      }
      catch (PatternSyntaxException pse)
      {
        System.err.println("Bad regex pattern");
      }
    }
    jTooTips.setText("");
    jtxtProcurar.setText("");
  }
  
  private void ChangeMedicoOnOff(int row, boolean ismouse, int KeyCode)
  {
    String codmed = medicoson.getModel().getValueAt(row, 0).toString().trim();
    if (ismouse)
    {
      if ((medicoson.getSelectedColumn() == 4) && (KeyCode == 1)) {
        if (medicoson.getModel().getValueAt(row, 4).equals("X"))
        {
          int fila = TableControl.seek(clvMedicos, 0, codmed);
          if (fila > -1)
          {
            int nfila = Integer.valueOf(clvMedicos.getValueAt(fila, 4).toString()).intValue();
            if (nfila == 0)
            {
              delmedativosonline(codmed);
              medicoson.getModel().setValueAt("", row, 4);
            }
          }
        }
        else
        {
          incmedativosonline(row, codmed);
          medicoson.getModel().setValueAt("X", row, 4);
        }
      }
    }
    else if ((medicoson.getSelectedColumn() == 4) && (KeyCode == 32)) {
      if (medicoson.getModel().getValueAt(row, 4).equals("X"))
      {
        int fila = TableControl.seek(clvMedicos, 0, codmed);
        if (fila > -1)
        {
          int nfila = Integer.valueOf(clvMedicos.getValueAt(fila, 4).toString()).intValue();
          if (nfila == 0)
          {
            delmedativosonline(codmed);
            medicoson.getModel().setValueAt("", row, 4);
          }
        }
      }
      else
      {
        incmedativosonline(row, codmed);
        medicoson.getModel().setValueAt("X", row, 4);
      }
    }
  }
  
  private void delmedativosonline(String codmed)
  {
    int pos = TableControl.seek(clvSeek, 0, codmed);
    if (pos > -1) {
      TableControl.del(clvSeek, pos);
    }
    pos = TableControl.seek(clvMedicos, 0, codmed);
    if (pos > -1)
    {
      int modelRow = clvMedicos.convertRowIndexToModel(pos);
      TableControl.del(clvMedicos, modelRow);
    }
  }
  
  private void incmedativosonline(int row, String codmed)
  {
    String col0 = medicoson.getModel().getValueAt(row, 0).toString();
    String col1 = medicoson.getModel().getValueAt(row, 1).toString();
    String col2 = medicoson.getModel().getValueAt(row, 2).toString();
    String col3 = medicoson.getModel().getValueAt(row, 3).toString();
    TableControl.add(clvSeek, new String[][] { { col0, col1, col2, col3 }, { "L", "L", "L", "L" } }, true);
    sorter2 = new TableRowSorter(clvSeek.getModel());
    clvSeek.setRowSorter(sorter2);
    
    TableControl.add(clvMedicos, new String[][] { { col0, col3, "0", "0", "0", "0", "0" }, { "L", "L", "L", "L", "L", "L", "L" } }, true);
  }
  
  private void ShowDadosMedicoSelecionado(int row, boolean ismouse, int kmEvent)
  {
    String tesp = clvSeek.getModel().getValueAt(row, 2).toString();
    lbespecialidade.setText(tesp);
    String tesptag = clvSeek.getModel().getValueAt(row, 0).toString();
    lbespecialidade.setToolTipText(tesptag);
    
    String tmed = clvSeek.getModel().getValueAt(row, 3).toString();
    lbmedico.setText(tmed);
    String tmedtag = clvSeek.getModel().getValueAt(row, 1).toString();
    lbmedico.setToolTipText(tmedtag);
    if (ismouse)
    {
      if (kmEvent == 1) {
        jpnMedicos.setVisible(false);
      }
    }
    else if (kmEvent == 10) {
      jpnMedicos.setVisible(false);
    }
  }
  
  private void SetarTela(boolean Enabled)
  {
    rbtEsperar.setEnabled(Enabled);
    rbtDesistiu.setEnabled(Enabled);
    rbtSaidaPaciente.setEnabled(Enabled);
    rbtLimpar.setEnabled(Enabled);
    rbtListaEncerrados.setEnabled(Enabled);
    tgbconsulta.setEnabled(Enabled);
    tgbrevisao.setEnabled(Enabled);
    tgbcortesia.setEnabled(Enabled);
    btSeekMed.setEnabled(Enabled);
    btMedicos.setEnabled(Enabled);
    clvEspera.setEnabled(Enabled);
    jpnMedicosemAtendimento.setEnabled(Enabled);
    clvPacientes.setEnabled(Enabled);
    tbmatricula.setEnabled(Enabled);
    tbnomepac.setEnabled(Enabled);
    rbtNovoPaciente.setEnabled(Enabled);
    clvMedicos.setEnabled(Enabled);
  }
  
  private void initPac()
  {
    SetarTela(false);
    boolean bespera = false;
    String tsql = "select * from marcar where ma_status <= 4 and to_char(ma_data, 'dd-mm-yyyy') = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' AND lower(ma_origem) = '" + VariaveisGlobais.origem.toLowerCase().trim() + "' order by ma_hora;";
    
    ResultSet tbpac = conn.AbrirTabela(tsql, 1007);
    try
    {
      tam = DbMain.RecordCount(tbpac);
      n = 1;
      jbarra.setValue(0);
      jbarra.setVisible(true);
      jTooTips.setVisible(false);
      lbProcurar.setVisible(false);
      jtxtProcurar.setVisible(false);
      jtxtProcurar.setText("");
      while (tbpac.next())
      {
        new Thread()
        {
          public void run()
          {
            int pos = n * 100 / tam;
            try
            {
              sleep(100L);
            }
            catch (Exception ex) {}
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
        String taut = tbpac.getString("autenticacao");
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
        
        Object[] linha = { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, istatatd, null, null, null, thorasai, taut };
        
        newTable.add(clvEspera, linha);
        
        listaespera = FuncoesGlobais.ArraysAdd(listaespera, new String[] { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, thorasai });
        
        n += 1;
      }
    }
    catch (Exception err)
    {
      err.printStackTrace();
    }
    DbMain.FecharTabela(tbpac);
    
    sorter3 = new TableRowSorter(clvEspera.getModel());
    clvEspera.setRowSorter(sorter3);
    jbarra.setVisible(false);
    jTooTips.setText("");
    jTooTips.setVisible(true);
    lbProcurar.setVisible(true);
    jtxtProcurar.setVisible(true);
    SetarTela(true);
  }
  
  private int[] contapac()
  {
    int sRow = clvEspera.getSelectedRow();
    if (sRow < 0) {
      return null;
    }
    int mRow = clvEspera.convertRowIndexToModel(sRow);
    String cdmed = clvEspera.getModel().getValueAt(mRow, 8).toString().trim();
    
    int tclvespera = clvEspera.getRowCount();
    int e = 0;int p = 0;
    int trow = clvEspera.getSelectedRow();
    trow--;
    if (trow >= 0) {
      for (int i = trow; i >= 0; i--)
      {
        int modelRow = clvEspera.convertRowIndexToModel(i);
        String tcdmed = clvEspera.getModel().getValueAt(modelRow, 8).toString().trim();
        String tep = clvEspera.getModel().getValueAt(modelRow, 11).toString().trim();
        if (tcdmed.equalsIgnoreCase(cdmed))
        {
          if ((tep.equalsIgnoreCase("W")) || (tep.equalsIgnoreCase(""))) {
            e++;
          }
          if (tep.equalsIgnoreCase("P")) {
            p++;
          }
        }
      }
    }
    return new int[] { e, p };
  }
  
  class MultiThreadChatClient implements Runnable {
    private BufferedReader inputLine = null;
    private boolean closed = false;
    private String user = "";
    private String type = "";
    private int portNumber = 2222;
    private String host = VariaveisGlobais.unidade;
    
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
        jRecepcao.clientSocket = new Socket(host, portNumber);
        inputLine = new BufferedReader(new InputStreamReader(System.in));
        jRecepcao.os = new PrintStream(jRecepcao.clientSocket.getOutputStream());
        jRecepcao.is = new DataInputStream(jRecepcao.clientSocket.getInputStream());
      }
      catch (UnknownHostException e)
      {
        System.err.println("Don't know about host " + host);
      }
      catch (IOException e)
      {
        System.err.println("Couldn't get I/O for the connection to the host " + host);
      }
      if ((jRecepcao.clientSocket != null) && (jRecepcao.os != null) && (jRecepcao.is != null)) {
        try {
          new Thread(new MultiThreadChatClient()).start();
          
          jRecepcao.os.println(user + ";" + type);
          closed = false;
          while (!closed) {
            jRecepcao.os.println(inputLine.readLine().trim());
          }
          jRecepcao.os.close();
          jRecepcao.is.close();
          jRecepcao.clientSocket.close();
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
        while ((responseLine = jRecepcao.is.readLine()) != null)
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
    String[] protocol = msg.split(";");
    String acao = protocol[1];
    String cdpac = protocol[0];
    String horasai = "";
    if (protocol.length > 2) {
      horasai = protocol[2];
    }
    if (acao.equalsIgnoreCase("ins")) {
      addPac(cdpac);
    } else if (acao.equalsIgnoreCase("des")) {
      Desistiu(cdpac);
    } else if (acao.equalsIgnoreCase("sai")) {
      Saida(cdpac);
    } else if (acao.equalsIgnoreCase("ate")) {
      Atendeu(cdpac);
    } else if (acao.equalsIgnoreCase("enc")) {
      Encerra(cdpac, horasai);
    } else if (acao.equalsIgnoreCase("can")) {
      Cancelou(cdpac);
    } else if (acao.equalsIgnoreCase("pen")) {
      Pendeu(cdpac);
    }
  }
  
  private void addPac(String cdpac)
  {
    boolean bespera = false;
    String tsql = "select * from marcar where (ma_pcnumero = '" + cdpac + "') and ma_status <= 4 and to_char(ma_data, 'dd-mm-yyyy') = '" + Dates.DateFormat("dd-MM-yyyy", new Date()) + "' AND lower(ma_origem) = '" + VariaveisGlobais.origem.toLowerCase().trim() + "' order by ma_hora;";
    
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
        
        Object[] linha = { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, istatatd, null, null, null, thorasai };
        
        newTable.add(clvEspera, linha);
        
        listaespera = FuncoesGlobais.ArraysAdd(listaespera, new String[] { tstatus, thora, tpcnumero, tisnc, tnome, tcvnum, tconvenio, tcategoria, tcdmedico, tmedico, ttipoatd, tstatatd, tstatimg, tstatproc, tstatmed, thorasai });
      }
    }
    catch (Exception err)
    {
      err.printStackTrace();
    }
    DbMain.FecharTabela(tbpac);
    
    sorter3 = new TableRowSorter(clvEspera.getModel());
    clvEspera.setRowSorter(sorter3);
  }
  
  private void Desistiu(String cdpac) {
    int row = TableControl.seek(clvEspera, 2, cdpac);
    
    TableControl.del(clvEspera, row);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1) {
      listaespera = FuncoesGlobais.ArraysDel(listaespera, pos);
    }
  }
  
  private void Saida(String cdpac) {
    int mRow = TableControl.seek(clvEspera, 2, cdpac);
    
    TableControl.del(clvEspera, mRow);
    sorter3 = new TableRowSorter(clvEspera.getModel());
    clvEspera.setRowSorter(sorter3);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1)
    {
      String thora = listaespera[pos][1];
      String thsai = listaespera[pos][15];
      String tnome = listaespera[pos][4];
      String tmedico = listaespera[pos][9];
      
      listaencerrados = FuncoesGlobais.ArraysAdd(listaencerrados, new String[] { thora, thsai, tnome, tmedico });
      
      listaespera = FuncoesGlobais.ArraysDel(listaespera, pos);
    }
  }
  
  private void Encerra(String cdpac, String hrsai)
  {
    int mRow = TableControl.seek(clvEspera, 2, cdpac);
    int row = clvEspera.convertRowIndexToModel(mRow);
    
    ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/encerrado.png"));
    TableControl.alt(clvEspera, "E", row, 11);
    TableControl.alt(clvEspera, istatatd, row, 15);
    TableControl.alt(clvEspera, hrsai, row, 19);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1)
    {
      listaespera[pos][11] = "E";
      listaespera[pos][15] = hrsai;
    }
  }
  
  private void Atendeu(String cdpac)
  {
    int mRow = TableControl.seek(clvEspera, 2, cdpac);
    int row = clvEspera.convertRowIndexToModel(mRow);
    
    ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/atendendo.png"));
    TableControl.alt(clvEspera, "A", row, 11);
    TableControl.alt(clvEspera, istatatd, row, 15);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1) {
      listaespera[pos][11] = "A";
    }
  }
  
  private void Cancelou(String cdpac)
  {
    int mRow = TableControl.seek(clvEspera, 2, cdpac);
    int row = clvEspera.convertRowIndexToModel(mRow);
    
    ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/aguardando.png"));
    TableControl.alt(clvEspera, "W", row, 11);
    TableControl.alt(clvEspera, istatatd, row, 15);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1) {
      listaespera[pos][11] = "W";
    }
  }
  
  private void Pendeu(String cdpac)
  {
    int mRow = TableControl.seek(clvEspera, 2, cdpac);
    int row = clvEspera.convertRowIndexToModel(mRow);
    
    ImageIcon istatatd = new ImageIcon(getClass().getResource("/Figuras/pendente.png"));
    TableControl.alt(clvEspera, "P", row, 11);
    TableControl.alt(clvEspera, istatatd, row, 15);
    
    int pos = FuncoesGlobais.FindinArrays(listaespera, 2, cdpac);
    if (pos > -1) {
      listaespera[pos][11] = "P";
    }
  }

}

// Pagamento ao médico, xxxxxxxxxxxxxxxxxxx, referente aos atendimentos do dia xx/xx/xxxx no valor de R$ xx,xx