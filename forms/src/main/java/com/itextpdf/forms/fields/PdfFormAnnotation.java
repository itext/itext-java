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

import com.itextpdf.commons.utils.ExperimentalFeatures;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.borders.FormBorderFactory;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.Radio;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.renderer.FormFieldValueNonTrimmingTextRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.forms.util.FontSizeUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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

    /**
     * Default padding X offset.
     */
    static final float X_OFFSET = 2;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfFormAnnotation.class);

    protected float borderWidth = 1;
    protected Color backgroundColor;
    protected Color borderColor;

    private Button formFieldElement;

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
    PdfFormAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
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
    
    public int getRotation() {
        PdfDictionary mk = getWidget().getAppearanceCharacteristics();
        return mk == null || mk.getAsInt(PdfName.R) == null ? 0 : (int) mk.getAsInt(PdfName.R);
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
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean regenerateField() {
        if (parent != null) {
            parent.updateDefaultAppearance();
        }
        return regenerateWidget();
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
     * Sets an element associated with the current field.
     *
     * @param element model element to set.
     *
     * @return this {@link PdfFormAnnotation}.
     */
    public PdfFormAnnotation setFormFieldElement(Button element) {
        this.formFieldElement = element;
        regenerateWidget();
        return this;
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
            field = new PdfFormAnnotation((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(dictionary),
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
     * Draws the visual appearance of text in a form field.
     *
     * @param rect       The location on the page for the list field.
     * @param font       a {@link PdfFont}.
     * @param fontSize   The size of the font.
     * @param value      The initial value.
     * @param appearance The appearance.
     */
    protected void drawTextAppearance(Rectangle rect, PdfFont font, float fontSize, String value,
            PdfFormXObject appearance) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        float height = rect.getHeight();
        float width = rect.getWidth();
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        drawBorder(canvas, xObject, width, height);
        if (parent.isPassword()) {
            value = obfuscatePassword(value);
        }

        canvas.
                beginVariableText().
                saveState().
                endPath();

        TextAlignment textAlignment =
                parent.getJustification() == null ? TextAlignment.LEFT : parent.getJustification();
        float x = 0;
        if (textAlignment == TextAlignment.RIGHT) {
            x = rect.getWidth();
        } else if (textAlignment == TextAlignment.CENTER) {
            x = rect.getWidth() / 2;
        }

        Canvas modelCanvas = new Canvas(canvas, new Rectangle(0, -height, 0, 2 * height));
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);

        setMetaInfoToCanvas(modelCanvas);

        Style paragraphStyle = new Style().setFont(font).setFontSize(fontSize);
        paragraphStyle.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 1));
        paragraphStyle.setFontColor(getColor());
        
        modelCanvas.showTextAligned(createParagraphForTextFieldValue(value).addStyle(paragraphStyle)
                        .setPaddings(0, X_OFFSET, 0, X_OFFSET), x, rect.getHeight() / 2, textAlignment,
                VerticalAlignment.MIDDLE);
        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }

    protected void drawMultiLineTextAppearance(Rectangle rect, PdfFont font, String value, PdfFormXObject appearance) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, getDocument());

        float width = rect.getWidth();
        float height = rect.getHeight();

        drawBorder(canvas, appearance, width, height);
        canvas.beginVariableText();

        Rectangle areaRect = new Rectangle(0, 0, width, height);
        Canvas modelCanvas = new Canvas(canvas, areaRect);
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);

        setMetaInfoToCanvas(modelCanvas);

        Paragraph paragraph = createParagraphForTextFieldValue(value).setFont(font)
                .setMargin(0)
                .setPadding(3)
                .setMultipliedLeading(1);
        if (getFontSize() == 0) {
            paragraph.setFontSize(FontSizeUtil.
                    approximateFontSizeToFitMultiLine(paragraph, areaRect, modelCanvas.getRenderer()));
        } else {
            paragraph.setFontSize(getFontSize());
        }
        paragraph.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
        paragraph.setTextAlignment(parent.getJustification());

        if (getColor() != null) {
            paragraph.setFontColor(getColor());
        }
        // here we subtract an epsilon to make sure that element won't be split but overflown
        paragraph.setHeight(height - 0.00001f);
        paragraph.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
        modelCanvas.add(paragraph);
        canvas.endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
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
     * Draws the appearance of a checkbox with a specified state value.
     *
     * @param width       the width of the checkbox to draw
     * @param height      the height of the checkbox to draw
     * @param onStateName the state of the form field that will be drawn
     */
    //TODO DEVSIX-7443 remove method
    protected void drawCheckAppearance(float width, float height, String onStateName) {
        Rectangle rect = new Rectangle(0, 0, width, height);

        PdfStream streamOn = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        drawBorder(canvasOn, xObjectOn, width, height);
        drawCheckBox(canvasOn, width, height, getFontSize());
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        xObjectOn.getResources().addFont(getDocument(), getFont());

        PdfStream streamOff = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);
        drawBorder(canvasOff, xObjectOff, width, height);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        xObjectOff.getResources().addFont(getDocument(), getFont());

        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(onStateName), xObjectOn.getPdfObject());
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(parent.text));

        PdfWidgetAnnotation widget = getWidget();
        widget.put(PdfName.MK, mk);
        widget.setNormalAppearance(normalAppearance);
    }

    /**
     * Draws PDF/A-2 compliant check appearance.
     * Actually it's just PdfA check appearance. According to corrigendum there is no difference between them
     *
     * @param width       width of the checkbox
     * @param height      height of the checkbox
     * @param onStateName name that corresponds to the "On" state of the checkbox
     * @param checkType   the type that determines how the checkbox will look like. Instance of {@link CheckBoxType}
     */
    //TODO DEVSIX-7443 remove method
    protected void drawPdfA2CheckAppearance(float width, float height, String onStateName, CheckBoxType checkType) {
        parent.checkType = checkType;
        Rectangle rect = new Rectangle(0, 0, width, height);

        PdfStream streamOn = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources(), getDocument());
        PdfFormXObject xObjectOn = new PdfFormXObject(rect);
        xObjectOn.getResources();

        drawBorder(canvasOn, xObjectOn, width, height);
        drawPdfACheckBox(canvasOn, width, height, true);
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());

        PdfStream streamOff = (PdfStream) new PdfStream().makeIndirect(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources(), getDocument());
        PdfFormXObject xObjectOff = new PdfFormXObject(rect);
        xObjectOff.getResources();

        drawBorder(canvasOff, xObjectOff, width, height);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());

        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(onStateName), xObjectOn.getPdfObject());
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        PdfDictionary mk = new PdfDictionary();
        mk.put(PdfName.CA, new PdfString(parent.text));

        PdfWidgetAnnotation widget = getWidget();
        widget.put(PdfName.MK, mk);
        widget.setNormalAppearance(normalAppearance);
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
            formFieldElement.setSingleLineValue(caption);
        }

        float imagePadding = borderColor == null ? 0 : borderWidth;
        if (parent.img != null) {
            // If we got here, the button will only contain the image that the user has set into the annotation.
            // There is no way to pass other elements with this image.
            formFieldElement.getChildren().clear();
            Image image = new Image(new PdfImageXObject(parent.img), imagePadding, imagePadding);
            image.setHeight(height - 2 * imagePadding);
            image.setWidth(width - 2 * imagePadding);
            formFieldElement.add(image);
        } else if (parent.form != null) {
            // If we got here, the button will only contain the image that the user has set as form into the annotation.
            // There is no way to pass other elements with this image as form.
            formFieldElement.getChildren().clear();
            Image image = new Image(parent.form, imagePadding, imagePadding);
            image.setHeight(height - 2 * imagePadding);
            formFieldElement.add(image);
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
     * Performs the low-level drawing operations to draw a checkbox object.
     *
     * @param canvas   the {@link PdfCanvas} of the page to draw on.
     * @param width    the width of the button
     * @param height   the width of the button
     * @param fontSize the size of the font
     */
    //TODO DEVSIX-7443 remove method
    protected void drawCheckBox(PdfCanvas canvas, float width, float height, float fontSize) {
        if (parent.checkType == CheckBoxType.CROSS) {
            DrawingUtil.drawCross(canvas, width, height, borderWidth);
            return;
        }
        PdfFont ufont = getFont();
        if (fontSize <= 0) {
            // there is no min font size for checkbox, however we can't set 0, because it means auto size.
            final float minFontSize = 0.1F;
            fontSize = FontSizeUtil.approximateFontSizeToFitSingleLine(ufont, new Rectangle(width, height),
                    parent.text, minFontSize, borderWidth);
        }
        // PdfFont gets all width in 1000 normalized units
        canvas.beginText().setFontAndSize(ufont, fontSize).resetFillColorRgb()
                .setTextMatrix((width - ufont.getWidth(parent.text, fontSize)) / 2,
                        (height - ufont.getAscent(parent.text, fontSize)) / 2).showText(parent.text).endText();
    }

    /**
     * Performs the low-level drawing operations to draw a PDF A complication checkbox.
     *
     * @param canvas the {@link PdfCanvas} of the page to draw on.
     * @param width  the width of the button
     * @param height the height of the button
     * @param on     the state of the checkbox
     */
    protected void drawPdfACheckBox(PdfCanvas canvas, float width, float height, boolean on) {
        //TODO DEVSIX-7443 remove method
        if (!on) {
            return;
        }
        switch (parent.checkType) {
            case CHECK:
                DrawingUtil.drawPdfACheck(canvas, width, height);
                break;
            case CIRCLE:
                DrawingUtil.drawPdfACircle(canvas, width, height);
                break;
            case CROSS:
                DrawingUtil.drawPdfACross(canvas, width, height);
                break;
            case DIAMOND:
                DrawingUtil.drawPdfADiamond(canvas, width, height);
                break;
            case SQUARE:
                DrawingUtil.drawPdfASquare(canvas, width, height);
                break;
            case STAR:
                DrawingUtil.drawPdfAStar(canvas, width, height);
                break;
        }
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

        Radio formField = createRadio();
        // First draw off appearance
        formField.setChecked(false);
        PdfFormXObject xObjectOff = new PdfFormXObject(
                new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
        Canvas canvasOff = new Canvas(xObjectOff, this.getDocument());
        canvasOff.add(formField);
        PdfDictionary normalAppearance = new PdfDictionary();
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        // Draw on appearance
        if (value != null && !value.isEmpty() && !PdfFormAnnotation.OFF_STATE_VALUE.equals(value)) {
            formField.setChecked(true);
            PdfFormXObject xObject = new PdfFormXObject(
                    new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
            Canvas canvas = new Canvas(xObject, this.getDocument());
            canvas.add(formField);
            normalAppearance.put(new PdfName(value), xObject.getPdfObject());
        }

        getWidget().setNormalAppearance(normalAppearance);
    }

    /**
     * Draws the appearance of a text form field with and saves it into an appearance stream.
     */
    protected void drawTextFormFieldAndSaveAppearance() {
        Rectangle rectangle = getRect(this.getPdfObject());
        if (rectangle == null) {
            return;
        }

        IFormField textFormField;
        if (parent.isMultiline()) {
            textFormField = new TextArea(getParentField().getPartialFieldName().toUnicodeString());
            textFormField.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(getFontSize()));
        } else {
            textFormField = new InputField(getParentField().getPartialFieldName().toUnicodeString());
            textFormField.setProperty(Property.FONT_SIZE,
                    UnitValue.createPointValue(getFontSize(new PdfArray(rectangle), parent.getValueAsString())));
        }
        textFormField.setProperty(FormProperty.FORM_FIELD_VALUE, parent.getDisplayValue());
        textFormField.setProperty(Property.FONT, getFont());
        textFormField.setProperty(Property.TEXT_ALIGNMENT, parent.getJustification());
        textFormField.setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, getParentField().isPassword());
        textFormField.setProperty(Property.ADD_MARKED_CONTENT_TEXT, true);
        if (getColor() != null) {
            textFormField.setProperty(Property.FONT_COLOR, new TransparentColor(getColor()));
        }

        textFormField.setProperty(Property.BORDER, getBorder());

        if (backgroundColor != null) {
            textFormField.setProperty(Property.BACKGROUND, new Background(backgroundColor, 1f, 0, 0, 0, 0));
        }

        // Always flatten
        textFormField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);

        // Rotation
        final int fieldRotation = getRotation() % 360;
        PdfArray matrix = getRotationMatrix(fieldRotation, rectangle.getHeight(), rectangle.getWidth());
        if (fieldRotation == 90 || fieldRotation == 270) {
            Rectangle invertedRectangle = rectangle.clone();
            invertedRectangle.setWidth(rectangle.getHeight());
            invertedRectangle.setHeight(rectangle.getWidth());
            rectangle = invertedRectangle;
        }

        // Set fixed size
        textFormField.setProperty(Property.WIDTH, UnitValue.createPointValue(rectangle.getWidth()));
        textFormField.setProperty(Property.HEIGHT, UnitValue.createPointValue(rectangle.getHeight()));

        PdfFormXObject xObject = new PdfFormXObject(
                new Rectangle(0, 0, rectangle.getWidth(), rectangle.getHeight()));
        if (matrix != null) {
            xObject.put(PdfName.Matrix, matrix);
        }
        Canvas canvas = new Canvas(xObject, this.getDocument());
        canvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);
        canvas.add(textFormField);

        getWidget().setNormalAppearance(xObject.getPdfObject());
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
        }
    }

    static void setMetaInfoToCanvas(Canvas canvas) {
        MetaInfoContainer metaInfo = FormsMetaInfoStaticContainer.getMetaInfoForLayout();
        if (metaInfo != null) {
            canvas.setProperty(Property.META_INFO, metaInfo);
        }
    }

    //TODO DEVSIX-7443 remove method
    void regenerateCheckboxField(CheckBoxType checkType) {
        parent.setCheckType(checkType);
        final String value = parent.getValueAsString();
        Rectangle rect = getRect(getPdfObject());

        PdfWidgetAnnotation widget = (PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject());
        if (getPdfAConformanceLevel() == null) {
            drawCheckAppearance(rect.getWidth(), rect.getHeight(),
                    OFF_STATE_VALUE.equals(value) ? ON_STATE_VALUE : value);
        } else {
            drawPdfA2CheckAppearance(rect.getWidth(), rect.getHeight(),
                    OFF_STATE_VALUE.equals(value) ? ON_STATE_VALUE : value, parent.checkType);
            widget.setFlag(PdfAnnotation.PRINT);
        }

        if (widget.getNormalAppearanceObject() != null &&
                widget.getNormalAppearanceObject().containsKey(new PdfName(value))) {
            widget.setAppearanceState(new PdfName(value));
        } else {
            widget.setAppearanceState(new PdfName(OFF_STATE_VALUE));
        }
    }

    boolean regenerateTextAndChoiceField() {
        String value = parent.getDisplayValue();
        final PdfName type = parent.getFormType();

        PdfPage page = PdfAnnotation.makeAnnotation(getPdfObject()).getPage();
        PdfArray bBox = getPdfObject().getAsArray(PdfName.Rect);

        //Apply Page rotation
        int pageRotation = 0;
        if (page != null) {
            pageRotation = page.getRotation();
            //Clockwise, so negative
            pageRotation *= -1;
        }
        PdfArray matrix;
        if (pageRotation % 90 == 0) {
            //Cast angle to [-360, 360]
            double angle = pageRotation % 360;
            //Get angle in radians
            angle = degreeToRadians(angle);
            Rectangle initialBboxRectangle = bBox.toRectangle();
            //rotate the bounding box
            Rectangle rect = initialBboxRectangle.clone();
            //Calculate origin offset
            double translationWidth = 0;
            double translationHeight = 0;
            if (angle >= -1 * Math.PI && angle <= -1 * Math.PI / 2) {
                translationWidth = rect.getWidth();
            }
            if (angle <= -1 * Math.PI) {
                translationHeight = rect.getHeight();
            }

            //Store rotation and translation in the matrix
            matrix = new PdfArray(
                    new double[] {Math.cos(angle), -Math.sin(angle), Math.sin(angle), Math.cos(angle), translationWidth,
                            translationHeight});
            // If the angle is a multiple of 90 and not a multiple of 180, height and width of the bounding box
            // need to be switched
            if (angle % (Math.PI / 2) == 0 && angle % (Math.PI) != 0) {
                rect.setWidth(initialBboxRectangle.getHeight());
                rect.setHeight(initialBboxRectangle.getWidth());
            }
            // Adapt origin
            rect.setX(rect.getX() + (float) translationWidth);
            rect.setY(rect.getY() + (float) translationHeight);
            //Copy Bounding box
            bBox = new PdfArray(rect);
        } else {
            //Avoid NPE when handling corrupt pdfs
            LOGGER.error(FormsLogMessageConstants.INCORRECT_PAGE_ROTATION);
            matrix = new PdfArray(new double[] {1, 0, 0, 1, 0, 0});
        }
        //Apply field rotation
        float fieldRotation = 0;
        if (this.getPdfObject().getAsDictionary(PdfName.MK) != null
                && this.getPdfObject().getAsDictionary(PdfName.MK).get(PdfName.R) != null) {
            fieldRotation = (float) this.getPdfObject().getAsDictionary(PdfName.MK).getAsFloat(PdfName.R);
            //Get relative field rotation
            fieldRotation += pageRotation;
        }
        if (fieldRotation % 90 == 0) {
            Rectangle initialBboxRectangle = bBox.toRectangle();
            //Cast angle to [-360, 360]
            double angle = fieldRotation % 360;
            //Get angle in radians
            angle = degreeToRadians(angle);
            //Calculate origin offset
            double translationWidth = calculateTranslationWidthAfterFieldRot(initialBboxRectangle,
                    degreeToRadians(pageRotation), angle);
            double translationHeight = calculateTranslationHeightAfterFieldRot(initialBboxRectangle,
                    degreeToRadians(pageRotation), angle);

            //Concatenate rotation and translation into the matrix
            Matrix currentMatrix = new Matrix(matrix.getAsNumber(0).floatValue(),
                    matrix.getAsNumber(1).floatValue(),
                    matrix.getAsNumber(2).floatValue(),
                    matrix.getAsNumber(3).floatValue(),
                    matrix.getAsNumber(4).floatValue(),
                    matrix.getAsNumber(5).floatValue());
            Matrix toConcatenate = new Matrix((float) Math.cos(angle),
                    (float) (-Math.sin(angle)),
                    (float) (Math.sin(angle)),
                    (float) (Math.cos(angle)),
                    (float) translationWidth,
                    (float) translationHeight);
            currentMatrix = currentMatrix.multiply(toConcatenate);
            matrix = new PdfArray(
                    new float[] {currentMatrix.get(0), currentMatrix.get(1), currentMatrix.get(3), currentMatrix.get(4),
                            currentMatrix.get(6), currentMatrix.get(7)});

            // Construct bounding box
            Rectangle rect = initialBboxRectangle.clone();
            // If the angle is a multiple of 90 and not a multiple of 180, height and width of the bounding box
            // need to be switched
            if (angle % (Math.PI / 2) == 0 && angle % (Math.PI) != 0) {
                rect.setWidth(initialBboxRectangle.getHeight());
                rect.setHeight(initialBboxRectangle.getWidth());
            }
            rect.setX(rect.getX() + (float) translationWidth);
            rect.setY(rect.getY() + (float) translationHeight);
            // Copy Bounding box
            bBox = new PdfArray(rect);
        }
        // Create appearance
        Rectangle bboxRectangle = bBox.toRectangle();
        PdfFormXObject appearance = new PdfFormXObject(new Rectangle(0, 0, bboxRectangle.getWidth(),
                bboxRectangle.getHeight()));
        appearance.put(PdfName.Matrix, matrix);
        //Create text appearance
        if (PdfName.Tx.equals(type)) {
            if (parent.isMultiline()) {
                drawMultiLineTextAppearance(bboxRectangle, getFont(), value, appearance);
            } else {
                drawTextAppearance(bboxRectangle, getFont(), getFontSize(bBox, value), value, appearance);
            }
        }
        PdfDictionary ap = new PdfDictionary();
        ap.put(PdfName.N, appearance.getPdfObject());
        ap.setModified();
        put(PdfName.AP, ap);

        return true;
    }

    boolean regenerateWidget() {
        if (parent == null) {
            return true;
        }
        final PdfName type = parent.getFormType();

        if (PdfName.Ch.equals(type) || this.isCombTextFormField()) {
            return TextAndChoiceLegacyDrawer.regenerateTextAndChoiceField(this);
        } else if (PdfName.Tx.equals(type)) {
            if (ExperimentalFeatures.ENABLE_EXPERIMENTAL_TEXT_FORM_RENDERING) {
                drawTextFormFieldAndSaveAppearance();
                return true;
            } else {
                return regenerateTextAndChoiceField();
            }
        } else if (PdfName.Btn.equals(type)) {
            if (parent.getFieldFlag(PdfButtonFormField.FF_PUSH_BUTTON)) {
                drawPushButtonFieldAndSaveAppearance();
            } else if (parent.getFieldFlag(PdfButtonFormField.FF_RADIO)) {
                drawRadioButtonAndSaveAppearance(getRadioButtonValue());
            } else {
                //TODO DEVSIX-7443 remove flag
                if (ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
                    drawCheckBoxAndSaveAppearanceExperimental(parent.getValueAsString());
                } else {
                    //TODO DEVSIX-7443 remove method
                    regenerateCheckboxField(parent.checkType);
                }
            }
            return true;
        }
        return false;
    }

    void createInputButton() {
        final Rectangle rect = getRect(getPdfObject());
        if (rect == null) {
            formFieldElement = null;
            return;
        }

        if (formFieldElement == null) {
            // Create it one time and re-set properties during each widget regeneration.
            formFieldElement = new Button(parent.getFieldName().toUnicodeString());
        }

        formFieldElement.setFont(getFont());
        formFieldElement.setFontSize(getFontSize(getPdfObject()
                .getAsArray(PdfName.Rect), parent.getDisplayValue()));
        if (getColor() == null) {
            final TransparentColor transparentColor =
                    formFieldElement.<TransparentColor>getProperty(Property.FONT_COLOR);
            color = transparentColor == null ? ColorConstants.BLACK : transparentColor.getColor();
        }
        formFieldElement.setFontColor(color);

        formFieldElement.setBackgroundColor(backgroundColor);
        if (borderWidth > 0 && borderColor != null) {
            final float borderWidth = Math.max(1, getBorderWidth());
            // Don't take border into account as it will be drawn inside
            Border border = FormBorderFactory.getBorder(getWidget().getBorderStyle(),
                    borderWidth, borderColor, backgroundColor);
            formFieldElement.setBorder(border != null ? border : new SolidBorder(borderColor, borderWidth));
        }

        // Set fixed size
        formFieldElement.setProperty(Property.WIDTH, UnitValue.createPointValue(rect.getWidth()));
        formFieldElement.setProperty(Property.HEIGHT, UnitValue.createPointValue(rect.getHeight()));

        // Always flatten
        formFieldElement.setInteractive(false);
    }

    Radio createRadio() {
        final Rectangle rect = getRect(getPdfObject());
        if (rect == null) {
            return null;
        }

        // id doesn't matter here
        Radio radio = new Radio("");

        // Border
        if (getBorderWidth() > 0 && borderColor != null) {
            Border border = new SolidBorder(Math.max(1, getBorderWidth()));
            border.setColor(borderColor);
            radio.setBorder(border);
        }

        if (backgroundColor != null) {
            radio.setBackgroundColor(backgroundColor);
        }

        // Set fixed size
        radio.setProperty(Property.WIDTH, UnitValue.createPointValue(rect.getWidth()));
        radio.setProperty(Property.HEIGHT, UnitValue.createPointValue(rect.getHeight()));

        // Always flatten
        radio.setInteractive(false);

        return radio;
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

    private static double degreeToRadians(double angle) {
        return Math.PI * angle / 180.0;
    }

    private static Paragraph createParagraphForTextFieldValue(String value) {
        Text text = new Text(value);
        text.setNextRenderer(new FormFieldValueNonTrimmingTextRenderer(text));
        return new Paragraph(text);
    }

    private boolean isCombTextFormField() {
        final PdfName type = parent.getFormType();
        if (PdfName.Tx.equals(type) && parent.getFieldFlag(PdfTextFormField.FF_COMB)) {
            int maxLen = new PdfTextFormField(parent.getPdfObject()).getMaxLen();
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

    /**
     * Calculate the necessary height offset after applying field rotation
     * so that the origin of the bounding box is the lower left corner with respect to the field text.
     *
     * @param bBox             bounding box rectangle before rotation
     * @param pageRotation     rotation of the page
     * @param relFieldRotation rotation of the field relative to the page
     *
     * @return translation value for height
     */
    private static float calculateTranslationHeightAfterFieldRot(Rectangle bBox, double pageRotation,
            double relFieldRotation) {
        if (relFieldRotation == 0) {
            return 0.0f;
        }
        if (pageRotation == 0) {
            if (relFieldRotation == Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI) {
                return bBox.getHeight();
            }

        }
        if (pageRotation == -Math.PI / 2) {
            if (relFieldRotation == -Math.PI / 2) {
                return bBox.getWidth() - bBox.getHeight();
            }
            if (relFieldRotation == Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI) {
                return bBox.getWidth();
            }

        }
        if (pageRotation == -Math.PI) {
            if (relFieldRotation == -1 * Math.PI) {
                return bBox.getHeight();
            }
            if (relFieldRotation == -1 * Math.PI / 2) {
                return bBox.getHeight() - bBox.getWidth();
            }

            if (relFieldRotation == Math.PI / 2) {
                return bBox.getWidth();
            }
        }
        if (pageRotation == -3 * Math.PI / 2) {
            if (relFieldRotation == -3 * Math.PI / 2) {
                return bBox.getWidth();
            }
            if (relFieldRotation == -Math.PI) {
                return bBox.getWidth();
            }
        }

        return 0.0f;
    }

    /**
     * Calculate the necessary width offset after applying field rotation
     * so that the origin of the bounding box is the lower left corner with respect to the field text.
     *
     * @param bBox             bounding box rectangle before rotation
     * @param pageRotation     rotation of the page
     * @param relFieldRotation rotation of the field relative to the page
     *
     * @return translation value for width
     */
    private static float calculateTranslationWidthAfterFieldRot(Rectangle bBox, double pageRotation,
            double relFieldRotation) {
        if (relFieldRotation == 0) {
            return 0.0f;
        }
        if (pageRotation == 0 && (relFieldRotation == Math.PI || relFieldRotation == 3 * Math.PI / 2)) {
            return bBox.getWidth();
        }
        if (pageRotation == -Math.PI / 2) {
            if (relFieldRotation == -Math.PI / 2 || relFieldRotation == Math.PI) {
                return bBox.getHeight();
            }
        }

        if (pageRotation == -Math.PI) {
            if (relFieldRotation == -1 * Math.PI) {
                return bBox.getWidth();
            }
            if (relFieldRotation == -1 * Math.PI / 2) {
                return bBox.getHeight();
            }
            if (relFieldRotation == Math.PI / 2) {
                return -1 * (bBox.getHeight() - bBox.getWidth());
            }
        }
        if (pageRotation == -3 * Math.PI / 2) {
            if (relFieldRotation == -3 * Math.PI / 2) {
                return -1 * (bBox.getWidth() - bBox.getHeight());
            }
            if (relFieldRotation == -Math.PI) {
                return bBox.getHeight();
            }
            if (relFieldRotation == -Math.PI / 2) {
                return bBox.getWidth();
            }
        }
        return 0.0f;
    }

    private static String obfuscatePassword(String text) {
        char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            pchar[i] = '*';
        }
        return new String(pchar);
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

    /**
     * Experimental method to draw a checkbox and save its appearance.
     *
     * @param onStateName the name of the appearance state for the checked state
     */
    //TODO DEVSIX-7443 rename experimental method
    protected void drawCheckBoxAndSaveAppearanceExperimental(String onStateName) {
        final Rectangle rect = getRect(this.getPdfObject());
        if (rect == null) {
            return;
        }
        final CheckBox formField = createCheckBox();
        if (formField == null) {
            return;
        }
        if (getWidget().getNormalAppearanceObject() == null) {
            getWidget().setNormalAppearance(new PdfDictionary());
        }
        final PdfDictionary normalAppearance = getWidget().getNormalAppearanceObject();
        formField.setChecked(false);
        final PdfFormXObject xObjectOff = new PdfFormXObject(new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
        final Canvas canvasOff = new Canvas(xObjectOff, getDocument());
        canvasOff.add(formField);
        xObjectOff.getResources().addFont(getDocument(), getFont());
        normalAppearance.put(new PdfName(OFF_STATE_VALUE), xObjectOff.getPdfObject());

        if (onStateName != null && !onStateName.isEmpty() && !PdfFormAnnotation.OFF_STATE_VALUE.equals(onStateName)) {
            formField.setChecked(true);
            final PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, rect.getWidth(), rect.getHeight()));
            final Canvas canvas = new Canvas(xObject, this.getDocument());
            canvas.add(formField);
            normalAppearance.put(new PdfName(onStateName), xObject.getPdfObject());
        }

        if (getWidget().getAppearanceCharacteristics() == null) {
            getWidget().setAppearanceCharacteristics(new PdfDictionary());
        }

        final PdfDictionary mk = getWidget().getAppearanceCharacteristics();
        if (parent.text != null) {
            mk.put(PdfName.CA, new PdfString(parent.text));
        }
        setCheckBoxAppearanceState(onStateName);

    }

    private void setCheckBoxAppearanceState(String onStateName) {
        final PdfWidgetAnnotation widget = getWidget();
        if (widget.getNormalAppearanceObject() != null && widget.getNormalAppearanceObject()
                .containsKey(new PdfName(onStateName))) {
            widget.setAppearanceState(new PdfName(onStateName));
        } else {
            widget.setAppearanceState(new PdfName(OFF_STATE_VALUE));
        }
        // Remove appearance state if it is not used
        final int amountOfAppearanceStatesAllowed = 2;
        if (widget.getNormalAppearanceObject().keySet().size() > amountOfAppearanceStatesAllowed
                && !ON_STATE_VALUE.equals(onStateName)) {
            widget.getNormalAppearanceObject().remove(new PdfName(ON_STATE_VALUE));
        }
    }

    private CheckBox createCheckBox() {
        Rectangle rect = getRect(getPdfObject());
        if (rect == null) {
            return null;
        }
        rect = rect.clone();
        final CheckBox checkBox = new CheckBox("");
        if (getBorderWidth() > 0 && borderColor != null) {
            float borderWidth = getBorderWidth();
            final PdfDictionary bs = getWidget().getBorderStyle();
            Border border = FormBorderFactory.getBorder(bs, borderWidth, borderColor, backgroundColor);
            if (border == null) {
                // TODO DEVSIX-7443 1 is copied from the previous implementation, but it is not clear why it is needed
                // we should actually check if we want to use a fallback value or if we would allow the user to have a
                // very thin border
                borderWidth = Math.max(1, getBorderWidth());
                border = new SolidBorder(borderColor, borderWidth);
            }
            checkBox.setBorder(border);
        }

        if (backgroundColor != null) {
            checkBox.setBackgroundColor(backgroundColor);
        }

        //make font size auto calculated
        checkBox.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(getFontSize()));
        // Set fixed size
        checkBox.setProperty(Property.WIDTH, UnitValue.createPointValue(rect.getWidth()));
        checkBox.setProperty(Property.HEIGHT, UnitValue.createPointValue(rect.getHeight()));
        // Always flatten

        checkBox.setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
        checkBox.setPdfAConformanceLevel(getPdfAConformanceLevel());
        checkBox.setCheckBoxType(parent.checkType);
        return checkBox;
    }
}
