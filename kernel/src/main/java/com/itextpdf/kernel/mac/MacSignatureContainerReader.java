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
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class MacSignatureContainerReader extends MacContainerReader {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String ID_ATTR_PDF_MAC_DATA = "1.0.32004.1.2";

    MacSignatureContainerReader(PdfDictionary authDictionary) {
        super(authDictionary);
    }

    @Override
    byte[] parseSignature(PdfDictionary authDictionary) {
        PdfDictionary signatureDictionary = getSignatureDictionary(authDictionary);
        PdfString contentsString = signatureDictionary.getAsString(PdfName.Contents);
        contentsString.markAsUnencryptedObject();
        return parseSignatureValueFromSignatureContainer(contentsString.getValueBytes());
    }

    @Override
    long[] parseByteRange(PdfDictionary authDictionary) {
        PdfDictionary signatureDictionary = getSignatureDictionary(authDictionary);
        return signatureDictionary.getAsArray(PdfName.ByteRange).toLongArray();
    }

    @Override
    byte[] parseMacContainer(PdfDictionary authDictionary) {
        PdfDictionary signatureDictionary = getSignatureDictionary(authDictionary);
        PdfString contentsString = signatureDictionary.getAsString(PdfName.Contents);
        contentsString.markAsUnencryptedObject();
        return parseMacContainerFromSignatureContainer(contentsString.getValueBytes());
    }

    private static byte[] parseSignatureValueFromSignatureContainer(byte[] signature) {
        try {
            IASN1Sequence signerInfoSeq = parseSignerInfoSequence(signature);

            int signatureValueIndex = 3;
            IASN1TaggedObject taggedSignedAttributes =
                    BC_FACTORY.createASN1TaggedObject(signerInfoSeq.getObjectAt(signatureValueIndex));
            if (taggedSignedAttributes != null) {
                ++signatureValueIndex;
            }
            IDEROctetString signatureDataOS = BC_FACTORY.createDEROctetString(
                    signerInfoSeq.getObjectAt(++signatureValueIndex));
            return signatureDataOS.getOctets();
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.MAC_EXTRACTION_EXCEPTION, e);
        }
    }

    private static byte[] parseMacContainerFromSignatureContainer(byte[] signature) {
        try {
            IASN1Sequence signerInfoSeq = parseSignerInfoSequence(signature);

            int unsignedAttributesIndex = 3;
            IASN1TaggedObject taggedSignedAttributes =
                    BC_FACTORY.createASN1TaggedObject(signerInfoSeq.getObjectAt(unsignedAttributesIndex));
            if (taggedSignedAttributes != null) {
                ++unsignedAttributesIndex;
            }
            unsignedAttributesIndex += 2;
            if (signerInfoSeq.size() > unsignedAttributesIndex) {
                IASN1Set unsignedAttributes = BC_FACTORY.createASN1Set(BC_FACTORY.createASN1TaggedObject(
                        signerInfoSeq.getObjectAt(unsignedAttributesIndex)), false);
                for (int i = 0; i < unsignedAttributes.size(); i++) {
                    IASN1Sequence attrSeq = BC_FACTORY.createASN1Sequence(unsignedAttributes.getObjectAt(i));
                    IASN1ObjectIdentifier attrType = BC_FACTORY.createASN1ObjectIdentifier(attrSeq.getObjectAt(0));
                    if (ID_ATTR_PDF_MAC_DATA.equals(attrType.getId())) {
                        IASN1Set macSet = BC_FACTORY.createASN1Set(attrSeq.getObjectAt(1));
                        return macSet.getObjectAt(0).toASN1Primitive().getEncoded();
                    }
                }
            }
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.MAC_EXTRACTION_EXCEPTION, e);
        }
        throw new PdfException(KernelExceptionMessageConstant.MAC_ATTRIBUTE_NOT_SPECIFIED);
    }

    private static PdfDictionary getSignatureDictionary(PdfDictionary authDictionary) {
        if (authDictionary.getAsDictionary(PdfName.SigObjRef) == null) {
            throw new PdfException(KernelExceptionMessageConstant.SIG_OBJ_REF_NOT_SPECIFIED);
        }
        return authDictionary.getAsDictionary(PdfName.SigObjRef);
    }

    private static IASN1Sequence parseSignerInfoSequence(byte[] signature) throws IOException {
        try (IASN1InputStream is = BC_FACTORY.createASN1InputStream(new ByteArrayInputStream(signature))) {
            IASN1Sequence contentInfo = BC_FACTORY.createASN1Sequence(is.readObject());
            IASN1Sequence signedData = BC_FACTORY.createASN1Sequence(
                    BC_FACTORY.createASN1TaggedObject(contentInfo.getObjectAt(1)).getObject());

            int signerInfoIndex = 4;
            IASN1TaggedObject taggedObj = BC_FACTORY.createASN1TaggedObject(signedData.getObjectAt(signerInfoIndex));
            if (taggedObj != null) {
                ++signerInfoIndex;
            }
            return BC_FACTORY.createASN1Sequence(BC_FACTORY.createASN1Set(
                    signedData.getObjectAt(signerInfoIndex)).getObjectAt(0));
        }
    }
}
