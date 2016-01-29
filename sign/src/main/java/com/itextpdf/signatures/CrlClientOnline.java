package com.itextpdf.signatures;

import com.itextpdf.kernel.PdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of the CrlClient that fetches the CRL bytes
 * from an URL.
 * @author Paulo Soares
 */
public class CrlClientOnline implements CrlClient {

    /** The Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrlClientOnline.class);

    /** The URLs of the CRLs. */
    protected List<URL> urls = new ArrayList<>();

    /**
     * Creates a CrlClientOnline instance that will try to find
     * a single CRL by walking through the certificate chain.
     */
    public CrlClientOnline() {
    }

    /**
     * Creates a CrlClientOnline instance using one or more URLs.
     */
    public CrlClientOnline(String... crls) {
        for (String url : crls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using one or more URLs.
     */
    public CrlClientOnline(URL... crls) {
        for (URL url : urls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using a certificate chain.
     */
    public CrlClientOnline(Certificate[] chain) {
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = (X509Certificate)chain[i];
            LOGGER.info("Checking certificate: " + cert.getSubjectDN());
            try {
                addUrl(CertificateUtil.getCRLURL(cert));
            } catch (CertificateParsingException e) {
                LOGGER.info("Skipped CRL url (certificate could not be parsed)");
            }
        }
    }

    /**
     * Adds an URL to the list of CRL URLs
     * @param url	an URL in the form of a String
     */
    protected void addUrl(String url) {
        try {
            addUrl(new URL(url));
        } catch (MalformedURLException e) {
            LOGGER.info("Skipped CRL url (malformed): " + url);
        }
    }

    /**
     * Adds an URL to the list of CRL URLs
     * @param url	an URL object
     */
    protected void addUrl(URL url) {
        if (urls.contains(url)) {
            LOGGER.info("Skipped CRL url (duplicate): " + url);
            return;
        }
        urls.add(url);
        LOGGER.info("Added CRL url: " + url);
    }

    /**
     * Fetches the CRL bytes from an URL.
     * If no url is passed as parameter, the url will be obtained from the certificate.
     * If you want to load a CRL from a local file, subclass this method and pass an
     * URL with the path to the local file to this method. An other option is to use
     * the CrlClientOffline class.
     * @see com.itextpdf.signatures.CrlClient#getEncoded(java.security.cert.X509Certificate, java.lang.String)
     */
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        if (checkCert == null)
            return null;
        List<URL> urllist = new ArrayList<>(urls);
        if (urllist.size() == 0) {
            LOGGER.info("Looking for CRL for certificate " + checkCert.getSubjectDN());
            try {
                if (url == null)
                    url = CertificateUtil.getCRLURL(checkCert);
                if (url == null)
                    throw new NullPointerException();
                urllist.add(new URL(url));
                LOGGER.info("Found CRL url: " + url);
            }
            catch (Exception e) {
                LOGGER.info("Skipped CRL url: " + e.getMessage());
            }
        }
        List<byte[]> ar = new ArrayList<>();
        for (URL urlt : urllist) {
            try {
                LOGGER.info("Checking CRL: " + urlt);
                HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
                if (con.getResponseCode() / 100 != 2) {
                    throw new PdfException(PdfException.InvalidHttpResponse1).setMessageParams(con.getResponseCode());
                }
                //Get Response
                InputStream inp = (InputStream) con.getContent();
                byte[] buf = new byte[1024];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                while (true) {
                    int n = inp.read(buf, 0, buf.length);
                    if (n <= 0)
                        break;
                    bout.write(buf, 0, n);
                }
                inp.close();
                ar.add(bout.toByteArray());
                LOGGER.info("Added CRL found at: " + urlt);
            }
            catch (Exception e) {
                LOGGER.info("Skipped CRL: " + e.getMessage() + " for " + urlt);
            }
        }
        return ar;
    }
}
