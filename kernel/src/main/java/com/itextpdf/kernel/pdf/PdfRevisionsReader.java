package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.pdf.PdfReader.XrefProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to retrieve important information about PDF document revisions.
 */
public class PdfRevisionsReader {
    private final PdfReader reader;
    private List<DocumentRevision> documentRevisions = null;

    /**
     * Creates {@link PdfRevisionsReader} class.
     *
     * @param reader {@link PdfReader} instance from which revisions to be collected
     */
    public PdfRevisionsReader(PdfReader reader) {
        this.reader = reader;
    }

    /**
     * Gets information about PDF document revisions.
     *
     * @return {@link List} of {@link DocumentRevision} objects
     *
     * @throws IOException in case of input-output related exceptions during PDF document reading
     */
    public List<DocumentRevision> getAllRevisions() throws IOException {
        if (documentRevisions == null) {
            RandomAccessFileOrArray raf = reader.getSafeFile();
            WindowRandomAccessSource source = new WindowRandomAccessSource(
                    raf.createSourceView(), 0, raf.length());

            try (InputStream inputStream = new RASInputStream(source);
                    PdfReader newReader = new PdfReader(inputStream);
                    PdfDocument newDocument = new PdfDocument(newReader)) {
                newDocument.getXref().unmarkReadingCompleted();
                newDocument.getXref().clearAllReferences();
                RevisionsXrefProcessor xrefProcessor = new RevisionsXrefProcessor();
                newReader.setXrefProcessor(xrefProcessor);
                newReader.readXref();
                documentRevisions = xrefProcessor.getDocumentRevisions();
            }
            Collections.reverse(documentRevisions);
        }
        return documentRevisions;
    }

    static class RevisionsXrefProcessor extends XrefProcessor {
        private final List<DocumentRevision> documentRevisions = new ArrayList<>();

        @Override
        void processXref(PdfXrefTable xrefTable, PdfTokenizer tokenizer) throws IOException {
            Set<PdfIndirectReference> modifiedObjects = new HashSet<>();
            for (int i = 0; i < xrefTable.size(); ++i) {
                if (xrefTable.get(i) != null) {
                    modifiedObjects.add(xrefTable.get(i));
                }
            }
            long eofOffset = tokenizer.getNextEof();
            documentRevisions.add(new DocumentRevision(eofOffset, modifiedObjects));
            xrefTable.clearAllReferences();
        }

        List<DocumentRevision> getDocumentRevisions() {
            return documentRevisions;
        }
    }
}
