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
package com.itextpdf.signatures.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.mac.AbstractMacIntegrityProtector;
import com.itextpdf.kernel.mac.MacProperties;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Class responsible for integrity protection in encrypted documents which uses MAC container in the signature mode.
 */
class SignatureMacIntegrityProtector extends AbstractMacIntegrityProtector {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String ID_ATTR_PDF_MAC_DATA = "1.0.32004.1.2";

    SignatureMacIntegrityProtector(PdfDocument document, MacProperties macProperties) {
        super(document, macProperties);
    }

    SignatureMacIntegrityProtector(PdfDocument document, PdfDictionary authDictionary) {
        super(document, authDictionary);
    }

    void prepareDocument() {
        document.addEventHandler(SignatureDocumentClosingEvent.START_SIGNATURE_PRE_CLOSE,
                new SignatureMacIntegrityProtector.SignatureMacPdfObjectAdder());
        document.addEventHandler(SignatureContainerGenerationEvent.START_SIGNATURE_CONTAINER_GENERATION,
                new SignatureMacIntegrityProtector.SignatureMacContainerEmbedder());
    }

    private void embedMacContainerInUnsignedAttributes(IASN1EncodableVector unsignedAttributes,
            InputStream documentInputStream, byte[] signature) throws IOException {
        IDERSequence mac;
        try {
            byte[] dataDigest = digestBytes(documentInputStream);
            mac = createMacContainer(dataDigest, generateRandomBytes(32), signature);
        } catch (GeneralSecurityException e) {
            throw new PdfException(KernelExceptionMessageConstant.CONTAINER_GENERATION_EXCEPTION, e);
        }
        IASN1EncodableVector macAttribute = BC_FACTORY.createASN1EncodableVector();
        macAttribute.add(BC_FACTORY.createASN1ObjectIdentifier(ID_ATTR_PDF_MAC_DATA));
        macAttribute.add(BC_FACTORY.createDERSet(mac));
        unsignedAttributes.add(BC_FACTORY.createDERSequence(macAttribute));
    }

    private final class SignatureMacPdfObjectAdder implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            if (event instanceof SignatureDocumentClosingEvent) {
                PdfDictionary signatureMacDictionary = new PdfDictionary();
                signatureMacDictionary.put(PdfName.MACLocation, PdfName.AttachedToSig);
                signatureMacDictionary.put(PdfName.SigObjRef,
                        ((SignatureDocumentClosingEvent) event).getSignatureReference());
                document.getTrailer().put(PdfName.AuthCode, signatureMacDictionary);
            }
        }
    }

    private final class SignatureMacContainerEmbedder implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            if (event instanceof SignatureContainerGenerationEvent) {
                SignatureContainerGenerationEvent signatureEvent = (SignatureContainerGenerationEvent) event;
                try {
                    embedMacContainerInUnsignedAttributes(signatureEvent.getUnsignedAttributes(),
                            signatureEvent.getDocumentInputStream(), signatureEvent.getSignature());
                } catch (IOException e) {
                    throw new PdfException(KernelExceptionMessageConstant.CONTAINER_EMBEDDING_EXCEPTION, e);
                }
            }
        }
    }
}
