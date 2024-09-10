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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.cms.CMSContainer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;

class SignatureIdentifier extends AbstractIdentifiableObject {
    private final CMSContainer signature;
    private final String signatureName;
    private final Date signingDate;
    private final CertificateWrapper signingCertificate;

    public SignatureIdentifier(ValidationObjects signatureValidationObjects, CMSContainer signature,
                               String signatureName, Date signingDate) {
        super("S");
        this.signature = signature;
        this.signatureName = signatureName;
        this.signingDate = signingDate;
        this.signingCertificate = signatureValidationObjects.addObject(
                new CertificateWrapper(signature.getSignerInfo().getSigningCertificate()));
    }

    public String getDigestMethodAlgorithm() {
        return "http://www.w3.org/2001/04/xmlenc#sha256";
    }

    public String getDigestValue() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeLong(DateTimeUtil.getRelativeTime(signingDate));
            dos.writeUTF(signingCertificate.getIdentifier().getId());
            dos.write(signature.getSignerInfo().getSignatureData());
            dos.writeUTF(signatureName);
            dos.flush();
            bos.flush();
            MessageDigest digest = new BouncyCastleDigest().getMessageDigest(DigestAlgorithms.SHA256);
            return Base64.encodeBytes(digest.digest(bos.toByteArray()));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error creating signature id digest.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error creating output stream.", e);
        }
    }

    public String getBase64SignatureValue() {
        return Base64.encodeBytes(signature.getSignerInfo().getSignatureData());
    }

    public boolean isHashOnly() {
        return false;
    }

    public boolean isDocHashOnly() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignatureIdentifier that = (SignatureIdentifier) o;
        return Objects.equals(signature, that.signature) && Objects.equals(signatureName,
                that.signatureName) && Objects.equals(signingDate, that.signingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) signature, signatureName, signingDate);
    }
}
