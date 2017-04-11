package com.itextpdf.signatures.testutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public final class Pkcs12FileHelper {
    private Pkcs12FileHelper() {
    }

    public static Certificate[] readFirstChain(String p12FileName, char[] ksPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        Certificate[] certChain = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(new FileInputStream(p12FileName), ksPass);

        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                certChain = p12.getCertificateChain(alias);
                break;
            }
        }

        return certChain;
    }

    public static PrivateKey readFirstKey(String p12FileName, char[] ksPass, char[] keyPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        PrivateKey pk = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(new FileInputStream(p12FileName), ksPass);

        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                pk = (PrivateKey) p12.getKey(alias, keyPass);
                break;
            }
        }

        return pk;
    }

    public static KeyStore initStore(String p12FileName, char[] ksPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, NoSuchProviderException {
        KeyStore p12 = KeyStore.getInstance("PKCS12", "BC");
        p12.load(new FileInputStream(p12FileName), ksPass);
        return p12;
    }
}
