package com.itextpdf.forms;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;

/**
 * The {@link FormDefaultAccessibilityProperties} class is used to create a specific forms related instance of the
 * {@link DefaultAccessibilityProperties} class.
 */
public class FormDefaultAccessibilityProperties extends DefaultAccessibilityProperties {
    /**
     * Represents the role: radio.
     */
    public static final String FORM_FIELD_RADIO = "rb";
    /**
     * Represents the role: Checkbox.
     */
    public static final String FORM_FIELD_CHECK = "cb";
    /**
     * Represents the role: PushButton.
     */
    public static final String FORM_FIELD_PUSH_BUTTON = "pb";
    /**
     * Represents the role: ListBox.
     */
    public static final String FORM_FIELD_LIST_BOX = "lb";
    /**
     * Represents the role: Text. This can be passwords, text areas, etc.
     */
    public static final String FORM_FIELD_TEXT = "tv";
    private static final String ROLE_NAME = "Role";
    private static final String OWNER_PRINT_FIELD_NAME = "PrintField";

    private static final String ATTRIBUTE_CHECKED = "Checked";
    private static final String ATTRIBUTE_ON = "on";
    private static final String ATTRIBUTE_OFF = "off";

    private static final String[] ALLOWED_VALUES = new String[] {
            FORM_FIELD_TEXT,
            FORM_FIELD_RADIO,
            FORM_FIELD_CHECK,
            FORM_FIELD_LIST_BOX,
            FORM_FIELD_PUSH_BUTTON
    };

    /**
     * Instantiates a new {@link FormDefaultAccessibilityProperties } instance based on structure element role.
     *
     * @param formFieldType the type of the formField
     */
    public FormDefaultAccessibilityProperties(String formFieldType) {
        super(StandardRoles.FORM);
        checkIfFormFieldTypeIsAllowed(formFieldType);
        PdfStructureAttributes attrs = new PdfStructureAttributes(OWNER_PRINT_FIELD_NAME);
        attrs.addEnumAttribute(ROLE_NAME, formFieldType);
        super.addAttributes(attrs);

        if (FORM_FIELD_RADIO.equals(formFieldType) || FORM_FIELD_CHECK.equals(formFieldType)) {
            PdfStructureAttributes checkedState = new PdfStructureAttributes(OWNER_PRINT_FIELD_NAME);
            checkedState.addEnumAttribute(ATTRIBUTE_CHECKED, ATTRIBUTE_OFF);
            super.addAttributes(checkedState);
        }
    }

    /**
     * Updates the checked value of the form field based on the {@link FormProperty#FORM_FIELD_CHECKED} property.
     * If no such property is found, the checked value is set to "off".
     *
     * @param element The element which contains a {@link FormProperty#FORM_FIELD_CHECKED} property.
     */
    public void updateCheckedValue(IPropertyContainer element) {
        for (PdfStructureAttributes pdfStructureAttributes : getAttributesList()) {
            if (pdfStructureAttributes.getAttributeAsEnum(ATTRIBUTE_CHECKED) != null) {
                String checkedValue =
                        Boolean.TRUE.equals(element.<Boolean>getProperty(FormProperty.FORM_FIELD_CHECKED))
                                ? ATTRIBUTE_ON : ATTRIBUTE_OFF;
                pdfStructureAttributes.addEnumAttribute(ATTRIBUTE_CHECKED, checkedValue);
            }
        }
    }

    private static void checkIfFormFieldTypeIsAllowed(String formFieldType) {
        for (String allowedValue : ALLOWED_VALUES) {
            if (allowedValue.equals(formFieldType)) {
                return;
            }
        }
        String allowedValues = String.join(", ", ALLOWED_VALUES);
        String message = MessageFormatUtil.format(FormsExceptionMessageConstant.ROLE_NAME_INVALID_FOR_FORM,
                formFieldType, allowedValues);
        throw new PdfException(message);
    }
}
