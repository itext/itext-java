/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.signatures;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the CrlClient that fetches the CRL bytes
 * from an URL.
 *
 * @author Paulo Soares
 */
public class CrlClientOnline implements ICrlClient {

    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrlClientOnline.class);

    /**
     * The URLs of the CRLs.
     */
    protected List<URL> urls = new ArrayList<>();

    /**
     * Creates a CrlClientOnline instance that will try to find
     * a single CRL by walking through the certificate chain.
     */
    public CrlClientOnline() {
    }

    /**
     * Creates a CrlClientOnline instance using one or more URLs.
     *
     * @param crls the CRLs as Strings
     */
    public CrlClientOnline(String... crls) {
        for (String url : crls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using one or more URLs.
     *
     * @param crls the CRLs as URLs
     */
    public CrlClientOnline(URL... crls) {
        for (URL url : crls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using a certificate chain.
     *
     * @param chain a certificate chain
     */
    public CrlClientOnline(Certificate[] chain) {
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = (X509Certificate) chain[i];
            LOGGER.info("Checking certificate: " + cert.getSubjectDN());
            String url = null;
            try {
                url = CertificateUtil.getCRLURL(cert);
                if (url != null) {
                    addUrl(url);
                }
            } catch (CertificateParsingException e) {
                LOGGER.info("Skipped CRL url (certificate could not be parsed)");
            }
        }
    }

    /**
     * Fetches the CRL bytes from an URL.
     * If no url is passed as parameter, the url will be obtained from the certificate.
     * If you want to load a CRL from a local file, subclass this method and pass an
     * URL with the path to the local file to this method. An other option is to use
     * the CrlClientOffline class.
     *
     * @see ICrlClient#getEncoded(java.security.cert.X509Certificate, java.lang.String)
     */
    @Override
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
                    throw new IllegalArgumentException("Passed url can not be null.");
                urllist.add(new URL(url));
                LOGGER.info("Found CRL url: " + url);
            } catch (Exception e) {
                LOGGER.info("Skipped CRL url: " + e.getMessage());
            }
        }
        List<byte[]> ar = new ArrayList<>();
        for (URL urlt : urllist) {
            try {
                LOGGER.info("Checking CRL: " + urlt);
                InputStream inp = SignUtils.getHttpResponse(urlt);
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
            } catch (Exception e) {
                LOGGER.info(MessageFormatUtil.format(LogMessageConstant.INVALID_DISTRIBUTION_POINT, e.getMessage()));
            }
        }
        return ar;
    }

    /**
     * Adds an URL to the list of CRL URLs
     *
     * @param url an URL in the form of a String
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
     *
     * @param url an URL object
     */
    protected void addUrl(URL url) {
        if (urls.contains(url)) {
            LOGGER.info("Skipped CRL url (duplicate): " + url);
            return;
        }
        urls.add(url);
        LOGGER.info("Added CRL url: " + url);
    }

    public int getUrlsSize() {
        return urls.size();
    }
}
