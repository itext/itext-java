package com.itextpdf.signatures;

import com.itextpdf.basics.PdfException;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Utility class with some KeyStore related methods.
 */
public class KeyStoreUtil {

    /**
     * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts.
     * @param provider the provider or <code>null</code> for the default provider
     * @return a <CODE>KeyStore</CODE>
     */
    public static KeyStore loadCacertsKeyStore(String provider) {
        File file = new File(System.getProperty("java.home"), "lib");
        file = new File(file, "security");
        file = new File(file, "cacerts");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            KeyStore k;
            if (provider == null)
                k = KeyStore.getInstance("JKS");
            else
                k = KeyStore.getInstance("JKS", provider);
            k.load(fin, null);
            return k;
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
        finally {
            try{if (fin != null) {fin.close();}}catch(Exception ex){}
        }
    }

    /**
     * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts
     * with the default provider.
     * @return a <CODE>KeyStore</CODE>
     */
    public static KeyStore loadCacertsKeyStore() {
        return loadCacertsKeyStore(null);
    }

}
