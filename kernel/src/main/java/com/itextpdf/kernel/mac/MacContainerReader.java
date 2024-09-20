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
package com.itextpdf.kernel.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.ByteArrayInputStream;
import java.io.IOException;

abstract class MacContainerReader {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final byte[] macContainer;
    private final long[] byteRange;
    private final byte[] signature;

    MacContainerReader(PdfDictionary authDictionary) {
        this.macContainer = parseMacContainer(authDictionary);
        this.byteRange = parseByteRange(authDictionary);
        this.signature = parseSignature(authDictionary);
    }

    static MacContainerReader getInstance(PdfDictionary authDictionary) {
        PdfName macLocation = authDictionary.getAsName(PdfName.MACLocation);
        if (PdfName.Standalone.equals(macLocation)) {
            return new MacStandaloneContainerReader(authDictionary);
        } else if (PdfName.AttachedToSig.equals(macLocation)) {
            return new MacSignatureContainerReader(authDictionary);
        }
        throw new PdfException(KernelExceptionMessageConstant.MAC_LOCATION_NOT_SPECIFIED);
    }

    abstract byte[] parseSignature(PdfDictionary authDictionary);

    abstract long[] parseByteRange(PdfDictionary authDictionary);

    abstract byte[] parseMacContainer(PdfDictionary authDictionary);

    long[] getByteRange() {
        return byteRange;
    }

    byte[] getSignature() {
        return signature;
    }

    byte[] parseMac() {
        IASN1Sequence authDataSequence = getAuthDataSequence();
        return BC_FACTORY.createASN1OctetString(authDataSequence.getObjectAt(6)).getOctets();
    }

    IASN1Set parseAuthAttributes() {
        IASN1Sequence authDataSequence = getAuthDataSequence();
        return BC_FACTORY.createASN1Set(BC_FACTORY.createASN1TaggedObject(authDataSequence.getObjectAt(5)), false);
    }

    IASN1Sequence parseMessageDigest() {
        IASN1Set authAttributes = parseAuthAttributes();
        return BC_FACTORY.createASN1Sequence(authAttributes.getObjectAt(2));
    }

    byte[] parseMacKey() {
        IASN1Sequence authDataSequence = getAuthDataSequence();
        IASN1Sequence recInfo = BC_FACTORY.createASN1Sequence(BC_FACTORY.createASN1TaggedObject(
                BC_FACTORY.createASN1Set(authDataSequence.getObjectAt(1)).getObjectAt(0)).getObject());
        IASN1OctetString encryptedKey = BC_FACTORY.createASN1OctetString(recInfo.getObjectAt(3));

        return encryptedKey.getOctets();
    }

    String parseDigestAlgorithm() {
        IASN1Sequence authDataSequence = getAuthDataSequence();
        IASN1Primitive digestAlgorithmContainer =
                BC_FACTORY.createASN1TaggedObject(authDataSequence.getObjectAt(3)).getObject();
        IASN1ObjectIdentifier digestAlgorithm;
        if (BC_FACTORY.createASN1ObjectIdentifier(digestAlgorithmContainer) != null) {
            digestAlgorithm = BC_FACTORY.createASN1ObjectIdentifier(digestAlgorithmContainer);
        } else {
            digestAlgorithm = BC_FACTORY.createASN1ObjectIdentifier(
                    BC_FACTORY.createASN1Sequence(digestAlgorithmContainer).getObjectAt(0));
        }

        return digestAlgorithm.getId();
    }

    private IASN1Sequence getAuthDataSequence() {
        IASN1Sequence contentInfoSequence;
        try (IASN1InputStream din =
                BC_FACTORY.createASN1InputStream(new ByteArrayInputStream(macContainer))) {
            contentInfoSequence = BC_FACTORY.createASN1Sequence(din.readObject());
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CONTAINER_PARSING_EXCEPTION, e);
        }
        return BC_FACTORY.createASN1Sequence(BC_FACTORY.createASN1TaggedObject(
                contentInfoSequence.getObjectAt(1)).getObject());
    }
}
