/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.io.codec.Base64;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SignaturePolicyId;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Class that encapsulates the signature policy information
 *
 * Sample:
 *
 *      SignaturePolicyInfo spi = new SignaturePolicyInfo("2.16.724.1.3.1.1.2.1.9",
 *      "G7roucf600+f03r/o0bAOQ6WAs0=", "SHA-1", "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf");
 */
public class SignaturePolicyInfo {
    private String policyIdentifier;
    private byte[] policyHash;
    private String policyDigestAlgorithm;
    private String policyUri;

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     * @param policyIdentifier the id of the signature policy
     * @param policyHash the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, byte[] policyHash, String policyDigestAlgorithm, String policyUri) {
        if (policyIdentifier == null || policyIdentifier.length() == 0) {
            throw new IllegalArgumentException("Policy identifier cannot be null");
        }
        if (policyHash == null) {
            throw new IllegalArgumentException("Policy hash cannot be null");
        }
        if (policyDigestAlgorithm == null || policyDigestAlgorithm.length() == 0) {
            throw new IllegalArgumentException("Policy digest algorithm cannot be null");
        }

        this.policyIdentifier = policyIdentifier;
        this.policyHash = policyHash;
        this.policyDigestAlgorithm = policyDigestAlgorithm;
        this.policyUri = policyUri;
    }

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     * @param policyIdentifier the id of the signature policy
     * @param policyHashBase64 the Base64 presentation of the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, String policyHashBase64, String policyDigestAlgorithm, String policyUri) {
        this(policyIdentifier, policyHashBase64 != null ? Base64.decode(policyHashBase64) : null, policyDigestAlgorithm, policyUri);
    }

    public String getPolicyIdentifier() {
        return policyIdentifier;
    }

    public byte[] getPolicyHash() {
        return policyHash;
    }

    public String getPolicyDigestAlgorithm() {
        return policyDigestAlgorithm;
    }

    public String getPolicyUri() {
        return policyUri;
    }

    SignaturePolicyIdentifier toSignaturePolicyIdentifier() {
        String algId = DigestAlgorithms.getAllowedDigest(this.policyDigestAlgorithm);

        if (algId == null || algId.length() == 0) {
            throw new IllegalArgumentException("Invalid policy hash algorithm");
        }

        SignaturePolicyIdentifier signaturePolicyIdentifier = null;
        SigPolicyQualifierInfo spqi = null;

        if (this.policyUri != null && this.policyUri.length() > 0) {
            spqi = new SigPolicyQualifierInfo(PKCSObjectIdentifiers.id_spq_ets_uri, new DERIA5String(this.policyUri));
        }

        signaturePolicyIdentifier = new SignaturePolicyIdentifier(new SignaturePolicyId(DERObjectIdentifier.getInstance(new DERObjectIdentifier(this.policyIdentifier.replace("urn:oid:", ""))),
                new OtherHashAlgAndValue(new AlgorithmIdentifier(new ASN1ObjectIdentifier(algId)), new DEROctetString(this.policyHash)), SignUtils.createSigPolicyQualifiers(spqi)));

        return signaturePolicyIdentifier;
    }
}
