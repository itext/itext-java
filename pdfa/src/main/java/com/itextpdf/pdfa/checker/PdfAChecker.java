/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.pdfa.checker;

import com.itextpdf.io.color.IccProfile;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;

import java.util.*;
import java.util.logging.Logger;

/**
 * An abstract class that will run through all necessary checks defined in the
 * different PDF/A standards and levels. A number of common checks are executed
 * in this class, while standard-dependent specifications are implemented in the
 * available subclasses.
 * 
 * While it is possible to subclass this method and implement its abstract
 * methods in client code, this is not encouraged and will have little effect.
 * It is not possible to plug custom implementations into iText, because iText
 * should always refuse to create non-compliant PDF/A, which would be possible
 * with client code implementations. Any future generations of the PDF/A
 * standard and its derivates will get their own implementation in the
 * iText 7 - pdfa project.
 */
public abstract class PdfAChecker {

    /**
     * @deprecated Use slf4j logging instead.
     */
    @Deprecated
    protected Logger LOGGER = Logger.getLogger(getClass().getName());

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

    protected PdfAConformanceLevel conformanceLevel;
    protected String pdfAOutputIntentColorSpace;

    protected int gsStackDepth = 0;
    protected boolean rgbIsUsed = false;
    protected boolean cmykIsUsed = false;
    protected boolean grayIsUsed = false;

    /**
     * Contains some objects that are already checked.
     * NOTE: Not all objects that were checked are stored in that set. This set is used for avoiding double checks for
     * actions, xObjects and page objects; and for letting those objects to be manually flushed.
     *
     * Use this mechanism carefully: objects that are able to be changed (or at least if object's properties
     * that shall be checked are able to be changed) shouldn't be marked as checked if they are not to be
     * flushed immediately.
     */
    protected Set<PdfObject> checkedObjects = new HashSet<>();
    protected Map<PdfObject, PdfColorSpace> checkedObjectsColorspace = new HashMap<>();

    protected PdfAChecker(PdfAConformanceLevel conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
    }

    /**
     * This method checks a number of document-wide requirements of the PDF/A
     * standard. The algorithms of some of these checks vary with the PDF/A
     * level and thus are implemented in subclasses; others are implemented
     * as private methods in this class.
     * 
     * @param catalog
     */
    public void checkDocument(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        setPdfAOutputIntentColorSpace(catalogDict);

        checkOutputIntents(catalogDict);
        checkMetaData(catalogDict);
        checkCatalogValidEntries(catalogDict);
        checkTrailer(catalog.getDocument().getTrailer());
        checkLogicalStructure(catalogDict);
        checkForm(catalogDict.getAsDictionary(PdfName.AcroForm));
        checkOutlines(catalogDict);
        checkPages(catalog.getDocument());
        checkOpenAction(catalogDict.get(PdfName.OpenAction));
        checkColorsUsages();
    }

    /**
     * This method checks all requirements that must be fulfilled by a page in a
     * PDF/A document.
     * @param page the page that must be checked
     */
    public void checkSinglePage(PdfPage page) {
        checkPage(page);
    }


    /**
     * This method checks the requirements that must be fulfilled by a COS
     * object in a PDF/A document.
     * @param obj the COS object that must be checked
     */
    public void checkPdfObject(PdfObject obj) {
        switch (obj.getType()) {
            case PdfObject.NUMBER:
                checkPdfNumber((PdfNumber) obj);
                break;
            case PdfObject.STREAM:
                PdfStream stream = (PdfStream) obj;
                //form xObjects, annotation appearance streams, patterns and type3 glyphs may have their own resources dictionary
                checkResources(stream.getAsDictionary(PdfName.Resources));
                checkPdfStream(stream);
                break;
            case PdfObject.STRING:
                checkPdfString((PdfString) obj);
                break;
            case PdfObject.DICTIONARY:
                PdfDictionary dict = (PdfDictionary) obj;
                PdfName type = dict.getAsName(PdfName.Type);
                if (PdfName.Filespec.equals(type)) {
                    checkFileSpec(dict);
                }
                break;
        }
    }

    /**
     * Gets the {@link PdfAConformanceLevel} for this file.
     * 
     * @return the defined conformance level for this document.
     */
    public PdfAConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    /**
     * Remembers which objects have already been checked, in order to avoid
     * redundant checks.
     * 
     * @param object the object to check
     * @return whether or not the object has already been checked
     */
    public boolean objectIsChecked(PdfObject object) {
        return checkedObjects.contains(object);
    }

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
     * @param color the color to check
     * @param currentColorSpaces a {@link PdfDictionary} containing the color spaces used in the document
     * @param fill
     */
    public abstract void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill);
    public abstract void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate, Boolean fill);
    public abstract void checkRenderingIntent(PdfName intent);
    public abstract void checkExtGState(CanvasGraphicsState extGState);

    protected abstract Set<PdfName> getForbiddenActions();
    protected abstract Set<PdfName> getAllowedNamedActions();
    protected abstract void checkAction(PdfDictionary action);
    protected abstract void checkAnnotation(PdfDictionary annotDic);
    protected abstract void checkCatalogValidEntries(PdfDictionary catalogDict);
    protected abstract void checkColorsUsages();
    protected abstract void checkImage(PdfStream image, PdfDictionary currentColorSpaces);
    protected abstract void checkFileSpec(PdfDictionary fileSpec);
    protected abstract void checkForm(PdfDictionary form);
    protected abstract void checkFormXObject(PdfStream form);
    protected abstract void checkLogicalStructure(PdfDictionary catalog);
    protected abstract void checkMetaData(PdfDictionary catalog);
    protected abstract void checkOutputIntents(PdfDictionary catalog);
    protected abstract void checkPageObject(PdfDictionary page, PdfDictionary pageResources);
    protected abstract void checkPageSize(PdfDictionary page);
    protected abstract void checkPdfNumber(PdfNumber number);
    protected abstract void checkPdfStream(PdfStream stream);
    protected abstract void checkPdfString(PdfString string);
    protected abstract void checkTrailer(PdfDictionary trailer);



    protected void checkResources(PdfDictionary resources) {
        if (resources == null)
            return;

        PdfDictionary xObjects = resources.getAsDictionary(PdfName.XObject);
        PdfDictionary shadings = resources.getAsDictionary(PdfName.Shading);

        if (xObjects != null) {
            for (PdfObject xObject : xObjects.values()) {
                PdfStream xObjStream = (PdfStream) xObject;
                PdfObject subtype = xObjStream.get(PdfName.Subtype);
                if (PdfName.Image.equals(subtype)) {
                    checkImage(xObjStream, resources.getAsDictionary(PdfName.ColorSpace));
                } else if (PdfName.Form.equals(subtype)) {
                    checkFormXObject(xObjStream);
                }
            }
        }

        if (shadings != null) {
            for (PdfObject shading : shadings.values()) {
                PdfDictionary shadingDict = (PdfDictionary) shading;
                checkColorSpace(PdfColorSpace.makeColorSpace(shadingDict.get(PdfName.ColorSpace)), resources.getAsDictionary(PdfName.ColorSpace), true, null);
            }
        }
    }

    protected static boolean checkFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    protected static boolean checkStructure(PdfAConformanceLevel conformanceLevel) {
        return conformanceLevel == PdfAConformanceLevel.PDF_A_1A
                || conformanceLevel == PdfAConformanceLevel.PDF_A_2A
                || conformanceLevel == PdfAConformanceLevel.PDF_A_3A;
    }

    protected boolean isAlreadyChecked(PdfDictionary dictionary) {
        if (checkedObjects.contains(dictionary)) {
            return true;
        }
        checkedObjects.add(dictionary);
        return false;
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
        checkResources(pageResources);
        checkAnnotations(pageDict);
        checkPageSize(pageDict);


        int contentStreamCount = page.getContentStreamCount();
        for (int j = 0; j < contentStreamCount; ++j) {
            checkedObjects.add(page.getContentStream(j));
        }
    }

    private void checkOpenAction(PdfObject openAction) {
        if (openAction == null)
            return;

        if (openAction.isDictionary()) {
            checkAction((PdfDictionary) openAction);
        } else if (openAction.isArray()) {
            PdfArray actions = (PdfArray) openAction;
            for (PdfObject action : actions) {
                checkAction((PdfDictionary) action);
            }
        }
    }

    private void checkAnnotations(PdfDictionary page) {
        PdfArray annots = page.getAsArray(PdfName.Annots);
        if (annots != null) {
            // explicit iteration to resolve indirect references on get().
            // TODO DEVSIX-591
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

    private void checkOutlines(PdfDictionary catalogDict){
        PdfDictionary outlines = catalogDict.getAsDictionary(PdfName.Outlines);
        if (outlines != null) {
            for (PdfDictionary outline : getOutlines(outlines)) {
                PdfDictionary action = outline.getAsDictionary(PdfName.A);
                if (action != null) {
                    checkAction(action);
                }
            }
        }
    }

    private List<PdfDictionary> getOutlines(PdfDictionary item) {
        List<PdfDictionary> outlines = new ArrayList<>();
        outlines.add(item);

        PdfDictionary processItem = item.getAsDictionary(PdfName.First);
        if (processItem != null){
            outlines.addAll(getOutlines(processItem));
        }
        processItem = item.getAsDictionary(PdfName.Next);
        if (processItem != null){
            outlines.addAll(getOutlines(processItem));
        }

        return outlines;
    }

    private void setPdfAOutputIntentColorSpace(PdfDictionary catalog) {
        PdfArray outputIntents = catalog.getAsArray(PdfName.OutputIntents);
        if (outputIntents == null)
            return;

        PdfDictionary pdfAOutputIntent = getPdfAOutputIntent(outputIntents);
        setCheckerOutputIntent(pdfAOutputIntent);
    }

    private PdfDictionary getPdfAOutputIntent(PdfArray outputIntents) {
        for (int i = 0; i < outputIntents.size(); ++i) {
            PdfName outputIntentSubtype = outputIntents.getAsDictionary(i).getAsName(PdfName.S);
            if (PdfName.GTS_PDFA1.equals(outputIntentSubtype)) {
                return outputIntents.getAsDictionary(i);
            }
        }

        return null;
    }

    private void setCheckerOutputIntent(PdfDictionary outputIntent) {
        if (outputIntent != null) {
            PdfStream destOutputProfile = outputIntent.getAsStream(PdfName.DestOutputProfile);
            if (destOutputProfile != null) {
                String intentCS = IccProfile.getIccColorSpaceName(destOutputProfile.getBytes());
                this.pdfAOutputIntentColorSpace = intentCS;
            }
        }
    }
}
