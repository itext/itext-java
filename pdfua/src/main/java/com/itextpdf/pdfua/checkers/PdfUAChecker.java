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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.utils.checkers.FontCheckUtil;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

/**
 * An abstract class that will run through all necessary checks defined in the different PDF/UA standards. A number of
 * common checks are executed in this class, while standard-dependent specifications are implemented in the available
 * subclasses. The standard that is followed is the series of ISO 14289 specifications, currently generations 1 and 2.
 *
 * <p>
 * While it is possible to subclass this method and implement its abstract methods in client code, this is not
 * encouraged and will have little effect. It is not possible to plug custom implementations into iText, because
 * iText should always refuse to create non-compliant PDF/UA, which would be possible with client code implementations.
 * Any future generations of the PDF/UA standard and its derivatives will get their own implementation in the iText -
 * pdfua project.
 */
public abstract class PdfUAChecker implements IValidationChecker {

    static final Function<String, PdfException> EXCEPTION_SUPPLIER = (msg) -> new PdfUAConformanceException(msg);

    private boolean warnedOnPageFlush = false;

    /**
     * Creates new {@link PdfUAChecker} instance.
     */
    protected PdfUAChecker() {
        // Empty constructor.
    }

    /**
     * Logs a warn on page flushing that page flushing is disabled in PDF/UA mode.
     */
    public void warnOnPageFlush() {
        if (!warnedOnPageFlush) {
            LoggerFactory.getLogger(PdfUAChecker.class).warn(PdfUALogMessageConstants.PAGE_FLUSHING_DISABLED);
            warnedOnPageFlush = true;
        }
    }

    /**
     * Checks that the default natural language for content and text strings is specified using the {@code Lang}
     * entry, with a nonempty value, in the document catalog dictionary.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkLang(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        PdfObject lang = catalogDict.get(PdfName.Lang);
        if (!(lang instanceof PdfString)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CATALOG_SHOULD_CONTAIN_LANG_ENTRY);
        }
        if (((PdfString) lang).getValue().isEmpty()) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY);
        }
    }

    /**
     * Checks that the {@code ViewerPreferences} dictionary of the document catalog dictionary is present and contains
     * at least the {@code DisplayDocTitle} key with a value of {@code true}, as defined in
     * ISO 32000-1:2008, 12.2, Table 150 or ISO 32000-2:2020, Table 147.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkViewerPreferences(PdfCatalog catalog) {
        PdfDictionary viewerPreferences = catalog.getPdfObject().getAsDictionary(PdfName.ViewerPreferences);
        if (viewerPreferences == null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        PdfObject displayDocTitle = viewerPreferences.get(PdfName.DisplayDocTitle);
        if (!(displayDocTitle instanceof PdfBoolean)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        if (PdfBoolean.FALSE.equals(displayDocTitle)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE);
        }
    }

    /**
     * Checks that all optional content configuration dictionaries in the file, including the default one, shall contain
     * a Name entry (see ISO 32000-2:2020, Table 96, or ISO 32000-1:2008, 8.11.2.1, Table 98) whose value is a non-empty
     * text string when document contains a Configs entry in the OCProperties entry of the document catalog dictionary
     * (see ISO 32000-2:2020, Table 29, or ISO 32000-1:2008, 7.7.2, Table 28), and the Configs entry contains at least
     * one optional content configuration dictionary.
     *
     * <p>
     * Also checks that the AS key does not appear in any optional content configuration dictionary.
     *
     * @param ocProperties OCProperties entry of the Catalog dictionary
     */
    void checkOCProperties(PdfDictionary ocProperties) {
        if (ocProperties == null) {
            return;
        }
        PdfArray configs = ocProperties.getAsArray(PdfName.Configs);
        if (configs != null && !configs.isEmpty()) {
            PdfDictionary d = ocProperties.getAsDictionary(PdfName.D);
            checkOCGNameAndASKey(d);
            for (PdfObject config : configs) {
                checkOCGNameAndASKey((PdfDictionary) config);
            }
            PdfArray ocgsArray = ocProperties.getAsArray(PdfName.OCGs);
            if (ocgsArray != null) {
                for (PdfObject ocg : ocgsArray) {
                    checkOCGNameAndASKey((PdfDictionary) ocg);
                }
            }
        }
    }

    /**
     * Checks if content marked as Artifact resides in Artifact content, but real content does not.
     *
     * @param stack      the tag structure stack
     * @param currentBmc the current BMC
     * @param document   {@link PdfDocument} to check
     */
    void checkLogicalStructureInBMC(Stack<Tuple2<PdfName, PdfDictionary>> stack,
                                    Tuple2<PdfName, PdfDictionary> currentBmc, PdfDocument document) {
        if (stack.isEmpty()) {
            return;
        }

        boolean isRealContent = isRealContent(currentBmc, document);
        boolean isArtifact = PdfName.Artifact.equals(currentBmc.getFirst());

        if (isArtifact && isInsideRealContent(stack, document)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT);
        }
        if (isRealContent && isInsideArtifact(stack)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT);
        }
    }

    /**
     * Checks if content is neither marked as Artifact nor tagged as real content.
     *
     * @param tagStack tag structure stack
     * @param document {@link PdfDocument} to check
     */
    void checkContentInCanvas(Stack<Tuple2<PdfName, PdfDictionary>> tagStack, PdfDocument document) {
        if (tagStack.isEmpty()) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING);
        }

        final boolean insideRealContent = isInsideRealContent(tagStack, document);
        final boolean insideArtifact = isInsideArtifact(tagStack);
        if (insideRealContent && insideArtifact) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.REAL_CONTENT_INSIDE_ARTIFACT_OR_VICE_VERSA);
        } else if (!insideRealContent && !insideArtifact) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT);
        }
    }

    /**
     * Checks that font programs for all fonts used for rendering within a conforming file, as determined by whether at
     * least one of its glyphs is referenced from one or more content streams, are embedded within that file, as defined
     * in ISO 32000-2:2020, 9.9 and ISO 32000-1:2008, 9.9.
     *
     * <p>
     * Checks character encodings rules as defined in ISO 14289-2, 8.4.5.7 and ISO 14289-1, 7.21.6.
     *
     * @param fontsInDocument collection of fonts used in the document
     */
    void checkFonts(Collection<PdfFont> fontsInDocument) {
        Set<String> fontNamesThatAreNotEmbedded = new HashSet<>();
        for (PdfFont font : fontsInDocument) {
            if (!font.isEmbedded()) {
                fontNamesThatAreNotEmbedded.add(font.getFontProgram().getFontNames().getFontName());
                continue;
            }
            if (font instanceof PdfTrueTypeFont) {
                PdfTrueTypeFont trueTypeFont = (PdfTrueTypeFont) font;
                int flags = trueTypeFont.getFontProgram().getPdfFontFlags();
                boolean symbolic = PdfCheckersUtil.checkFlag(flags, FontDescriptorFlags.SYMBOLIC) &&
                        !PdfCheckersUtil.checkFlag(flags, FontDescriptorFlags.NONSYMBOLIC);
                if (symbolic) {
                    checkSymbolicTrueTypeFont(trueTypeFont);
                } else {
                    checkNonSymbolicTrueTypeFont(trueTypeFont);
                }
            }
        }
        if (!fontNamesThatAreNotEmbedded.isEmpty()) {
            throw new PdfUAConformanceException(
                    MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED,
                            String.join(", ", fontNamesThatAreNotEmbedded)
                    ));
        }
    }

    /**
     * Checks cmap entries present in the embedded TrueType font program of the non-symbolic TrueType font.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    abstract void checkNonSymbolicCmapSubtable(TrueTypeFont fontProgram);

    /**
     * Checks cmap entries present in the embedded TrueType font program of the symbolic TrueType font.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    abstract void checkSymbolicCmapSubtable(TrueTypeFont fontProgram);

    /**
     * Checks that embedded fonts define all glyphs referenced for rendering within the conforming file.
     *
     * @param str  the text to check
     * @param font the font to check
     */
    void checkText(String str, PdfFont font) {
        int index = FontCheckUtil.checkGlyphsOfText(str, font, new PdfUAChecker.UaCharacterChecker());

        if (index != -1) {
            throw new PdfUAConformanceException(MessageFormatUtil.format(
                    PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, str.charAt(index)));
        }
    }

    private static void checkOCGNameAndASKey(PdfDictionary dict) {
        if (dict == null) {
            return;
        }
        if (dict.get(PdfName.AS) != null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.OCG_SHALL_NOT_CONTAIN_AS_ENTRY);
        }
        if (!(dict.get(PdfName.Name) instanceof PdfString) ||
                (((PdfString) dict.get(PdfName.Name)).toString().isEmpty())) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG);
        }
    }

    private static boolean isInsideArtifact(Stack<Tuple2<PdfName, PdfDictionary>> tagStack) {
        for (Tuple2<PdfName, PdfDictionary> tag : tagStack) {
            if (PdfName.Artifact.equals(tag.getFirst())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInsideRealContent(Stack<Tuple2<PdfName, PdfDictionary>> tagStack, PdfDocument document) {
        for (Tuple2<PdfName, PdfDictionary> tag : tagStack) {
            if (isRealContent(tag, document)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRealContent(Tuple2<PdfName, PdfDictionary> tag, PdfDocument document) {
        if (PdfName.Artifact.equals(tag.getFirst())) {
            return false;
        }
        PdfDictionary properties = tag.getSecond();
        if (properties == null || !properties.containsKey(PdfName.MCID)) {
            return false;
        }
        PdfMcr mcr = mcrExists(document, (int) properties.getAsInt(PdfName.MCID));
        if (mcr == null) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT);
        }
        return true;
    }

    private static PdfMcr mcrExists(PdfDocument document, int mcid) {
        int amountOfPages = document.getNumberOfPages();
        for (int i = 1; i <= amountOfPages; ++i) {
            PdfPage page = document.getPage(i);
            PdfMcr mcr = document.getStructTreeRoot().findMcrByMcid(page.getPdfObject(), mcid);
            if (mcr != null) {
                return mcr;
            }
        }
        return null;
    }

    private void checkNonSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {
        TrueTypeFont fontProgram = (TrueTypeFont) trueTypeFont.getFontProgram();
        checkNonSymbolicCmapSubtable(fontProgram);

        String encoding = trueTypeFont.getFontEncoding().getBaseEncoding();
        // Non-symbolic TTF will always have the dictionary value in the Encoding key of the Font dictionary in itext.
        if (!PdfEncodings.WINANSI.equals(encoding) && !PdfEncodings.MACROMAN.equals(encoding)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.NON_SYMBOLIC_TTF_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING);
        }

        if (trueTypeFont.getFontEncoding().hasDifferences() && !fontProgram.isCmapPresent(3, 1)) {
            // If font has differences array, itext ensures that all the glyph names in the Differences array are listed
            // in the Adobe Glyph List.
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.NON_SYMBOLIC_TTF_SHALL_NOT_DEFINE_DIFFERENCES);
        }
    }

    private void checkSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont) {
        if (trueTypeFont.getPdfObject().containsKey(PdfName.Encoding)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.SYMBOLIC_TTF_SHALL_NOT_CONTAIN_ENCODING);
        }

        TrueTypeFont fontProgram = (TrueTypeFont) trueTypeFont.getFontProgram();
        checkSymbolicCmapSubtable(fontProgram);
    }

    private static final class UaCharacterChecker implements FontCheckUtil.CharacterChecker {

        /**
         * Creates new {@link UaCharacterChecker} instance.
         */
        public UaCharacterChecker() {
            // Empty constructor.
        }

        @Override
        public boolean check(int ch, PdfFont font) {
            if (font.containsGlyph(ch)) {
                return !font.getGlyph(ch).hasValidUnicode();
            } else {
                return true;
            }
        }
    }
}
