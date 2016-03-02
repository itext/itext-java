package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that makes it cleaner to process content from pages of a {@link PdfDocument}
 * through a specified RenderListener.
 */
public class PdfDocumentContentParser {

    private final PdfDocument pdfDocument;

    public PdfDocumentContentParser(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Processes content from the specified page number using the specified listener.
     * Also allows registration of custom ContentOperators
     *
     * @param <E>                        the type of the renderListener - this makes it easy to chain calls
     * @param pageNumber                 the page number to process
     * @param renderListener             the listener that will receive render callbacks
     * @param additionalContentOperators an optional map of custom ContentOperators for rendering instructions
     * @return the provided renderListener
     */
    public <E extends EventListener> E processContent(int pageNumber, E renderListener, Map<String, ContentOperator> additionalContentOperators) {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(renderListener);
        for (Map.Entry<String, ContentOperator> entry : additionalContentOperators.entrySet()) {
            processor.registerContentOperator(entry.getKey(), entry.getValue());
        }
        processor.processPageContent(pdfDocument.getPage(pageNumber));
        return renderListener;
    }

    /**
     * Processes content from the specified page number using the specified listener
     *
     * @param <E>            the type of the renderListener - this makes it easy to chain calls
     * @param pageNumber     the page number to process
     * @param renderListener the listener that will receive render callbacks
     * @return the provided renderListener
     */
    public <E extends EventListener> E processContent(int pageNumber, E renderListener) {
        return processContent(pageNumber, renderListener, new HashMap<String, ContentOperator>());
    }

}
