/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.util.Arrays;

/**
 * Represents the DSS dictionary.
 */
public class PdfDSS extends PdfObjectWrapper<PdfDictionary> {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * Creates new {@link PdfDSS} instance.
     *
     * @param pdfObject {@link PdfDictionary} to create new {@link PdfDSS} instance from
     */
    public PdfDSS(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Returns certificates stored in DSS dictionary.
     *
     * @return certificates stored in DSS dictionary
     */
    public Certificate[] getCertificates() {
        PdfArray certs = super.getPdfObject().getAsArray(PdfName.Certs);
        Certificate[] result = new Certificate[certs == null ? 0 : certs.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = CertificateUtil.generateCertificate(
                        new ByteArrayInputStream(certs.getAsStream(i).getBytes()));
            } catch (Exception ignored) {
                // Certificate will be ignored.
            }
        }
        return result;
    }

    /**
     * Returns OCSP responses stored in DSS dictionary.
     *
     * @return OCSP responses stored in DSS dictionary
     */
    public IOCSPResp[] getOcsps() {
        PdfArray ocsps = getPdfObject().getAsArray(PdfName.OCSPs);
        IOCSPResp[] result = new IOCSPResp[ocsps == null ? 0 : ocsps.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = BOUNCY_CASTLE_FACTORY.createOCSPResp(ocsps.getAsStream(i).getBytes());
            } catch (Exception ignored) {
                // ignore failing OCSP responses.
            }
        }
        return result;
    }

    /**
     * Returns CRL responses stored in DSS dictionary.
     *
     * @return CRL responses stored in DSS dictionary
     */
    public X509CRL[] getCrls() {
        PdfArray crls = getPdfObject().getAsArray(PdfName.CRLs);
        X509CRL[] result = new X509CRL[crls == null ? 0 : crls.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = (X509CRL) CertificateUtil.parseCrlFromBytes(crls.getAsStream(i).getBytes());
            } catch (Exception ignored) {
                // Ignore failing CRLs.
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.hashCode(getCertificates());
        result = 31 * result + Arrays.hashCode(getOcsps());
        result = 31 * result + Arrays.hashCode(getCrls());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
