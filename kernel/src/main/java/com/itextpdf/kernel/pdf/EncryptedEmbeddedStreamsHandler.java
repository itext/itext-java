/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import java.util.HashSet;
import java.util.Set;

class EncryptedEmbeddedStreamsHandler {

    private final PdfDocument document;

    private final Set<PdfStream> embeddedStreams = new HashSet<>();

    /**
     * Creates {@link EncryptedEmbeddedStreamsHandler} instance.
     *
     * @param document {@link PdfDocument} associated with this handler
     */
    EncryptedEmbeddedStreamsHandler(PdfDocument document) {
        this.document = document;
    }

    /**
     * Stores all embedded streams present in the {@link PdfDocument}.
     * Note that during this method we traverse through every indirect object of the document.
     */
    void storeAllEmbeddedStreams() {
        for (int i = 0; i < document.getNumberOfPdfObjects(); ++i) {
            PdfObject indirectObject = document.getPdfObject(i);
            if (indirectObject instanceof PdfDictionary) {
                PdfStream embeddedStream = getEmbeddedFileStreamFromDictionary((PdfDictionary) indirectObject);
                if (embeddedStream != null) {
                    storeEmbeddedStream(embeddedStream);
                }
            }
        }
    }

    void storeEmbeddedStream(PdfStream embeddedStream) {
        embeddedStreams.add(embeddedStream);
    }

    /**
     * Checks, whether this {@link PdfStream} was stored as embedded stream.
     *
     * @param stream to be checked
     * @return true if this stream is embedded, false otherwise
     */
    boolean isStreamStoredAsEmbedded(PdfStream stream) {
        return embeddedStreams.contains(stream);
    }

    private static PdfStream getEmbeddedFileStreamFromDictionary(PdfDictionary dictionary) {
        PdfDictionary embeddedFileDictionary = dictionary.getAsDictionary(PdfName.EF);
        if (PdfName.Filespec.equals(dictionary.getAsName(PdfName.Type)) && embeddedFileDictionary != null) {
            return embeddedFileDictionary.getAsStream(PdfName.F);
        }
        return null;
    }
}
