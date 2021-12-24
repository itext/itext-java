package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;

/**
 * Builder for checkbox form field.
 */
public class CheckBoxFormFieldBuilder extends TerminalFormFieldBuilder<CheckBoxFormFieldBuilder> {

    private int checkType = PdfFormField.TYPE_CROSS;

    /**
     * Creates builder for {@link PdfButtonFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public CheckBoxFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets check type for checkbox form field.
     *
     * @return check type to be set for checkbox form field
     */
    public int getCheckType() {
        return checkType;
    }

    /**
     * Sets check type for checkbox form field. Default value is {@link PdfFormField#TYPE_CROSS}.
     *
     * @param checkType check type to be set for checkbox form field
     * @return this builder
     */
    public CheckBoxFormFieldBuilder setCheckType(int checkType) {
        this.checkType = checkType;
        return this;
    }

    /**
     * Creates checkbox form field based on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createCheckBox() {
        PdfButtonFormField check;
        if (getWidgetRectangle() == null) {
            check = new PdfButtonFormField(getDocument());
        } else {
            PdfWidgetAnnotation annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            annotation.setAppearanceState(new PdfName(PdfFormField.OFF_STATE_VALUE));
            if (getConformanceLevel() != null) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
            check = new PdfButtonFormField(annotation, getDocument());
        }
        check.pdfAConformanceLevel = getConformanceLevel();
        check.updateFontAndFontSize(getDocument().getDefaultFont(), 0);
        check.setCheckType(checkType);
        check.setFieldName(getFormFieldName());
        check.put(PdfName.V, new PdfName(PdfFormField.OFF_STATE_VALUE));

        if (getWidgetRectangle() != null) {
            if (getConformanceLevel() == null) {
                check.drawCheckAppearance(getWidgetRectangle().getWidth(),
                        getWidgetRectangle().getHeight(), PdfFormField.ON_STATE_VALUE);
            } else {
                check.drawPdfA2CheckAppearance(getWidgetRectangle().getWidth(),
                        getWidgetRectangle().getHeight(), PdfFormField.ON_STATE_VALUE, checkType);
            }
            setPageToField(check);
        }

        return check;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckBoxFormFieldBuilder getThis() {
        return this;
    }
}
