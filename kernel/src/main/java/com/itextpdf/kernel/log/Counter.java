package com.itextpdf.kernel.log;

/**
 * Interface that can be implemented if you want to count the number of documents
 * that are being processed by iText.
 * <p>
 * Implementers may use this method to record actual system usage for licensing purposes
 * (e.g. count the number of documents or the volumne in bytes in the context of a SaaS license).
 */
public interface Counter {

    /**
     * Gets a Counter instance for a specific class.
     */
    Counter getCounter(Class<?> cls);

    /**
     * This method gets triggered if a document is read.
     *
     * @param size the length of the document that was read
     */
    void onDocumentRead(long size);

    /**
     * This method gets triggered if a document is written.
     *
     * @param size the length of the document that was written
     */
    void onDocumentWritten(long size);

}
