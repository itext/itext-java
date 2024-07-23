/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the CrlClient that fetches the CRL bytes
 * from an URL.
 */
public class CrlClientOnline implements ICrlClient {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
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
        for (Certificate certificate : chain) {
            X509Certificate cert = (X509Certificate) certificate;
            LOGGER.info("Checking certificate: " + cert.getSubjectDN());
            List<String> urls = CertificateUtil.getCRLURLs(cert);
            for (String url : urls) {
                addUrl(url);
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
     * @throws CertificateEncodingException if an encoding error occurs in {@link X509Certificate}.
     * @see ICrlClient#getEncoded(java.security.cert.X509Certificate, java.lang.String)
     */
    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) throws CertificateEncodingException {
        if (checkCert == null) {
            return null;
        }
        List<URL> urlList = new ArrayList<>(urls);
        if (urlList.isEmpty()) {
            LOGGER.info(MessageFormatUtil.format(
                    "Looking for CRL for certificate {0}", BOUNCY_CASTLE_FACTORY.createX500Name(checkCert)));
            try {
                List<String> urlsList = new ArrayList<>();
                if (url == null) {
                    urlsList = CertificateUtil.getCRLURLs(checkCert);
                } else {
                    urlsList.add(url);
                }
                if (urlsList.isEmpty()) {
                    throw new IllegalArgumentException("Passed url can not be null.");
                }
                for (String urlString : urlsList) {
                    urlList.add(new URL(urlString));
                    LOGGER.info("Found CRL url: " + urlString);
                }
            } catch (Exception e) {
                LOGGER.info("Skipped CRL url: " + e.getMessage());
            }
        }
        List<byte[]> ar = new ArrayList<>();
        for (URL urlt : urlList) {
            try {
                LOGGER.info("Checking CRL: " + urlt);
                InputStream inp = getCrlResponse(checkCert, urlt);
                byte[] buf = new byte[1024];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                while (true) {
                    int n = inp.read(buf, 0, buf.length);
                    if (n <= 0) {
                        break;
                    }
                    bout.write(buf, 0, n);
                }
                inp.close();
                ar.add(bout.toByteArray());
                LOGGER.info("Added CRL found at: " + urlt);
            } catch (Exception e) {
                LOGGER.info(MessageFormatUtil.format(IoLogMessageConstant.INVALID_DISTRIBUTION_POINT,
                        e.getMessage()));
            }
        }
        return ar;
    }

    /**
     * Get CRL response represented as {@link InputStream}.
     * 
     * @param cert {@link X509Certificate} certificate to get CRL response for
     * @param urlt {@link URL} link, which is expected to be used to get CRL response from
     * 
     * @return CRL response bytes, represented as {@link InputStream}
     * 
     * @throws IOException if an I/O error occurs
     */
    protected InputStream getCrlResponse(X509Certificate cert, URL urlt) throws IOException {
        return SignUtils.getHttpResponse(urlt);
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

    /**
     * Get an amount of URLs provided for this CRL.
     *
     * @return {@code int} number of URLs
     */
    public int getUrlsSize() {
        return urls.size();
    }
}
