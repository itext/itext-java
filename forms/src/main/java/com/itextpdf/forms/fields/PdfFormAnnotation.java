/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.borders.FormBorderFactory;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfCheckBoxRenderingStrategy;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.FontSizeUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.MetaInfoContainer;

import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a single annotation in form fields hierarchy in an {@link com.itextpdf.forms.PdfAcroForm
 * AcroForm}.
 *
 * <p>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfFormAnnotation extends AbstractPdfFormField {

    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;
    public static final int VISIBLE = 4;

    /**
     * Value which represents "off" state of form field.
     */
    public static final String OFF_STATE_VALUE = "Off";
    /**
     * Value which represents "on" state of form field.
     */
    public static final String ON_STATE_VALUE = "Yes";
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfFormAnnotation.class);
    private static final String LINE_ENDINGS_REGEXP = "\\r\\n|\\r|\\n";
    protected float borderWidth = 1;
    protected Color backgroundColor;
    protected Color borderColor;
    private IFormField formFieldElement;

    /**
     * Creates a form field annotation as a wrapper of a {@link PdfWidgetAnnotation}.
     *
     * @param widget      The widget which will be a kid of the {@link PdfFormField}
     * @param pdfDocument The {@link PdfDocument} instance.
     */
    protected PdfFormAnnotation(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        this(widget.makeIndirect(pdfDocument).getPdfObject());
    }

    /**
     * Creates a form field annotation as a wrapper object around a {@link PdfDictionary}.
     * This {@link PdfDictionary} must be an indirect object.
     *
     * @param pdfObject the dictionary to be wrapped, must have an indirect reference.
     */
    protected PdfFormAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a {@link PdfFormAnnotation} object.
     *
     * @param pdfObject assumed to be either a {@link PdfDictionary}, or a
     *                  {@link PdfIndirectReference} to a {@link PdfDictionary}.
     * @param document  the {@link PdfDocument} to create the field in.
     *
     * @return a new {@link PdfFormAnnotation}, or <code>null</code> if
     * <code>pdfObject</code> is not a widget annotation.
     */
    public static PdfFormAnnotation makeFormAnnotation(PdfObject pdfObject, PdfDocument document) {
        if (!pdfObject.isDictionary()) {
            return null;
        }

        PdfFormAnnotation field;
        PdfDictionary dictionary = (PdfDictionary) pdfObject;
        final PdfName subType = dictionary.getAsName(PdfName.Subtype);
        // If widget annotation
        if (PdfName.Widget.equals(subType)) {
            field = PdfFormCreator.createFormAnnotation((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(dictionary),
                    document);
        } else {
            return null;
        }
        field.makeIndirect(document);

        if (document != null && document.getReader() != null
                && document.getReader().getPdfAConformanceLevel() != null) {
            field.pdfAConformanceLevel = document.getReader().getPdfAConformanceLevel();
        }

        return field;
    }

    /**
     * Gets {@link PdfWidgetAnnotation} that this form field refers to.
     *
     * @return {@link PdfWidgetAnnotation}.
     */
    public PdfWidgetAnnotation getWidget() {
        PdfName subType = getPdfObject().getAsName(PdfName.Subtype);
        if (subType != null && subType.equals(PdfName.Widget)) {
            return (PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject());
        }

        // Should never be here
        assert "You are not an annotation then" == null;
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean regenerateField() {
        if (parent != null && parent.isFieldRegenerationEnabled()) {
            parent.updateDefaultAppearance();
        }
        return regenerateWidget();
    }

    /**
     * Gets the appearance state names.
     *
     * @return an array of Strings containing the names of the appearance states.
     */
    @Override
    public String[] getAppearanceStates() {
        Set<String> names = new LinkedHashSet<>();

        PdfDictionary dic = getPdfObject();
        dic = dic.getAsDictionary(PdfName.AP);
        if (dic != null) {
            dic = dic.getAsDictionary(PdfName.N);
            if (dic != null) {
                for (PdfName state : dic.keySet()) {
                    names.add(state.getValue());
                }
            }
        }

        return names.toArray(new String[names.size()]);
    }

    @Override
    void retrieveStyles() {
        super.retrieveStyles();

        PdfDictionary appearanceCharacteristics = getPdfObject().getAsDictionary(PdfName.MK);
        if (appearanceCharacteristics != null) {
            backgroundColor = appearancePropToColor(appearanceCharacteristics, PdfName.BG);
            Color extractedBorderColor = appearancePropToColor(appearanceCharacteristics, PdfName.BC);
            if (extractedBorderColor != null) {
                borderColor = extractedBorderColor;
            }
            if (parent != null) {
                parent.text = appearancePropToCaption(appearanceCharacteristics);
            }
        }
    }

    /**
     * Basic setter for the <code>backgroundColor</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param backgroundColor The new color to be set or {@code null} if no background needed.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        PdfDictionary mk;
        PdfWidgetAnnotation kid = getWidget();
        mk = kid.getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
        }
        if (backgroundColor == null) {
            mk.remove(PdfName.BG);
        } else {
            mk.put(PdfName.BG, new PdfArray(backgroundColor.getColorValue()));
        }
        kid.setAppearanceCharacteristics(mk);

        regenerateField();
        return this;
    }

    /**
     * Basic setter for the push button caption. Regenerates the field appearance after setting the new caption.
     *
     * @param caption button caption to be set.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setCaption(String caption) {
        return setCaption(caption, true);
    }

    /**
     * Basic setter for the push button caption. Regenerates the field appearance after setting the new caption
     * if corresponding parameter is specified.
     *
     * @param caption         button caption to be set.
     * @param regenerateField true if field should be regenerated, false otherwise.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setCaption(String caption, boolean regenerateField) {
        if (parent != null) {
            parent.text = caption;
        }
        PdfDictionary mk;
        PdfWidgetAnnotation kid = getWidget();
        mk = kid.getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
        }
        if (caption == null) {
            mk.remove(PdfName.CA);
        } else {
            mk.put(PdfName.CA, new PdfString(caption));
        }
        kid.setAppearanceCharacteristics(mk);

        if (regenerateField) {
            regenerateField();
        }
        return this;
    }

    /**
     * Get rotation property specified in this form annotation.
     *
     * @return {@code int} value which represents field's rotation
     */
    public int getRotation() {
        PdfDictionary mk = getWidget().getAppearanceCharacteristics();
        return mk == null || mk.getAsInt(PdfName.R) == null ? 0 : (int) mk.getAsInt(PdfName.R);
    }

    /**
     * Basic setter for the <code>degRotation</code> property. Regenerates
     * the field appearance after setting the new value.
     *
     * @param degRotation The new degRotation to be set
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setRotation(int degRotation) {
        if (degRotation % 90 != 0) {
            throw new IllegalArgumentException("degRotation.must.be.a.multiple.of.90");
        } else {
            degRotation %= 360;
            if (degRotation < 0) {
                degRotation += 360;
            }
        }
        PdfDictionary mk = getWidget().getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
            this.put(PdfName.MK, mk);
        }
        mk.put(PdfName.R, new PdfNumber(degRotation));

        regenerateField();
        return this;
    }

    /**
     * Sets the action on {@link PdfWidgetAnnotation widget} of this annotation form field.
     *
     * @param action The action.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setAction(PdfAction action) {
        PdfWidgetAnnotation widget = getWidget();
        if (widget != null) {
            widget.setAction(action);
        }
        return this;
    }

    /**
     * Set the visibility flags of the form field annotation.
     * Options are: HIDDEN, HIDDEN_BUT_PRINTABLE, VISIBLE, VISIBLE_BUT_DOES_NOT_PRINT.
     *
     * @param visibility visibility option.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setVisibility(int visibility) {
        switch (visibility) {
            case HIDDEN:
                put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.HIDDEN));
                break;
            case VISIBLE_BUT_DOES_NOT_PRINT:
                break;
            case HIDDEN_BUT_PRINTABLE:
                put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT | PdfAnnotation.NO_VIEW));
                break;
            case VISIBLE:
            default:
                put(PdfName.F, new PdfNumber(PdfAnnotation.PRINT));
                break;
        }

        return this;
    }

    /**
     * Gets the border width for the field.
     *
     * @return the current border width.
     */
    public float getBorderWidth() {
        PdfDictionary bs = getWidget().getBorderStyle();
        if (bs != null) {
            PdfNumber w = bs.getAsNumber(PdfName.W);
            if (w != null) {
                borderWidth = w.floatValue();
            }
        }
        return borderWidth;
    }

    /**
     * Sets the border width for the field.
     *
     * @param borderWidth The new border width.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setBorderWidth(float borderWidth) {
        // Acrobat doesn't support float border width therefore we round it.
        int roundedBorderWidth = (int) Math.round(borderWidth);
        PdfDictionary bs = getWidget().getBorderStyle();
        if (bs == null) {
            bs = new PdfDictionary();
            put(PdfName.BS, bs);
        }
        bs.put(PdfName.W, new PdfNumber(roundedBorderWidth));
        this.borderWidth = roundedBorderWidth;

        regenerateField();
        return this;
    }

    /**
     * Get border object specified in the widget annotation dictionary.
     *
     * @return {@link Border} specified in the widget annotation dictionary
     */
    public Border getBorder() {
        float borderWidth = getBorderWidth();
        Border border = FormBorderFactory.getBorder(
                this.getWidget().getBorderStyle(), borderWidth, borderColor, backgroundColor);
        if (border == null && borderWidth > 0 && borderColor != null) {
            border = new SolidBorder(borderColor, Math.max(1, borderWidth));
        }
        return border;
    }

    /**
     * Sets the border style for the field.
     *
     * @param style the new border style.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setBorderStyle(PdfDictionary style) {
        getWidget().setBorderStyle(style);
        regenerateField();
        return this;
    }

    /**
     * Sets the Border Color.
     *
     * @param color the new value for the Border Color.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setBorderColor(Color color) {
        borderColor = color;
        PdfDictionary mk;
        PdfWidgetAnnotation kid = getWidget();
        mk = kid.getAppearanceCharacteristics();
        if (mk == null) {
            mk = new PdfDictionary();
        }
        if (borderColor == null) {
            mk.remove(PdfName.BC);
        } else {
            mk.put(PdfName.BC, new PdfArray(borderColor.getColorValue()));
        }
        kid.setAppearanceCharacteristics(mk);

        regenerateField();
        return this;
    }

    /**
     * Specifies on which page the form field's widget must be shown.
     *
     * @param pageNum the page number.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setPage(int pageNum) {
        PdfWidgetAnnotation widget = getWidget();
        if (widget != null) {
            widget.setPage(getDocument().getPage(pageNum));
        }

        return this;
    }

    /**
     * This method sets the model element associated with the current annotation and can be useful to take into account
     * when drawing those properties that the annotation does not have. Note that annotation properties will take
     * precedence, so such properties cannot be overridden by using this method (e.g. background, text color, etc.).
     *
     * <p>
     * Also note that the model element won't be used for annotations for choice form field.
     *
     * @param element model element to set.
     *
     * @return this {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setFormFieldElement(IFormField element) {
        this.formFieldElement = element;
        regenerateWidget();
        return this;
    }

    /**
     * Sets an appearance for (the widgets related to) the form field.
     *
     * @param appearanceType   the type of appearance stream to be added
     *                         <ul>
     *                         <li> PdfName.N: normal appearance
     *                         <li> PdfName.R: rollover appearance
     *                         <li> PdfName.D: down appearance
     *                         </ul>
     * @param appearanceState  the state of the form field that needs to be true
     *                         for the appearance to be used. Differentiates between several streams
     *                         of the same type.
     * @param appearanceStream the appearance instructions, as a {@link PdfStream}.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setAppearance(PdfName appearanceType, String appearanceState,
            PdfStream appearanceStream) {
        PdfDictionary dic = getPdfObject();
        PdfDictionary ap = dic.getAsDictionary(PdfName.AP);
        if (ap != null) {
            PdfDictionary appearanceDictionary = ap.getAsDictionary(appearanceType);
            if (appearanceDictionary == null) {
                ap.put(appearanceType, appearanceStream);
            } else {
                appearanceDictionary.put(new PdfName(appearanceState), appearanceStream);
            }
        }

        return this;
    }

    /**
     * Sets on state name for the checkbox annotation normal appearance and regenerates widget.
     *
     * @param onStateName the new appearance name representing on state.
     *
     * @return The edited {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setCheckBoxAppearanceOnStateName(String onStateName) {
        if (isCheckBox() && onStateName != null && !onStateName.isEmpty()
                && !PdfFormAnnotation.OFF_STATE_VALUE.equals(onStateName)) {
            drawCheckBoxAndSaveAppearance(onStateName);
            getWidget().setAppearanceState(new PdfName(onStateName.equals(parent.getValueAsString()) ?
                    onStateName : OFF_STATE_VALUE));
        }
        return this;
    }

    /**
     * Gets a {@link Rectangle} that matches the current size and position of this form field.
     *
     * @param field current form field.
     *
     * @return a {@link Rectangle} that matches the current size and position of this form field
     * annotation.
     */
    protected Rectangle getRect(PdfDictionary field) {
        PdfArray rect = field.getAsArray(PdfName.Rect);
        return rect == null ? null : rect.toRectangle();
    }

    /**
     * Draws a border using the borderWidth and borderColor of the form field.
     *
     * @param canvas  The {@link PdfCanvas} on which to draw
     * @param xObject The PdfFormXObject
     * @param width   The width of the rectangle to draw
     * @param height  The height of the rectangle to draw
     */
    protected void drawBorder(PdfCanvas canvas, PdfFormXObject xObject, float width, float height) {
        canvas.saveState();
        float borderWidth = getBorderWidth();
        PdfDictionary bs = getWidget().getBorderStyle();
        if (borderWidth < 0) {
            borderWidth = 0;
        }

        if (backgroundColor != null) {
            canvas
                    .setFillColor(backgroundColor)
                    .rectangle(0, 0, width, height)
                    .fill();
        }

        if (borderWidth > 0 && borderColor != null) {
            borderWidth = Math.max(1, borderWidth);
            canvas
                    .setStrokeColor(borderColor)
                    .setLineWidth(borderWidth);
            Border border = FormBorderFactory.getBorder(bs, borderWidth, borderColor, backgroundColor);
            if (border != null) {
                float borderWidthX2 = borderWidth + borderWidth;
                border.draw(canvas, new Rectangle(borderWidth, borderWidth,
                        width - borderWidthX2, height - borderWidthX2));
            } else {
                canvas
                        .rectangle(0, 0, width, height)
                        .stroke();
            }
        }

        PdfArray matrix = getRotationMatrix(getRotation() % 360, height, width);
        if (matrix != null) {
            xObject.put(PdfName.Matrix, matrix);
        }
        canvas.restoreState();
    }

    /**
     * Draws the appearance of a push button and saves it into an appearance stream.
     */
    protected void drawPushButtonFieldAndSaveAppearance() {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }
        float width = rectangle.getWidth();
        float height = rectangle.getHeight();

        createInputButton();

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        PdfArray matrix = getRotationMatrix(getRotation() % 360, height, width);
        if (matrix != null) {
            xObject.put(PdfName.Matrix, matrix);
        }
        Canvas canvas = new Canvas(xObject, this.getDocument());
        setMetaInfoToCanvas(canvas);

        String caption = parent.getDisplayValue();
        if (caption != null && !caption.isEmpty()) {
            ((Button) formFieldElement).setSingleLineValue(caption);
        }

        float imagePadding = borderColor == null ? 0 : borderWidth;
        if (parent.img != null) {
            // If we got here, the button will only contain the image that the user has set into the annotation.
            // There is no way to pass other elements with this image.
            formFieldElement.getChildren().clear();
            Image image = new Image(new PdfImageXObject(parent.img), imagePadding, imagePadding);
            image.setHeight(height - 2 * imagePadding);
            image.setWidth(width - 2 * imagePadding);
            ((Button) formFieldElement).add(image);
        } else if (parent.form != null) {
            // If we got here, the button will only contain the image that the user has set as form into the annotation.
            // There is no way to pass other elements with this image as form.
            formFieldElement.getChildren().clear();
            Image image = new Image(parent.form, imagePadding, imagePadding);
            image.setHeight(height - 2 * imagePadding);
            ((Button) formFieldElement).add(image);
        } else {
            xObject.getResources().addFont(getDocument(), getFont());
        }
        canvas.add(formFieldElement);

        PdfDictionary ap = new PdfDictionary();
        PdfStream normalAppearanceStream = xObject.getPdfObject();
        if (normalAppearanceStream != null) {
            PdfName stateName = getPdfObject().getAsName(PdfName.AS);
            if (stateName == null) {
                stateName = new PdfName("push");
            }
            getPdfObject().put(PdfName.AS, stateName);
            PdfDictionary normalAppearance = new PdfDictionary();
            normalAppearance.put(stateName, normalAppearanceStream);
            ap.put(PdfName.N, normalAppearance);
            ap.setModified();
        }
        put(PdfName.AP, ap);
        // We need to draw waitingDrawingElements (drawn inside close method), but the close method
        // flushes TagTreePointer that will be used later, so set null to the corresponding property.
        canvas.setProperty(Property.TAGGING_HELPER, null);
        canvas.close();
    }

    /**
     * Draws the appearance of a radio button with a specified value and saves it into an appearance stream.
     *
     * @param value the value of the radio button.
     */
    protected void drawRadioButtonAndSaveAppearance(String value) {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }

        if (!(formFieldElement instanceof Radio)) {
            // Create it one time and re-set properties during each widget regeneration.
            formFieldElement = new Radio("");
        }
        setModelElementProperties(getRect(getPdfObject()));

        // First draw off appearance
        ((Radio) formFieldElement).setChecked(false);
        PdfFormXObject xObjectOff = new PdfFormXObject(
                new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
        Canvas canvasOff = new Canvas(xObjectOff, this.getDocument());
        setMetaInfoToCanvas(canvasOff);
        canvasOff.add(formFieldElement);
        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        // Draw on appearance
        if (value != null && !value.isEmpty() && !PdfFormAnnotation.OFF_STATE_VALUE.equals(value)) {
            ((Radio) formFieldElement).setChecked(true);
            PdfFormXObject xObject = new PdfFormXObject(
                    new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
            Canvas canvas = new Canvas(xObject, this.getDocument());
            setMetaInfoToCanvas(canvas);
            canvas.add(formFieldElement);
            normalAppearance.put(new PdfName(value), xObject.getPdfObject());
        }

        getWidget().setNormalAppearance(normalAppearance);
    }

    /**
     * Draws the appearance of a list box form field and saves it into an appearance stream.
     */
    protected void drawListFormFieldAndSaveAppearance() {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }

        if (!(formFieldElement instanceof ListBoxField)) {
            // Create it once and reset properties during each widget regeneration.
            formFieldElement = new ListBoxField("", 0, parent.getFieldFlag(PdfChoiceFormField.FF_MULTI_SELECT));
        }
        formFieldElement.setProperty(FormProperty.FORM_FIELD_MULTIPLE,
                parent.getFieldFlag(PdfChoiceFormField.FF_MULTI_SELECT));

        PdfArray indices = getParent().getAsArray(PdfName.I);
        PdfArray options = parent.getOptions();
        for (int index = 0; index < options.size(); ++index) {
            final PdfObject option = options.get(index);
            String exportValue = null;
            String displayValue = null;
            if (option.isString()) {
                exportValue = option.toString();
            } else if (option.isArray()) {
                PdfArray optionArray = (PdfArray) option;
                if (optionArray.size() > 1) {
                    exportValue = optionArray.get(0).toString();
                    displayValue = optionArray.get(1).toString();
                }
            }
            if (exportValue == null) {
                continue;
            }

            final boolean selected = indices == null ? false : indices.contains(new PdfNumber(index));
            SelectFieldItem existingItem = ((ListBoxField) formFieldElement).getOption(exportValue);
            if (existingItem == null) {
                existingItem = new SelectFieldItem(exportValue, displayValue);
                ((ListBoxField) formFieldElement).addOption(existingItem);
            }
            existingItem.getElement().setProperty(Property.TEXT_ALIGNMENT, parent.getJustification());
            existingItem.getElement().setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            existingItem.getElement().setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            existingItem.getElement().setProperty(FormProperty.FORM_FIELD_SELECTED, selected);
        }

        formFieldElement.setProperty(Property.FONT, getFont());
        if (getColor() != null) {
            formFieldElement.setProperty(Property.FONT_COLOR, new TransparentColor(getColor()));
        }

        setModelElementProperties(rectangle);

        PdfFormXObject xObject = new PdfFormXObject(
                new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));

        Canvas canvas = new Canvas(xObject, this.getDocument());
        setMetaInfoToCanvas(canvas);
        canvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);
        canvas.getPdfCanvas().beginVariableText().saveState().endPath();
        canvas.add(formFieldElement);
        canvas.getPdfCanvas().restoreState().endVariableText();

        getWidget().setNormalAppearance(xObject.getPdfObject());
    }

    /**
     * Draws the appearance of a text form field and saves it into an appearance stream.
     */
    protected void drawTextFormFieldAndSaveAppearance() {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }

        String value = parent.getDisplayValue();
        if (!(parent.isMultiline() && formFieldElement instanceof TextArea ||
                !parent.isMultiline() && formFieldElement instanceof InputField)) {
            // Create it one time and re-set properties during each widget regeneration.
            formFieldElement = parent.isMultiline() ?
                    (IFormField) new TextArea("") :
                    (IFormField) new InputField("");
        }
        if (parent.isMultiline()) {
            formFieldElement.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(getFontSize()));
        } else {
            formFieldElement.setProperty(Property.FONT_SIZE,
                    UnitValue.createPointValue(getFontSize(new PdfArray(rectangle), parent.getValueAsString())));
            value = value.replaceAll(LINE_ENDINGS_REGEXP, " ");
        }
        formFieldElement.setValue(value);
        formFieldElement.setProperty(Property.FONT, getFont());
        formFieldElement.setProperty(Property.TEXT_ALIGNMENT, parent.getJustification());
        formFieldElement.setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, getParentField().isPassword());
        formFieldElement.setProperty(Property.ADD_MARKED_CONTENT_TEXT, Boolean.TRUE);

        if (getColor() != null) {
            formFieldElement.setProperty(Property.FONT_COLOR, new TransparentColor(getColor()));
        }

        // Rotation
        final int fieldRotation = getRotation() % 360;
        PdfArray matrix = getRotationMatrix(fieldRotation, rectangle.getHeight(), rectangle.getWidth());
        if (fieldRotation == 90 || fieldRotation == 270) {
            Rectangle invertedRectangle = rectangle.clone();
            invertedRectangle.setWidth(rectangle.getHeight());
            invertedRectangle.setHeight(rectangle.getWidth());
            rectangle = invertedRectangle;
        }

        setModelElementProperties(rectangle);

        PdfFormXObject xObject = new PdfFormXObject(
                new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
        if (matrix != null) {
            xObject.put(PdfName.Matrix, matrix);
        }
        Canvas canvas = new Canvas(xObject, this.getDocument());
        setMetaInfoToCanvas(canvas);
        canvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);
        canvas.add(formFieldElement);

        getWidget().setNormalAppearance(xObject.getPdfObject());
    }


    /**
     * Draws the appearance of a Combo box form field and saves it into an appearance stream.
     */
    protected void drawComboBoxAndSaveAppearance() {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }
        if (!(formFieldElement instanceof ComboBoxField)) {
            formFieldElement = new ComboBoxField("");
        }

        ComboBoxField comboBoxField = (ComboBoxField) formFieldElement;
        prepareComboBoxFieldWithCorrectOptionsAndValues(comboBoxField);
        comboBoxField.setFont(getFont());
        setModelElementProperties(rectangle);
        if (getFontSize() <= 0) {
            Rectangle r2 = rectangle.clone();
            // because the border is drawn inside the rectangle, we need to take this into account
            float marginToApply = borderWidth;
            r2.applyMargins(marginToApply, marginToApply, marginToApply, marginToApply, false);
            UnitValue estimatedFontSize = UnitValue.createPointValue(
                    getFontSize(new PdfArray(r2), parent.getValueAsString()));
            comboBoxField.setFontSize(estimatedFontSize.getValue());
        } else {
            comboBoxField.setFontSize(getFontSize());
        }
        if (getColor() != null) {
            comboBoxField.setFontColor(getColor());
        }
        comboBoxField.setTextAlignment(parent.getJustification());

        Rectangle pdfXobjectRectangle = new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight());
        final PdfFormXObject xObject = new PdfFormXObject(pdfXobjectRectangle);
        final Canvas canvas = new Canvas(xObject, getDocument());
        canvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);
        setMetaInfoToCanvas(canvas);
        canvas.setFont(getFont());
        canvas.getPdfCanvas().beginVariableText().saveState().endPath();
        canvas.add(comboBoxField);
        canvas.getPdfCanvas().restoreState().endVariableText();
        getWidget().setNormalAppearance(xObject.getPdfObject());

    }

    private void prepareComboBoxFieldWithCorrectOptionsAndValues(ComboBoxField comboBoxField) {
        for (PdfObject option : parent.getOptions()) {
            SelectFieldItem item = null;
            if (option.isString()) {
                item = new SelectFieldItem(((PdfString) option).getValue());
            }
            if (option.isArray()) {
                assert option instanceof PdfArray;
                PdfArray array = (PdfArray) option;
                final int thereShouldBeTwoElementsInArray = 2;
                if (array.size() == thereShouldBeTwoElementsInArray) {
                    String exportValue = ((PdfString) array.get(0)).getValue();
                    String displayValue = ((PdfString) array.get(1)).getValue();
                    item = new SelectFieldItem(exportValue, displayValue);
                }
            }
            if (item != null && comboBoxField.getOption(item.getExportValue()) == null) {
                comboBoxField.addOption(item);
            }
        }
        comboBoxField.setSelected(parent.getDisplayValue());
    }

    /**
     * Draw a checkbox and save its appearance.
     *
     * @param onStateName the name of the appearance state for the checked state
     */
    protected void drawCheckBoxAndSaveAppearance(String onStateName) {
        final Rectangle rect = getRect(this.getPdfObject());
        if (rect == null) {
            return;
        }
        reconstructCheckBoxType();
        createCheckBox();
        final PdfDictionary normalAppearance = new PdfDictionary();
        ((CheckBox) formFieldElement).setChecked(false);
        final PdfFormXObject xObjectOff = new PdfFormXObject(
                new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
        final Canvas canvasOff = new Canvas(xObjectOff, getDocument());
        setMetaInfoToCanvas(canvasOff);
        canvasOff.add(formFieldElement);
        if (getPdfAConformanceLevel() == null) {
            xObjectOff.getResources().addFont(getDocument(), getFont());
        }
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        String onStateNameForAp = onStateName;
        if (onStateName == null || onStateName.isEmpty() || PdfFormAnnotation.OFF_STATE_VALUE.equals(onStateName)) {
            onStateNameForAp = ON_STATE_VALUE;
        }

        ((CheckBox) formFieldElement).setChecked(true);
        final PdfFormXObject xObject = new PdfFormXObject(
                new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
        final Canvas canvas = new Canvas(xObject, this.getDocument());
        setMetaInfoToCanvas(canvas);
        canvas.add(formFieldElement);
        normalAppearance.put(new PdfName(onStateNameForAp), xObject.getPdfObject());

        getWidget().setNormalAppearance(normalAppearance);

        final PdfDictionary mk = new PdfDictionary();

        // We put the zapfDingbats code of the checkbox in the MK dictionary to make sure there is a way
        // to retrieve the checkbox type even if the appearance is not present.
        mk.put(PdfName.CA,
                new PdfString(PdfCheckBoxRenderingStrategy.ZAPFDINGBATS_CHECKBOX_MAPPING.getByKey(
                        parent.checkType.getValue())));
        getWidget().put(PdfName.MK, mk);
    }

    static void setMetaInfoToCanvas(Canvas canvas) {
        MetaInfoContainer metaInfo = FormsMetaInfoStaticContainer.getMetaInfoForLayout();
        if (metaInfo != null) {
            canvas.setProperty(Property.META_INFO, metaInfo);
        }
    }

    boolean regenerateWidget() {
        if (!isFieldRegenerationEnabled()) {
            return false;
        }
        if (parent == null) {
            return true;
        }
        final PdfName type = parent.getFormType();
        retrieveStyles();

        if ((PdfName.Ch.equals(type) && parent.getFieldFlag(PdfChoiceFormField.FF_COMBO))
                || this.isCombTextFormField()) {
            if (parent.getFieldFlag(PdfChoiceFormField.FF_COMBO) && formFieldElement != null) {
                drawComboBoxAndSaveAppearance();
                return true;
            }
            return TextAndChoiceLegacyDrawer.regenerateTextAndChoiceField(this);
        } else if (PdfName.Ch.equals(type) && !parent.getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
            if (formFieldElement != null) {
                drawListFormFieldAndSaveAppearance();
                return true;
            } else {
                return TextAndChoiceLegacyDrawer.regenerateTextAndChoiceField(this);
            }
        } else if (PdfName.Tx.equals(type)) {
            drawTextFormFieldAndSaveAppearance();
            return true;
        } else if (PdfName.Btn.equals(type)) {
            if (parent.getFieldFlag(PdfButtonFormField.FF_PUSH_BUTTON)) {
                drawPushButtonFieldAndSaveAppearance();
            } else if (parent.getFieldFlag(PdfButtonFormField.FF_RADIO)) {
                drawRadioButtonAndSaveAppearance(getRadioButtonValue());
            } else {
                drawCheckBoxAndSaveAppearance(getCheckBoxValue());
            }
            return true;
        }
        return false;
    }

    void createInputButton() {
        if (!(formFieldElement instanceof Button)) {
            // Create it one time and re-set properties during each widget regeneration.
            formFieldElement = new Button(parent.getPartialFieldName().toUnicodeString());
        }

        ((Button) formFieldElement).setFont(getFont());
        ((Button) formFieldElement).setFontSize(getFontSize(getPdfObject()
                .getAsArray(PdfName.Rect), parent.getDisplayValue()));
        if (getColor() != null) {
            ((Button) formFieldElement).setFontColor(color);
        }

        setModelElementProperties(getRect(getPdfObject()));
    }

    float getFontSize(PdfArray bBox, String value) {
        if (getFontSize() == 0) {
            if (bBox == null || value == null || value.isEmpty()) {
                return DEFAULT_FONT_SIZE;
            } else {
                return FontSizeUtil.approximateFontSizeToFitSingleLine(getFont(), bBox.toRectangle(), value,
                        MIN_FONT_SIZE, borderWidth);
            }
        }
        return getFontSize();
    }

    private boolean isCombTextFormField() {
        final PdfName type = parent.getFormType();
        if (PdfName.Tx.equals(type) && parent.getFieldFlag(PdfTextFormField.FF_COMB)) {
            int maxLen = PdfFormCreator.createTextFormField(parent.getPdfObject()).getMaxLen();
            if (maxLen == 0 || parent.isMultiline()) {
                LOGGER.error(
                        MessageFormatUtil.format(IoLogMessageConstant.COMB_FLAG_MAY_BE_SET_ONLY_IF_MAXLEN_IS_PRESENT));
                return false;
            }
            return true;
        }
        return false;
    }

    private String getRadioButtonValue() {
        for (String state : getAppearanceStates()) {
            if (!OFF_STATE_VALUE.equals(state)) {
                return state;
            }
        }
        return null;
    }

    private String getCheckBoxValue() {
        for (String state : getAppearanceStates()) {
            if (!OFF_STATE_VALUE.equals(state)) {
                return state;
            }
        }
        return parent.getValueAsString();
    }

    private void reconstructCheckBoxType() {
        // if checkbox type is null it means we are reading from a document and we need to retrieve the type from the
        // mk dictionary in the ca
        if (parent.checkType == null) {
            PdfDictionary oldMk = getWidget().getAppearanceCharacteristics();
            if (oldMk != null) {
                PdfString oldCa = oldMk.getAsString(PdfName.CA);
                if (oldCa != null && PdfCheckBoxRenderingStrategy.ZAPFDINGBATS_CHECKBOX_MAPPING.containsValue(
                        oldCa.getValue())) {
                    parent.checkType = new NullableContainer<>(
                            PdfCheckBoxRenderingStrategy.ZAPFDINGBATS_CHECKBOX_MAPPING.getByValue(oldCa.getValue()));
                    // we need to set the font size to 0 to make sure the font size is recalculated
                    fontSize = 0;
                }
            }
        }
        // if its still null default to default value
        if (parent.checkType == null) {
            parent.checkType = new NullableContainer<>(CheckBoxType.CROSS);
        }
    }

    private void createCheckBox() {
        if (!(formFieldElement instanceof CheckBox)) {
            // Create it one time and re-set properties during each widget regeneration.
            formFieldElement = new CheckBox("");
        }

        formFieldElement.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(getFontSize()));
        setModelElementProperties(getRect(getPdfObject()));
        ((CheckBox) formFieldElement).setPdfAConformanceLevel(getPdfAConformanceLevel());
        ((CheckBox) formFieldElement).setCheckBoxType(parent.checkType.getValue());
    }

    private void setModelElementProperties(Rectangle rectangle) {
        if (backgroundColor != null) {
            formFieldElement.setProperty(Property.BACKGROUND, new Background(backgroundColor));
        }
        formFieldElement.setProperty(Property.BORDER, getBorder());
        // Set fixed size
        BoxSizingPropertyValue boxSizing = formFieldElement.<BoxSizingPropertyValue>getProperty(Property.BOX_SIZING);
        // Borders are already taken into account for rectangle area, but shouldn't be included into width and height
        // of the field in case of content-box value of box-sizing property.
        float extraBorderWidth = BoxSizingPropertyValue.CONTENT_BOX == boxSizing ? 2 * borderWidth : 0;
        formFieldElement.setWidth(rectangle.getWidth() - extraBorderWidth);
        formFieldElement.setHeight(rectangle.getHeight() - extraBorderWidth);
        // Always flatten
        formFieldElement.setInteractive(false);
    }

    private static PdfArray getRotationMatrix(int rotation, float height, float width) {
        switch (rotation) {
            case 0:
                return null;
            case 90:
                return new PdfArray(new float[] {0, 1, -1, 0, height, 0});
            case 180:
                return new PdfArray(new float[] {-1, 0, 0, -1, width, height});
            case 270:
                return new PdfArray(new float[] {0, -1, 1, 0, 0, width});
            default:
                Logger logger = LoggerFactory.getLogger(PdfFormAnnotation.class);
                logger.error(FormsLogMessageConstants.INCORRECT_WIDGET_ROTATION);
                return null;
        }
    }

    private static Color appearancePropToColor(PdfDictionary appearanceCharacteristics, PdfName property) {
        PdfArray colorData = appearanceCharacteristics.getAsArray(property);
        if (colorData != null) {
            float[] backgroundFloat = new float[colorData.size()];
            for (int i = 0; i < colorData.size(); i++) {
                backgroundFloat[i] = colorData.getAsNumber(i).floatValue();
            }
            switch (colorData.size()) {
                case 0:
                    return null;
                case 1:
                    return new DeviceGray(backgroundFloat[0]);
                case 3:
                    return new DeviceRgb(backgroundFloat[0], backgroundFloat[1], backgroundFloat[2]);
                case 4:
                    return new DeviceCmyk(backgroundFloat[0], backgroundFloat[1], backgroundFloat[2],
                            backgroundFloat[3]);
            }
        }
        return null;
    }

    private static String appearancePropToCaption(PdfDictionary appearanceCharacteristics) {
        PdfString captionData = appearanceCharacteristics.getAsString(PdfName.CA);
        if (captionData != null) {
            return captionData.getValue();
        }
        return null;
    }

    private boolean isCheckBox() {
        return parent != null && PdfName.Btn.equals(parent.getFormType())
                && !parent.getFieldFlag(PdfButtonFormField.FF_RADIO)
                && !parent.getFieldFlag(PdfButtonFormField.FF_PUSH_BUTTON);
    }
}
