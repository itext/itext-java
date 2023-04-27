package com.itextpdf.forms.form.element;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;

/**
 * A field that represents a control for selecting one of the provided options.
 * It is used in the {@link ComboBoxField} class.
 */
public class SelectFieldItem {
    /**
     * The export value of the item.
     * this is the value of the form which will be submitted. If the display value is not set, the export value will be
     * used as display value.
     */
    private final String exportValue;
    /**
     * The display value of the item.
     * This is the value which will be displayed in the dropdown.
     */
    private final String displayValue;

    /**
     * The option element of the item.
     * This is the element which will be displayed in the dropdown.
     * It allows for customization
     */
    private final IBlockElement optionElement;


    /**
     * Create a new {@link SelectFieldItem}.
     *
     * @param exportValue  the export value of the item.
     * @param displayValue the display value of the item.
     */
    public SelectFieldItem(String exportValue, String displayValue) {
        this(exportValue, displayValue, new Paragraph(displayValue).setMargin(0).setMultipliedLeading(1));
    }

    /**
     * Create a new {@link SelectFieldItem}.
     *
     * @param value the export value of the item.
     */
    public SelectFieldItem(String value) {
        this(value, null, new Paragraph(value).setMargin(0).setMultipliedLeading(1));
    }

    /**
     * Create a new {@link SelectFieldItem}.
     *
     * @param value         the export value of the item.
     * @param optionElement the option element of the item.
     */
    public SelectFieldItem(String value, IBlockElement optionElement) {
        this(value, null, optionElement);
    }

    /**
     * Create a new {@link SelectFieldItem}.
     *
     * @param exportValue   the export value of the item.
     * @param displayValue  the display value of the item.
     * @param optionElement the option element of the item.
     */
    public SelectFieldItem(String exportValue, String displayValue, IBlockElement optionElement) {
        if (exportValue == null) {
            throw new PdfException(
                    MessageFormatUtil.format(FormsExceptionMessageConstant.VALUE_SHALL_NOT_BE_NULL, "exportValue"));
        }
        this.exportValue = exportValue;
        this.displayValue = displayValue;
        if (optionElement == null) {
            throw new PdfException(FormsExceptionMessageConstant.OPTION_ELEMENT_SHALL_NOT_BE_NULL);
        }
        this.optionElement = optionElement;
        setLabel();
    }

    /**
     * Get the export value of the item.
     *
     * @return export value.
     */
    public String getExportValue() {
        return exportValue;
    }

    /**
     * Get the display value of the item.
     * If the display value is not set, the export value will be used as display value.
     *
     * @return display value.
     */
    public String getDisplayValue() {
        if (displayValue != null) {
            return displayValue;
        }
        return exportValue;
    }

    /**
     * Get the option element of the item.
     *
     * <p>
     * This is the element which will be displayed in the dropdown.
     * It allows for customization.
     *
     * @return option element.
     */
    public IBlockElement getElement() {
        return optionElement;
    }

    /**
     * Check if the item has a display value. and export value.
     *
     * @return {@code true} if the item has both export and display values, {@code false} otherwise.
     */
    public boolean hasExportAndDisplayValues() {
        return exportValue != null && displayValue != null;
    }

    private void setLabel() {
        optionElement.setProperty(FormProperty.FORM_FIELD_LABEL, getDisplayValue());
    }
}
