package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.layer.PdfOCProperties;

public class PdfCatalog extends PdfObjectWrapper<PdfDictionary> {

    protected final PdfPagesTree pageTree;
    protected PdfOCProperties ocProperties;

    protected PdfCatalog(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject);
        if (pdfObject == null) {
            throw new PdfException(PdfException.DocumentHasNoCatalogObject);
        }
        getPdfObject().makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Catalog);
        pageTree = new PdfPagesTree(this);
    }

    protected PdfCatalog(PdfDocument pdfDocument) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
    }

    public void addPage(PdfPage page) throws PdfException {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        pageTree.addPage(page);
    }

    public void addPage(int index, PdfPage page) throws PdfException {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        pageTree.addPage(index, page);
    }

    public PdfPage getPage(int pageNum) throws PdfException {
        return pageTree.getPage(pageNum);
    }

    public int getNumOfPages() {
        return pageTree.getNumOfPages();
    }

    public int getPageNum(PdfPage page) {
        return pageTree.getPageNum(page);
    }

    public boolean removePage(PdfPage page) throws PdfException {
        //TODO log removing flushed page
        return pageTree.removePage(page);
    }

    public PdfPage removePage(int pageNum) throws PdfException {
        //TODO log removing flushed page
        return pageTree.removePage(pageNum);
    }

    /**
     * Use this method to get the <B>Optional Content Properties Dictionary</B>.
     * Note that if you call this method, then the PdfDictionary with OCProperties will be
     * generated from PdfOCProperties object right before closing the PdfDocument,
     * so if you want to make low-level changes in Pdf structures themselves (PdfArray, PdfDictionary, etc),
     * then you should address directly those objects, e.g.:
     * <CODE>
     *     PdfCatalog pdfCatalog = pdfDoc.getCatalog();
     *     PdfDictionary ocProps = pdfCatalog.getAsDictionary(PdfName.OCProperties);
     *     // manipulate with ocProps.
     * </CODE>
     * Also note that this method is implicitly called when creating a new PdfLayer instance,
     * so you should either use hi-level logic of operating with layers,
     * or manipulate low-level Pdf objects by yourself.
     * @param createIfNotExists true to create new /OCProperties entry in catalog if not exists,
     *                          false to return null if /OCProperties entry in catalog is not present.
     * @return the Optional Content Properties Dictionary
     */
    public PdfOCProperties getOCProperties(boolean createIfNotExists) throws PdfException {
        if (ocProperties != null)
            return ocProperties;
        else {
            PdfDictionary ocPropertiesDict = getPdfObject().getAsDictionary(PdfName.OCProperties);
            if (ocPropertiesDict != null) {
                ocProperties = new PdfOCProperties(ocPropertiesDict, getDocument());
            } else if (createIfNotExists) {
                ocProperties = new PdfOCProperties(new PdfDictionary(), getDocument());
            }
        }
        return ocProperties;
    }

    /**
     * PdfCatalog will be flushed in PdfDocument.close(). User mustn't flush PdfCatalog!
     */
    @Override
    public void flush() throws PdfException {
        throw new PdfException(PdfException.YouCannotFlushPdfCatalogManually);
    }

    /**
     * True indicates that getOCProperties() was called, may have been modified,
     * and thus its dictionary needs to be reconstructed.
     */
    protected boolean isOCPropertiesMayHaveChanged() {
        return ocProperties != null;
    }
}
