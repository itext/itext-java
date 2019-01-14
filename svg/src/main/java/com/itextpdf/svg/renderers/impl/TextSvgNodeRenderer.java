/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws text to a PdfCanvas.
 * Currently supported:
 * - only the default font of PDF
 * - x, y
 */
public class TextSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_CONTENT)) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            String xRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.X);
            String yRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.Y);
            String fontSizeRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_SIZE);

            List<String> xValuesList = SvgCssUtils.splitValueList(xRawValue);
            List<String> yValuesList = SvgCssUtils.splitValueList(yRawValue);

            float x = 0f;
            float y = 0f;
            float fontSize = 0f;

            if (fontSizeRawValue != null && !fontSizeRawValue.isEmpty()) {
                fontSize = CssUtils.parseAbsoluteLength(fontSizeRawValue, CommonCssConstants.PT);
            }

            if (!xValuesList.isEmpty()) {
                x = CssUtils.parseAbsoluteLength(xValuesList.get(0));
            }

            if (!yValuesList.isEmpty()) {
                y = CssUtils.parseAbsoluteLength(yValuesList.get(0));
            }

            currentCanvas.beginText();
            FontProvider provider = context.getFontProvider();
            FontSet tempFonts = context.getTempFonts();
            PdfFont font = null;
            if (!provider.getFontSet().isEmpty() || (tempFonts != null && !tempFonts.isEmpty())) {
                String fontFamily = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_FAMILY);
                String fontWeight = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_WEIGHT);
                String fontStyle = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_STYLE);

                fontFamily = fontFamily != null ? fontFamily.trim() : "";
                FontInfo fontInfo = resolveFontName(fontFamily, fontWeight, fontStyle,
                        provider, tempFonts);
                font = provider.getPdfFont(fontInfo, tempFonts);
            }
            if (font == null) {
                try {
                    // TODO (DEVSIX-2057)
                    // TODO each call of createFont() create a new instance of PdfFont.
                    // TODO FontProvider shall be used instead.
                    font = PdfFontFactory.createFont();
                } catch (IOException e) {
                    throw new SvgProcessingException(SvgLogMessageConstant.FONT_NOT_FOUND, e);
                }
            }
            currentCanvas.setFontAndSize(font, fontSize);

            //Current transformation matrix results in the character glyphs being mirrored, correct with inverse tf
            currentCanvas.setTextMatrix(1, 0, 0, -1, x, y);
            currentCanvas.setColor(ColorConstants.BLACK, true);
            currentCanvas.showText(this.attributesAndStyles.get(SvgConstants.Attributes.TEXT_CONTENT));
            currentCanvas.endText();
        }
    }

    private FontInfo resolveFontName(String fontFamily, String fontWeight, String fontStyle,
                                     FontProvider provider, FontSet tempFonts) {
        boolean isBold = fontWeight != null && fontWeight.equalsIgnoreCase(SvgConstants.Attributes.BOLD);
        boolean isItalic = fontStyle != null && fontStyle.equalsIgnoreCase(SvgConstants.Attributes.ITALIC);

        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        List<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(fontFamily);
        fontCharacteristics.setBoldFlag(isBold);
        fontCharacteristics.setItalicFlag(isItalic);

        return provider.getFontSelector(stringArrayList, fontCharacteristics, tempFonts).bestMatch();
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        TextSvgNodeRenderer copy = new TextSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}
