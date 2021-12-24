package com.itextpdf.forms.fields;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * Builder for push button form field.
 */
public class PushButtonFormFieldBuilder extends TerminalFormFieldBuilder<PushButtonFormFieldBuilder> {

    private String caption = "";

    /**
     * Creates builder for {@link PdfButtonFormField} creation.
     *
     * @param document document to be used for form field creation
     * @param formFieldName name of the form field
     */
    public PushButtonFormFieldBuilder(PdfDocument document, String formFieldName) {
        super(document, formFieldName);
    }

    /**
     * Gets caption for button form field creation.
     *
     * @return caption value to be used for form field creation
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets caption for button form field creation.
     *
     * @param caption caption value to be used for form field creation
     * @return this builder
     */
    public PushButtonFormFieldBuilder setCaption(String caption) {
        this.caption = caption;
        return getThis();
    }

    /**
     * Creates push button form field base on provided parameters.
     *
     * @return new {@link PdfButtonFormField} instance
     */
    public PdfButtonFormField createPushButton() {
        PdfButtonFormField field;
        PdfWidgetAnnotation annotation = null;
        if (getWidgetRectangle() == null) {
            field = new PdfButtonFormField(getDocument());
        } else {
            annotation = new PdfWidgetAnnotation(getWidgetRectangle());
            field = new PdfButtonFormField(annotation, getDocument());
            if (null != getConformanceLevel()) {
                annotation.setFlag(PdfAnnotation.PRINT);
            }
        }

        field.pdfAConformanceLevel = getConformanceLevel();
        field.setPushButton(true);
        field.setFieldName(getFormFieldName());
        field.text = caption;
        field.updateFontAndFontSize(getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
        field.backgroundColor = ColorConstants.LIGHT_GRAY;

        if (annotation != null) {
            PdfFormXObject xObject = field.drawPushButtonAppearance(
                    getWidgetRectangle().getWidth(), getWidgetRectangle().getHeight(),
                    caption, getDocument().getDefaultFont(), PdfFormField.DEFAULT_FONT_SIZE);
            annotation.setNormalAppearance(xObject.getPdfObject());

            PdfDictionary mk = new PdfDictionary();
            mk.put(PdfName.CA, new PdfString(caption));
            mk.put(PdfName.BG, new PdfArray(field.backgroundColor.getColorValue()));
            annotation.setAppearanceCharacteristics(mk);

            if (getConformanceLevel() != null) {
                PdfFormField.createPushButtonAppearanceState(annotation.getPdfObject());
            }
            setPageToField(field);
        }

        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PushButtonFormFieldBuilder getThis() {
        return this;
    }
}
