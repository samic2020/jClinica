/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Db;

import Funcoes.FuncoesGlobais;
import java.sql.ResultSet;

/**
 *
 * @author supervisor
 */
public class MigraCeps {
    public MigraCeps() {
        DbMain sconn = new DbMain("mysql","127.0.0.1","root","","ceps");
        DbMain pconn = new DbMain("postgres","127.0.0.1","postgres","","clinica");
        
        ResultSet srs = null;
        String sSql = null, pSql = null;
        try {
            sSql = "SELECT * FROM bairros;";
            srs = sconn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
            while (srs.next()) {                
                pSql = "INSERT INTO cep_bairros(id, uf, cidade, nome) VALUES ('&1.', '&2.', '&3.', '&4.');";
                pSql = FuncoesGlobais.Subst(pSql, new String[] {
                   srs.getString("id"),
                   srs.getString("uf"),
                   srs.getString("cidade"),
                   srs.getString("nome").replace("'", "''")
                });
                System.out.println(pSql);
                pconn.ExecutarComando(pSql);
            }
            DbMain.FecharTabela(srs);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            sSql = "SELECT * FROM cidades;";
            srs = sconn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
            while (srs.next()) {                
                pSql = "INSERT INTO cep_cidades(id, nome, uf, cep2, estado_cod, cep) VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.');";
                pSql = FuncoesGlobais.Subst(pSql, new String[] {
                   srs.getString("id"),
                   srs.getString("nome").replace("'", "''"),
                   srs.getString("uf"),
                   srs.getString("cep2"),
                   srs.getString("estado_cod"),
                   srs.getString("cep")
                });
                System.out.println(pSql);
                pconn.ExecutarComando(pSql);
            }
            DbMain.FecharTabela(srs);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            sSql = "SELECT * FROM enderecos;";
            srs = sconn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
            while (srs.next()) {                
                pSql = "INSERT INTO cep_enderecos(id, uf, cidade_id, nomeslog, nomeclog, bairro_id, logradouro, cep, uf_cod, logracompl) VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.', '&10.');";
                pSql = FuncoesGlobais.Subst(pSql, new String[] {
                   srs.getString("id"),
                   srs.getString("uf"),
                   srs.getString("cidade_id"),
                   srs.getString("nomeslog").replace("'", "''"),
                   srs.getString("nomeclog").replace("'", "''"),
                   srs.getString("bairro_id"),
                   srs.getString("logradouro").replace("'", "''"),
                   srs.getString("cep"),
                   srs.getString("uf_cod"),
                   srs.getString("logracompl").replace("'", "''")
                });
                System.out.println(pSql);
                pconn.ExecutarComando(pSql);
            }
            DbMain.FecharTabela(srs);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            sSql = "SELECT * FROM estados;";
            srs = sconn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
            while (srs.next()) {                
                pSql = "INSERT INTO cep_estados(id, nome, uf, ibge) VALUES ('&1.', '&2.', '&3.', '&4.');";
                pSql = FuncoesGlobais.Subst(pSql, new String[] {
                   srs.getString("id"),
                   srs.getString("nome").replace("'", "''"),
                   srs.getString("uf"),
                   srs.getString("ibge")
                });
                System.out.println(pSql);
                pconn.ExecutarComando(pSql);
            }
            DbMain.FecharTabela(srs);
        } catch (Exception e) { e.printStackTrace(); }

        sconn.FecharConexao();
        pconn.FecharConexao();        
    }
}
