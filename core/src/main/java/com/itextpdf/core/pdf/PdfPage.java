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
    protected List<PdfStream> contentStreams = null;

    public PdfPage(PdfDocument doc) {
        this(doc, doc.getDefaultPageSize());
    }

    public PdfPage(PdfDocument doc, PageSize pageSize) {
        super();
        makeIndirect(doc);
        //NOTE: Write PdfResources as Direct Object
        put(PdfName.Resources, new PdfResources());
        contentStreams = new ArrayList<PdfStream>();
        PdfStream contentStream = new PdfStream(getDocument());
        contentStreams.add(contentStream);
        put(PdfName.Type, PdfName.Page);
        put(PdfName.MediaBox, new PdfArray(pageSize));
        this.pageSize = pageSize;
        doc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfStream getContentStream() {
        return contentStreams.get(contentStreams.size() - 1);
    }

    public PdfStream newContentStreamBefore() {
        PdfStream contentStream = new PdfStream(getDocument());
        contentStreams.add(0, contentStream);
        return contentStream;
    }

    public PdfStream newContentStreamAfter() {
        PdfStream contentStream = new PdfStream(getDocument());
        contentStreams.add(contentStream);
        return contentStream;
    }

    public PdfResources getResources() {
        return (PdfResources)get(PdfName.Resources);
    }

    @Override
    public void flush() throws IOException, PdfException {
        if (isFlushed())
            return;
        if (contentStreams != null) {
            for (PdfStream contentStream : contentStreams)
                contentStream.flush();
            if (contentStreams.size() == 1) {
                put(PdfName.Contents, contentStreams.get(0));
            } else {
                ArrayList<PdfObject> streams = new ArrayList<PdfObject>();
                for (PdfStream contentStream : contentStreams)
                    streams.add(contentStream);
                put(PdfName.Contents, new PdfArray(streams));
            }
            contentStreams.clear();
            contentStreams = null;
        }
        super.flush();
    }

}
