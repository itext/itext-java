/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * @param crls
     */
    public CrlClientOnline(String... crls) {
        for (String url : crls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using one or more URLs.
     *
     * @param crls
     */
    public CrlClientOnline(URL... crls) {
        for (URL url : crls) {
            addUrl(url);
        }
    }

    /**
     * Creates a CrlClientOnline instance using a certificate chain.
     *
     * @param chain
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
                LOGGER.info("Skipped CRL: " + e.getMessage() + " for " + urlt);
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
        } catch (IOException e) {
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
