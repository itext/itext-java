/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.CssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;

import java.io.IOException;
import java.util.List;

/**
 * Draws text to a PdfCanvas.
 * Currently supported:
 *  - only the default font of PDF
 *  - x, y
 */
public class TextSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        if ( this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgTagConstants.TEXT_CONTENT) ) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            String xRawValue = this.attributesAndStyles.get(SvgTagConstants.X);
            String yRawValue = this.attributesAndStyles.get(SvgTagConstants.Y);
            String fontSizeRawValue = this.attributesAndStyles.get(SvgTagConstants.FONT_SIZE);

            List<String> xValuesList = SvgCssUtils.splitValueList(xRawValue);
            List<String> yValuesList = SvgCssUtils.splitValueList(yRawValue);

            float x = 0f;
            float y = 0f;
            float fontSize = 0f;

            if ( fontSizeRawValue != null && !fontSizeRawValue.isEmpty()) {
                fontSize = CssUtils.parseAbsoluteLength(fontSizeRawValue, CssConstants.PT);
            }

            if ( !xValuesList.isEmpty() ) {
                x = CssUtils.parseAbsoluteLength(xValuesList.get(0));
            }

            if ( !yValuesList.isEmpty() ) {
                y = CssUtils.parseAbsoluteLength(yValuesList.get(0));
            }

            currentCanvas.beginText();
            try {
                // TODO font resolution RND-883
                currentCanvas.setFontAndSize(PdfFontFactory.createFont(), fontSize);
            } catch (IOException e) {
                throw new SvgProcessingException(SvgLogMessageConstant.FONT_NOT_FOUND, e);
            }
            //Current transformation matrix results in the character glyphs being mirrored, correct with inverse tf
            currentCanvas.setTextMatrix(1,0,0,-1,x,y);
            currentCanvas.setColor(ColorConstants.BLACK, true);
            currentCanvas.showText(this.attributesAndStyles.get(SvgTagConstants.TEXT_CONTENT));

            currentCanvas.endText();
        }
    }
}