/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

/**
 *
 * @author supervisor
 */
public class Soundex {
    public Soundex() {}
    
    private String getsoundcodenumber(String schar) {
        String rchar = "";
        if (schar.equalsIgnoreCase("b") || schar.equalsIgnoreCase("f") || schar.equalsIgnoreCase("p") || schar.equalsIgnoreCase("v")) rchar = "1";
        if (schar.equalsIgnoreCase("c") || schar.equalsIgnoreCase("g") || schar.equalsIgnoreCase("j") || schar.equalsIgnoreCase("k") || schar.equalsIgnoreCase("q") || schar.equalsIgnoreCase("s") || schar.equalsIgnoreCase("x") || schar.equalsIgnoreCase("z")) rchar = "2";
        if (schar.equalsIgnoreCase("d") || schar.equalsIgnoreCase("t")) rchar = "3";
        if (schar.equalsIgnoreCase("l")) rchar = "4";
        if (schar.equalsIgnoreCase("m") || schar.equalsIgnoreCase("n")) rchar = "5";
        if (schar.equalsIgnoreCase("r")) rchar = "6";
        
        return rchar;
    }
    
    public String soundex(String sword) {
        String mretorno = "";
        String num = sword.substring(0,1);
        String slastcode = getsoundcodenumber(num);
        int lwordlength = sword.length();
        String schar = "";
        for (int i=2;i<lwordlength;i++) {
            schar = getsoundcodenumber(sword.toUpperCase().substring(i - 1, i));
            if (schar.length() > 0 && slastcode != schar) num = num + schar;
            slastcode = schar;
        }

        mretorno = (num + "          ").substring(0,4).trim();
        if (num.length() < 4) mretorno = mretorno + FuncoesGlobais.Repete("0", 4 - num.length());
        
        return mretorno;
    }
    
    public String makesondexstring(String strphrase) {
        String mretorno = "";
        String[] mwords = {};
        String tmpword = "";
        
        mwords = strphrase.trim().split(" ");
        
        if (mwords.length > 0) {
            tmpword = mwords[0];
            mretorno = mretorno + soundex(tmpword);
            tmpword = mwords[mwords.length - 1];
            mretorno = mretorno + soundex(tmpword);
        } else mretorno = "";
        return mretorno;
    }
    
}
