package com.itextpdf.core.pdf;

import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.geom.PageSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfPage extends PdfDictionary {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    protected PageSize pageSize = null;
    protected List<PdfContentStream> contentStreams = null;

    public PdfPage(PdfDocument doc) {
        this(doc, doc.getDefaultPageSize());
    }

    public PdfPage(PdfDocument doc, PageSize pageSize) {
        super(doc);
        contentStreams = new ArrayList<PdfContentStream>() {{
            add(new PdfContentStream(pdfDocument));
        }};
        put(PdfName.Type, PdfName.Page);
        put(PdfName.MediaBox, new PdfArray(pageSize));
        put(PdfName.Resources, new PdfDictionary());
        this.pageSize = pageSize;
        doc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfContentStream getContentStream() {
        return contentStreams.get(contentStreams.size() - 1);
    }

    public PdfContentStream newContentStreamBefore() {
        PdfContentStream contentStream = new PdfContentStream(pdfDocument);
        contentStreams.add(0, contentStream);
        return contentStream;
    }

    public PdfContentStream newContentStreamAfter() {
        PdfContentStream contentStream = new PdfContentStream(pdfDocument);
        contentStreams.add(contentStream);
        return contentStream;
    }

    @Override
    protected void flush(PdfWriter writer) throws IOException, PdfException {
        if (flushed)
            return;
        if (contentStreams != null) {
            for (PdfContentStream contentStream : contentStreams)
                contentStream.flush();
            if (contentStreams.size() == 1) {
                put(PdfName.Contents, contentStreams.get(0));
            } else {
                ArrayList<PdfObject> streams = new ArrayList<PdfObject>();
                for (PdfContentStream contentStream : contentStreams)
                    streams.add(contentStream);
                put(PdfName.Contents, new PdfArray(streams));
            }
            contentStreams.clear();
            contentStreams = null;
        }
        super.flush(writer);
    }
}
