/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Db;

/**
 *
 * @author supervisor
 */

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import veridis.biometric.BiometricIdentification;
import veridis.biometric.BiometricTemplate;
import veridis.biometric.samples.applet.Base64;

/**
 *
 * @author wellspinto@gmail.com
 *
 * Rotinas de manipulação de Banco de Dados mySQL
 */
public class DbMain {

    public Connection conn = null;
    private String hostName = "127.0.0.1";
    private String userName = "root";
    private String password = "";
    private String url = null;
    private String jdbcDriver = null;
    private String dataBaseName = null;
    private String dataBasePrefix = null;
    private String dabaBasePort = null;

    public DbMain(String host, String user, String passwd, String databasename) {
        jdbcDriver = "org.postgresql.Driver";
        hostName = ((!host.trim().equalsIgnoreCase("")) ? host : hostName);
        userName = ((!user.trim().equalsIgnoreCase("")) ? user : userName);
        password = ((!passwd.trim().equalsIgnoreCase("")) ? passwd : password);
        dataBaseName = databasename;
        dataBasePrefix = "jdbc:postgresql://";
        dabaBasePort = "5432";
        url = dataBasePrefix + hostName + ":" + dabaBasePort + "/" + dataBaseName +
                  "?useUnicode=true&characterEncoding=utf8";

        this.conn = AbrirConexao();
        if (this.conn != null) { VariaveisGlobais.conn = this.conn; VariaveisGlobais.con = this; }
    }

    public DbMain(String server, String host, String user, String passwd, String databasename) {
        if (server.equalsIgnoreCase("postgres")) {
            jdbcDriver = "org.postgresql.Driver";
            dataBasePrefix = "jdbc:postgresql://";
            dabaBasePort = "5432";
        } else {
            jdbcDriver = "com.mysql.jdbc.Driver";
            dataBasePrefix = "jdbc:mysql://";
            dabaBasePort = "3306";            
        }
        hostName = ((!host.trim().equalsIgnoreCase("")) ? host : hostName);
        userName = ((!user.trim().equalsIgnoreCase("")) ? user : userName);
        password = ((!passwd.trim().equalsIgnoreCase("")) ? passwd : password);
        dataBaseName = databasename;
        url = dataBasePrefix + hostName + ":" + dabaBasePort + "/" + dataBaseName +
                  "?useUnicode=true&characterEncoding=utf8";

        this.conn = AbrirConexao();
        if (this.conn != null) { VariaveisGlobais.conn = this.conn; VariaveisGlobais.con = this; }
    }

    public Connection AbrirConexao() {
        // Tenta conexao com o Drive
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
	                     + "Include in your library path!");        
            e.printStackTrace();
        }
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            System.out.println("Conexao Falhou! Cheque a saida de console!");
            e.printStackTrace();
        }
        if (conn != null) {
            System.out.println("Conectado com o Baco de Dados!");
	} else {
            System.out.println("Falha ao criar conexão!");
	}        
        
        return conn;
    }
    
    /**
    * Fecha a conexão com BD.
    *
    */
    public void FecharConexao() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public ResultSet AbrirTabela(String sqlString, int iTipo) {
        ResultSet hResult = null;
        Connection connectionSQL = this.conn;
        Statement stm = null;

        //System.out.println(sqlString);
        try {
            stm = connectionSQL.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, iTipo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hResult = stm.executeQuery(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hResult;
    }

    /**
     *
     * @param hResult
     */
    public static void FecharTabela(ResultSet hResult) {
        try {
            hResult.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public int ExecutarComando(String sqlString) {
        int hRetorno = 0;
        Connection connectionSQL = this.conn;
        Statement stm = null;
        try {
            stm = connectionSQL.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hRetorno = stm.executeUpdate(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    public int ExecutarComando(String sqlString, Object[][] param) {
        int hRetorno = 0;
        Connection connectionSQL = this.conn;

        if (param.length <= 0) {
            Statement stm = null;
            try {
                stm = connectionSQL.createStatement();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                hRetorno = stm.executeUpdate(sqlString);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            PreparedStatement stm = null;
            try {
                stm = connectionSQL.prepareStatement(sqlString);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                for (int i = 0; i <= param.length - 1; i++) {
                    if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    } else if (param[i][0].equals("bigint")) {
                        stm.setObject(i + 1, (BigInteger) param[i][1], Types.BIGINT);
                    } else if (param[i][0].equals("date")) {
                        stm.setDate(i + 1, (Date) param[i][1]);
                    } else if (param[i][0].equals("string")) {
                        stm.setString(i + 1, (String) param[i][1]);
                    } else if (param[i][0].equals("decimal")) {
                        stm.setBigDecimal(i + 1, (BigDecimal) param[i][1]);
                    } else if (param[i][0].equals("boolean")) {
                        stm.setBoolean(i + 1, (Boolean) param[i][1]);
                    } else if (param[i][0].equals("float")) {
                        stm.setFloat(i + 1, (Float) param[i][1]);
                    } else if (param[i][0].equals("double")) {
                        stm.setDouble(i + 1, (Double) param[i][1]);
                    } else if (param[i][0].equals("array")) {
                        stm.setArray(i + 1, (Array) param[i][1]);
                    } else if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                hRetorno = stm.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return hRetorno;
    }
    
    /**
     * Criar String Sql
     */
    public static String CreateSqlText(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + ",";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    auxCpo += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") auxCpo += aFiels[i][1] + ",";
            }

            cRet = cRet.substring(0,cRet.length() -1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + "=";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    cRet += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") {
                    cRet += aFiels[i][1] + ",";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += ((aFiels.equals("")) ? aFiels[i][0] : aFiels[i][1] + ", ");
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + ((cWhere.equals("")) ? " WHERE " + cWhere : ";");

        }

        return cRet;
    }

    /**
     * LerParametros
     */
    public String LerParametros(String cVar) throws SQLException {
        String rVar = null;

        ResultSet hResult = AbrirTabela("SELECT variavel, conteudo, tipo FROM PARAMETROS WHERE LOWER(TRIM(variavel)) = '" + cVar.toLowerCase().trim() + "';", ResultSet.CONCUR_READ_ONLY);

        if (hResult.first()) {
            rVar = hResult.getString("conteudo");
        }

        return rVar;
    }

    /**
     * GravarParametros
     */
    public boolean GravarParametros(String cVar[]) throws SQLException {
        boolean rVar = false;
        boolean bInsert = false;
        String sql = "";

        bInsert = (LerParametros(cVar[0]) == null);
        if (bInsert) {
            sql = "INSERT INTO PARAMETROS (variavel, conteudo, tipo) VALUES ('" + cVar[0] + "','" + cVar[1] + "','" + cVar[2] + "')";
        } else {
            sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[1] + "' WHERE VARIAVEL = '" + cVar[0] + "';";
        }

        rVar = (ExecutarComando(sql)) > 0;
        return rVar;
    }

    public boolean GravarMultiParametros(String cVar[][]) throws SQLException {
        boolean bInsert = false;
        int i = 0; int nVar = 0;

        for (i=0;i<=cVar.length - 1;i++) {
            String sql = "";

            if (!"".equals(cVar[i][0])) {
                bInsert = (LerParametros(cVar[i][0]) == null);
                if (bInsert) {
                    sql = "INSERT INTO PARAMETROS (variavel, tipo, conteudo) VALUES ('" + cVar[i][0] + "','" + cVar[i][1] + "','" + cVar[i][2] + "')";
                } else {
                    sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[i][2] + "' WHERE VARIAVEL = '" + cVar[i][0] + "';";
                }

                nVar += ExecutarComando(sql);
            }
        }
        return (nVar > 0);
    }

    public String[][] LerCamposTabela(String[] aCampos, String tbNome, String sWhere) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere;
        //System.out.println(sSql);
        ResultSet tmpResult = AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        ResultSetMetaData md = tmpResult.getMetaData();
        String[][] vRetorno = new String[aCampos.length][4];
        int i = 0;

        if(tmpResult.first()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(aCampos[i]).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    vRetorno[i][3] = tmpResult.getString(aCampos[i]);
                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        } else {
            vRetorno = null;
        }

        FecharTabela(tmpResult);

        return vRetorno;
    }

    public static int RecordCount(ResultSet hrs) {
        
        int retorno = 0;
        try {
            int pos = hrs.getRow();
            hrs.last();
            retorno = hrs.getRow();
            hrs.beforeFirst();
            if (pos > 0) hrs.absolute(pos);
        } catch (SQLException e) {retorno = 0;}
        return retorno;
    }    
    
    public static String CreateSqlText2(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0] + ",";
                    auxCpo += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0] + "=";
                    cRet += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += (!"".equals(aFiels[i][2]) ? aFiels[i][0] : aFiels[i][2]) + ", ";
                }
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + (!"".equals(cWhere.trim()) ? " WHERE " + cWhere : "") + ";";
        }

        return cRet;
    }

    public boolean ExistTable(String TableName) throws SQLException {
        ResultSet tbl = AbrirTabela("SHOW TABLES LIKE '" + TableName + "';", ResultSet.CONCUR_READ_ONLY);
        tbl.last();
        boolean retorno = tbl.getRow() > 0;
        tbl.beforeFirst();
        FecharTabela(tbl);
        return retorno;
    }
    
    public void FillCombo(JComboBox combo, String sql, String[] campo) {
        combo.setRenderer(new MultiColumnRender(campo.length));
        ResultSet rs = AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            //combo.removeAllItems();
            combo.addItem(new String[] {"TODOS","TODOS"});
            while (rs.next()) {
                String[] fieldValues = {};
                for (int i=0;i<campo.length;i++){
                    fieldValues =  FuncoesGlobais.ArrayAdd(fieldValues,rs.getString(campo[i]).trim());
                }
                combo.addItem(fieldValues);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
    
    }

    public void Auditor(String cVelho, String cNovo) {
        try {
            ExecutarComando("INSERT INTO auditor (usuario, datahora, origem, maquina, velho, novo) VALUES ('" +
            VariaveisGlobais.logado + "','" + Dates.DateFormat("yyyy-MM-dd hh:mm:ss", new java.util.Date()) +
            "','" + VariaveisGlobais.origem + "','" + VariaveisGlobais.unidade + "','" +
            cVelho.toUpperCase() + "','" + cNovo.toUpperCase() + "')");
        } catch (Exception err) {}        
    }
    
    /*
    * Custom list cell render for our JComboBox
    */
    class MultiColumnRender implements ListCellRenderer {
 
	private JPanel _panelListCell = new JPanel();
 
	/**
	 * The serialVersionUID is a universal version identifier for a Serializable class. 
	 * Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. 
	 * If no match is found, then an InvalidClassException is thrown. 
	 */
	private static final long serialVersionUID = -722402815727238028L;
 
	private JLabel _lableComponentColumn1;
	private JLabel _lableComponentColumn2;
	private JLabel _lableComponentColumn3;
	private JLabel _lableComponentColumn4;
 
	/*
	 * Constructor
	 */
	public MultiColumnRender(int colunas) {
 
		// Set layout
		GridLayout gridLayout = new GridLayout(
				1, // rows 
				colunas  // columns
				);
		_panelListCell.setLayout(gridLayout);

                final Dimension dim = new Dimension(100, 20);
                _lableComponentColumn1 = new JLabel() {
                  @Override public Dimension getPreferredSize() {
                    return dim;
                  }
                };
                _lableComponentColumn1.setOpaque(false);
                _lableComponentColumn1.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
                
                _lableComponentColumn2 = new JLabel() {
                  @Override public Dimension getPreferredSize() {
                    return dim;
                  }
                };
                _lableComponentColumn2.setOpaque(false);
                _lableComponentColumn2.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));

                _lableComponentColumn3 = new JLabel() {
                  @Override public Dimension getPreferredSize() {
                    return dim;
                  }
                };
                _lableComponentColumn3.setOpaque(false);
                _lableComponentColumn3.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
                
                _lableComponentColumn4 = new JLabel() {
                  @Override public Dimension getPreferredSize() {
                    return dim;
                  }
                };
                _lableComponentColumn4.setOpaque(false);
                _lableComponentColumn4.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
                
		// Add components
                if (colunas >= 1) _panelListCell.add(_lableComponentColumn1);
		if (colunas >= 2) _panelListCell.add(_lableComponentColumn2, BorderLayout.EAST);
		if (colunas >= 3) _panelListCell.add(_lableComponentColumn3, BorderLayout.EAST);
		if (colunas >= 4) _panelListCell.add(_lableComponentColumn4, BorderLayout.EAST);
 
		// Add column 1 content type
		if (colunas >= 1) _panelListCell.add(_lableComponentColumn1);
 
		// Add column 2 content type
		if (colunas >= 2) _panelListCell.add(_lableComponentColumn2);
 
		// Add column 3 content type
		if (colunas >= 3) _panelListCell.add(_lableComponentColumn3);
 
		// Add column 4 content type
		if (colunas >= 4) _panelListCell.add(_lableComponentColumn4);
	}	
 
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
 
		String[] values = (String[]) value;
 
		try {_lableComponentColumn1.setText(values[0]); _lableComponentColumn1.setSize(_lableComponentColumn1.getPreferredSize());} catch (Exception err) {}
		try {_lableComponentColumn2.setText(values[1]); _lableComponentColumn2.setSize(_lableComponentColumn2.getPreferredSize());} catch (Exception err) {}
		try {_lableComponentColumn3.setText(values[2]); _lableComponentColumn3.setSize(_lableComponentColumn3.getPreferredSize());} catch (Exception err) {}
		try {_lableComponentColumn4.setText(values[3]); _lableComponentColumn4.setSize(_lableComponentColumn4.getPreferredSize());} catch (Exception err) {}
 
		try {_lableComponentColumn1.setOpaque(true);} catch (Exception err) {}
		try {_lableComponentColumn2.setOpaque(true);} catch (Exception err) {}
		try {_lableComponentColumn3.setOpaque(true);} catch (Exception err) {}
		try {_lableComponentColumn4.setOpaque(true);} catch (Exception err) {}
 
		if (index % 2 == 0) {
			try {_lableComponentColumn1.setBackground(new Color(255, 255, 204));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn2.setBackground(new Color(255, 255, 204));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn3.setBackground(new Color(255, 255, 204));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn4.setBackground(new Color(255, 255, 204));} catch (Exception err) {} // FFFFCC
		} else {
			try {_lableComponentColumn1.setBackground(new Color(255, 255, 255));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn2.setBackground(new Color(255, 255, 255));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn3.setBackground(new Color(255, 255, 255));} catch (Exception err) {} // FFFFCC
			try {_lableComponentColumn4.setBackground(new Color(255, 255, 255));} catch (Exception err) {} // FFFFCC
		}
 
		return _panelListCell;
	}
    }
    
    public int LancarCaixa(String tipo, String toper, String oper, int pcnumero, java.util.Date datap, String bco, String age, String ncheque, String nrcartao, BigDecimal valor, int cdcaixa) {
        int ret = 0; // Não lançou
        String sql = "INSERT INTO ncaixa(tipo, toper, oper, pc_numero, data, datap, bco, agencia, ncheque, nrcartao, valor, f_cod)" +
                     " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            java.util.Date data = new java.util.Date();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, tipo);
            statement.setString(2, toper);
            statement.setString(3, oper);
            statement.setInt(4, pcnumero);
            statement.setDate(5, Dates.toSqlDate(data));
            statement.setDate(6, Dates.toSqlDate(datap));
            statement.setString(7, bco);
            statement.setString(8, age);
            statement.setString(9, ncheque);
            statement.setString(10, nrcartao);
            statement.setBigDecimal(11, valor);
            statement.setInt(12, cdcaixa);
            statement.execute();
            
            // Se conseguir retornar numero da autenticacao
            sql = "SELECT autenticacao FROM ncaixa WHERE tipo = '%s' AND toper = '%s' AND oper = '%s' AND pc_numero = '%s' AND data = '%s' AND valor = '%s' AND f_cod = '%s';";
            sql = String.format(sql, tipo, toper, oper, pcnumero, Dates.DateFormat("yyyy-MM-dd", data), valor, cdcaixa);
            ResultSet rs = AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {ret = rs.getInt("autenticacao");}
            rs.close(); rs = null;
        } catch (SQLException e) {ret = 0;}
        return ret;
    }

    /*Function to add template to database*/
    public static boolean AddTemplate(BiometricTemplate template, String _nID, int dedo) throws SQLException{
        try{
            String _id = _nID;
            String digital = Base64.encodeBytes(template.getData());
            String key = "";
            if (dedo == 50) key = "e_polegar";
            if (dedo == 51) key = "e_indicador";                
            if (dedo == 52) key = "e_medio";
            if (dedo == 53) key = "e_anelar";
            if (dedo == 54) key = "e_minimo";

            if (dedo == 60) key = "d_polegar";
            if (dedo == 61) key = "d_indicador";
            if (dedo == 62) key = "d_medio";
            if (dedo == 63) key = "d_anelar";
            if (dedo == 64) key = "d_minimo";
            
            String sql = "UPDATE pacientes SET " + key + " = '" + digital + "' WHERE pc_numero = '" + _nID + "';";
            try { VariaveisGlobais.con.ExecutarComando(sql); } catch (Exception e) {return false;}
            
            return true;
        } catch(Exception e){
                System.out.println(e.getMessage());
                return false;
        }
    }
    
    /*Given a ID, tries to find correspondent template*/
    public static String  findIDTemplate(BiometricTemplate template) throws SQLException{
        String sql = "SELECT pc_numero, e_polegar, e_indicador, e_medio, e_anelar, e_minimo, " + 
                     "d_polegar, d_indicador, d_medio, d_anelar, d_minimo " + 
                     "FORM pacientes ORDER BY pc_numero;";
        ResultSet cursor = VariaveisGlobais.con.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            BiometricIdentification temp = new BiometricIdentification(template);
            while (cursor.next()) {
                String _rid = cursor.getString("pc_numero");
                
                BiometricTemplate tempBD;

                String e_polegar = cursor.getString("e_polegar");
                if (e_polegar != null) {
                    if (e_polegar.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(e_polegar)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String e_indicador = cursor.getString("e_indicador");
                if (e_indicador != null) {
                    if (e_indicador.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(e_indicador)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String e_medio = cursor.getString("e_medio");
                if (e_medio != null) {
                    if (e_medio.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(e_medio)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String e_anelar = cursor.getString("e_anelar");
                if (e_anelar != null) {
                    if (e_anelar.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(e_anelar)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String e_minimo = cursor.getString("e_minimo");
                if (e_minimo != null) {
                    if (e_minimo.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(e_minimo)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String d_polegar = cursor.getString("d_polegar");
                if (d_polegar != null) {
                    if (d_polegar.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(d_polegar)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String d_indicador = cursor.getString("d_indicador");
                if (d_indicador != null) {
                    if (d_indicador.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(d_indicador)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String d_medio = cursor.getString("d_medio");
                if (d_medio != null) {
                    if (d_medio.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(d_medio)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String d_anelar = cursor.getString("d_anelar");
                if (d_anelar != null) {
                    if (d_anelar.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(d_anelar)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
                    
                String d_minimo = cursor.getString("d_minimo");
                if (d_minimo != null) {
                    if (d_minimo.indexOf("BiometricTemplate") == -1) {
                        tempBD = new BiometricTemplate((Base64.decode(d_minimo)));
                        if( temp.match(tempBD) > 40) return _rid;
                    }
                }
            }
        } catch (Exception e) {                            
            System.out.println(e.getLocalizedMessage());
            return null;
        }

        cursor.close(); cursor = null;
        return null;
    }
}

//CREATE TABLE ncaixa
//(
//  autenticacao serial NOT NULL,
//  tipo character varying(3), -- CRE/DBE
//  toper character varying(2), -- DN/CH/CT
//  oper character varying(3), -- REC/DES/PCX
//  pc_numero integer,
//  data date,
//  datap date,
//  valor numeric(13,2),
//  f_cod integer,
//  CONSTRAINT ncaixa_pkey PRIMARY KEY (autenticacao)
//)
//WITH (
//  OIDS=FALSE
//);
//ALTER TABLE ncaixa
//  OWNER TO postgres;
//COMMENT ON COLUMN ncaixa.tipo IS 'CRE/DBE';
//COMMENT ON COLUMN ncaixa.toper IS 'DN/CH/CT';
//COMMENT ON COLUMN ncaixa.oper IS 'REC/DES/PCX';


