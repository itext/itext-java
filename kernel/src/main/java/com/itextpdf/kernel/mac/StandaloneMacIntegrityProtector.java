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

import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Class responsible for integrity protection in encrypted documents, which uses MAC container in the standalone mode.
 */
class StandaloneMacIntegrityProtector extends AbstractMacIntegrityProtector {
    private MacPdfObject macPdfObject;

    StandaloneMacIntegrityProtector(PdfDocument document, MacProperties macProperties) {
        super(document, macProperties);
    }

    StandaloneMacIntegrityProtector(PdfDocument document, PdfDictionary authDictionary) {
        super(document, authDictionary);
    }

    void prepareDocument() {
        document.addEventHandler(PdfDocumentEvent.START_DOCUMENT_CLOSING,
                new StandaloneMacIntegrityProtector.StandaloneMacPdfObjectAdder());
        document.addEventHandler(PdfDocumentEvent.START_WRITER_CLOSING,
                new StandaloneMacIntegrityProtector.StandaloneMacContainerEmbedder());
    }

    private void embedMacContainerInTrailer() throws IOException {
        byte[] documentBytes = getDocumentByteArrayOutputStream().toByteArray();
        long[] byteRange = macPdfObject.computeByteRange(documentBytes.length);

        long byteRangePosition = macPdfObject.getByteRangePosition();
        ByteArrayOutputStream localBaos = new ByteArrayOutputStream();
        PdfOutputStream os = new PdfOutputStream(localBaos);
        os.write('[');
        for (long l : byteRange) {
            os.writeLong(l).write(' ');
        }
        os.write(']');
        System.arraycopy(localBaos.toByteArray(), 0, documentBytes, (int) byteRangePosition, localBaos.size());

        byte[] mac = createDocumentDigestAndMacContainer(documentBytes, byteRange);
        PdfString macString = new PdfString(mac).setHexWriting(true);

        // fill in the MAC
        localBaos.reset();
        os.write(macString);
        System.arraycopy(localBaos.toByteArray(), 0, documentBytes, (int) byteRange[1], localBaos.size());
        getDocumentByteArrayOutputStream().reset();
        document.getWriter().getOutputStream().write(documentBytes, 0, documentBytes.length);
    }

    private byte[] createDocumentDigestAndMacContainer(byte[] documentBytes, long[] byteRange) throws IOException {
        IRandomAccessSource ras = new RandomAccessSourceFactory().createSource(documentBytes);
        try (InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(ras, byteRange))) {
            byte[] dataDigest = digestBytes(rg);
            return createMacContainer(dataDigest, generateRandomBytes(32), null).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new PdfException(KernelExceptionMessageConstant.CONTAINER_GENERATION_EXCEPTION, e);
        }
    }

    private int getContainerSizeEstimate() {
        try {
            return createMacContainer(digestBytes(new byte[0]), generateRandomBytes(32), null)
                    .getEncoded().length * 2 + 2;
        } catch (GeneralSecurityException | IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CONTAINER_GENERATION_EXCEPTION, e);
        }
    }

    private ByteArrayOutputStream getDocumentByteArrayOutputStream() {
        return ((ByteArrayOutputStream) document.getWriter().getOutputStream());
    }

    private final class StandaloneMacPdfObjectAdder extends AbstractPdfDocumentEventHandler {
        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            macPdfObject = new MacPdfObject(getContainerSizeEstimate());
            document.getTrailer().put(PdfName.AuthCode, macPdfObject.getPdfObject());
        }
    }

    private final class StandaloneMacContainerEmbedder extends AbstractPdfDocumentEventHandler {
        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            try {
                embedMacContainerInTrailer();
            } catch (IOException e) {
                throw new PdfException(KernelExceptionMessageConstant.CONTAINER_EMBEDDING_EXCEPTION, e);
            }
        }
    }
}
