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
package com.itextpdf.pdfa.checker;

import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.AbstractColorValidationContext;
import com.itextpdf.kernel.validation.context.CanvasStackValidationContext;
import com.itextpdf.kernel.validation.context.CryptoValidationContext;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.context.IContentStreamValidationParameter;
import com.itextpdf.kernel.validation.context.IGraphicStateValidationParameter;
import com.itextpdf.kernel.validation.context.InlineImageValidationContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.validation.context.PdfObjectValidationContext;
import com.itextpdf.kernel.validation.context.PdfPageValidationContext;
import com.itextpdf.kernel.validation.context.RenderingIntentValidationContext;
import com.itextpdf.kernel.validation.context.SignTypeValidationContext;
import com.itextpdf.kernel.validation.context.SignatureValidationContext;
import com.itextpdf.kernel.validation.context.TagStructElementValidationContext;
import com.itextpdf.kernel.validation.context.XrefTableValidationContext;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that will run through all necessary checks defined in the
 * different PDF/A standards and levels. A number of common checks are executed
 * in this class, while standard-dependent specifications are implemented in the
 * available subclasses. The standard that is followed is the series of ISO
 * 19005 specifications, currently generations 1 through 3. The ZUGFeRD standard
 * is derived from ISO 19005-3.
 *
 * While it is possible to subclass this method and implement its abstract
 * methods in client code, this is not encouraged and will have little effect.
 * It is not possible to plug custom implementations into iText, because iText
 * should always refuse to create non-compliant PDF/A, which would be possible
 * with client code implementations. Any future generations of the PDF/A
 * standard and its derivates will get their own implementation in the
 * iText - pdfa project.
 */
public abstract class PdfAChecker implements IValidationChecker {

    /**
     * The Red-Green-Blue color profile as defined by the International Color
     * Consortium.
     */
    public static final String ICC_COLOR_SPACE_RGB = "RGB ";

    /**
     * The Cyan-Magenta-Yellow-Key (black) color profile as defined by the
     * International Color Consortium.
     */
    public static final String ICC_COLOR_SPACE_CMYK = "CMYK";

    /**
     * The Grayscale color profile as defined by the International Color
     * Consortium.
     */
    public static final String ICC_COLOR_SPACE_GRAY = "GRAY";

    /**
     * The Output device class
     */
    public static final String ICC_DEVICE_CLASS_OUTPUT_PROFILE = "prtr";

    /**
     * The Monitor device class
     */
    public static final String ICC_DEVICE_CLASS_MONITOR_PROFILE = "mntr";

    /**
     * The maximum Graphics State stack depth in PDF/A documents, i.e. the
     * maximum number of graphics state operators with code <code>q</code> that
     * may be opened (i.e. not yet closed by a corresponding <code>Q</code>) at
     * any point in a content stream sequence.
     *
     * Defined as 28 by PDF/A-1 section 6.1.12, by referring to the PDF spec
     * Appendix C table 1 "architectural limits".
     */
    public static final int maxGsStackDepth = 28;

    protected PdfAConformance conformance;
    protected PdfStream pdfAOutputIntentDestProfile;
    protected String pdfAOutputIntentColorSpace;

    protected int gsStackDepth = 0;

    protected Set<PdfObject> rgbUsedObjects = new HashSet<>();
    protected Set<PdfObject> cmykUsedObjects = new HashSet<>();
    protected Set<PdfObject> grayUsedObjects = new HashSet<>();

    /**
     * Contains some objects that are already checked.
     * NOTE: Not all objects that were checked are stored in that set. This set is used for avoiding double checks for
     * actions, signatures, xObjects and page objects; and for letting those objects to be manually flushed.
     *
     * Use this mechanism carefully: objects that are able to be changed (or at least if object's properties
     * that shall be checked are able to be changed) shouldn't be marked as checked if they are not to be
     * flushed immediately.
     */
    protected Set<PdfObject> checkedObjects = new HashSet<>();
    protected Map<PdfObject, PdfColorSpace> checkedObjectsColorspace = new HashMap<>();

    private boolean fullCheckMode = false;
    private boolean alreadyLoggedThatPageFlushingWasNotPerformed = false;
    private boolean alreadyLoggedThatObjectFlushingWasNotPerformed = false;

    /**
     * Creates a PdfAChecker with the required conformance.
     *
     * @param aConformance the required conformance
     */
    protected PdfAChecker(PdfAConformance aConformance) {
        this.conformance = aConformance;
    }

    /**
     * This method checks a number of document-wide requirements of the PDF/A
     * standard. The algorithms of some of these checks vary with the PDF/A
     * level and thus are implemented in subclasses; others are implemented
     * as private methods in this class.
     *
     * @param catalog The catalog being checked
     */
    public void checkDocument(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        setPdfAOutputIntentColorSpace(catalogDict);

        checkOutputIntents(catalogDict);
        checkMetaData(catalogDict);
        checkCatalogValidEntries(catalogDict);
        checkTrailer(catalog.getDocument().getTrailer());
        checkCatalog(catalog);
        checkLogicalStructure(catalogDict);
        checkForm(catalogDict.getAsDictionary(PdfName.AcroForm));
        if (catalog.getDocument().hasOutlines()) {
            checkOutlines(catalog.getDocument().getOutlines(false));
        }
        checkPages(catalog.getDocument());
        checkOpenAction(catalogDict.get(PdfName.OpenAction));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void validate(IValidationContext context) {
        CanvasGraphicsState gState = null;
        if (context instanceof IGraphicStateValidationParameter) {
            gState = ((IGraphicStateValidationParameter) context).getGraphicsState();
        }
        PdfStream contentStream = null;
        if (context instanceof IContentStreamValidationParameter) {
            contentStream = ((IContentStreamValidationParameter) context).getContentStream();
        }

        switch (context.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) context;
                for (PdfFont pdfFont : pdfDocContext.getDocumentFonts()) {
                    checkFont(pdfFont);
                }
                checkDocument(pdfDocContext.getPdfDocument().getCatalog());
                break;
            case CANVAS_STACK:
                checkCanvasStack(((CanvasStackValidationContext) context).getOperator());
                break;
            case FILL_COLOR:
            case STROKE_COLOR:
                boolean isFill = ValidationType.FILL_COLOR == context.getType();
                final Color color = isFill ? gState.getFillColor() : gState.getStrokeColor();
                AbstractColorValidationContext colorContext = (AbstractColorValidationContext) context;
                checkColor(gState, color, colorContext.getCurrentColorSpaces(), isFill, contentStream);
                break;
            case EXTENDED_GRAPHICS_STATE:
                checkExtGState(gState, contentStream);
                break;
            case INLINE_IMAGE:
                InlineImageValidationContext imageContext = (InlineImageValidationContext) context;
                checkInlineImage(imageContext.getImage(), imageContext.getCurrentColorSpaces());
                break;
            case PDF_PAGE:
                PdfPageValidationContext pageContext = (PdfPageValidationContext) context;
                checkSinglePage(pageContext.getPage());
                break;
            case PDF_OBJECT:
                PdfObjectValidationContext objContext = (PdfObjectValidationContext) context;
                checkPdfObject(objContext.getObject());
                break;
            case RENDERING_INTENT:
                RenderingIntentValidationContext intentContext = (RenderingIntentValidationContext) context;
                checkRenderingIntent(intentContext.getIntent());
                break;
            case TAG_STRUCTURE_ELEMENT:
                TagStructElementValidationContext tagContext = (TagStructElementValidationContext) context;
                checkTagStructureElement(tagContext.getObject());
                break;
            case FONT_GLYPHS:
                checkFontGlyphs(gState.getFont(), contentStream);
                break;
            case XREF_TABLE:
                XrefTableValidationContext xrefContext = (XrefTableValidationContext) context;
                checkXrefTable(xrefContext.getXrefTable());
                break;
            case SIGNATURE:
                SignatureValidationContext signContext = (SignatureValidationContext) context;
                checkSignature(signContext.getSignature());
                break;
            case SIGNATURE_TYPE:
                SignTypeValidationContext signTypeContext = (SignTypeValidationContext) context;
                checkSignatureType(signTypeContext.isCAdES());
                break;
            case CRYPTO:
                CryptoValidationContext cryptoContext = (CryptoValidationContext) context;
                checkCrypto(cryptoContext.getCrypto());
                break;
            case FONT:
                FontValidationContext fontContext = (FontValidationContext) context;
                checkText(fontContext.getText(), fontContext.getFont());
                break;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        if (checkedObjects.contains(object)) {
            return true;
        }

        if (object instanceof PdfDictionary && PdfName.Page.equals(((PdfDictionary) object).getAsName(PdfName.Type))) {
            if (!alreadyLoggedThatPageFlushingWasNotPerformed) {
                alreadyLoggedThatPageFlushingWasNotPerformed = true;
                // This log message will be printed once for one instance of the document.
                LoggerFactory.getLogger(PdfAChecker.class).warn(PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED);
            }
        } else if (!alreadyLoggedThatObjectFlushingWasNotPerformed) {
            alreadyLoggedThatObjectFlushingWasNotPerformed = true;
            // This log message will be printed once for one instance of the document.
            LoggerFactory.getLogger(PdfAChecker.class).warn(PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED);

        }
        return false;
    }

    /**
     * This method checks all requirements that must be fulfilled by a page in a
     * PDF/A document.
     * @param page the page that must be checked
     */
    public void checkSinglePage(PdfPage page) {
        setPdfAOutputIntentColorSpace(page.getDocument().getCatalog().getPdfObject());
        checkPage(page);
    }

    /**
     * This method checks the requirements that must be fulfilled by a COS
     * object in a PDF/A document.
     * @param obj the COS object that must be checked
     */
    public void checkPdfObject(PdfObject obj) {
        switch (obj.getType()) {
            case PdfObject.NAME:
                checkPdfName((PdfName) obj);
                break;
            case PdfObject.NUMBER:
                checkPdfNumber((PdfNumber) obj);
                break;
            case PdfObject.STRING:
                checkPdfString((PdfString) obj);
                break;
            case PdfObject.ARRAY:
                PdfArray array = (PdfArray) obj;
                checkPdfArray(array);
                checkArrayRecursively(array);
                break;
            case PdfObject.DICTIONARY:
                PdfDictionary dict = (PdfDictionary) obj;
                PdfName type = dict.getAsName(PdfName.Type);
                if (PdfName.Filespec.equals(type)) {
                    checkFileSpec(dict);
                }
                checkPdfDictionary(dict);
                checkDictionaryRecursively(dict);
                break;
            case PdfObject.STREAM:
                PdfStream stream = (PdfStream) obj;
                checkPdfStream(stream);
                checkDictionaryRecursively(stream);
                break;
        }
    }

    /**
     * Gets the {@link PdfAConformance} for this file.
     *
     * @return the defined conformance for this document.
     */
    public PdfAConformance getAConformance() {
        return conformance;
    }

    /**
     * In full check mode all objects will be tested for ISO conformance. If full check mode is
     * switched off objects which were not modified might be skipped to speed up the validation
     * of the document
     * @return true if full check mode is switched on
     * @see PdfObject#isModified()
     */
    public boolean isFullCheckMode() {
        return fullCheckMode;
    }

    /**
     * In full check mode all objects will be tested for ISO conformance. If full check mode is
     * switched off objects which were not modified might be skipped to speed up the validation
     * of the document
     * @param fullCheckMode is a new value for full check mode switcher
     * @see PdfObject#isModified()
     */
    public void setFullCheckMode(boolean fullCheckMode) {
        this.fullCheckMode = fullCheckMode;
    }

    /**
     * This method checks compliance of the tag structure elements, such as struct elements
     * or parent tree entries.
     *
     * @param obj an object that represents tag structure element.
     */
    public void checkTagStructureElement(PdfObject obj) {
        // We don't check tag structure as there are no strict constraints,
        // so we just mark tag structure elements to be able to flush them
        checkedObjects.add(obj);
    }

    /**
     * This method checks compliance of the signature dictionary
     *
     * @param signatureDict a {@link PdfDictionary} containing the signature.
     */
    public void checkSignature(PdfDictionary signatureDict) {
        checkedObjects.add(signatureDict);
    }

    /**
     * This method checks compliance of the signature type.
     *
     * @param isCAdES {@code true} if CAdES signature type is used, {@code false} otherwise
     */
    public abstract void checkSignatureType(boolean isCAdES);

    /**
     * This method checks compliance with the graphics state architectural
     * limitation, explained by {@link PdfAChecker#maxGsStackDepth}.
     *
     * @param stackOperation the operation to check the graphics state counter for
     */
    public abstract void checkCanvasStack(char stackOperation);

    /**
     * This method checks compliance with the inline image restrictions in the
     * PDF/A specs, specifically filter parameters.
     *
     * @param inlineImage a {@link PdfStream} containing the inline image
     * @param currentColorSpaces a {@link PdfDictionary} containing the color spaces used in the document
     */
    public abstract void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces);

    /**
     * This method checks compliance with the color restrictions imposed by the
     * available color spaces in the document.
     *
     * @param gState canvas graphics state
     * @param color the color to check
     * @param currentColorSpaces a {@link PdfDictionary} containing the color spaces used in the document
     * @param fill whether the color is used for fill or stroke operations
     * @param contentStream current content stream
     */
    public abstract void checkColor(CanvasGraphicsState gState, Color color, PdfDictionary currentColorSpaces,
                                    Boolean fill, PdfStream contentStream);

    /**
     * This method performs a range of checks on the given color space, depending
     * on the type and properties of that color space.
     *
     * @param colorSpace the color space to check
     * @param pdfObject the pdf object to check color space in
     * @param currentColorSpaces a {@link PdfDictionary} containing the color spaces used in the document
     * @param checkAlternate whether or not to also check the parent color space
     * @param fill whether the color space is used for fill or stroke operations
     */
    public abstract void checkColorSpace(PdfColorSpace colorSpace, PdfObject pdfObject, PdfDictionary currentColorSpaces,
            boolean checkAlternate, Boolean fill);

    /**
     * Set Pdf A output intent color space.
     *
     * @param catalog Catalog dictionary to retrieve the color space from.
     */
    public void setPdfAOutputIntentColorSpace(PdfDictionary catalog) {
        PdfArray outputIntents = catalog.getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            return;
        }

        PdfDictionary pdfAOutputIntent = getPdfAOutputIntent(outputIntents);
        setCheckerOutputIntent(pdfAOutputIntent);
    }

    /**
     * Checks whether the rendering intent of the document is within the allowed
     * range of intents. This is defined in ISO 19005-1 section 6.2.9, and
     * unchanged in newer generations of the PDF/A specification.
     *
     * @param intent the intent to be analyzed
     */
    public abstract void checkRenderingIntent(PdfName intent);

    /**
     * Performs a check of the each font glyph as a Form XObject. See ISO 19005-2 Annex A.5.
     * This only applies to type 3 fonts.
     * This method will be abstract in update 7.2
     *
     * @param font {@link PdfFont} to be checked
     * @param contentStream stream containing checked font
     */
    public abstract void checkFontGlyphs(PdfFont font, PdfStream contentStream);

    /**
     * Performs a number of checks on the graphics state, among others ISO
     * 19005-1 section 6.2.8 and 6.4 and ISO 19005-2 section 6.2.5 and 6.2.10.
     * This method will be abstract in the update 7.2
     *
     * @param extGState the graphics state to be checked
     * @param contentStream current content stream
     */
    public abstract void checkExtGState(CanvasGraphicsState extGState, PdfStream contentStream);

    /**
     * Performs a number of checks on the font. See ISO 19005-1 section 6.3,
     * ISO 19005-2 and ISO 19005-3 section 6.2.11.
     * Be aware that not all constraints defined in the ISO are checked in this method,
     * for most of them we consider that iText always creates valid fonts.
     * @param pdfFont font to be checked
     */
    public abstract void checkFont(PdfFont pdfFont);

    /**
     * Verify the conformity of the cross-reference table.
     *
     * @param xrefTable is the Xref table
     */
    public abstract void checkXrefTable(PdfXrefTable xrefTable);

    /**
     * Verify the conformity of encryption usage.
     *
     * @param crypto encryption object to verify
     */
    public abstract void checkCrypto(PdfObject crypto);

    /**
     * Verify the conformity of the text written by the specified font.
     *
     * @param text text to verify
     * @param font font to verify the text against
     */
    public abstract void checkText(String text, PdfFont font);

    /**
     * Attest content stream conformance with appropriate specification.
     * Throws PdfAConformanceException if any discrepancy was found
     *
     * @param contentStream is a content stream to validate
     */
    protected abstract void checkContentStream(PdfStream contentStream);

    /**
     * Verify the conformity of the operand of content stream with appropriate
     * specification. Throws PdfAConformanceException if any discrepancy was found
     *
     * @param object is an operand of content stream to validate
     */
    protected void checkContentStreamObject(PdfObject object) {
        byte type = object.getType();
        switch (type) {
            case PdfObject.NAME:
                checkPdfName((PdfName) object);
                break;
            case PdfObject.STRING:
                checkPdfString((PdfString) object);
                break;
            case PdfObject.NUMBER:
                checkPdfNumber((PdfNumber) object);
                break;
            case PdfObject.ARRAY:
                PdfArray array = (PdfArray) object;
                checkPdfArray(array);
                for (PdfObject obj : array) {
                    checkContentStreamObject(obj);
                }
                break;
            case PdfObject.DICTIONARY:
                PdfDictionary dictionary = (PdfDictionary) object;
                checkPdfDictionary(dictionary);
                for (final PdfName name: dictionary.keySet()) {
                    checkPdfName(name);
                    checkPdfObject(dictionary.get(name, false));
                }
                for (final PdfObject obj : dictionary.values()) {
                    checkContentStreamObject(obj);
                }
                break;
        }
    }

    /**
     * Retrieve maximum allowed number of indirect objects in conforming document.
     *
     * @return maximum allowed number of indirect objects
     */
    protected abstract long getMaxNumberOfIndirectObjects();

    /**
     * Retrieve forbidden actions in conforming document.
     *
     * @return set of {@link PdfName} with forbidden actions
     */
    protected abstract Set<PdfName> getForbiddenActions();

    /**
     * Retrieve allowed actions in conforming document.
     *
     * @return set of {@link PdfName} with allowed named actions
     */
    protected abstract Set<PdfName> getAllowedNamedActions();

    /**
     * Checks if the action is allowed.
     *
     * @param action to be checked
     */
    protected abstract void checkAction(PdfDictionary action);

    /**
     * Verify the conformity of the annotation dictionary.
     *
     * @param annotDic the annotation {@link PdfDictionary} to be checked
     */
    protected abstract void checkAnnotation(PdfDictionary annotDic);

    /**
     * Checks if entries in catalog dictionary are valid.
     *
     * @param catalogDict the catalog {@link PdfDictionary} to be checked
     */
    protected abstract void checkCatalogValidEntries(PdfDictionary catalogDict);

    /**
     * Verify the conformity of used color spaces on the page.
     *
     * @param pageDict the {@link PdfDictionary} contains contents for colors to be checked
     * @param pageResources the {@link PdfDictionary} contains resources for colors to be checked
     */
    protected abstract void checkPageColorsUsages(PdfDictionary pageDict, PdfDictionary pageResources);

    /**
     * Verify the conformity of the given image.
     *
     * @param image the image to check
     * @param currentColorSpaces the {@link PdfDictionary} containing the color spaces used in the document
     */
    protected abstract void checkImage(PdfStream image, PdfDictionary currentColorSpaces);

    /**
     * Verify the conformity of the file specification dictionary.
     *
     * @param fileSpec the {@link PdfDictionary} containing file specification to be checked
     */
    protected abstract void checkFileSpec(PdfDictionary fileSpec);

    /**
     * Verify the conformity of the form dictionary.
     *
     * @param form the form {@link PdfDictionary} to be checked
     */
    protected abstract void checkForm(PdfDictionary form);

    /**
     * Verify the conformity of the form XObject dictionary.
     *
     * @param form the {@link PdfStream} to check
     */
    protected abstract void checkFormXObject(PdfStream form);

    /**
     * Performs a number of checks on the logical structure of the document.
     *
     * @param catalog the catalog {@link PdfDictionary} to check
     */
    protected abstract void checkLogicalStructure(PdfDictionary catalog);

    /**
     * Performs a number of checks on the metadata of the document.
     *
     * @param catalog the catalog {@link PdfDictionary} to check
     */
    protected abstract void checkMetaData(PdfDictionary catalog);

    /**
     * Verify the conformity of the non-symbolic TrueType font.
     *
     * @param trueTypeFont the {@link PdfTrueTypeFont} to check
     */
    protected abstract void checkNonSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont);

    /**
     * Verify the conformity of the output intents array in the catalog dictionary.
     *
     * @param catalogOrPageDict the {@link PdfDictionary} of a catalog or page to check
     */
    protected abstract void checkOutputIntents(PdfDictionary catalogOrPageDict);

    /**
     * Verify the conformity of the page dictionary.
     *
     * @param page the {@link PdfDictionary} to check
     * @param pageResources the page's resources dictionary
     */
    protected abstract void checkPageObject(PdfDictionary page, PdfDictionary pageResources);

    /**
     * Checks the allowable size of the page.
     *
     * @param page the {@link PdfDictionary} of page which size being checked
     */
    protected abstract void checkPageSize(PdfDictionary page);

    /**
     * Verify the conformity of the PDF array.
     *
     * @param array the {@link PdfArray} to check
     */
    protected abstract void checkPdfArray(PdfArray array);

    /**
     * Verify the conformity of the PDF dictionary.
     *
     * @param dictionary the {@link PdfDictionary} to check
     */
    protected abstract void checkPdfDictionary(PdfDictionary dictionary);

    /**
     * Verify the conformity of the PDF name.
     *
     * @param name the {@link PdfName} to check
     */
    protected abstract void checkPdfName(PdfName name);

    /**
     * Verify the conformity of the PDF number.
     *
     * @param number the {@link PdfNumber} to check
     */
    protected abstract void checkPdfNumber(PdfNumber number);

    /**
     * Verify the conformity of the PDF stream.
     *
     * @param stream the {@link PdfStream} to check
     */
    protected abstract void checkPdfStream(PdfStream stream);

    /**
     * Verify the conformity of the PDF string.
     *
     * @param string the {@link PdfString} to check
     */
    protected abstract void checkPdfString(PdfString string);

    /**
     * Verify the conformity of the symbolic TrueType font.
     *
     * @param trueTypeFont the {@link PdfTrueTypeFont} to check
     */
    protected abstract void checkSymbolicTrueTypeFont(PdfTrueTypeFont trueTypeFont);

    /**
     * Verify the conformity of the trailer dictionary.
     *
     * @param trailer the {@link PdfDictionary} of trailer to check
     */
    protected abstract void checkTrailer(PdfDictionary trailer);

    /**
     * Verify the conformity of the pdf catalog.
     *
     * @param catalog the {@link PdfCatalog} of trailer to check
     */
    protected abstract void checkCatalog(PdfCatalog catalog);

    /**
     * Verify the conformity of the page transparency.
     *
     * @param pageDict the {@link PdfDictionary} contains contents for transparency to be checked
     * @param pageResources the {@link PdfDictionary} contains resources for transparency to be checked
     */
    protected abstract void checkPageTransparency(PdfDictionary pageDict, PdfDictionary pageResources);

    /**
     * Verify the conformity of the resources dictionary.
     *
     * @param resources the {@link PdfDictionary} to be checked
     * @param pdfObject the pdf object to check resources for
     */
    protected void checkResources(PdfDictionary resources, PdfObject pdfObject) {
        if (resources == null)
            return;

        PdfDictionary xObjects = resources.getAsDictionary(PdfName.XObject);
        PdfDictionary shadings = resources.getAsDictionary(PdfName.Shading);
        PdfDictionary patterns = resources.getAsDictionary(PdfName.Pattern);

        if (xObjects != null) {
            for (PdfObject xObject : xObjects.values()) {
                PdfStream xObjStream = (PdfStream) xObject;
                PdfObject subtype = null;
                boolean isFlushed = xObjStream.isFlushed();
                if (!isFlushed) {
                    subtype = xObjStream.get(PdfName.Subtype);
                }

                if (PdfName.Image.equals(subtype)
                        || isFlushed) { // if flushed still may be need to check colorspace in given context
                    checkImage(xObjStream, resources.getAsDictionary(PdfName.ColorSpace));
                } else if (PdfName.Form.equals(subtype)) {
                    checkFormXObject(xObjStream);
                }
            }
        }

        if (shadings != null) {
            for (PdfObject shading : shadings.values()) {
                PdfDictionary shadingDict = (PdfDictionary) shading;
                if (!isAlreadyChecked(shadingDict)) {
                    checkColorSpace(PdfColorSpace.makeColorSpace(shadingDict.get(PdfName.ColorSpace)), pdfObject,
                            resources.getAsDictionary(PdfName.ColorSpace), true, null);
                }
            }
        }

        if (patterns != null) {
            for (PdfObject p : patterns.values()) {
                if (p.isStream()) {
                    PdfStream pStream = (PdfStream) p;
                    if (!isAlreadyChecked(pStream)) {
                        checkResources(pStream.getAsDictionary(PdfName.Resources), pdfObject);
                    }
                }
            }
        }
    }

    /**
     * Checks if the specified flag is set.
     *
     * @param flags a set of flags specifying various characteristics of the PDF object
     * @param flag to be checked
     * @return true if the specified flag is set
     */
    protected static boolean checkFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    /**
     * Checks conformance of PDF/A standard.
     *
     * @param aConformance the {@link PdfAConformance} to be checked
     * @return true if the specified aConformance is <code>a</code> for PDF/A-1, PDF/A-2 or PDF/A-3
     */
    protected static boolean checkStructure(PdfAConformance aConformance) {
        return aConformance == PdfAConformance.PDF_A_1A
                || aConformance == PdfAConformance.PDF_A_2A
                || aConformance == PdfAConformance.PDF_A_3A;
    }

    /**
     * Checks whether the specified dictionary has a transparency group.
     *
     * @param dictionary the {@link PdfDictionary} to check
     * @return true if and only if the specified dictionary has a {@link PdfName#Group} key and its value is
     * a dictionary with {@link PdfName#Transparency} subtype
     */
    protected static boolean isContainsTransparencyGroup(PdfDictionary dictionary) {
        return dictionary.containsKey(PdfName.Group) && PdfName.Transparency.equals(
                dictionary.getAsDictionary(PdfName.Group).getAsName(PdfName.S));
    }

    /**
     * Checks whether the specified dictionary was already checked.
     *
     * @param dictionary the {@link PdfDictionary} to check
     * @return true if the specified dictionary was checked
     */
    protected boolean isAlreadyChecked(PdfDictionary dictionary) {
        if (checkedObjects.contains(dictionary)) {
            return true;
        }
        checkedObjects.add(dictionary);
        return false;
    }

    /**
     * Checks resources of the appearance streams.
     *
     * @param appearanceStreamsDict the dictionary with appearance streams to check.
     */
    protected void checkResourcesOfAppearanceStreams(PdfDictionary appearanceStreamsDict) {
        checkResourcesOfAppearanceStreams(appearanceStreamsDict, new HashSet<PdfObject>());
    }

    /**
     * Check single annotation appearance stream.
     *
     * @param appearanceStream the {@link PdfStream} to check
     */
    protected void checkAppearanceStream(PdfStream appearanceStream) {
        if (isAlreadyChecked(appearanceStream)) {
            return;
        }

        checkResources(appearanceStream.getAsDictionary(PdfName.Resources), appearanceStream);
    }

    PdfDictionary getPdfAOutputIntent(PdfArray outputIntents) {
        for (int i = 0; i < outputIntents.size(); ++i) {
            PdfName outputIntentSubtype = outputIntents.getAsDictionary(i).getAsName(PdfName.S);
            if (PdfName.GTS_PDFA1.equals(outputIntentSubtype)) {
                return outputIntents.getAsDictionary(i);
            }
        }

        return null;
    }

    private void checkResourcesOfAppearanceStreams(PdfDictionary appearanceStreamsDict, Set<PdfObject> checkedObjects) {
        if (checkedObjects.contains(appearanceStreamsDict)) {
            return;
        } else {
            checkedObjects.add(appearanceStreamsDict);
        }
        for (PdfObject val : appearanceStreamsDict.values()) {
            if (val instanceof PdfDictionary) {
                PdfDictionary ap = (PdfDictionary) val;
                if (ap.isDictionary()) {
                    checkResourcesOfAppearanceStreams(ap, checkedObjects);
                } else if (ap.isStream()) {
                    checkAppearanceStream((PdfStream) ap);
                }
            }
        }
    }

    private void checkArrayRecursively(PdfArray array) {
        for (int i = 0; i < array.size(); i++) {
            PdfObject object = array.get(i, false);
            if (object != null && ! object.isIndirect()) {
                checkPdfObject(object);
            }
        }
    }

    private void checkDictionaryRecursively(PdfDictionary dictionary) {
        for (PdfName name: dictionary.keySet()) {
            checkPdfName(name);
            PdfObject object = dictionary.get(name, false);
            if (object != null && ! object.isIndirect()) {
                checkPdfObject(object);
            }
        }
    }

    private void checkPages(PdfDocument document) {
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            checkPage(document.getPage(i));
        }
    }

    private void checkPage(PdfPage page) {
        PdfDictionary pageDict = page.getPdfObject();

        if (isAlreadyChecked(pageDict)) return;

        checkPageObject(pageDict, page.getResources().getPdfObject());
        PdfDictionary pageResources = page.getResources().getPdfObject();
        checkResources(pageResources, pageDict);
        checkAnnotations(pageDict);
        checkPageSize(pageDict);
        checkPageTransparency(pageDict, page.getResources().getPdfObject());
        checkPageColorsUsages(pageDict, page.getResources().getPdfObject());
        //This check is valid for pdf/a-4 only, but it's not a problem
        //to add additional restrictions on earlier versions
        checkOutputIntents(pageDict);

        int contentStreamCount = page.getContentStreamCount();
        for (int j = 0; j < contentStreamCount; ++j) {
            PdfStream contentStream = page.getContentStream(j);
            checkContentStream(contentStream);
            checkedObjects.add(contentStream);
        }
    }

    private void checkOpenAction(PdfObject openAction) {
        if (openAction != null && openAction.isDictionary()) {
            checkAction((PdfDictionary) openAction);
        }
    }

    private void checkAnnotations(PdfDictionary page) {
        PdfArray annots = page.getAsArray(PdfName.Annots);
        if (annots != null) {
            for (int i = 0; i < annots.size(); i++) {
                PdfDictionary annot = annots.getAsDictionary(i);
                checkAnnotation(annot);
                PdfDictionary action = annot.getAsDictionary(PdfName.A);
                if (action != null) {
                    checkAction(action);
                }
            }
        }
    }

    private void checkOutlines(PdfOutline outline){
        if (outline != null) {
            final PdfDictionary action = outline.getContent().getAsDictionary(PdfName.A);
            if (action != null) {
                checkAction(action);
            }
            for (PdfOutline child : outline.getAllChildren()) {
                checkOutlines(child);
            }
        }
    }

    private void setCheckerOutputIntent(PdfDictionary outputIntent) {
        if (outputIntent != null) {
            pdfAOutputIntentDestProfile = outputIntent.getAsStream(PdfName.DestOutputProfile);
            if (pdfAOutputIntentDestProfile != null) {
                String intentCS = IccProfile.getIccColorSpaceName(pdfAOutputIntentDestProfile.getBytes());
                pdfAOutputIntentColorSpace = intentCS;
            }
        }
    }
}
