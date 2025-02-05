/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.Range;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.font.CssFontFace;
import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.svg.css.impl.SvgStyleResolver;

import java.util.Collection;

import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import org.slf4j.LoggerFactory;

/**
 * Class that processes and add resolved css fonts to the FontProvider
 */
public class SvgFontProcessor {

    private SvgProcessorContext context;

    public SvgFontProcessor(SvgProcessorContext context) {
        this.context = context;
    }

    /**
     * Adds @font-face fonts to the FontProvider.
     *
     * @param cssResolver the css styles resolver
     */
    public void addFontFaceFonts(ICssResolver cssResolver) {
        if (cssResolver instanceof SvgStyleResolver) {
            for (CssFontFaceRule fontFace : ((SvgStyleResolver) cssResolver).getFonts()) {
                boolean findSupportedSrc = false;
                CssFontFace ff = CssFontFace.create(fontFace.getProperties());
                if (ff != null) {
                    for (CssFontFace.CssFontFaceSrc src : ff.getSources()) {
                        if (createFont(ff.getFontFamily(), src, fontFace.resolveUnicodeRange())) {
                            findSupportedSrc = true;
                            break;
                        }
                    }
                }
                if (!findSupportedSrc) {
                    LoggerFactory.getLogger(SvgFontProcessor.class)
                            .error(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_FONT,
                                    fontFace));
                }
            }
        }
    }

    /**
     * Creates a font and adds it to the context.
     *
     * @param fontFamily the font family
     * @param src        the source of the font
     * @return true, if successful
     */
    private boolean createFont(String fontFamily, CssFontFace.CssFontFaceSrc src, Range unicodeRange) {
        if (!CssFontFace.isSupportedFontFormat(src.getFormat())) {
            return false;
        } else if (src.isLocal()) { // to method with lazy initialization
            Collection<FontInfo> fonts = context.getFontProvider().getFontSet().get(src.getSrc());
            if (fonts.size() > 0) {
                for (FontInfo fi : fonts) {
                    context.addTemporaryFont(fi, fontFamily);//
                }
                return true;
            } else {
                return false;
            }
        } else {
            try {
                // Cache at resource resolver level only, at font level we will create font in any case.
                // The instance of fontProgram will be collected by GC if the is no need in it.
                byte[] bytes = context.getResourceResolver().retrieveBytesFromResource(src.getSrc());
                if (bytes != null) {
                    FontProgram fp = FontProgramFactory.createFont(bytes, false);
                    context.addTemporaryFont(fp, PdfEncodings.IDENTITY_H, fontFamily, unicodeRange);
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        }
    }
}
