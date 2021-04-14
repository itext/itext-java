package com.itextpdf.kernel.pdf;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

class EncryptedEmbeddedStreamsHandler implements Serializable {

    private static final long serialVersionUID = -4542644924377740467L;

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
