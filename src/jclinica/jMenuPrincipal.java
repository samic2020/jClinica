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
import Cadastos.CadTaxasConv;
import Cadastos.jConvenios;
import Cadastos.jMedico;
import Cadastos.jUsuarios;
import Db.DbMain;
import Faturamentos.AdmGrupos;
import Faturamentos.Tiss.Tiss;
import Gerencia.jPasCaixa;
import Faturamentos.jFarmacia;
import Funcoes.BackGroundDeskTopPane;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.ResizeImageIcon;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import Gerencia.jCaixa;
import Gerencia.jDespesas;
import Login.jLogin;
import Modulos.CadastroPL;
import Modulos.LaboPL;
import Modulos.RecepcaoPL;
import Relatorios.Financeiro;
import Relatorios.iRelEstatisticas;
import Relatorios.nAtendimentos;
import Relatorios.nRelDesp;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
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
        
        if (VariaveisGlobais.emedico) {
            jMenu1.setVisible(false);
            jMenu2.setVisible(false);
            jmnuRelFatura.setVisible(false);

            jmnuAtdMed.setVisible(true);
            jmnuRecepcao.setVisible(false);
            jmnuAgenda.setVisible(false);
            jMenuItem6.setVisible(true);
            jmnuFarmacia.setVisible(false);
            
            jmnuProced.setVisible(false);
            jmnuLabo.setVisible(false);
            jMenuItemProced.setVisible(false);
        } else {
            jMenu1.setVisible(true);
            jMenu2.setVisible(true);
            jmnuRelFatura.setVisible(true);

            jmnuAtdMed.setVisible(true);
            jmnuRecepcao.setVisible(true);
            jmnuAgenda.setVisible(true);
            jMenuItem6.setVisible(false);
            jmnuFarmacia.setVisible(true);            

            jmnuProced.setVisible(true);
            jmnuLabo.setVisible(true);
            jMenuItemProced.setVisible(true);
        }
   
        ID();
    }

    private void ID() {
        ImageIcon img = new ResizeImageIcon("E", "resources/clima.png", jIMG.getWidth(), jIMG.getHeight()).getImg();
        jIMG.setIcon(img);
        
        jUsuario.setText(VariaveisGlobais.logado);
        if (VariaveisGlobais.unidade.equalsIgnoreCase("127.0.0.1") || VariaveisGlobais.unidade.equalsIgnoreCase("localhost")) {
            jUsuario.setForeground(Color.BLACK);
        } else {
            jUsuario.setForeground(Color.red);
        }
        
        jInicio.setText(new Date().toString());

        jOS.setText(System.getProperty("os.name") + " - " + System.getProperty("os.version"));
        jLocal.setText(VariaveisGlobais.unidade);
        jBaseDados.setText(VariaveisGlobais.dbnome);
        //jtermica.setText(VariaveisGlobais.DefaultThermalPort);
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
        
        ActionListener action = new ActionListener() {
        public void actionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent e) {
                jRelogio.setText(Dates.DateFormat("HH:mm.ss", new Date()));
            }
        };
        Timer t = new Timer(1000, action);
        t.start();        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new BackGroundDeskTopPane("/Figuras/clinica_cm2.jpg");
        jStatus = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jUsuario = new javax.swing.JLabel();
        jInicio = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jOS = new javax.swing.JLabel();
        jLocal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jBaseDados = new javax.swing.JLabel();
        jtermica = new javax.swing.JLabel();
        jRelogio = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jIMG = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jmnuUsuarios = new javax.swing.JMenuItem();
        jmnuMedicos = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jmnuGrupos = new javax.swing.JMenuItem();
        jmnuCadTaxasConv = new javax.swing.JMenuItem();
        jmnuProced = new javax.swing.JMenuItem();
        jmnuLabo = new javax.swing.JMenuItem();
        jmnuAtdMed = new javax.swing.JMenu();
        jmnuRecepcao = new javax.swing.JMenuItem();
        jmnuAgenda = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jmnuFarmacia = new javax.swing.JMenuItem();
        jMenuItemProced = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMnuDespesas = new javax.swing.JMenuItem();
        jMnuPassCaixa = new javax.swing.JMenuItem();
        jMnuFecCaixa = new javax.swing.JMenuItem();
        jmnuRelFatura = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jmnuEstatisticas = new javax.swing.JMenuItem();
        mnuDespesas = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jmnuLogOff = new javax.swing.JMenuItem();
        mnuFim = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuSkin = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: jClinica - Sistema de Gestão de Clinicas...");
        setBackground(new java.awt.Color(101, 227, 255));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jDesktopPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jStatus.setBackground(new java.awt.Color(254, 254, 254));
        jStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel3.setText("Usuário:");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel4.setText("Logado em:");

        jUsuario.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jUsuario.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jInicio.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jInicio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel5.setText("OS:");

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel6.setText("Local:");

        jOS.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jOS.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLocal.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jLocal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel7.setText("Impressoras:");

        jLabel8.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        jLabel8.setText("BasedeDados");

        jBaseDados.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jBaseDados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtermica.setBackground(new java.awt.Color(255, 255, 255));
        jtermica.setFont(new java.awt.Font("Ubuntu", 0, 8)); // NOI18N
        jtermica.setForeground(new java.awt.Color(0, 153, 0));
        jtermica.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jtermica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtermicaMouseReleased(evt);
            }
        });

        jRelogio.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jRelogio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRelogio.setText("00:00.00");
        jRelogio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jStatusLayout = new javax.swing.GroupLayout(jStatus);
        jStatus.setLayout(jStatusLayout);
        jStatusLayout.setHorizontalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatusLayout.createSequentialGroup()
                .addComponent(jIMG, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jStatusLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(21, 21, 21)))
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jRelogio)
                .addGap(0, 0, 0)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jStatusLayout.setVerticalGroup(
            jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jOS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtermica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .addComponent(jBaseDados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLocal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jSeparator8)
            .addComponent(jIMG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jStatusLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jRelogio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jDesktopPane1.setLayer(jStatus, javax.swing.JLayeredPane.DRAG_LAYER);
        jDesktopPane1.add(jStatus);
        jStatus.setBounds(0, 0, 760, 38);

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

        jMenuItem2.setText("Valor Convênios");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jmnuGrupos.setText("Adm Grupos");
        jmnuGrupos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuGruposActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuGrupos);

        jmnuCadTaxasConv.setText("Taxas Convenios");
        jmnuCadTaxasConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuCadTaxasConvActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuCadTaxasConv);

        jmnuProced.setText("Procedimentos");
        jmnuProced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuProcedActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuProced);

        jmnuLabo.setText("Laboratórios");
        jmnuLabo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuLaboActionPerformed(evt);
            }
        });
        jMenu1.add(jmnuLabo);

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

        jMenuItemProced.setText("Procedimentos");
        jMenuItemProced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProcedActionPerformed(evt);
            }
        });
        jmnuAtdMed.add(jMenuItemProced);

        jMenuBar1.add(jmnuAtdMed);

        jMenu2.setText("Caixa");

        jMnuDespesas.setText("Despesas");
        jMnuDespesas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuDespesasActionPerformed(evt);
            }
        });
        jMenu2.add(jMnuDespesas);

        jMnuPassCaixa.setText("Pass de Caixa");
        jMnuPassCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuPassCaixaActionPerformed(evt);
            }
        });
        jMenu2.add(jMnuPassCaixa);

        jMnuFecCaixa.setText("Fec de Caixa");
        jMnuFecCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuFecCaixaActionPerformed(evt);
            }
        });
        jMenu2.add(jMnuFecCaixa);

        jMenuBar1.add(jMenu2);

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

        mnuDespesas.setText("Despesas");
        mnuDespesas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDespesasActionPerformed(evt);
            }
        });
        jmnuRelFatura.add(mnuDespesas);

        jMenuItem1.setText("Financeiro");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jmnuRelFatura.add(jMenuItem1);

        jMenuItem4.setText("Gerar XML Tiss");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jmnuRelFatura.add(jMenuItem4);

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
        mnuSkin.setEnabled(false);
        jMenu4.add(mnuSkin);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
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

    private void jMnuPassCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuPassCaixaActionPerformed
        try {
            jPasCaixa pscTela = new jPasCaixa();
            jDesktopPane1.add(pscTela);

            jDesktopPane1.getDesktopManager().activateFrame(pscTela);
            CentralizaTela.setCentro(pscTela, jDesktopPane1, 0, 0);
            pscTela.requestFocus();
            pscTela.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMnuPassCaixaActionPerformed

    private void jMnuDespesasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuDespesasActionPerformed
        try {
            jDespesas dpTela = new jDespesas();
            jDesktopPane1.add(dpTela);

            jDesktopPane1.getDesktopManager().activateFrame(dpTela);
            CentralizaTela.setCentro(dpTela, jDesktopPane1, 0, 0);
            dpTela.requestFocus();
            dpTela.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMnuDespesasActionPerformed

    private void jMnuFecCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuFecCaixaActionPerformed
        try {
            jCaixa oTela1 = new jCaixa();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMnuFecCaixaActionPerformed

    private void jmnuCadTaxasConvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuCadTaxasConvActionPerformed
        try {
            CadTaxasConv oTela1 = new CadTaxasConv();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuCadTaxasConvActionPerformed

    private void mnuDespesasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDespesasActionPerformed
        nRelDesp oTela = null;
        try {
            oTela = new nRelDesp();
            jDesktopPane1.add(oTela);

            jDesktopPane1.getDesktopManager().activateFrame(oTela);
            CentralizaTela.setCentro(oTela, jDesktopPane1, 0, 0);
            oTela.requestFocus();
            oTela.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_mnuDespesasActionPerformed

    private void jmnuProcedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuProcedActionPerformed
        try {
            CadastroPL oTela1 = new CadastroPL();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuProcedActionPerformed

    private void jmnuLaboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuLaboActionPerformed
        try {
            LaboPL oTela1 = new LaboPL();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jmnuLaboActionPerformed

    private void jMenuItemProcedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProcedActionPerformed
        try {
            RecepcaoPL oTela1 = new RecepcaoPL();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItemProcedActionPerformed

    private void jtermicaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtermicaMouseReleased
        jtermica.setForeground(!VariaveisGlobais.statPrinter ? new java.awt.Color(0, 153, 0) : Color.RED);
        VariaveisGlobais.statPrinter = !VariaveisGlobais.statPrinter;
        jtermica.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
    }//GEN-LAST:event_jtermicaMouseReleased

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jStatus.setBounds(0, 0, jDesktopPane1.getWidth(), 38);
    }//GEN-LAST:event_formComponentResized

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            Financeiro oTela1 = new Financeiro();            
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            jConvenios oTela1 = new jConvenios();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
            Tiss oTela1 = new Tiss();
            jDesktopPane1.add(oTela1);

            jDesktopPane1.getDesktopManager().activateFrame(oTela1);
            CentralizaTela.setCentro(oTela1, jDesktopPane1, 0, 0);
            oTela1.requestFocus();
            oTela1.setSelected(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

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
    private javax.swing.JLabel jBaseDados;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jIMG;
    private javax.swing.JLabel jInicio;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLocal;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItemProced;
    private javax.swing.JMenuItem jMnuDespesas;
    private javax.swing.JMenuItem jMnuFecCaixa;
    private javax.swing.JMenuItem jMnuPassCaixa;
    private javax.swing.JLabel jOS;
    private javax.swing.JLabel jRelogio;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel jStatus;
    private javax.swing.JLabel jUsuario;
    private javax.swing.JMenuItem jmnuAgenda;
    private javax.swing.JMenu jmnuAtdMed;
    private javax.swing.JMenuItem jmnuCadTaxasConv;
    private javax.swing.JMenuItem jmnuEstatisticas;
    private javax.swing.JMenuItem jmnuFarmacia;
    private javax.swing.JMenuItem jmnuGrupos;
    private javax.swing.JMenuItem jmnuLabo;
    private javax.swing.JMenuItem jmnuLogOff;
    private javax.swing.JMenuItem jmnuMedicos;
    private javax.swing.JMenuItem jmnuProced;
    private javax.swing.JMenuItem jmnuRecepcao;
    private javax.swing.JMenu jmnuRelFatura;
    private javax.swing.JMenuItem jmnuUsuarios;
    private javax.swing.JLabel jtermica;
    private javax.swing.JMenuItem mnuDespesas;
    private javax.swing.JMenuItem mnuFim;
    private javax.swing.JMenu mnuSkin;
    // End of variables declaration//GEN-END:variables
}
