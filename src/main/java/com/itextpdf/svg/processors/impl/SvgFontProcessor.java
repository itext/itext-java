package com.itextpdf.svg.processors.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.svg.css.impl.DefaultSvgStyleResolver;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that processes and add resolved css fonts to the FontProvider
 */

public class SvgFontProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SvgFontProcessor.class);

    private ProcessorContext context;

    public SvgFontProcessor(ProcessorContext context) {
        this.context = context;
    }

    /**
     * Adds @font-face fonts to the FontProvider.
     * @param cssResolver the css styles resolver
     */
    public void addFontFaceFonts(ICssResolver cssResolver) {
        //TODO Shall we add getFonts() to ICssResolver?
        if (cssResolver instanceof DefaultSvgStyleResolver) {
            for (CssFontFaceRule fontFace : ((DefaultSvgStyleResolver) cssResolver).getFonts()) {
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
                    LOGGER.error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_FONT, fontFace));
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
                byte[] bytes = context.getResourceResolver().retrieveStream(src.src);
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
     * @param format {@link com.itextpdf.svg.processors.impl.FontFace.FontFormat}
     * @return true, if supported or unrecognized.
     */
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
