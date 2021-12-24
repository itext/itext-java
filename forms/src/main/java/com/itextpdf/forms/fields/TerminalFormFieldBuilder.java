package com.itextpdf.forms.fields;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

/**
 * Builder for terminal form field.
 *
 * @param <T> specific terminal form field builder which extends this class.
 */
public abstract class TerminalFormFieldBuilder<T extends TerminalFormFieldBuilder<T>> extends FormFieldBuilder<T> {
    /**
     * Rectangle which defines widget placement.
     */
    private Rectangle widgetRectangle = null;
    /**
     * Page number to place widget at.
     */
    private int page = 0;

    /**
     * Creates builder for terminal form field creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    protected TerminalFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets rectangle which defines widget's placement.
     *
     * @return instance of {@link Rectangle} for widget placement
     */
    public Rectangle getWidgetRectangle() {
        return widgetRectangle;
    }

    /**
     * Gets page to be used for widget creation.
     *
     * @return number of page to place widget at
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets page to be used for widget creation.
     *
     * @param page instance of {@link PdfPage}. Shall belong to already provided {@link PdfDocument}
     * @return this builder
     */
    public T setPage(PdfPage page) {
        this.page = getDocument().getPageNumber(page);
        return getThis();
    }

    /**
     * Sets page to be used for widget creation.
     *
     * @param page number of page to place widget at
     * @return this builder
     */
    public T setPage(int page) {
        this.page = page;
        return getThis();
    }

    /**
     * Sets rectangle which defines widget's placement.
     *
     * @param widgetRectangle instance of {@link Rectangle} for widget placement
     * @return this builder
     */
    public T setWidgetRectangle(Rectangle widgetRectangle) {
        this.widgetRectangle = widgetRectangle;
        return getThis();
    }

    void setPageToField(PdfFormField field) {
        if (page != 0) {
            field.setPage(page);
        }
    }
}
