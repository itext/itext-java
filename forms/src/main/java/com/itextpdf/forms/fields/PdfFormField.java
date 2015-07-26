package com.itextpdf.forms.fields;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class PdfFormField extends PdfObjectWrapper<PdfDictionary> {

    protected static final int DEFAULT_FONT_SIZE = 12;

    public PdfFormField(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
        put(PdfName.FT, getFormType());
    }

    public PdfFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        this(new PdfDictionary(), pdfDocument);
        addKid(widget);
        put(PdfName.FT, getFormType());
    }

    protected PdfFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    /**
     * Makes a field flag by bit position. Bit positions are numbered 1 to 32.
     * But position 1 corresponds to flag 1, position 3 corresponds to flag 4 etc.
     * @param bitPosition bit position of a flag in range 1 to 32 from the pdf specification.
     * @return corresponding field flag.
     */
    public static int makeFieldFlag(int bitPosition) {
        return (1 << (bitPosition - 1));
    }

    public static PdfFormField createEmptyField(PdfDocument doc) {
        PdfFormField field = new PdfFormField(doc);
        return field;
    }

    public static PdfButtonFormField createButton(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField field = new PdfButtonFormField(doc, annot);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfButtonFormField createButton(PdfDocument doc, int flags) {
        PdfButtonFormField field = new PdfButtonFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfTextFormField createText(PdfDocument doc) {
        return new PdfTextFormField(doc);
    }

    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfTextFormField field = new PdfTextFormField(doc, annot);

        return field;

    }

    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, String value, String name) {
        try{
            return createText(doc, rect, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE, value, name);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    public static PdfTextFormField createText(PdfDocument doc, Rectangle rect, PdfFont font, int fontSize, String value, String name) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfTextFormField field = new PdfTextFormField(doc, annot);
        field.setValue(new PdfString(value));
        field.setFieldName(name);

        annot.setNormalAppearance(field.drawTextAppearance(rect, font, fontSize, value).getPdfObject());

        return field;
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, int flags) {
        PdfChoiceFormField field = new PdfChoiceFormField(doc);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfChoiceFormField field = new PdfChoiceFormField(doc, annot);
        field.setFieldFlags(flags);
        return field;
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, PdfArray options, String value, String name, int flags) {
        try{
            return createChoice(doc, rect, options, value, name, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE, flags);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
    }

    public static PdfChoiceFormField createChoice(PdfDocument doc, Rectangle rect, PdfArray options, String value, String name, PdfFont font, int fontSize, int flags) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfChoiceFormField field = new PdfChoiceFormField(doc, annot);
        field.put(PdfName.Opt, options);
        field.setFieldFlags(flags);
        field.setFieldName(name);
        field.setValue(new PdfString(value));

        annot.setNormalAppearance(field.drawTextAppearance(rect, font, fontSize, value).getPdfObject());

        return field;
    }

    public static PdfSignatureFormField createSignature(PdfDocument doc) {
        return new PdfSignatureFormField(doc);
    }

    public static PdfSignatureFormField createSignature(PdfDocument doc, Rectangle rect) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        return new PdfSignatureFormField(doc, annot);
    }

    public static PdfButtonFormField createRadioGroup(PdfDocument doc, String name, String defaultValue) {
        PdfButtonFormField radio = createButton(doc, PdfButtonFormField.FF_RADIO);
        radio.setFieldName(name);
        radio.setValue(new PdfName(defaultValue));
        return radio;
    }

    public static PdfFormField createRadioButton(PdfDocument doc, Rectangle rect, PdfButtonFormField radioGroup, String value) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfFormField radio = new PdfFormField(doc, annot);
        String name = radioGroup.getValue().toString().substring(1);
        if (name.equals(value)) {
            annot.setAppearanceState(new PdfName(value));
        } else {
            annot.setAppearanceState(new PdfName("Off"));
        }
        radio.drawRadioAppearance(rect.getWidth(), rect.getHeight(), value);
        radioGroup.addKid(radio);
        return radio;
    }

    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption) {
        PdfButtonFormField field;
        try {
            field = createPushButton(doc, rect, name, caption, PdfFont.getDefaultFont(doc), DEFAULT_FONT_SIZE);
        } catch (IOException e) {
            throw new PdfException(e.getLocalizedMessage());
        }
        return field;
    }

    public static PdfButtonFormField createPushButton(PdfDocument doc, Rectangle rect, String name, String caption, PdfFont font, int fontSize) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField field = new PdfButtonFormField(doc, annot);
        field.setPushButton(true);
        field.setFieldName(name);

        PdfFormXObject xObject = field.drawButtonAppearance(rect.getWidth(), rect.getHeight(), caption, font, fontSize);
        annot.setNormalAppearance(xObject.getPdfObject());

        return field;
    }

    public static PdfButtonFormField createCheckBox(PdfDocument doc, Rectangle rect, String value, String name) {
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        PdfButtonFormField check = new PdfButtonFormField(doc, annot);
        check.setFieldName(name);
        check.setValue(new PdfName(value));
        annot.setAppearanceState(new PdfName(value));
        check.drawCheckAppearance(rect.getWidth(), rect.getHeight(), value);

        return check;
    }

    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[][], String value, String name) {
        return createChoice(doc, rect, processOptions(options), value, name, PdfChoiceFormField.FF_COMBO);
    }

    public static PdfChoiceFormField createComboBox(PdfDocument doc, Rectangle rect, String options[], String value, String name) {
        return createChoice(doc, rect, processOptions(options), value, name, PdfChoiceFormField.FF_COMBO);
    }

    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[][], String value, String name) {
        StringBuffer text = new StringBuffer();
        for (String[] option : options) {
            text.append(option[1]).append('\n');
        }
        return createChoice(doc, rect, processOptions(options), text.toString(), name, 0);
    }

    public static PdfChoiceFormField createList(PdfDocument doc, Rectangle rect, String options[], String value, String name) {
        StringBuffer text = new StringBuffer();
        for (String option : options) {
            text.append(option).append('\n');
        }
        return createChoice(doc, rect, processOptions(options), text.toString(), name, 0);
    }

    public static <T extends PdfFormField> T makeFormField(PdfObject pdfObject, PdfDocument document) {
        T field = null;
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName formType = dictionary.getAsName(PdfName.FT);
            if (PdfName.Tx.equals(formType))
                field = (T) new PdfTextFormField(dictionary, document);
            else if (PdfName.Btn.equals(formType))
                field = (T) new PdfButtonFormField(dictionary, document);
            else if (PdfName.Ch.equals(formType))
                field = (T) new PdfChoiceFormField(dictionary, document);
            else if (PdfName.Sig.equals(formType))
                field = (T) new PdfSignatureFormField(dictionary, document);
            else
                field = (T) new PdfFormField(dictionary, document);
        }

        return field;
    }

    public PdfName getFormType() {
        return null;
    }

    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    public <T extends PdfFormField> T setParent(PdfFormField parent) {
        return put(PdfName.Parent, parent);
    }

    public PdfDictionary getParent() {
        return getPdfObject().getAsDictionary(PdfName.Parent);
    }

    public PdfArray getKids() {
        return getPdfObject().getAsArray(PdfName.Kids);
    }

    public <T extends PdfFormField> T addKid(PdfFormField kid) {
        kid.setParent(this);
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        return put(PdfName.Kids, kids);
    }

    public <T extends PdfFormField> T addKid(PdfWidgetAnnotation kid) {
        kid.setParent(getPdfObject());
        PdfArray kids = getKids();
        if (kids == null) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        return put(PdfName.Kids, kids);
    }

    public <T extends PdfFormField> T setFieldName(String name) {
        return put(PdfName.T, new PdfString(name));
    }

    public PdfString getFieldName() {
        return getPdfObject().getAsString(PdfName.T);
    }

    public <T extends PdfFormField> T setAlternativeName(String name) {
        return put(PdfName.TU, new PdfString(name));
    }

    public PdfString getAlternativeName() {
        return getPdfObject().getAsString(PdfName.TU);
    }

    public <T extends PdfFormField> T setMappingName(String name) {
        return put(PdfName.TM, new PdfString(name));
    }

    public PdfString getMappingName() {
        return getPdfObject().getAsString(PdfName.TM);
    }

    public boolean getFieldFlag(int flag) {
        return (getFieldFlags() & flag) != 0;
    }

    public <T extends PdfFormField> T setFieldFlag(int flag) {
        return setFieldFlag(flag, true);
    }

    public <T extends PdfFormField> T setFieldFlag(int flag, boolean value) {
        int flags = getFieldFlags();

        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }

        return setFieldFlags(flags);
    }

    public <T extends PdfFormField> T setFieldFlags(int flags) {
        return put(PdfName.Ff, new PdfNumber(flags));
    }

    public int getFieldFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.Ff);
        if (f != null)
            return f.getIntValue();
        else
            return 0;
    }

    public PdfObject getValue() {
        return getPdfObject().get(PdfName.V);
    }

    public <T extends PdfFormField> T setDefaultValue(PdfObject value) {
        return put(PdfName.DV, value);
    }

    public PdfObject getDefaultValue() {
        return getPdfObject().get(PdfName.DV);
    }

    public <T extends PdfFormField> T setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    public PdfDictionary getAdditionalAction() {
        return getPdfObject().getAsDictionary(PdfName.AA);
    }

    public <T extends PdfFormField> T setOptions(PdfArray options) {
        return put(PdfName.Opt, options);
    }

    public PdfArray getOptions() {
        return getPdfObject().getAsArray(PdfName.Opt);
    }

    public List<PdfWidgetAnnotation> getWidgets() {
        List<PdfWidgetAnnotation> widgets = new ArrayList<>();

        PdfName subType = getPdfObject().getAsName(PdfName.Subtype);
        if (subType != null && subType.equals(PdfName.Widget)) {
            widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(getPdfObject(), getDocument()));
        }

        PdfArray kids = getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                subType = ((PdfDictionary)kid).getAsName(PdfName.Subtype);
                if (subType != null && subType.equals(PdfName.Widget)) {
                    widgets.add((PdfWidgetAnnotation) PdfAnnotation.makeAnnotation(kid, getDocument()));
                }
            }
        }

        return widgets;
    }

    /**
     * Gets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field’s text size and color.
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * Sets default appearance string containing a sequence of valid page-content graphics or text state operators that
     * define such properties as the field’s text size and color.
     */
    public <T extends PdfFormField> T setDefaultAppearance(PdfString defaultAppearance) {
        getPdfObject().put(PdfName.DA, defaultAppearance);
        return (T) this;
    }

    /**
     * Gets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     */
    public Integer getJustification() {
        return getPdfObject().getAsInt(PdfName.Q);
    }

    /**
     * Sets a code specifying the form of quadding (justification) to be used in displaying the text:
     * 0 Left-justified
     * 1 Centered
     * 2 Right-justified
     */
    public <T extends PdfFormField> T setJustification(int justification) {
        getPdfObject().put(PdfName.Q, new PdfNumber(justification));
        return (T) this;
    }

    /**
     * Gets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     */
    public PdfString getDefaultStyle() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    /**
     * Sets a default style string, as described in "Rich Text Strings" section of Pdf spec.
     */
    public <T extends PdfFormField> T setDefaultStyle(PdfString defaultStyleString) {
        getPdfObject().put(PdfName.DS, defaultStyleString);
        return (T) this;
    }

    /**
     * Gets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     */
    public PdfObject getRichText() {
        return getPdfObject().get(PdfName.RV);
    }

    /**
     * Sets a rich text string, as described in "Rich Text Strings" section of Pdf spec.
     * May be either {@link PdfStream} or {@link PdfString}.
     */
    public <T extends PdfFormField> T setRichText(PdfObject richText) {
        getPdfObject().put(PdfName.RV, richText);
        return (T) this;
    }

    public PdfFormXObject drawTextAppearance(Rectangle rect, PdfFont font, int fontSize, String value) {
        PdfStream stream = new PdfStream(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources());
        float width = rect.getWidth();
        float height = rect.getHeight();

        drawTextField(canvas, 0, 0, width, height);
        canvas.
                beginVariableText().
                saveState().
                rectangle(3, 3, width - 6, height - 6).
                clip().
                newPath().
                beginText().
                setFontAndSize(font, fontSize).
                resetFillColorRgb().
                setTextMatrix(4, 5);
        StringTokenizer tokenizer = new StringTokenizer(value, "\n");
        while (tokenizer.hasMoreTokens()) {
            height -= fontSize * 1.2;
            canvas.
                    setTextMatrix(3, height).
                    showText(tokenizer.nextToken());
        }
        canvas.
                endText().
                restoreState().
                endVariableText();


        PdfFormXObject xObject = new PdfFormXObject(getDocument(), rect);
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());
        xObject.getResources().addFont(font);

        return xObject;
    }

    public void drawTextField(PdfCanvas canvas, float x, float y, float width, float height) {

        float upperRightX = x + width;
        float upperRightY = y + height;

        canvas.
                saveState().
                setStrokeColor(Color.Silver).
                setLineWidth(1).
                setLineCapStyle(0).
                rectangle(x, y, width, height).
                stroke().
                setLineWidth(1).
                setLineCapStyle(0).
                setFillColor(Color.White).
                rectangle(x + 0.5f, y + 0.5f, width - 1, height - 1).
                fill().
                setStrokeColor(Color.Silver).
                setLineWidth(1).
                setLineCapStyle(0).
                moveTo(x + 1, y + 1.5f).
                lineTo(upperRightX - 1.5f, y + 1.5f).
                lineTo(upperRightX - 1.5f, upperRightY - 1).
                stroke().
                setStrokeColor(Color.Gray).
                setLineWidth(1).
                setLineCapStyle(0).
                moveTo(x + 1, y + 1).
                lineTo(x + 1, upperRightY - 1).
                lineTo(upperRightX - 1, upperRightY - 1).
                stroke().
                setStrokeColor(Color.Black).
                setLineWidth(1).
                setLineCapStyle(0).
                moveTo(x + 2, y + 2).
                lineTo(x + 2, upperRightY - 2).
                lineTo(upperRightX - 2, upperRightY - 2).
                stroke().
                restoreState();
    }

    public void drawRadioAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources());
        drawRadioField(canvasOn, 0, 0, width, height, true);

        PdfStream streamOff = new PdfStream(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources());
        drawRadioField(canvasOff, 0, 0, width, height, false);

        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfWidgetAnnotation widget = getWidgets().get(0);
        PdfFormXObject xObjectOn = new PdfFormXObject(getDocument(), rect);
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        PdfFormXObject xObjectOff = new PdfFormXObject(getDocument(), rect);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }

    public void drawRadioField(PdfCanvas canvas, final float x, final float y, final float width, final float height, final boolean on) {
        canvas.
                saveState().
                setLineWidth(1).
                setLineCapStyle(1).
                setStrokeColor(Color.Silver).
                arc(x + 1, y + 1, x + width - 1, y + height - 1, 0, 360).
                stroke().
                setLineWidth(1).
                setLineCapStyle(1).
                setStrokeColor(Color.Gray).
                arc(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, 45, 180).
                stroke().
                setLineWidth(1).
                setLineCapStyle(1).
                setStrokeColor(Color.Black).
                arc(x + 1.5f, y + 1.5f, x + width - 1.5f, y + height - 1.5f, 45, 180).
                stroke();
        if (on) {
            canvas.
                    setLineWidth(1).
                    setLineCapStyle(1).
                    setFillColor(Color.Black).
                    arc(x + 4, y + 4, x + width - 4, y + height - 4, 0, 360).
                    fill();
        }
        canvas.restoreState();
    }

    public void drawCheckAppearance(float width, float height, String value) {
        PdfStream streamOn = new PdfStream(getDocument());
        PdfCanvas canvasOn = new PdfCanvas(streamOn, new PdfResources());
        drawTextField(canvasOn, 0, 0, width, height);

        PdfStream streamOff = new PdfStream(getDocument());
        PdfCanvas canvasOff = new PdfCanvas(streamOff, new PdfResources());
        drawTextField(canvasOff, 0, 0, width, height);

        Rectangle rect = new Rectangle(0, 0, width, height);
        PdfWidgetAnnotation widget = getWidgets().get(0);
        PdfFormXObject xObjectOn = new PdfFormXObject(getDocument(), rect);
        xObjectOn.getPdfObject().getOutputStream().writeBytes(streamOn.getBytes());
        widget.setNormalAppearance(new PdfDictionary());
        widget.getNormalAppearanceObject().put(new PdfName(value), xObjectOn.getPdfObject());

        PdfFormXObject xObjectOff = new PdfFormXObject(getDocument(), rect);
        xObjectOff.getPdfObject().getOutputStream().writeBytes(streamOff.getBytes());
        widget.getNormalAppearanceObject().put(new PdfName("Off"), xObjectOff.getPdfObject());
    }

    public PdfFormXObject drawButtonAppearance(float width, float height, String text, PdfFont font, int fontSize) {
        PdfStream stream = new PdfStream(getDocument());
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources());

        drawButton(canvas, 0, 0, width, height, text, font, fontSize);

        PdfFormXObject xObject = new PdfFormXObject(getDocument(), new Rectangle(0, 0, width, height));
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());
        xObject.getResources().addFont(font);

        return xObject;
    }

    public void drawButton(PdfCanvas canvas, float x, float y, float width, float height, String text, PdfFont font, int fontSize) {
        canvas.
                saveState().
                setStrokeColor(Color.Black).
                setLineWidth(1).
                setLineCapStyle(0).
                rectangle(x, y, width, height).
                stroke().
                setLineWidth(1).
                setLineCapStyle(0).
                setFillColor(Color.Silver).
                rectangle(x + 0.5f, y + 0.5f, width - 1, height - 1).
                fill().
                setStrokeColor(Color.White).
                setLineWidth(1).
                setLineCapStyle(0).
                moveTo(x + 1, y + 1).
                lineTo(x + 1, y + height - 1).
                lineTo(x + width - 1, y + height - 1).
                stroke().
                setStrokeColor(Color.Gray).
                setLineWidth(1).
                setLineCapStyle(0).
                moveTo(x + 1, y + 1).
                lineTo(x + width - 1, y + 1).
                lineTo(x + width - 1, y + height - 1).
                stroke().
                resetFillColorRgb().
                beginText().
                setFontAndSize(font, fontSize).
                setTextMatrix(0, y + (height - fontSize) / 2).
                showText(text).
                endText().
                restoreState();
    }

    protected static PdfArray processOptions(String options[][]) {
        PdfArray array = new PdfArray();
        for (String option[] : options) {
            String subOption[] = option;
            PdfArray subArray = new PdfArray(new PdfString(subOption[0]));
            subArray.add(new PdfString(subOption[1]));
            array.add(subArray);
        }
        return array;
    }

    protected static PdfArray processOptions(String options[]) {
        PdfArray array = new PdfArray();
        for (String option : options) {
            array.add(new PdfString(option));
        }
        return array;
    }
}
