/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 *
 * @author supervisor
 */
public class FuncoesGlobais {

    public FuncoesGlobais() {
    }

    public static int IndexOf(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int OfIndex(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].contentEquals(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOf2(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (sOque.contains(aString[i])) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOfPart(String aString[], String sOque, int pos1, int pos2) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].substring(pos1, pos2).equals(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOfn(String aString[], String sOque, int pos) {
        int i =  (pos <=0 ? 0 : pos);
        boolean achei = false;
        int retorno = -1;

        for (; i <= aString.length - 1; i++) {
            if (aString[i].contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindinArrays(String[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (marray[i][coluna].equalsIgnoreCase(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindinArrays(String[][] marray, int coluna[], String oque[]) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (marray[i][coluna[0]].equalsIgnoreCase(oque[0])) {
                if (marray[i][coluna[1]].equalsIgnoreCase(oque[1])) {
                    achei = true;
                    break;
                }
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static String DecriptaNome(String cNome) {
        String cRetorno = "";
        String auxVar;
        int nConta;

        auxVar = cNome.trim().toUpperCase();
        if (auxVar.length() > 0) {
            for (nConta = 1; nConta <= auxVar.length(); nConta += 2) {
                cRetorno += (char) Integer.parseInt(auxVar.substring(nConta - 1, (nConta - 1) + 2));
            }

            cRetorno = cRetorno.substring(0,1).toUpperCase() + cRetorno.substring(1).toLowerCase();
        }

        return ("".equals(cRetorno.trim()) ? null : cRetorno);
    }

    public static String CriptaNome(String cNome) {
        String cRetorno = ""; int i = 0;
        for (i=0;i<=cNome.length() - 1; i++) {
            char c = cNome.toUpperCase().charAt(i);
            int asc = (int) c;
            cRetorno += StrZero(String.valueOf(asc), 2);
        }
        return cRetorno;
    }

    public static String[] ArrayAdd(String[] mArray, String value) {
        String[] temp = new String[mArray.length+1];

        System.arraycopy(mArray,0,temp,0,mArray.length);

        temp[mArray.length] = value;

        return temp;
    }

//    public static String[] ArrayDel(String[] array, int index) {
//            ArrayList list = CreateStringList(array);
//            list.remove(index);
//            return ConvertToStringArray(list);
//    }

    public static String[][] ArraysAdd(String[][] mArray, String[] value) {
        String[][] temp = new String[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static String[][] ArraysDel(String[][] array, int index) {
        if (array.length == 0) return null;

        String[][] temp = {};
        for (int i=0; i <= array.length - 1; i++) {
            if (i != index) {
                temp = ArraysAdd(temp, array[i]);
            }
        }

        return temp;
    }

//    public static String[][] ArraysDelSub(String[][] array, int index, int pos) {
//        if (array.length == 0) return null;
//
//        String[][] temp = {};
//        for (int i=0; i <= array.length - 1; i++) {
//            if (i != index) {
//                temp = ArraysAdd(temp, array[i]);
//            } else {
//                temp = ArraysAdd(temp, ArrayDel(array[i], pos));
//            }
//        }
//
//        return temp;
//    }

    public static Object[][] ObjectsAdd(Object[][] mArray, Object[] value) {
        Object[][] temp = new Object[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static int FindinObjects(Object[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            String aonde = String.valueOf(marray[i][coluna]);
            if (aonde.contains(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }
    
    public static String[][] ArraySuperAdd(String[][] mArray, String[][] value) {
        String[][] temp = new String[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i][0];
        }
        return temp;
    }

//    public static ArrayList<String> CreateStringList(String ... values) {
//        ArrayList<String> results = new ArrayList<String>();
//        Collections.addAll(results, values);
//        return results;
//    }

    public static String[] ConvertToStringArray(ArrayList list)
    {
        return (String[])list.toArray(new String[0]);
    }

    public static String join(String[] s, String delimiter) {
        int i = 0;
        String sRet = "";
        for (i=0;i<=s.length - 1;i++) {
            sRet += s[i] + delimiter;
        }
        return sRet.substring(0, sRet.length() - delimiter.length());
    }

    public static String GravaValor(String valor) {
        String tmpValor = valor.replace(".", "");
        tmpValor = tmpValor.replace(" ", "");

        int ponto = tmpValor.indexOf(",");
        String part1 = "00000000" + tmpValor.substring(0, ponto);
        String part2 = tmpValor.substring(ponto + 1);

        return part1.substring(part1.length() - 8, part1.length()) + part2.substring(0,2);
    }

    public static int OcourCount(String valor, String oQue, int nQtd) {
        int i = 0; int j = 0; boolean achei = false;
        for (i=0; i <= valor.length() - 1; i++) {
            if (valor.substring(i, i + oQue.length()).equals(oQue)) j++;
            if (j == nQtd) {achei = true; break;}
        }
        return (achei ? i : -1);
    }

    public static String StrZero(String valor, int Tam) {
        String tmpValor = valor.replace(".0", "").replace(" ", "").replace(",", "");
        int i = 0; String zeros = Repete("0", Tam);
        String part1 = zeros + tmpValor;

        return part1.substring(part1.length() - Tam);
    }

    public static String nStrZero(int value, int Tam) {
        String valor = String.valueOf(value);
        String tmpValor = valor.replace(".0", "").replace(" ", "").replace(",", "");
        int i = 0; String zeros = Repete("0", Tam);
        String part1 = zeros + tmpValor;

        return part1.substring(part1.length() - Tam);
    }

    public static String Repete(String texto, int length) {
        StringBuffer retorno = new StringBuffer();
        for (int i=1; i<=length;i++) {
            retorno.append(texto);
        }
        return retorno.toString();
    }

    public static float strCurrencyToFloat(String Value)
    {
        if (Value.length() == 0)
            return 0;
        else
            return Float.parseFloat(Value.replace(" ", "").replace(".", "").replace(",", "."));
    }

    public static String GravaValores(String valor, int decimal) {
        String tmpValor = valor.replace(".", "");
        tmpValor = tmpValor.replace(" ", "");

        int ponto = tmpValor.indexOf(",");
        String part1 = StrZero("0", 10 - decimal) + (decimal > 0 ? tmpValor.substring(0, ponto) : valor);
        String part2 = "";
        if (decimal > 0) {
            part2 = tmpValor.substring(ponto + 1);
            if (part2.length() < decimal) part2 += Repete("0", decimal - part2.length());
        } else {
            part2 = "";
        }

        return part1.substring(part1.length() - (10 - decimal), part1.length()) + ("".equals(part2) ? "" : part2.substring(0,decimal));
    }

    public static String[] ComboLista(JComboBox oBox) {
        String[] lista = {};
        for (int i=0; i< oBox.getItemCount(); i++) {
            lista = ArrayAdd(lista, oBox.getItemAt(i).toString());
        }
        return lista;
    }

    public static String Subst(String Variavel, String[] Conteudos) {
        String retorno = Variavel;
        if (Conteudos.length > 0) {
            for (int i=0;i<Conteudos.length;i++) {
                retorno = retorno.replace("&" + String.valueOf(i + 1).trim() + ".", Conteudos[i]);
            }
        }

        return retorno;
    }

    public static String Choose(int pos, String[] Itens) {
        return Itens[pos];
    }   

    public static String myLetra(String cword) {
        String iLetras = "à;è;ì;ò;ù;ã;õ;â;ê;î;ô;û;á;é;í;ó;ú;ä;ë;ï;ö;ü;ç;À;È;Ì;Ò;Ù;Ã;Õ;Â;Ê;Î;Ô;Û;Á;É;Í;Ó;Ú;Ä;Ë;Ï;Ö;Ü;Ç";
        String oLetras = "a;e;i;o;u;a;o;a;e;i;o;u;a;e;i;o;u;a;e;i;o;u;c;A;E;I;O;U;A;O;A;E;I;O;U;A;E;I;O;U;A;E;I;O;U;C";
        
        String[] aiLetras = iLetras.split(";"); String[] aoLetras = oLetras.split(";");
        for (int i=0;i<aiLetras.length;i++) {
            cword = cword.replace(aiLetras[i], aoLetras[i]);
        }
        
        return cword;
    }   
}

        //List<String> lista = new ArrayList<String>();
        //lista.add("joao"); lista.add("jose"); lista.add("wellington");
        //AutoCompleteDecorator.decorate(jTextField1, lista, true);
        
//        String[] nomes = {"joao", "jose", "marcelo", "wellington"};  
//        JList jlista = new JList(nomes);
//        AutoCompleteDecorator.decorate(jlista,jTextField1);
        
