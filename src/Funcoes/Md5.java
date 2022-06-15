package Funcoes;

import java.security.MessageDigest;

/**
 *
 * @author YOGA 510
 */
public class Md5 {
    private String _hash = null;

    public String getHash() {
        return _hash;
    }

    public Md5(String frase) {
        byte[] hashMd5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(frase.getBytes());
            hashMd5 = md.digest();
        } catch (Exception e) {}

        _hash = stringHexa(hashMd5);        
    }

    private static String stringHexa(byte[] bytes) {
       StringBuilder s = new StringBuilder();
       for (int i = 0; i < bytes.length; i++) {
           int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
           int parteBaixa = bytes[i] & 0xf;
           if (parteAlta == 0) s.append('0');
           s.append(Integer.toHexString(parteAlta | parteBaixa));
       }
       return s.toString();
    }
}
