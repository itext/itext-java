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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
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
    private IMetaInfo metaInfo;

    /**
     * Creates {@link PdfRevisionsReader} class.
     *
     * @param reader {@link PdfReader} instance from which revisions to be collected
     */
    public PdfRevisionsReader(PdfReader reader) {
        this.reader = reader;
    }

    /**
     * Sets the {@link IMetaInfo} that will be used during {@link PdfDocument} creation.
     *
     * @param metaInfo meta info to set
     */
    public void setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
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
                    PdfReader newReader = new PdfReader(inputStream, reader.getPropertiesCopy());
                    PdfDocument newDocument = new PdfDocument(newReader,
                            new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
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
