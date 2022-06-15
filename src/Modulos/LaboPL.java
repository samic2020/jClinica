/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modulos;

import Db.DbMain;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Samic
 */
public class LaboPL extends javax.swing.JInternalFrame {
    DbMain conn = VariaveisGlobais.con;
    boolean isNew = false;
    boolean isAlt = false;

    /**
     * Creates new form LaboPL
     */
    public LaboPL() {
        initComponents();
        
        LimpaTela();
        labNome.setEnabled(false);
        
        LerGrid();
        labGrid.setEnabled(true);
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
        btnIncluir = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnGravar = new javax.swing.JButton();
        btnRetornar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        labId = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labNome = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        labGrid = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txfBuscar = new javax.swing.JTextField();
        btnClear = new javax.swing.JLabel();

        setBackground(new java.awt.Color(101, 227, 255));
        setIconifiable(true);
        setTitle(".:: Cadastro de Laboratórios");
        setOpaque(true);
        setVisible(true);

        jPanel1.setBackground(new java.awt.Color(101, 227, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "[ Opções ]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        btnIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/plus.png"))); // NOI18N
        btnIncluir.setText("Incluir");
        btnIncluir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnIncluir.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnIncluir.setIconTextGap(5);
        btnIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncluirActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/edit.png"))); // NOI18N
        btnAlterar.setText("Alterar");
        btnAlterar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAlterar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAlterar.setIconTextGap(5);
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/remove.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExcluir.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnExcluir.setIconTextGap(5);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/save.png"))); // NOI18N
        btnGravar.setText("Gravar");
        btnGravar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGravar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGravar.setIconTextGap(5);
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnRetornar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/IconesPL/cancel.png"))); // NOI18N
        btnRetornar.setText("Retornar");
        btnRetornar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRetornar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRetornar.setIconTextGap(5);
        btnRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addComponent(btnGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRetornar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIncluir)
                    .addComponent(btnAlterar)
                    .addComponent(btnExcluir)
                    .addComponent(btnGravar)
                    .addComponent(btnRetornar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Id:");

        labId.setBackground(new java.awt.Color(255, 255, 204));
        labId.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labId.setForeground(new java.awt.Color(255, 0, 51));
        labId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labId.setText("0");
        labId.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labId.setOpaque(true);

        jLabel3.setText("Nome:");

        labGrid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(labGrid);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Buscar:");

        txfBuscar.setBorder(null);
        txfBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txfBuscarKeyReleased(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnClear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnClear.setText("X");
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txfBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnClear)))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labId, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labNome))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(labId)
                    .addComponent(jLabel3)
                    .addComponent(labNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        txfBuscar.setText("");
        txfBuscar.requestFocus();
    }//GEN-LAST:event_btnClearMouseClicked

    private void txfBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txfBuscarKeyReleased
        String busca = txfBuscar.getText().trim();
        int pos = TableControl.seek(labGrid, 1, busca);
        if (pos > -1) {
            labGrid.getSelectionModel().setSelectionInterval(pos, pos);
        } else labGrid.getSelectionModel().clearSelection();
    }//GEN-LAST:event_txfBuscarKeyReleased

    private void btnRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetornarActionPerformed
        if (isNew || isAlt) {
            if (isNew || isAlt) {
                Object[] options = { "Sim", "Não" };
                int n = JOptionPane.showOptionDialog(this, "Voçê esta " + (isNew ? "Incluindo" : "Alterando") + " e os dados lançados serão perdidos.\n\nConfirma", "Atenção", 0, 3, null, options, options[1]);
                if (n == 1) return;                
            }
            
            LimpaTela();
            labNome.setEnabled(false);
            labGrid.setEnabled(true);

            txfBuscar.setEnabled(true);
            btnClear.setEnabled(true);
            
            int pos = -1;
            if (isAlt) pos = labGrid.getSelectedRow();
            LerGrid();
            if (isAlt) {
                if (pos > -1) labGrid.getSelectionModel().setSelectionInterval(pos, pos); else labGrid.getSelectionModel().clearSelection();
            }

            isNew = false; isAlt = false;            
        } else {
            dispose();
        }
    }//GEN-LAST:event_btnRetornarActionPerformed

    private void btnIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncluirActionPerformed
        isNew = true; isAlt = false;
        LimpaTela();
        labId.setText("-1");
        labNome.setEnabled(true);
        labGrid.setEnabled(false);
        
        txfBuscar.setEnabled(false);
        btnClear.setEnabled(false);
        
        btnIncluir.setEnabled(false);
        btnAlterar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnGravar.setEnabled(true);
        btnRetornar.setEnabled(true);
        
        labNome.requestFocus();
    }//GEN-LAST:event_btnIncluirActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        isNew = false; isAlt = true;
        LimpaTela();
        labNome.setEnabled(true);
        labGrid.setEnabled(false);
        
        txfBuscar.setEnabled(false);
        btnClear.setEnabled(false);
        
        btnIncluir.setEnabled(false);
        btnAlterar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnGravar.setEnabled(true);
        btnRetornar.setEnabled(true);

        labId.setText(labGrid.getValueAt(labGrid.getSelectedRow(), 0).toString());
        labNome.setText(labGrid.getValueAt(labGrid.getSelectedRow(), 1).toString());
        labNome.requestFocus();
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        isNew = false; isAlt = false;
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(this, "Deseja excluir este laboratório ?", "Atenção", 0, 3, null, options, options[1]);
        if (n == 1) return;
        
        // Excluir 
        String deleteSQL = "DELETE FROM laboratorios WHERE id = ?";
        conn.ExecutarComando(deleteSQL, new Object[][]{{"int", Integer.valueOf(labGrid.getValueAt(labGrid.getSelectedRow(), 0).toString())}});
        labGrid.setEnabled(true);
        LerGrid();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        String acaoSQL = null;
        Object[][] param = {};
        if (isNew || isAlt) {
            if (isNew) {
                acaoSQL = "INSERT INTO laboratorios(nome) VALUES (?);";
                param = new Object[][] {{"string", labNome.getText().trim()}};
            }
            if (isAlt) {
                acaoSQL = "UPDATE laboratorios SET nome=? WHERE id = ?;";
                param = new Object[][] {{"string", labNome.getText().trim()}, {"int", Integer.valueOf(labId.getText().trim())}};
            }
        }
        conn.ExecutarComando(acaoSQL, param);
        
        LimpaTela();
        labNome.setEnabled(false);
        labGrid.setEnabled(true);
        
        txfBuscar.setEnabled(true);
        btnClear.setEnabled(true);
        
        int pos = -1;
        if (isAlt) pos = labGrid.getSelectedRow();
        LerGrid();
        if (isAlt) {
            if (pos > -1) labGrid.getSelectionModel().setSelectionInterval(pos, pos); else labGrid.getSelectionModel().clearSelection();
        }

        isNew = false; isAlt = false;
    }//GEN-LAST:event_btnGravarActionPerformed

    private void LimpaTela() {
        labId.setText("0");
        labNome.setText("");
    }
    
    private void LerGrid() {
        try { TableControl.Clear(labGrid); } catch (Exception err) {}
        String[][] cab = {{ "id", "nome" }, { "50", "600" }};            
        TableControl.header(labGrid, cab);

        String tsql = "select id codigo, nome nome from laboratorios ORDER BY id;";
        ResultSet tblab = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
            
        try {
          while (tblab.next()) {
              TableControl.add(labGrid, new String[][] { { tblab.getString("codigo"), tblab.getString("nome") }, { "C", "L" } }, true);                    
          }
        }
        catch (Exception err) {}
        DbMain.FecharTabela(tblab);                   
        
        labGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = labGrid.getSelectionModel();

        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnIncluir.setEnabled(true);
                btnAlterar.setEnabled(labGrid.getRowCount() > 0);
                btnExcluir.setEnabled(labGrid.getRowCount() > 0);
                btnGravar.setEnabled(false);
                btnRetornar.setEnabled(true);
            }
        });

        btnIncluir.setEnabled(true);
        btnAlterar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnGravar.setEnabled(false);
        btnRetornar.setEnabled(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JLabel btnClear;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton btnRetornar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable labGrid;
    private javax.swing.JLabel labId;
    private javax.swing.JTextField labNome;
    private javax.swing.JTextField txfBuscar;
    // End of variables declaration//GEN-END:variables
}
