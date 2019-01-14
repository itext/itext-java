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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.styledxmlparser.LogMessageConstant;
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
                FontFace ff = FontFace.create(fontFace.getProperties());
                if (ff != null) {
                    for (FontFace.FontFaceSrc src : ff.getSources()) {
                        if (createFont(ff.getFontFamily(), src)) {
                            findSupportedSrc = true;
                            break;
                        }
                    }
                }
                if (!findSupportedSrc) {
                    LoggerFactory.getLogger(SvgFontProcessor.class)
                            .error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_FONT, fontFace));
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
    private boolean createFont(String fontFamily, FontFace.FontFaceSrc src) {
        if (!supportedFontFormat(src.format)) {
            return false;
        } else if (src.isLocal) { // to method with lazy initialization
            Collection<FontInfo> fonts = context.getFontProvider().getFontSet().get(src.src);
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
                byte[] bytes = context.getResourceResolver().retrieveBytesFromResource(src.src);
                if (bytes != null) {
                    FontProgram fp = FontProgramFactory.createFont(bytes, false);
                    context.addTemporaryFont(fp, PdfEncodings.IDENTITY_H, fontFamily);
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        }
    }

    /**
     * Checks whether in general we support requested font format.
     *
     * @param format {@link com.itextpdf.svg.processors.impl.font.FontFace.FontFormat}
     * @return true, if supported or unrecognized.
     */
    //TODO (RND-1065) code duplication
    private boolean supportedFontFormat(FontFace.FontFormat format) {
        switch (format) {
            case None:
            case TrueType:
            case OpenType:
            case WOFF:
            case WOFF2:
                return true;
            default:
                return false;
        }
    }
}
