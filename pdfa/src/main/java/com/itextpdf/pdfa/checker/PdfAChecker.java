package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class PdfAChecker {
    public static final String ICC_COLOR_SPACE_RGB = "RGB ";
    public static final String ICC_COLOR_SPACE_CMYK = "CMYK";
    public static final String ICC_COLOR_SPACE_GRAY = "GRAY";

    protected PdfAConformanceLevel conformanceLevel;
    protected String pdfAOutputIntentColorSpace;

    public PdfAChecker(PdfAConformanceLevel conformanceLevel, String pdfAOutputIntentColorSpace) {
        this.conformanceLevel = conformanceLevel;
        this.pdfAOutputIntentColorSpace = pdfAOutputIntentColorSpace;
    }

    public void checkDocument(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();

        checkCatalogValidEntries(catalogDict);
        checkTrailer(catalog.getDocument().getTrailer());
        checkForm(catalogDict.getAsDictionary(PdfName.AcroForm));
        checkOpenAction(catalogDict.get(PdfName.OpenAction));
        checkOutlines(catalogDict);
        checkPages(catalog);
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
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    public abstract void checkCanvasStack(char stackOperation);
    public abstract void checkInlineImage(PdfImageXObject inlineImage);

    protected abstract HashSet<PdfName> getForbiddenActions();
    protected abstract HashSet<PdfName> getAllowedNamedActions();
    protected abstract void checkExtGState(PdfDictionary extGState);
    protected abstract void checkImageXObject(PdfStream image);
    protected abstract void checkFormXObject(PdfStream form);
    protected abstract void checkFont(PdfDictionary font);
    protected abstract void checkPdfNumber(PdfNumber number);
    protected abstract void checkPdfStream(PdfStream stream);
    protected abstract void checkPdfString(PdfString string);
    protected abstract void checkAnnotation(PdfDictionary annotDic);
    protected abstract void checkForm(PdfDictionary form);
    protected abstract void checkCatalogValidEntries(PdfDictionary catalogDict);
    protected abstract void checkPage(PdfDictionary pageDict);
    protected abstract void checkTrailer(PdfDictionary trailer);

    protected void checkResources(PdfDictionary resources) {
        if (resources == null)
            return;

        PdfDictionary extGStates = resources.getAsDictionary(PdfName.ExtGState);
        PdfDictionary patterns = resources.getAsDictionary(PdfName.Pattern);
        PdfDictionary fonts = resources.getAsDictionary(PdfName.Font);
        PdfDictionary xObjects = resources.getAsDictionary(PdfName.XObject);

        if (extGStates != null) {
            for (PdfName name : extGStates.keySet()) {
                PdfDictionary extGStateDict = extGStates.getAsDictionary(name);
                checkExtGState(extGStateDict);
            }
        }

        if (patterns != null) {
            int shadingPatternType = 2;
            for (PdfName name : patterns.keySet()) {
                PdfDictionary patternDict = patterns.getAsDictionary(name);
                if (patternDict.getAsInt(PdfName.PatternType) == shadingPatternType) {
                    checkExtGState(patternDict.getAsDictionary(PdfName.ExtGState));
                }
            }
        }

        if (fonts != null) {
            for (PdfName name : fonts.keySet()) {
                PdfDictionary fontDict = fonts.getAsDictionary(name);
                checkFont(fontDict);
            }
        }

        if (xObjects != null) {
            for (PdfName name : xObjects.keySet()) {
                PdfStream xObjStream = xObjects.getAsStream(name);
                PdfObject subtype = xObjStream.get(PdfName.Subtype);
                if (PdfName.Image.equals(subtype)) {
                    checkImageXObject(xObjStream);
                } else if (PdfName.Form.equals(subtype)) {
                    checkFormXObject(xObjStream);
                }
            }
        }
    }

    protected void checkAction(PdfDictionary action) {
        PdfName s = action.getAsName(PdfName.S);
        if (getForbiddenActions().contains(s)) {
            throw new PdfAConformanceException(PdfAConformanceException._1ActionsIsNotAllowed).setMessageParams(s.getValue());
        }
        if (s.equals(PdfName.Named)) {
            PdfName n = action.getAsName(PdfName.N);
            if (n != null && !getAllowedNamedActions().contains(n)) {
                throw new PdfAConformanceException(PdfAConformanceException.NamedActionType1IsNotAllowed).setMessageParams(n.getValue());
            }
        }
        if (s.equals(PdfName.SetState) || s.equals(PdfName.NoOp)) {
            throw new PdfAConformanceException(PdfAConformanceException.DeprecatedSetStateAndNoOpActionsAreNotAllowed);
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

    private void checkPages(PdfCatalog catalog) {
        for (int i = 1; i <= catalog.getNumOfPages(); i++) {
            PdfPage p = catalog.getPage(i);
            PdfDictionary pageDict = p.getPdfObject();
            PdfDictionary pageResources = pageDict.getAsDictionary(PdfName.Resources);
            checkPage(pageDict);
            checkResources(pageResources);
            checkAnnotations(pageDict);
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
}
