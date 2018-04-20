/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.
    
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
package com.itextpdf.styledxmlparser.resolver.font;

import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.layout.font.FontProvider;

import java.io.InputStream;

/**
 * The default {@link FontProvider} that, as opposed to
 * the font provider in iText 7, also includes a series of fonts that
 * are shipped with the implementation.
 */
public class DefaultFontProvider extends FontProvider {

    /** The path to the shipped fonts. */
    private static final String SHIPPED_FONT_RESOURCE_PATH = "com/itextpdf/html2pdf/font/";
    
    /** The file names of the shipped fonts. */
    private static final String[] SHIPPED_FONT_NAMES = new String[] {
            "FreeMono.ttf",
            "FreeMonoBold.ttf",
            "FreeMonoBoldOblique.ttf",
            "FreeMonoOblique.ttf",
            "FreeSans.ttf",
            "FreeSansBold.ttf",
            "FreeSansBoldOblique.ttf",
            "FreeSansOblique.ttf",
            "FreeSerif.ttf",
            "FreeSerifBold.ttf",
            "FreeSerifBoldItalic.ttf",
            "FreeSerifItalic.ttf",
    };

    /**
     * Creates a new {@link DefaultFontProvider} instance.
     */
    public DefaultFontProvider() {
        this(true, true, false);
    }

    /**
     * Creates a new {@link DefaultFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerShippedFreeFonts use true if you want to register the shipped fonts (can be embedded)
     * @param registerSystemFonts use true if you want to register the system fonts (can require quite some resources)
     */
    public DefaultFontProvider(boolean registerStandardPdfFonts, boolean registerShippedFreeFonts, boolean registerSystemFonts) {
        super();
        if (registerStandardPdfFonts) {
            addStandardPdfFonts();
        }
        if (registerShippedFreeFonts) {
            addShippedFreeFonts();
        }
        if (registerSystemFonts) {
            addSystemFonts();
        }
    }

    /**
     * Adds the shipped free fonts.
     */
    private void addShippedFreeFonts() {
        for (String fontName : SHIPPED_FONT_NAMES) {
            InputStream stream = ResourceUtil.getResourceStream(SHIPPED_FONT_RESOURCE_PATH + fontName);
            try {
                byte[] fontProgramBytes = StreamUtil.inputStreamToArray(stream);
                addFont(fontProgramBytes);
            } catch (Exception exc) {
            }
        }
    }

}
