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

import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TextAndChoiceLegacyDrawer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAndChoiceLegacyDrawer.class);
    
    private TextAndChoiceLegacyDrawer() {
        //Empty constructor.
    }

    static boolean regenerateTextAndChoiceField(PdfFormAnnotation formAnnotation) {
        String value = formAnnotation.parent.getDisplayValue();
        final PdfName type = formAnnotation.parent.getFormType();

        PdfPage page = PdfAnnotation.makeAnnotation(formAnnotation.getPdfObject()).getPage();
        PdfArray bBox = formAnnotation.getPdfObject().getAsArray(PdfName.Rect);

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
        if (formAnnotation.getPdfObject().getAsDictionary(PdfName.MK) != null
                && formAnnotation.getPdfObject().getAsDictionary(PdfName.MK).get(PdfName.R) != null) {
            fieldRotation = (float) formAnnotation.getPdfObject().getAsDictionary(PdfName.MK).getAsFloat(PdfName.R);
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
            drawCombTextAppearance(formAnnotation, bboxRectangle, formAnnotation.getFont(),
                    formAnnotation.getFontSize(bBox, value), value, appearance);
        } else {
            int topIndex = 0;
            if (!formAnnotation.parent.getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
                PdfNumber topIndexNum = formAnnotation.getParent().getAsNumber(PdfName.TI);
                PdfArray options = formAnnotation.parent.getOptions();
                if (null != options) {
                    topIndex = null != topIndexNum ? topIndexNum.intValue() : 0;
                    PdfArray visibleOptions = topIndex > 0
                            ? new PdfArray(options.subList(topIndex, options.size())) : (PdfArray) options.clone();
                    value = PdfFormField.optionsArrayToString(visibleOptions);
                }
            }
            drawChoiceAppearance(formAnnotation, bboxRectangle, formAnnotation.getFontSize(bBox, value),
                    value, appearance, topIndex);
        }
        PdfDictionary ap = new PdfDictionary();
        ap.put(PdfName.N, appearance.getPdfObject());
        ap.setModified();
        formAnnotation.put(PdfName.AP, ap);

        return true;
    }
    
    static void drawChoiceAppearance(PdfFormAnnotation formAnnotation, Rectangle rect, float fontSize, String value,
            PdfFormXObject appearance, int topIndex) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(formAnnotation.getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, formAnnotation.getDocument());

        float width = rect.getWidth();
        float height = rect.getHeight();
        float widthBorder = 6.0f;
        float heightBorder = 2.0f;

        formAnnotation.drawBorder(canvas, appearance, width, height);
        canvas.
                beginVariableText().
                saveState().
                rectangle(3, 3, width - widthBorder, height - heightBorder).
                clip().
                endPath();

        Canvas modelCanvas = new Canvas(canvas, new Rectangle(3, 0, Math.max(0, width - widthBorder),
                Math.max(0, height - heightBorder)));
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);

        PdfFormAnnotation.setMetaInfoToCanvas(modelCanvas);

        Div div = new Div();
        if (formAnnotation.parent.getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
            div.setVerticalAlignment(VerticalAlignment.MIDDLE);
        }
        div.setHeight(Math.max(0, height - heightBorder));
        List<String> strings = formAnnotation.getFont().splitString(value, fontSize, width - widthBorder);
        for (int index = 0; index < strings.size(); index++) {
            Boolean isFull = modelCanvas.getRenderer().getPropertyAsBoolean(Property.FULL);
            if (Boolean.TRUE.equals(isFull)) {
                break;
            }

            Paragraph paragraph = new Paragraph(strings.get(index)).setFont(formAnnotation.getFont())
                    .setFontSize(fontSize).setMargins(0, 0, 0, 0).setMultipliedLeading(1);
            paragraph.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            paragraph.setTextAlignment(formAnnotation.parent.getJustification());

            if (formAnnotation.getColor() != null) {
                paragraph.setFontColor(formAnnotation.getColor());
            }
            if (!formAnnotation.parent.getFieldFlag(PdfChoiceFormField.FF_COMBO)) {
                PdfArray indices = formAnnotation.getParent().getAsArray(PdfName.I);
                if (indices != null && indices.size() > 0) {
                    for (PdfObject ind : indices) {
                        if (!ind.isNumber()) {
                            continue;
                        }
                        if (((PdfNumber) ind).getValue() == index + topIndex) {
                            paragraph.setBackgroundColor(new DeviceRgb(10, 36, 106));
                            paragraph.setFontColor(ColorConstants.LIGHT_GRAY);
                        }
                    }
                }
            }
            div.add(paragraph);
        }
        modelCanvas.add(div);
        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }
    
    static private void drawCombTextAppearance(PdfFormAnnotation formAnnotation, Rectangle rect, PdfFont font,
            float fontSize, String value, PdfFormXObject appearance) {
        PdfStream stream = (PdfStream) new PdfStream().makeIndirect(formAnnotation.getDocument());
        PdfResources resources = appearance.getResources();
        PdfCanvas canvas = new PdfCanvas(stream, resources, formAnnotation.getDocument());

        float height = rect.getHeight();
        float width = rect.getWidth();
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));
        formAnnotation.drawBorder(canvas, xObject, width, height);
        if (formAnnotation.parent.isPassword()) {
            value = obfuscatePassword(value);
        }

        canvas.
                beginVariableText().
                saveState().
                endPath();

        Canvas modelCanvas = new Canvas(canvas, new Rectangle(0, -height, 0, 2 * height));
        modelCanvas.setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);

        PdfFormAnnotation.setMetaInfoToCanvas(modelCanvas);

        Style paragraphStyle = new Style().setFont(font).setFontSize(fontSize);
        paragraphStyle.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 1));
        if (formAnnotation.getColor() != null) {
            paragraphStyle.setProperty(Property.FONT_COLOR, new TransparentColor(formAnnotation.getColor()));
        }

        int maxLen = PdfFormCreator.createTextFormField(formAnnotation.parent.getPdfObject()).getMaxLen();
        // check if /Comb has been set
        float widthPerCharacter = width / maxLen;
        int numberOfCharacters = Math.min(maxLen, value.length());

        int start;
        TextAlignment textAlignment = formAnnotation.parent.getJustification() == null ?
                TextAlignment.LEFT : formAnnotation.parent.getJustification();
        switch (textAlignment) {
            case RIGHT:
                start = (maxLen - numberOfCharacters);
                break;
            case CENTER:
                start = (maxLen - numberOfCharacters) / 2;
                break;
            default:
                start = 0;
        }
        float startOffset = widthPerCharacter * (start + 0.5f);
        for (int i = 0; i < numberOfCharacters; i++) {
            modelCanvas.showTextAligned(new Paragraph(value.substring(i, i + 1)).addStyle(paragraphStyle),
                    startOffset + widthPerCharacter * i, rect.getHeight() / 2, TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE);
        }
        canvas.
                restoreState().
                endVariableText();

        appearance.getPdfObject().setData(stream.getBytes());
    }

    private static String obfuscatePassword(String text) {
        char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); i++) {
            pchar[i] = '*';
        }
        return new String(pchar);
    }
    
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

    private static double degreeToRadians(double angle) {
        return Math.PI * angle / 180.0;
    }
}
