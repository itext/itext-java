package com.itextpdf.pdfa.checker;

import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.canvas.CanvasGraphicsState;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.PdfAConformanceLevel;

import java.util.*;

public abstract class PdfAChecker {
    public static final String ICC_COLOR_SPACE_RGB = "RGB ";
    public static final String ICC_COLOR_SPACE_CMYK = "CMYK";
    public static final String ICC_COLOR_SPACE_GRAY = "GRAY";

    public static final String ICC_DEVICE_CLASS_OUTPUT_PROFILE = "prtr";
    public static final String ICC_DEVICE_CLASS_MONITOR_PROFILE = "mntr";

    static public final int maxGsStackDepth = 28;

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
    protected HashSet<PdfObject> checkedObjects = new HashSet<>();
    protected Map<PdfObject, PdfColorSpace> checkedObjectsColorspace = new HashMap<>();

    public PdfAChecker(PdfAConformanceLevel conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
    }

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
        checkPages(catalog);
        checkOpenAction(catalogDict.get(PdfName.OpenAction));
        checkColorsUsages();
    }

    public void checkSinglePage(PdfPage page) {
        checkPage(page);
    }


    public void checkPdfObject(PdfObject obj) {
        switch (obj.getType()) {
            case PdfObject.Number:
                checkPdfNumber((PdfNumber) obj);
                break;
            case PdfObject.Stream:
                PdfStream stream = (PdfStream) obj;
                //form xObjects, annotation appearance streams, patterns and type3 glyphs may have their own resources dictionary
                checkResources(stream.getAsDictionary(PdfName.Resources));
                checkPdfStream(stream);
                break;
            case PdfObject.String:
                checkPdfString((PdfString) obj);
                break;
            case PdfObject.Dictionary:
                PdfDictionary dict = (PdfDictionary) obj;
                PdfName type = dict.getAsName(PdfName.Type);
                if (PdfName.Filespec.equals(type)) {
                    checkFileSpec(dict);
                }
                break;
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    public boolean objectIsChecked(PdfObject object) {
        return checkedObjects.contains(object);
    }

    public abstract void checkCanvasStack(char stackOperation);
    public abstract void checkInlineImage(PdfStream inlineImage, PdfDictionary currentColorSpaces);
    public abstract void checkColor(Color color, PdfDictionary currentColorSpaces, Boolean fill);
    public abstract void checkColorSpace(PdfColorSpace colorSpace, PdfDictionary currentColorSpaces, boolean checkAlternate, Boolean fill);
    public abstract void checkRenderingIntent(PdfName intent);
    public abstract void checkExtGState(CanvasGraphicsState extGState);

    protected abstract HashSet<PdfName> getForbiddenActions();
    protected abstract HashSet<PdfName> getAllowedNamedActions();
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
                checkColorSpace(PdfColorSpace.makeColorSpace(shadingDict.get(PdfName.ColorSpace), null), resources.getAsDictionary(PdfName.ColorSpace), true, null);
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

    private void checkPages(PdfCatalog catalog) {
        for (int i = 1; i <= catalog.getNumOfPages(); i++) {
            checkPage(catalog.getPage(i));
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
            for (PdfObject annot : annots) {
                PdfDictionary annotDic = (PdfDictionary) annot;
                checkAnnotation(annotDic);
                PdfDictionary action = annotDic.getAsDictionary(PdfName.A);
                if (action != null) {
                    checkAction(action);
                }
                action = annotDic.getAsDictionary(PdfName.AA);
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
