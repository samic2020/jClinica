/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Atendimentos;

import Db.DbMain;
import Funcoes.AutoCompletion;
import Funcoes.Dates;
import static Funcoes.FuncoesGlobais.StrZero;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import com.toedter.calendar.JCalendar;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.PatternSyntaxException;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author infotronica
 */
public class JAgendar extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.con;
    TableRowSorter<TableModel> sorter;
    TableRowSorter<TableModel> sorter2;

    /**
     * Creates new form JAgendar
     */
    public JAgendar() {
        initComponents();
        
        populateMedicos(jMedicos);
        
        // Agenda
        {
            String[][] aheader3 = { { "Horário", "Paciênte" }, { "10", "380" } };
            TableControl.header(jHorarios, aheader3, new boolean[] {false, true});

            jHorarios.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if (e.getColumn() == 1 && btMarcar.isSelected()) {
                        String paciente = jHorarios.getValueAt(jHorarios.getSelectedRow(), 1).toString();
                        String horario = jHorarios.getValueAt(jHorarios.getSelectedRow(), 0).toString();
                        int hora = Integer.valueOf(horario.substring(0,2));
                        int minuto = Integer.valueOf(horario.substring(3,5));
                        
                        String nmMedico = jMedicos.getSelectedItem().toString();
                        String mcod = nmMedico.substring(0, 3); mcod = mcod.substring(0,1).equalsIgnoreCase("0") ? mcod.substring(1, 3) : mcod;
                        String nmEspecialidade = jEspecialidades.getSelectedItem().toString().toUpperCase();
                        
                        int nano = jDia.getYearChooser().getYear() - 1900;
                        int nmes = jDia.getMonthChooser().getMonth();
                        int ndia = jDia.getDayChooser().getDay();
                        
                        //System.out.println(Dates.toSqlTime(new Date(nano, nmes, ndia, hora, minuto,00)));
                        
                        String sql = "DELETE FROM nagenda WHERE datahora = ? and cdmedico = ? and Upper(especialidade) = ?;";
                        Object[][] param = {
                            {"time", Dates.toSqlTime(new Date(nano, nmes, ndia, hora, minuto,00))},
                            {"int", Integer.valueOf(mcod)},
                            {"string", nmEspecialidade}
                        };
                        //sql = String.format(sql, 
                        //    Dates.DateFormat("yyyy-MM-dd HH:mm:ss", new Date(nano, nmes, ndia,hora, minuto, 00)),
                        //    mcod,
                        //    nmEspecialidade
                        //);
                        try {conn.ExecutarComando(sql, param);} catch (Exception ex) {ex.printStackTrace();}
                        
                        paciente = paciente.length() > 100 ? paciente.substring(0,100) : paciente;
                        if (!paciente.trim().equalsIgnoreCase("")) {
                            //sql = "INSERT INTO nagenda(cdmedico, especialidade, datahora, paciente) VALUES ('%s','%s','%s','%s');";
                            sql = "INSERT INTO nagenda(cdmedico, especialidade, datahora, paciente) VALUES (?,?,?,?);";
                            param = new Object[][] {
                                {"int", Integer.valueOf(mcod)},
                                {"string", nmEspecialidade},
                                {"time", Dates.toSqlTime(new Date(nano, nmes, ndia, hora, minuto,00))},
                                {"string", paciente}
                            };
                            //sql = String.format(sql, 
                            //    mcod,
                            //    nmEspecialidade,
                            //    Dates.DateFormat("yyyy-MM-dd HH:mm:ss", new Date(nano, nmes, ndia,hora, minuto, 00)),
                            //    paciente
                            //);
                            try {conn.ExecutarComando(sql, param);} catch (Exception ex) {ex.printStackTrace();}
                        }
                    }
                }
            });
        }
    }

    private void populateMedicos(JComboBox box) {
        box.removeAllItems();
        String sql = "SELECT md_codigo, md_nome FROM medicos ORDER BY Upper(md_nome);";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String mcod = rs.getString("md_codigo"); mcod = StrZero(mcod, 3);
                String mnome = rs.getString("md_nome").toUpperCase();
                box.addItem(mcod + " - " + mnome);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
        AutoCompletion.enable(box);
    }
    
    private void populateEspecialidade(JComboBox box, String cdMedico) {
        box.removeAllItems();
        String sql = "SELECT md_categoria FROM medicos WHERE md_codigo = '%s' ORDER BY Upper(md_categoria)";
        sql = String.format(sql, cdMedico);
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String mcat = rs.getString("md_categoria").toUpperCase();
                box.addItem(mcat);
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
    }
    
    private void populateHorarios(JTable tbl, String cdMed, JCalendar dia) {
        TableControl.Clear(tbl);
        int intervalo = 10;
        try { 
            intervalo = Integer.valueOf(conn.LerCamposTabela(new String[] {"md_intervs"}, "medicos", "md_codigo = '" + cdMed + "'")[0][3]);
        } catch (SQLException e) {}
        intervalo = intervalo < 10 ? 10 : intervalo;
        
        int nano = dia.getYearChooser().getYear() - 1900;
        int nmes = dia.getMonthChooser().getMonth();
        int ndia = dia.getDayChooser().getDay();
        String nmEsp = null;
        try { nmEsp = jEspecialidades.getSelectedItem().toString().toUpperCase();} catch (Exception e) {}
        for (int i = 1; i < 25; i++) {
            for (int m = 0; m < 60; m += intervalo) {
                TableControl.add(jHorarios, new String[][] { { Dates.DateFormat("HH:mm", new Date(nano,nmes,ndia,i,m)), "" }, { "C", "L" } }, true);
            }
        }        
        sorter2 = new TableRowSorter(jHorarios.getModel());
        jHorarios.setRowSorter(sorter2);
                
        if (nmEsp != null) initAgenda(tbl, Dates.DateFormat("yyyy-MM-dd", new Date(nano,nmes,ndia)), cdMed, nmEsp);
    }
    
    private void initAgenda(JTable tbl, String data, String cdMedico, String nmEspecialidade) {
        String sql = "SELECT datahora, paciente FROM nagenda WHERE cdmedico = '%s' and Upper(especialidade) = '%s' and date(datahora) = '%s' ORDER BY datahora";
        sql = String.format(sql,
            cdMedico,
            nmEspecialidade,
            data
        );
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String nHora = Dates.DateFormat("HH:mm",rs.getTimestamp("datahora"));
                String nmPaciente = rs.getString("paciente");
                
                int pos = TableControl.seek(tbl, 0, nHora);
                if (pos != -1) {
                    TableControl.alt(tbl, nmPaciente, pos, 1);
                }
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
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
        jDia = new com.toedter.calendar.JCalendar();
        jLabel1 = new javax.swing.JLabel();
        jMedicos = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jEspecialidades = new javax.swing.JComboBox();
        btMarcar = new javax.swing.JToggleButton();
        btProcurar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jHorarios = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jProcurar = new javax.swing.JTextPane();
        btnClear = new javax.swing.JLabel();

        setBackground(new java.awt.Color(101, 227, 255));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setTitle(".:: Agenda de Consultas");
        setVisible(true);

        jPanel1.setBackground(new java.awt.Color(101, 227, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jDia.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setBackground(new java.awt.Color(102, 102, 255));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("   Selecione o Médico:");
        jLabel1.setOpaque(true);

        jMedicos.setEditable(true);
        jMedicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMedicosActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(102, 102, 255));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("   Selecione a Especialidade:");
        jLabel2.setOpaque(true);

        jEspecialidades.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jEspecialidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEspecialidadesActionPerformed(evt);
            }
        });

        btMarcar.setText("Agendar");
        btMarcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMarcarActionPerformed(evt);
            }
        });

        btProcurar.setText("Procurar Paciênte");
        btProcurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProcurarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDia, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMedicos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jEspecialidades, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btMarcar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btProcurar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDia, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jMedicos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jEspecialidades, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btMarcar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btProcurar)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(101, 227, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jHorarios.setAutoCreateRowSorter(true);
        jHorarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Horario", "Paciênte"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jHorarios.setEnabled(false);
        jHorarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jHorarios.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jHorarios);
        if (jHorarios.getColumnModel().getColumnCount() > 0) {
            jHorarios.getColumnModel().getColumn(0).setResizable(false);
            jHorarios.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabel3.setText("Procurar:");

        jProcurar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jProcurarKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jProcurar);

        btnClear.setBackground(new java.awt.Color(255, 51, 51));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 0));
        btnClear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnClear.setText("X");
        btnClear.setOpaque(true);
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btMarcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMarcarActionPerformed
        jDia.setEnabled(!btMarcar.isSelected());
        jMedicos.setEnabled(!btMarcar.isSelected());
        jEspecialidades.setEnabled(!btMarcar.isSelected());
        btProcurar.setEnabled(!btMarcar.isSelected());
        jHorarios.setEnabled(btMarcar.isSelected());
        
        if (btMarcar.isSelected()) {
            String nmMedico = jMedicos.getSelectedItem().toString();
            String mcod = nmMedico.substring(0, 3); mcod = mcod.substring(0,1).equalsIgnoreCase("0") ? mcod.substring(1, 3) : mcod;
            populateHorarios(jHorarios, mcod, jDia);            
        } else TableControl.Clear(jHorarios);
    }//GEN-LAST:event_btMarcarActionPerformed

    private void jMedicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMedicosActionPerformed
        String nmMedico = jMedicos.getSelectedItem().toString();
        String mcod = nmMedico.substring(0, 3); mcod = mcod.substring(0,1).equalsIgnoreCase("0") ? mcod.substring(1, 3) : mcod;
        populateEspecialidade(jEspecialidades, mcod);
    }//GEN-LAST:event_jMedicosActionPerformed

    private void jEspecialidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEspecialidadesActionPerformed
//        String nmMedico = jMedicos.getSelectedItem().toString();
//        String mcod = nmMedico.substring(0, 3); mcod = mcod.substring(0,1).equalsIgnoreCase("0") ? mcod.substring(1, 3) : mcod;
//        populateHorarios(jHorarios, mcod, jDia);
    }//GEN-LAST:event_jEspecialidadesActionPerformed

    private void btProcurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btProcurarActionPerformed
        String tsql = "SELECT m.md_nome, a.especialidade, a.datahora, a.paciente FROM nagenda a, medicos m WHERE a.cdmedico = m.md_codigo ORDER BY Lower(a.paciente);";
        ResultSet trs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
        JTable tbl = new JTable();
        tbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        String[][] aheader = { { "Medico", "Especialidade", "Data", "Horario", "Paciênte" }, { "100", "50", "30", "10", "300" } };
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
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + jbuscar.getText().trim()));
                    } catch (PatternSyntaxException pse) {System.err.println("Bad regex pattern");}
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
        
        JOptionPane.showInternalMessageDialog(this, panel,"Selecione o médico!",JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_btProcurarActionPerformed

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        jProcurar.setText("");
        jProcurar.requestFocus();
    }//GEN-LAST:event_btnClearMouseClicked

    private void jProcurarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jProcurarKeyReleased
        if ("".equals(jProcurar.getText().trim())) {
            sorter2.setRowFilter(null);
        } else {
            try {
                sorter2.setRowFilter(RowFilter.regexFilter("(?i)" +jProcurar.getText().trim()));
            } catch (PatternSyntaxException pse) {System.err.println("Bad regex pattern");}
        }
    }//GEN-LAST:event_jProcurarKeyReleased

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
            java.util.logging.Logger.getLogger(JAgendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JAgendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JAgendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JAgendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JAgendar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btMarcar;
    private javax.swing.JButton btProcurar;
    private javax.swing.JLabel btnClear;
    private com.toedter.calendar.JCalendar jDia;
    private javax.swing.JComboBox jEspecialidades;
    private javax.swing.JTable jHorarios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox jMedicos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextPane jProcurar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}

