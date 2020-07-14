/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import Db.DbMain;

/**
 *
 * @author mariana
 */
public class Calculos {
    public static String[] CalcPreco(String mconv, String mch, String ctipo) {
        DbMain conn = VariaveisGlobais.con;
        String mtabela = "";
        String xfaturar = "";
        String chcirurgiao = "";
        String chfilme = "";
        String vrcirurgiao = "";
        String vrfilme = "";

        try {
            mtabela = conn.LerCamposTabela(new String[] {"cv_tabelas"}, "convenios", "cv_numero = '" + mconv + "'")[0][3];
            xfaturar = conn.LerCamposTabela(new String[] {"faturar"}, "ultilizados", "codigo LIKE '" + mch + "%'")[0][3];
            chcirurgiao = conn.LerCamposTabela(new String[] {"pc_chcirurgiao"}, "tabproc", "Upper(tb_tabela) = '" + mtabela.toUpperCase() + "' AND pc_codigo LIKE '" + mch + "%'")[0][3];
            chfilme = conn.LerCamposTabela(new String[] {"ex_filmes"}, "tabproc", "Upper(tb_tabela) = '" + mtabela.toUpperCase() + "' AND pc_codigo LIKE '" + mch + "%'")[0][3];

            vrcirurgiao = achavalor(xfaturar,FuncoesGlobais.StrZero(mconv, 6));
            vrfilme = achafilme(xfaturar, FuncoesGlobais.StrZero(mconv, 6));

            if (ctipo.equals("mat")) {
                chcirurgiao = "1";
                chfilme = "1";
                vrcirurgiao = conn.LerCamposTabela(new String[] {"tx_valor"}, "taxas", "tx_cod = '" + mch.trim() + "'")[0][3].replace(".", ",");
                vrfilme = "0";
            } else if (ctipo.equals("med")) {
                chcirurgiao = "1";
                chfilme = "1";
                vrcirurgiao = conn.LerCamposTabela(new String[] {"mm_valor"}, "medicamentos", "mm_codigo = '" + mch.trim() + "'")[0][3].replace(".", ",");
                vrfilme = "0";
            }

        } catch (Exception e) {e.printStackTrace();}

        return new String[] {mconv, mtabela, mch, chcirurgiao, vrcirurgiao, chfilme, (vrfilme.equals("") ? "0,00" : vrfilme)};
    }

    public static String achavalor(String campo, String covenio) {
        String mret = "0,00";

        int npos = campo.indexOf(covenio + ":");
        if (npos > -1) {
            mret = campo.substring(npos + 7, npos + 17);
            int possepdec = Integer.valueOf(mret.substring(0,1));
            mret = LerValor.FormatNumber("0" + mret.substring(1, 10), possepdec);
        }
        return mret;
    }

    public static String achafilme(String campo, String convenio) {
        String mret = "0,00";

        int npos = campo.indexOf(convenio + ":");
        if (npos > -1) {
            if (campo.substring(npos + 17, npos + 18).equals(";")) {
                mret = "";
            } else {
                mret = campo.substring(npos + 18, npos + 28);
            }

            if (!mret.equals("")) {
                int possepdec = Integer.valueOf(mret.substring(0,1));
                mret = LerValor.FormatNumber("0" + mret.substring(1, 10), possepdec);
            } else {
                mret = "0,00";
            }
        }
        return mret;
    }
}
