/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.forms.fields;

import com.itextpdf.commons.datastructures.NullableContainer;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.HighPrecisionOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a single field or field group in an {@link com.itextpdf.forms.PdfAcroForm
 * AcroForm}.
 *
 * <p>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfFormField extends AbstractPdfFormField {

    /**
     * Flag that designates, if set, that the field can contain multiple lines
     * of text.
     */
    public static final int FF_MULTILINE = makeFieldFlag(13);

    /**
     * Flag that designates, if set, that the field's contents must be obfuscated.
     */
    public static final int FF_PASSWORD = makeFieldFlag(14);

    /**
     * The ReadOnly flag, which specifies whether or not the field can be changed.
     */
    public static final int FF_READ_ONLY = makeFieldFlag(1);

    /**
     * The Required flag, which specifies whether or not the field must be filled in.
     */
    public static final int FF_REQUIRED = makeFieldFlag(2);

    /**
     * The NoExport flag, which specifies whether or not exporting is forbidden.
     */
    public static final int FF_NO_EXPORT = makeFieldFlag(3);

    /**
     * List of all allowable keys in form fields.
     */
    private static final Set<PdfName> FORM_FIELD_KEYS = new HashSet<>();


    private static final Logger LOGGER = LoggerFactory.getLogger(PdfFormField.class);

    protected String text;
    protected ImageData img;
    protected PdfFormXObject form;

    protected NullableContainer<CheckBoxType> checkType = null;

    private String displayValue;

    private List<AbstractPdfFormField> childFields = new ArrayList<>();

    static {
        FORM_FIELD_KEYS.add(PdfName.FT);
        // It exists in form field and widget annotation
        //formFieldKeys.add(PdfName.Parent);
        FORM_FIELD_KEYS.add(PdfName.Kids);
        FORM_FIELD_KEYS.add(PdfName.T);
        FORM_FIELD_KEYS.add(PdfName.TU);
        FORM_FIELD_KEYS.add(PdfName.TM);
        FORM_FIELD_KEYS.add(PdfName.Ff);
        FORM_FIELD_KEYS.add(PdfName.V);
        FORM_FIELD_KEYS.add(PdfName.DV);
        // It exists in form field and widget annotation
        //formFieldKeys.add(PdfName.AA);
        FORM_FIELD_KEYS.add(PdfName.DA);
        FORM_FIELD_KEYS.add(PdfName.Q);
        FORM_FIELD_KEYS.add(PdfName.DS);
        FORM_FIELD_KEYS.add(PdfName.RV);
        FORM_FIELD_KEYS.add(PdfName.Opt);
        FORM_FIELD_KEYS.add(PdfName.MaxLen);
        FORM_FIELD_KEYS.add(PdfName.TI);
        FORM_FIELD_KEYS.add(PdfName.I);
        FORM_FIELD_KEYS.add(PdfName.Lock);
        FORM_FIELD_KEYS.add(PdfName.SV);
    }

    /**
     * Creates a form field as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param pdfObject the dictionary to be wrapped, must have an indirect reference.
     */
    public PdfFormField(PdfDictionary pdfObject) {
        super(pdfObject);
        createKids(pdfObject);
    }

    private void createKids(PdfDictionary pdfObject) {
        PdfArray kidsArray = pdfObject.getAsArray(PdfName.Kids);
        if (kidsArray == null) {
            // Here widget annotation might be merged with form field
            final PdfName subType = pdfObject.getAsName(PdfName.Subtype);
            if (PdfName.Widget.equals(subType)) {
                AbstractPdfFormField childField = PdfFormAnnotation.makeFormAnnotation(pdfObject, getDocument());
                if (childField != null) {
                    this.setChildField(childField);
                }
            }
        } else {
            for (PdfObject kid : kidsArray) {
                if (kid.isFlushed()) {
                    LOGGER.info(FormsLogMessageConstants.FORM_FIELD_WAS_FLUSHED);
                    continue;
                }
                AbstractPdfFormField childField = PdfFormField.makeFormFieldOrAnnotation(kid, getDocument());
                if (childField != null) {
                    this.setChildField(childField);
                } else {
                    LOGGER.warn(MessageFormatUtil.format(FormsLogMessageConstants.CANNOT_CREATE_FORMFIELD,
                            pdfObject.getIndirectReference() == null ? pdfObject :
                                    (PdfObject) pdfObject.getIndirectReference()));
                }
            }
        }
    }

    /**
     * Creates a minimal {@link PdfFormField}.
     *
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfFormField(PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
        PdfName formType = getFormType();
        if (formType != null) {
            put(PdfName.FT, formType);
        }
    }

    /**
     * Creates a form field as a parent of a {@link PdfWidgetAnnotation}.
     *
     * @param widget The widget which will be a kid of the {@link PdfFormField}.
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
        widget.makeIndirect(pdfDocument);
        addKid(widget);
        if (getFormType() != null) {
            put(PdfName.FT, getFormType());
        }
    }

    /**
     * Creates a (subtype of) {@link PdfFormField} object. The type of the object
     * depends on the <code>FT</code> entry in the <code>pdfObject</code> parameter.
     *
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *                  {@link PdfIndirectReference} to a {@link PdfDictionary}.
     * @param document  the {@link PdfDocument} to create the field in.
     * @return a new {@link PdfFormField}, or <code>null</code> if
     * <code>pdfObject</code> is not a form field.
     */
    public static PdfFormField makeFormField(PdfObject pdfObject, PdfDocument document) {
        if (!pdfObject.isDictionary()) {
            return null;
        }

        PdfDictionary dictionary = (PdfDictionary) pdfObject;
        if (!PdfFormField.isFormField(dictionary)) {
            return null;
        }

        PdfFormField field;
        PdfName formType = dictionary.getAsName(PdfName.FT);
        if (PdfName.Tx.equals(formType)) {
            field = PdfFormCreator.createTextFormField(dictionary);
        } else if (PdfName.Btn.equals(formType)) {
            field = PdfFormCreator.createButtonFormField(dictionary);
        } else if (PdfName.Ch.equals(formType)) {
            field = PdfFormCreator.createChoiceFormField(dictionary);
        } else if (PdfName.Sig.equals(formType)) {
            field = PdfFormCreator.createSignatureFormField(dictionary);
        } else {
            // No form type but still a form field
            field = PdfFormCreator.createFormField(dictionary);
        }
        field.makeIndirect(document);

        if (document != null) {
            field.pdfConformance = document.getConformance();
        }

        return field;
    }

    /**
     * Creates a (subtype of) {@link PdfFormField} or {@link PdfFormAnnotation} object depending on
     * <code>pdfObject</code>.
     *
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *                  {@link PdfIndirectReference} to a {@link PdfDictionary}.
     * @param document  the {@link PdfDocument} to create the field in.
     * @return a new {@link AbstractPdfFormField}, or <code>null</code> if
     * <code>pdfObject</code> is not a form field and is not a widget annotation.
     */
    public static AbstractPdfFormField makeFormFieldOrAnnotation(PdfObject pdfObject, PdfDocument document) {
        AbstractPdfFormField formField = PdfFormField.makeFormField(pdfObject, document);
        if (formField == null) {
            formField = PdfFormAnnotation.makeFormAnnotation(pdfObject, document);
        }

        return formField;
    }
    /**
     * Makes a field flag by bit position. Bit positions are numbered 1 to 32.
     * But position 0 corresponds to flag 1, position 3 corresponds to flag 4 etc.
     *
     * @param bitPosition bit position of a flag in range 1 to 32 from the pdf specification.
     * @return corresponding field flag.
     */
    public static int makeFieldFlag(int bitPosition) {
        return (1 << (bitPosition - 1));
    }

    /**
     * Checks if dictionary contains any of the form field keys.
     *
     * @param dict field dictionary to check.
     *
     * @return true if it is a form field dictionary, false otherwise.
     */
    public static boolean isFormField(PdfDictionary dict) {
        for (final PdfName formFieldKey : getFormFieldKeys()) {
            if (dict.containsKey(formFieldKey)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a set of all possible form field keys except {@code PdfName.Parent}.
     *
     * @return a set of form field keys.
     */
    public static Collection<PdfName> getFormFieldKeys() {
        return Collections.unmodifiableCollection(FORM_FIELD_KEYS);
    }

    /**
     * Returns the type of the form field dictionary, or of the parent
     * &lt;PdfDictionary&gt; object.
     *
     * @param fieldDict field dictionary to get its type.
     *
     * @return the form type, as a {@link PdfName}.
     */
    public static PdfName getFormType(PdfDictionary fieldDict) {
        PdfName formType = fieldDict.getAsName(PdfName.FT);
        if (formType == null) {
            return getTypeFromParent(fieldDict);
        }
        return formType;
    }

    /**
     * Returns the type of the parent form field, or of the wrapped
     * &lt;PdfDictionary&gt; object.
     *
     * @return the form type, as a {@link PdfName}.
     */
    public PdfName getFormType() {
        return getFormType(getPdfObject());
    }

    /**
     * Sets a value to the field and generating field appearance if needed.
     *
     * @param value of the field.
     * @return the field.
     */
    public PdfFormField setValue(String value) {
        PdfName formType = getFormType();
        boolean autoGenerateAppearance = !(PdfName.Btn.equals(formType) && getFieldFlag(PdfButtonFormField.FF_RADIO));
        return setValue(value, autoGenerateAppearance);
    }

    /**
     * Sets a value to the field (and fields with the same names) and generates field appearance if needed.
     *
     * @param value of the field.
     * @param generateAppearance if false, appearance won't be regenerated.
     * @return the field.
     */
    public PdfFormField setValue(String value, boolean generateAppearance) {
        if (parent == null) {
            setFieldValue(value, generateAppearance);
        } else {
            String fieldName = getPartialFieldName().toUnicodeString();

            for (PdfFormField field : parent.getChildFormFields()) {
                if (fieldName.equals(field.getPartialFieldName().toUnicodeString())) {
                    field.setFieldValue(value, generateAppearance);
                }
            }
        }

        return this;
    }

    /**
     * Set text field value with given font and size.
     *
     * @param value text value.
     * @param font a {@link PdfFont}.
     * @param fontSize the size of the font.
     * @return the edited field.
     */
    public PdfFormField setValue(String value, PdfFont font, float fontSize) {
        updateFontAndFontSize(font, fontSize);
        return setValue(value);
    }

    /**
     * Sets the field value and the display string. The display string
     * is used to build the appearance.
     *
     * @param value the field value.
     * @param displayValue the string that is used for the appearance. If <CODE>null</CODE>
     *                the <CODE>value</CODE> parameter will be used.
     * @return the edited field.
     */
    public PdfFormField setValue(String value, String displayValue) {
        if (value == null) {
            LOGGER.warn(FormsLogMessageConstants.FIELD_VALUE_CANNOT_BE_NULL);
            return this;
        }

        // Not valid for checkboxes and radiobuttons
        // TODO: DEVSIX-6344 - Move specific methods to related form fields classes
        if (displayValue == null || displayValue.equals(value)) {
            return setValue(value);
        }

        setValue(displayValue, true);
        setValue(value, false);
        this.displayValue = displayValue;

        return this;
    }

    /**
     * Removes the childField object of this field.
     *
     * @param fieldName a {@link PdfFormField}, that needs to be removed from form field children.
     */
    public void removeChild(AbstractPdfFormField fieldName) {
        childFields.remove(fieldName);
        PdfArray kids = getPdfObject().getAsArray(PdfName.Kids);
        if (kids != null) {
            kids.remove(fieldName.getPdfObject());
            if (kids.isEmpty()) {
                getPdfObject().remove(PdfName.Kids);
            }
        }
    }

    /**
     * Removes all children from the current field.
     */
    public void removeChildren() {
        childFields.clear();
        getPdfObject().remove(PdfName.Kids);
    }

    /**
     * Gets the kids of this object.
     *
     * @return contents of the dictionary's <code>Kids</code> property, as a {@link PdfArray}.
     */
    public PdfArray getKids() {
        return getPdfObject().getAsArray(PdfName.Kids);
    }

    /**
     * Gets the childFields of this object.
     *
     * @return the children of the current field.
     */
    public List<AbstractPdfFormField> getChildFields() {
        return Collections.unmodifiableList(childFields);
    }

    /**
     * Gets all child form fields of this form field. Annotations are not returned.
     *
     * @return a list of {@link PdfFormField}.
     */
    public List<PdfFormField> getChildFormFields() {
        List<PdfFormField> fields = new ArrayList<>();
        for (AbstractPdfFormField child : childFields) {
            if (child instanceof PdfFormField) {
                fields.add((PdfFormField)child);
            }
        }

        return fields;
    }


    /**
     * Gets all childFields of this object, including the children of the children
     * but not annotations.
     *
     * @return the children of the current field and their children.
     */
    public List<PdfFormField> getAllChildFormFields() {
        List<PdfFormField> allKids = new ArrayList<>();
        List<PdfFormField> kids = this.getChildFormFields();
        for (PdfFormField formField : kids) {
            allKids.add(formField);
            allKids.addAll(formField.getAllChildFormFields());
        }
        return allKids;
    }

    /**
     * Gets all childFields of this object, including the children of the children.
     *
     * @return the children of the current field and their children.
     */
    public List<AbstractPdfFormField> getAllChildFields() {
        List<AbstractPdfFormField> kids = this.getChildFields();
        List<AbstractPdfFormField> allKids = new ArrayList<>(kids);
        for (AbstractPdfFormField field : kids) {
            if (field instanceof PdfFormField) {
                allKids.addAll(((PdfFormField)field).getAllChildFields());
            }
        }
        return allKids;
    }

    /**
     * Gets the child field of form field. If there is no child field with such name, {@code null} is returned.
     *
     * @param fieldName a {@link String}, name of the received field.
     * @return the child of the current field as a {@link PdfFormField}.
     */
    public PdfFormField getChildField(String fieldName) {
        for (PdfFormField formField : this.getChildFormFields()) {
            PdfString partialFieldName = formField.getPartialFieldName();
            if (partialFieldName != null && partialFieldName.toUnicodeString().equals(fieldName)) {
                return formField;
            }
        }
        return null;
    }

    /**
     * Adds a new kid to the <code>Kids</code> array property from a
     * {@link AbstractPdfFormField}. Also sets the kid's <code>Parent</code> property to this object.
     *
     * @param kid a new {@link AbstractPdfFormField} entry for the field's <code>Kids</code> array property.
     *
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField addKid(AbstractPdfFormField kid) {
        return addKid(kid, true);
    }

    /**
     * Adds a new kid to the <code>Kids</code> array property from a
     * {@link AbstractPdfFormField}. Also sets the kid's <code>Parent</code> property to this object.
     *
     * @param kid a new {@link AbstractPdfFormField} entry for the field's <code>Kids</code> array property.
     * @param throwExceptionOnError define whether exception (true) or log (false) is expected in case kid with
     *                              the same name exists and merge of two kids failed.
     *
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField addKid(AbstractPdfFormField kid, boolean throwExceptionOnError) {
        PdfFormAnnotationUtil.separateWidgetAndField(this);

        kid.setParent(this);
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        if (!mergeKidsIfKidWithSuchNameExists(kid, throwExceptionOnError)) {
            kids.add(kid.getPdfObject());
            this.childFields.add(kid);
        }

        put(PdfName.Kids, kids);
        return this;
    }

    /**
     * Adds a new kid to the <code>Kids</code> array property from a
     * {@link PdfWidgetAnnotation}. Also sets the kid's <code>Parent</code> property to this object.
     *
     * @param kid a new {@link PdfWidgetAnnotation} entry for the field's <code>Kids</code> array property.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField addKid(PdfWidgetAnnotation kid) {
        kid.setParent(getPdfObject());
        PdfDictionary pdfObject = kid.getPdfObject();
        pdfObject.makeIndirect(this.getDocument());
        AbstractPdfFormField field = PdfFormCreator.createFormAnnotation(pdfObject);
        return addKid(field);
    }

    /**
     * Changes the name of the field to the specified value.
     *
     * @param name the new field name, as a String.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setFieldName(String name) {
        put(PdfName.T, new PdfString(name));
        PdfFormField parent = getParentField();
        if (parent != null) {
            parent.mergeKidsIfKidWithSuchNameExists(this, true);
        }
        return this;
    }

    /**
     * Gets the current field partial name.
     *
     * @return the current field partial name, as a {@link PdfString}. If the field has no partial name,
     * an empty {@link PdfString} is returned.
     */
    public PdfString getPartialFieldName() {
        PdfString partialName = getPdfObject().getAsString(PdfName.T);
        return partialName == null ? new PdfString("") : partialName;
    }

    /**
     * Changes the alternate name of the field to the specified value. The
     * alternate is a descriptive name to be used by status messages etc.
     *
     * @param name the new alternate name, as a String.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setAlternativeName(String name) {
        put(PdfName.TU, new PdfString(name));
        return this;
    }

    /**
     * Gets the current alternate name. The alternate is a descriptive name to
     * be used by status messages etc.
     *
     * @return the current alternate name, as a {@link PdfString}.
     */
    public PdfString getAlternativeName() {
        return getPdfObject().getAsString(PdfName.TU);
    }

    /**
     * Changes the mapping name of the field to the specified value. The
     * mapping name can be used when exporting the form data in the document.
     *
     * @param name the new alternate name, as a String.
     * @return the edited field.
     */
    public PdfFormField setMappingName(String name) {
        put(PdfName.TM, new PdfString(name));
        return this;
    }

    /**
     * Gets the current mapping name. The mapping name can be used when
     * exporting the form data in the document.
     *
     * @return the current mapping name, as a {@link PdfString}.
     */
    public PdfString getMappingName() {
        return getPdfObject().getAsString(PdfName.TM);
    }

    /**
     * Checks whether a certain flag, or any of a combination of flags, is set
     * for this form field.
     *
     * @param flag an <code>int</code> interpreted as a series of a binary flags.
     * @return true if any of the flags specified in the parameter is also set
     * in the form field.
     */
    public boolean getFieldFlag(int flag) {
        return (getFieldFlags() & flag) != 0;
    }

    /**
     * Adds a flag, or combination of flags, for the form field. This method is
     * intended to be used one flag at a time, but this is not technically
     * enforced. To <em>replace</em> the current value, use
     * {@link #setFieldFlags(int)}.
     *
     * @param flag an <code>int</code> interpreted as a series of a binary flags.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setFieldFlag(int flag) {
        return setFieldFlag(flag, true);
    }

    /**
     * Adds or removes a flag, or combination of flags, for the form field. This
     * method is intended to be used one flag at a time, but this is not
     * technically enforced. To <em>replace</em> the current value, use
     * {@link #setFieldFlags(int)}.
     *
     * @param flag  an <code>int</code> interpreted as a series of a binary flags.
     * @param value if <code>true</code>, adds the flag(s). if <code>false</code>,
     *              removes the flag(s).
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setFieldFlag(int flag, boolean value) {
        int flags = getFieldFlags();

        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }

        return setFieldFlags(flags);
    }

    /**
     * If true, the field can contain multiple lines of text; if false, the field's text is restricted to a single line.
     *
     * @return whether the field can span over multiple lines.
     */
    public boolean isMultiline() {
        return getFieldFlag(FF_MULTILINE);
    }

    /**
     * If true, the field is intended for entering a secure password that should not be echoed visibly to the screen.
     * Characters typed from the keyboard should instead be echoed in some unreadable form, such as asterisks
     * or bullet characters.
     *
     * @return whether or not the contents of the field must be obfuscated.
     */
    public boolean isPassword() {
        return getFieldFlag(FF_PASSWORD);
    }

    /**
     * Sets a flag, or combination of flags, for the form field. This method
     * <em>replaces</em> the previous value. Compare with {@link #setFieldFlag(int)}
     * which <em>adds</em> a flag to the existing flags.
     *
     * @param flags an <code>int</code> interpreted as a series of a binary flags.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setFieldFlags(int flags) {
        int oldFlags = getFieldFlags();
        put(PdfName.Ff, new PdfNumber(flags));
        if (((oldFlags ^ flags) & PdfTextFormField.FF_COMB) != 0 && PdfName.Tx.equals(getFormType())
                && PdfFormCreator.createTextFormField(getPdfObject()).getMaxLen() != 0)
            regenerateField();
        return this;
    }

    /**
     * Gets the current list of PDF form field flags.
     *
     * @return the current list of flags, encoded as an <code>int</code>.
     */
    public int getFieldFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.Ff);
        if (f != null) {
            return f.intValue();
        } else {
            PdfFormField parent = getParentField();
            if (parent != null) {
                return parent.getFieldFlags();
            } else {
                return 0;
            }
        }
    }

    /**
     * Gets the current value contained in the form field.
     *
     * @return the current value, as a {@link PdfObject}.
     */
    public PdfObject getValue() {
        PdfObject value = getPdfObject().get(PdfName.V);
        // V is not taken into account if T is missing. This is the way Acrobat behaves.
        if ((getPdfObject().get(PdfName.T) == null || value == null) && getParentField() != null) {
            return getParentField().getValue();
        }
        return value;
    }

    /**
     * Gets the current value contained in the form field.
     *
     * @return the current value, as a {@link String}.
     */
    public String getValueAsString() {
        PdfObject value = getValue();
        if (value == null) {
            return "";
        } else if (value instanceof PdfStream) {
            return new String(((PdfStream) value).getBytes(), StandardCharsets.UTF_8);
        } else if (value instanceof PdfName) {
            return ((PdfName) value).getValue();
        } else if (value instanceof PdfString) {
            return ((PdfString) value).toUnicodeString();
        } else {
            return "";
        }
    }

    /**
     * Gets the current display value of the form field.
     *
     * @return the current display value, as a {@link String}, if it exists.
     * If not, returns the value as a {@link String}.
     */
    public String getDisplayValue() {
        if (displayValue != null) {
            return displayValue;
        } else if (text != null) {
            return text;
        } else {
            return getValueAsString();
        }
    }

    /**
     * Sets the default fallback value for the form field.
     *
     * @param value the default value.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setDefaultValue(PdfObject value) {
        put(PdfName.DV, value);
        return this;
    }

    /**
     * Gets the default fallback value for the form field.
     *
     * @return the default value.
     */
    public PdfObject getDefaultValue() {
        return getPdfObject().get(PdfName.DV);
    }

    /**
     * Sets an additional action for the form field.
     *
     * @param key    the dictionary key to use for storing the action.
     * @param action the action.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * Gets the currently additional action dictionary for the form field.
     *
     * @return the additional action dictionary.
     */
    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    /**
     * Sets options for the form field. Only to be used for checkboxes and radio buttons.
     *
     * @param options an array of {@link PdfString} objects that each represent
     *                the 'on' state of one of the choices.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setOptions(PdfArray options) {
        put(PdfName.Opt, options);
        return this;
    }

    /**
     * Gets options for the form field. Should only return usable values for
     * checkboxes and radio buttons.
     *
     * @return the options, as an {@link PdfArray} of {@link PdfString} objects.
     */
    public PdfArray getOptions() {
        return getPdfObject().getAsArray(PdfName.Opt);
    }

    /**
     * Gets all {@link PdfWidgetAnnotation} that its children refer to.
     *
     * @return a list of {@link PdfWidgetAnnotation}.
     */
    public List<PdfWidgetAnnotation> getWidgets() {
        List<PdfWidgetAnnotation> widgets = new ArrayList<>();
        for (AbstractPdfFormField child : childFields) {
            PdfDictionary kid = child.getPdfObject();
            PdfName subType = kid.getAsName(PdfName.Subtype);
            if (subType != null && subType.equals(PdfName.Widget)) {
                widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(kid));
            }
        }

        return widgets;
    }

    /**
     * Gets all child form field's annotations {@link PdfFormAnnotation} of this form field.
     *
     * @return a list of {@link PdfFormAnnotation}.
     */
    public List<PdfFormAnnotation> getChildFormAnnotations() {
        List<PdfFormAnnotation> annots = new ArrayList<>();
        for (AbstractPdfFormField child : childFields) {
            if (child instanceof PdfFormAnnotation) {
                annots.add((PdfFormAnnotation)child);
            }
        }

        return annots;
    }

    /**
     * Gets a single child form field's annotation {@link PdfFormAnnotation}.
     *
     * @return {@link PdfFormAnnotation} or null if there are no child annotations.
     */
    public PdfFormAnnotation getFirstFormAnnotation() {
        for (AbstractPdfFormField child : childFields) {
            if (child instanceof PdfFormAnnotation) {
                return (PdfFormAnnotation)child;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public PdfString getDefaultAppearance() {
        PdfString defaultAppearance = getPdfObject().getAsString(PdfName.DA);
        if (defaultAppearance == null) {
            PdfDictionary parent = getParent();
            if (parent != null) {
                //If this is not merged form field we should get default appearance from the parent which actually is a
                //form field dictionary
                if (parent.containsKey(PdfName.FT)) {
                    defaultAppearance = parent.getAsString(PdfName.DA);
                }
            }
        }
        // DA is an inherited key, therefore AcroForm shall be checked if there is no parent or no DA in parent.
        if (defaultAppearance == null) {
            defaultAppearance = (PdfString) getAcroFormKey(PdfName.DA, PdfObject.STRING);
        }
        return defaultAppearance;
    }

    /**
     * Updates DA for Variable text, Push button and choice form fields.
     * The resources required for DA will be put to AcroForm's DR.
     * Note, for other form field types DA will be removed.
     */
    public void updateDefaultAppearance() {
        if (hasDefaultAppearance()) {
            if (getFont() == null) {
                return;
            }
            PdfDictionary defaultResources = (PdfDictionary) getAcroFormObject(PdfName.DR, PdfObject.DICTIONARY);
            if (defaultResources == null) {
                // Ensure that AcroForm dictionary exists
                addAcroFormToCatalog();
                defaultResources = new PdfDictionary();
                putAcroFormObject(PdfName.DR, defaultResources);
            }
            PdfDictionary fontResources = defaultResources.getAsDictionary(PdfName.Font);
            if (fontResources == null) {
                fontResources = new PdfDictionary();
                defaultResources.put(PdfName.Font, fontResources);
            }
            PdfName fontName = getFontNameFromDR(fontResources, getFont().getPdfObject());
            if (fontName == null) {
                fontName = getUniqueFontNameForDR(fontResources);
                fontResources.put(fontName, getFont().getPdfObject());
                fontResources.setModified();
            }

            put(PdfName.DA, generateDefaultAppearance(fontName, getFontSize(), color));
            // Font from DR may not be added to document through PdfResource.
            getDocument().addFont(getFont());
        } else {
            getPdfObject().remove(PdfName.DA);
            setModified();
        }
    }

    /**
     * Gets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     *
     * @return the current justification attribute.
     */
    public TextAlignment getJustification() {
        Integer justification = getPdfObject().getAsInt(PdfName.Q);
        if (justification == null && getParent() != null) {
            justification = getParent().getAsInt(PdfName.Q);
        }
        return justification == null ? null : numberToHorizontalAlignment((int) justification);
    }

    /**
     * Sets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     *
     * @param justification the value to set the justification attribute to.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setJustification(TextAlignment justification) {
        if (justification != null) {
            put(PdfName.Q, new PdfNumber(justification.ordinal()));
            regenerateField();
        }
        return this;
    }

    /**
     * Gets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     *
     * @return the default style, as a {@link PdfString}.
     */
    public PdfString getDefaultStyle() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    /**
     * Sets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     *
     * @param defaultStyleString a new default style for the form field.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setDefaultStyle(PdfString defaultStyleString) {
        put(PdfName.DS, defaultStyleString);
        return this;
    }

    /**
     * Gets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     *
     * @return the current rich text value.
     */
    public PdfObject getRichText() {
        return getPdfObject().get(PdfName.RV);
    }

    /**
     * Sets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     *
     * @param richText a new rich text value.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setRichText(PdfObject richText) {
        put(PdfName.RV, richText);
        return this;
    }

    /**
     * Changes the type of graphical marker used to mark a checkbox as 'on'.
     * Notice that in order to complete the change one should call
     * {@link #regenerateField() regenerateField} method.
     *
     * @param checkType the new checkbox marker.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setCheckType(CheckBoxType checkType) {
        if (checkType == null) {
            checkType = CheckBoxType.CROSS;
        }
        this.checkType = new NullableContainer<>(checkType);
        if (getPdfConformance() != null && getPdfConformance().isPdfAOrUa()) {
            return this;
        }
        try {
            font = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
        } catch (IOException e) {
            throw new PdfException(e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean regenerateField() {
        boolean result = true;
        if (isFieldRegenerationEnabled()) {
            updateDefaultAppearance();
        } else {
            result = false;
        }
        for (AbstractPdfFormField child : childFields) {
            if (child instanceof PdfFormAnnotation) {
                PdfFormAnnotation annotation = (PdfFormAnnotation) child;
                result &= annotation.regenerateWidget();
            } else {
                child.regenerateField();
            }
        }
        return result;
    }

    /**
     * Sets the ReadOnly flag, specifying whether or not the field can be changed.
     *
     * @param readOnly if <code>true</code>, then the field cannot be changed.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setReadOnly(boolean readOnly) {
        return setFieldFlag(FF_READ_ONLY, readOnly);
    }

    /**
     * Gets the ReadOnly flag, specifying whether or not the field can be changed.
     *
     * @return <code>true</code> if the field cannot be changed.
     */
    public boolean isReadOnly() {
        return getFieldFlag(FF_READ_ONLY);
    }

    /**
     * Sets the Required flag, specifying whether or not the field must be filled in.
     *
     * @param required if <code>true</code>, then the field must be filled in.
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setRequired(boolean required) {
        return setFieldFlag(FF_REQUIRED, required);
    }

    /**
     * Gets the Required flag, specifying whether or not the field must be filled in.
     *
     * @return <code>true</code> if the field must be filled in.
     */
    public boolean isRequired() {
        return getFieldFlag(FF_REQUIRED);
    }

    /**
     * Sets the NoExport flag, specifying whether or not exporting is forbidden.
     *
     * @param noExport if <code>true</code>, then exporting is <em>forbidden</em>
     * @return the edited {@link PdfFormField}.
     */
    public PdfFormField setNoExport(boolean noExport) {
        return setFieldFlag(FF_NO_EXPORT, noExport);
    }

    /**
     * Gets the NoExport attribute.
     *
     * @return whether exporting the value following a form action is forbidden.
     */
    public boolean isNoExport() {
        return getFieldFlag(FF_NO_EXPORT);
    }

    /**
     * Checks if the document that contains the field is created in reading mode.
     *
     * @return true if reading mode is used, false otherwise.
     */
    public boolean isInReadingMode() {
        return getDocument().getWriter() == null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String[] getAppearanceStates() {
        Set<String> names = new LinkedHashSet<>();
        PdfString stringOpt = getPdfObject().getAsString(PdfName.Opt);
        if (stringOpt != null) {
            names.add(stringOpt.toUnicodeString());
        } else {
            PdfArray arrayOpt = getPdfObject().getAsArray(PdfName.Opt);
            if (arrayOpt != null) {
                for (PdfObject pdfObject : arrayOpt) {
                    PdfString valStr = null;
                    if (pdfObject.isArray()) {
                        valStr = ((PdfArray) pdfObject).getAsString(1);
                    } else if (pdfObject.isString()) {
                        valStr = (PdfString) pdfObject;
                    }
                    if (valStr != null) {
                        names.add(valStr.toUnicodeString());
                    }
                }
            }
        }

        for (AbstractPdfFormField child : childFields) {
            String[] states = child.getAppearanceStates();
            Collections.addAll(names, states);
        }

        return names.toArray(new String[names.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        for (AbstractPdfFormField child : childFields) {
            child.release();
        }

        childFields.clear();
        childFields = null;

        super.release();
    }

    /**
     * {@inheritDoc}
     *
     * @param color {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public AbstractPdfFormField setColor(Color color) {
        this.color = color;
        for (AbstractPdfFormField child : childFields) {
            child.setColorNoRegenerate(color);
        }

        regenerateField();
        return this;
    }

    @Override
    void updateFontAndFontSize(PdfFont font, float fontSize) {
        super.updateFontAndFontSize(font, fontSize);
        for (AbstractPdfFormField child : childFields) {
            child.updateFontAndFontSize(font, fontSize);
        }
    }

    static String optionsArrayToString(PdfArray options) {
        if (options == null || options.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (PdfObject obj : options) {
            if (obj.isString()) {
                sb.append(((PdfString) obj).toUnicodeString()).append('\n');
            } else if (obj.isArray()) {
                PdfObject element = ((PdfArray) obj).size() > 1 ? ((PdfArray) obj).get(1) : null;
                if (element != null && element.isString()) {
                    sb.append(((PdfString) element).toUnicodeString()).append('\n');
                }
            } else {
                sb.append('\n');
            }
        }
        // last '\n'
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Adds a field to the children of the current field.
     *
     * @param kid the field, which should become a child.
     * @return the kid itself.
     */
    AbstractPdfFormField setChildField(AbstractPdfFormField kid) {
        kid.setParent(this);
        this.childFields.add(kid);
        return kid;
    }

    /**
     * Replaces /Kids value with passed kids dictionaries, and keeps old flashed fields there.
     * Also updates childFields array for {@link PdfFormField}.
     *
     * @param kids collection of new kids.
     */
    void replaceKids(Collection<AbstractPdfFormField> kids) {
        PdfArray kidsValues = new PdfArray();

        // Field may already have flushed widgets in /Kids, so we need to keep them.
        PdfArray oldKids = getKids();
        if (oldKids != null) {
            for (PdfObject kid : oldKids) {
                if (kid.isFlushed()) {
                    kidsValues.add(kid);
                }
            }
        }

        // Update childFields and /Kids.
        this.childFields.clear();
        for (AbstractPdfFormField kid : kids) {
            kid.setParent(this);
            kidsValues.add(kid.getPdfObject());
            this.childFields.add(kid);
        }
        put(PdfName.Kids, kidsValues);
    }

    private static PdfString generateDefaultAppearance(PdfName font, float fontSize, Color textColor) {
        assert font != null;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfOutputStream pdfStream = new PdfOutputStream(new HighPrecisionOutputStream<>(output));
        final byte[] g = new byte[]{(byte) 'g'};
        final byte[] rg = new byte[]{(byte) 'r', (byte) 'g'};
        final byte[] k = new byte[]{(byte) 'k'};
        final byte[] Tf = new byte[]{(byte) 'T', (byte) 'f'};

        pdfStream.write(font)
                .writeSpace()
                .writeFloat(fontSize).writeSpace()
                .writeBytes(Tf);

        if (textColor != null) {
            if (textColor instanceof DeviceGray) {
                pdfStream.writeSpace()
                        .writeFloats(textColor.getColorValue())
                        .writeSpace()
                        .writeBytes(g);
            } else if (textColor instanceof DeviceRgb) {
                pdfStream.writeSpace()
                        .writeFloats(textColor.getColorValue())
                        .writeSpace()
                        .writeBytes(rg);
            } else if (textColor instanceof DeviceCmyk) {
                pdfStream.writeSpace()
                        .writeFloats(textColor.getColorValue())
                        .writeSpace()
                        .writeBytes(k);
            } else {
                LOGGER.error(FormsLogMessageConstants.UNSUPPORTED_COLOR_IN_DA);
            }
        }
        return new PdfString(output.toByteArray());
    }

    private static PdfName getTypeFromParent(PdfDictionary field) {
        PdfDictionary parent = field.getAsDictionary(PdfName.Parent);
        PdfName formType = field.getAsName(PdfName.FT);
        if (parent != null) {
            formType = parent.getAsName(PdfName.FT);
            if (formType == null) {
                formType = getTypeFromParent(parent);
            }
        }
        return formType;
    }

    private static TextAlignment numberToHorizontalAlignment(int alignment) {
        switch (alignment) {
            case 1:
                return TextAlignment.CENTER;
            case 2:
                return TextAlignment.RIGHT;
            default:
                return TextAlignment.LEFT;
        }
    }

    private PdfFormField setFieldValue(String value, boolean generateAppearance) {
        if (value == null) {
            LOGGER.warn(FormsLogMessageConstants.FIELD_VALUE_CANNOT_BE_NULL);
            return this;
        }

        // First, get rid of displayValue
        displayValue = null;

        PdfName formType = getFormType();
        if (PdfName.Btn.equals(formType)) {
            if (getFieldFlag(PdfButtonFormField.FF_PUSH_BUTTON)) {
                try {
                    img = ImageDataFactory.create(Base64.decode(value));
                } catch (Exception e) {
                    if (generateAppearance) {
                        // Display value.
                        for (PdfFormAnnotation annot : getChildFormAnnotations()) {
                            annot.setCaption(value, false);
                        }
                    } else {
                        text = value;
                    }
                }
            } else {
                // We expect that radio buttons should have only widget children,
                // so we need to get rid of the form fields kids
                PdfFormFieldMergeUtil.processDirtyAnnotations(this, true);
                put(PdfName.V, new PdfName(value));
                if (generateAppearance && !getFieldFlag(PdfButtonFormField.FF_RADIO)) {
                    if (tryGenerateCheckboxAppearance(value)) {
                        return this;
                    }
                }
                for (PdfWidgetAnnotation widget : getWidgets()) {
                    List<String> states = Arrays.asList(PdfFormAnnotation
                            .makeFormAnnotation(widget.getPdfObject(), getDocument()).getAppearanceStates());
                    if (states.contains(value)) {
                        widget.setAppearanceState(new PdfName(value));
                    } else {
                        widget.setAppearanceState(new PdfName(PdfFormAnnotation.OFF_STATE_VALUE));
                    }
                }
            }
        } else {
            if (PdfName.Ch.equals(formType)) {
                if (this instanceof PdfChoiceFormField) {
                    ((PdfChoiceFormField) this).setListSelected(new String[] {value}, false);
                } else {
                    PdfChoiceFormField choice = PdfFormCreator.createChoiceFormField(this.getPdfObject());
                    choice.setListSelected(new String[] {value}, false);
                }
            } else {
                put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
            }
        }

        if (generateAppearance) {
            regenerateField();
        }

        this.setModified();
        return this;
    }

    /**
     * Distinguish mutually exclusive and regular checkboxes: check all the on states of the widgets, if they are
     * not all equal, then consider that this checkbox is mutually exclusive and do nothing, otherwise regenerate
     * normal appearance with value as on appearance state for all the widgets.
     *
     * @param value not empty value different from "Off".
     */
    private boolean tryGenerateCheckboxAppearance(String value) {
        if (value == null || value.isEmpty() || PdfFormAnnotation.OFF_STATE_VALUE.equals(value)) {
            return false;
        }
        Set<String> allStates = new HashSet<>();
        for (PdfFormAnnotation annotation : getChildFormAnnotations()) {
            allStates.addAll(Arrays.asList(annotation.getAppearanceStates()));
            if (allStates.size() > 2) {
                return false;
            }
        }
        allStates.remove(PdfFormAnnotation.OFF_STATE_VALUE);
        if (allStates.isEmpty() || allStates.size() == 1 &&
                !value.equals(allStates.toArray(new String[allStates.size()])[0])) {
            for (PdfFormAnnotation annotation : getChildFormAnnotations()) {
                annotation.setCheckBoxAppearanceOnStateName(value);
            }
            updateDefaultAppearance();
            return true;
        }
        return false;
    }

    private boolean mergeKidsIfKidWithSuchNameExists(AbstractPdfFormField newKid, boolean throwExceptionOnError) {
        if (childFields.contains(newKid)) {
            return true;
        }
        if (isInReadingMode() || PdfFormAnnotationUtil.isPureWidget(newKid.getPdfObject())) {
            return false;
        }
        String newKidPartialName = PdfFormFieldMergeUtil.getPartialName(newKid);
        for (AbstractPdfFormField kid : childFields) {
            String kidPartialName = PdfFormFieldMergeUtil.getPartialName(kid);
            if (kidPartialName != null && kidPartialName.equals(newKidPartialName)) {
                // Merge kid with the first found field with the same name.
                return PdfFormFieldMergeUtil.mergeTwoFieldsWithTheSameNames((PdfFormField) kid, (PdfFormField) newKid,
                        throwExceptionOnError);
            }
        }
        return false;
    }

    private boolean hasDefaultAppearance() {
        PdfName type = getFormType();
        return type == PdfName.Tx
                || type == PdfName.Ch
                || (type == PdfName.Btn && (getFieldFlags() & PdfButtonFormField.FF_PUSH_BUTTON) != 0);
    }

    private PdfName getUniqueFontNameForDR(PdfDictionary fontResources) {
        int indexer = 1;
        Set<PdfName> fontNames = fontResources.keySet();
        PdfName uniqueName;
        do {
            uniqueName = new PdfName("F" + indexer++);
        } while (fontNames.contains(uniqueName));
        return uniqueName;
    }

    private PdfName getFontNameFromDR(PdfDictionary fontResources, PdfObject font) {
        for (Map.Entry<PdfName, PdfObject> drFont : fontResources.entrySet()) {
            if (drFont.getValue() == font) {
                return drFont.getKey();
            }
        }
        return null;
    }

    /**
     * Puts object directly to AcroForm dictionary.
     * It works much faster than consequent invocation of {@link PdfAcroForm#getAcroForm(PdfDocument, boolean)}
     * and {@link PdfAcroForm#getPdfObject()}.
     * <p>
     * Note, this method assume that Catalog already has AcroForm object.
     * {@link #addAcroFormToCatalog()} should be called explicitly.
     *
     * @param acroFormKey    the key of the object.
     * @param acroFormObject the object to add.
     */
    private void putAcroFormObject(PdfName acroFormKey, PdfObject acroFormObject) {
        getDocument().getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm).put(acroFormKey, acroFormObject);
    }

    private void addAcroFormToCatalog() {
        if (getDocument().getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm) == null) {
            PdfDictionary acroform = new PdfDictionary();
            acroform.makeIndirect(getDocument());
            // PdfName.Fields is the only required key.
            acroform.put(PdfName.Fields, new PdfArray());
            getDocument().getCatalog().put(PdfName.AcroForm, acroform);
        }
    }

    private PdfObject getAcroFormKey(PdfName key, int type) {
        PdfObject acroFormKey = null;
        PdfDocument document = getDocument();
        if (document != null) {
            PdfDictionary acroFormDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
            if (acroFormDictionary != null) {
                acroFormKey = acroFormDictionary.get(key);
            }
        }
        return (acroFormKey != null && acroFormKey.getType() == type) ? acroFormKey : null;
    }
}
