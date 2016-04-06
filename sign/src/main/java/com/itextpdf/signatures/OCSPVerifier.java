/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows you to verify a certificate against
 * one or more OCSP responses.
 */
public class OCSPVerifier extends RootStoreVerifier {

    /** The Logger instance */
    protected final static Logger LOGGER = LoggerFactory.getLogger(OCSPVerifier.class);

    /** The list of OCSP responses. */
    protected List<BasicOCSPResp> ocsps;

    /**
     * Creates an OCSPVerifier instance.
     * @param verifier	the next verifier in the chain
     * @param ocsps a list of OCSP responses
     */
    public OCSPVerifier(CertificateVerifier verifier, List<BasicOCSPResp> ocsps) {
        super(verifier);
        this.ocsps = ocsps;
    }

    /**
     * Verifies if a a valid OCSP response is found for the certificate.
     * If this method returns false, it doesn't mean the certificate isn't valid.
     * It means we couldn't verify it against any OCSP response that was available.
     * @param signCert	the certificate that needs to be checked
     * @param issuerCert	its issuer
     * @return a list of <code>VerificationOK</code> objects.
     * The list will be empty if the certificate couldn't be verified.
     * @see com.itextpdf.text.pdf.security.RootStoreVerifier#verify(java.security.cert.X509Certificate, java.security.cert.X509Certificate, java.util.Date)
     */
    public List<VerificationOK> verify(X509Certificate signCert,
                                       X509Certificate issuerCert, Date signDate)
            throws GeneralSecurityException, IOException {
        List<VerificationOK> result = new ArrayList<>();
        int validOCSPsFound = 0;
        // first check in the list of OCSP responses that was provided
        if (ocsps != null) {
            for (BasicOCSPResp ocspResp : ocsps) {
                if (verify(ocspResp, signCert, issuerCert, signDate))
                    validOCSPsFound++;
            }
        }
        // then check online if allowed
        boolean online = false;
        if (onlineCheckingAllowed && validOCSPsFound == 0) {
            if (verify(getOcspResponse(signCert, issuerCert), signCert, issuerCert, signDate)) {
                validOCSPsFound++;
                online = true;
            }
        }
        // show how many valid OCSP responses were found
        LOGGER.info("Valid OCSPs found: " + validOCSPsFound);
        if (validOCSPsFound > 0)
            result.add(new VerificationOK(signCert, this.getClass(), "Valid OCSPs Found: " + validOCSPsFound + (online ? " (online)" : "")));
        if (verifier != null)
            result.addAll(verifier.verify(signCert, issuerCert, signDate));
        // verify using the previous verifier in the chain (if any)
        return result;
    }


    /**
     * Verifies a certificate against a single OCSP response
     * @param ocspResp	the OCSP response
     * @param serialNumber	the serial number of the certificate that needs to be checked
     * @param issuerCert
     * @param signDate
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean verify(BasicOCSPResp ocspResp, X509Certificate signCert, X509Certificate issuerCert, Date signDate) throws GeneralSecurityException, IOException {
        if (ocspResp == null)
            return false;
        // Getting the responses
        SingleResp[] resp = ocspResp.getResponses();
        for (int i = 0; i < resp.length; i++) {
            // check if the serial number corresponds
            if (!signCert.getSerialNumber().equals(resp[i].getCertID().getSerialNumber())) {
                continue;
            }
            // check if the issuer matches
            try {
                if (issuerCert == null) issuerCert = signCert;
                if (!resp[i].getCertID().matchesIssuer(new X509CertificateHolder(issuerCert.getEncoded()), new BcDigestCalculatorProvider())) {
                    LOGGER.info("OCSP: Issuers doesn't match.");
                    continue;
                }
            } catch (OCSPException e) {
                continue;
            }
            // check if the OCSP response was valid at the time of signing
            Date nextUpdate = resp[i].getNextUpdate();
            if (nextUpdate == null) {
                nextUpdate = new Date(resp[i].getThisUpdate().getTime() + 180000l);
                LOGGER.info(MessageFormat.format("No 'next update' for OCSP Response; assuming {0}", nextUpdate));
            }
            if (signDate.after(nextUpdate)) {
                LOGGER.info(MessageFormat.format("OCSP no longer valid: {0} after {1}", signDate, nextUpdate));
                continue;
            }
            // check the status of the certificate
            Object status = resp[i].getCertStatus();
            if (status == CertificateStatus.GOOD) {
                // check if the OCSP response was genuine
                isValidResponse(ocspResp, issuerCert);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if an OCSP response is genuine
     * @param ocspResp	the OCSP response
     * @param issuerCert	the issuer certificate
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void isValidResponse(BasicOCSPResp ocspResp, X509Certificate issuerCert) throws GeneralSecurityException, IOException {
        // by default the OCSP responder certificate is the issuer certificate
        X509Certificate responderCert = issuerCert;
        // check if there's a responder certificate
        X509CertificateHolder[] certHolders = ocspResp.getCerts();
        if (certHolders.length > 0) {
            responderCert = new JcaX509CertificateConverter().setProvider( "BC" ).getCertificate(certHolders[0]);
            try {
                responderCert.verify(issuerCert.getPublicKey());
            }
            catch(GeneralSecurityException e) {
                if (super.verify(responderCert, issuerCert, null).size() == 0)
                    throw new VerificationException(responderCert, "Responder certificate couldn't be verified");
            }
        }
        // verify if the signature of the response is valid
        if (!verifyResponse(ocspResp, responderCert))
            throw new VerificationException(responderCert, "OCSP response could not be verified");
    }

    /**
     * Verifies if the signature of the response is valid.
     * If it doesn't verify against the responder certificate, it may verify
     * using a trusted anchor.
     * @param ocspResp	the response object
     * @param responderCert	the certificate that may be used to sign the response
     * @return	true if the response can be trusted
     */
    public boolean verifyResponse(BasicOCSPResp ocspResp, X509Certificate responderCert) {
        // testing using the responder certificate
        if (isSignatureValid(ocspResp, responderCert))
            return true;
        // testing using trusted anchors
        if (rootStore == null)
            return false;
        try {
            // loop over the certificates in the root store
            for (Enumeration<String> aliases = rootStore.aliases(); aliases.hasMoreElements();) {
                String alias = aliases.nextElement();
                try {
                    if (!rootStore.isCertificateEntry(alias))
                        continue;
                    X509Certificate anchor = (X509Certificate)rootStore.getCertificate(alias);
                    if (isSignatureValid(ocspResp, anchor))
                        return true;
                } catch (GeneralSecurityException e) {
                    continue;
                }
            }
        }
        catch (GeneralSecurityException e) {
            return false;
        }
        return false;
    }

    /**
     * Checks if an OCSP response is genuine
     * @param ocspResp	the OCSP response
     * @param responderCert	the responder certificate
     * @return	true if the OCSP response verifies against the responder certificate
     */
    public boolean isSignatureValid(BasicOCSPResp ocspResp, Certificate responderCert) {
        try {
            ContentVerifierProvider verifierProvider = new JcaContentVerifierProviderBuilder().setProvider("BC").build(responderCert.getPublicKey());
            return ocspResp.isSignatureValid(verifierProvider);
        } catch (OperatorCreationException e) {
            return false;
        } catch (OCSPException e) {
            return false;
        }
    }

    /**
     * Gets an OCSP response online and returns it if the status is GOOD
     * (without further checking).
     * @param signCert	the signing certificate
     * @param issuerCert	the issuer certificate
     * @return an OCSP response
     */
    public BasicOCSPResp getOcspResponse(X509Certificate signCert, X509Certificate issuerCert) {
        if (signCert == null && issuerCert == null) {
            return null;
        }
        OcspClientBouncyCastle ocsp = new OcspClientBouncyCastle();
        BasicOCSPResp ocspResp = ocsp.getBasicOCSPResp(signCert, issuerCert, null);
        if (ocspResp == null) {
            return null;
        }
        SingleResp[] resp = ocspResp.getResponses();
        for (int i = 0; i < resp.length; i++) {
            Object status = resp[i].getCertStatus();
            if (status == CertificateStatus.GOOD) {
                return ocspResp;
            }
        }
        return null;
    }
}
