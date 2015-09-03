package com.itextpdf.pdfa.checker;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.pdfa.PdfAConformanceLevel;

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

        checkCatalog(catalogDict);

        if (catalogDict.containsKey(PdfName.AcroForm)){
            checkForm(catalogDict.getAsDictionary(PdfName.AcroForm));
        }

        for (int i = 1; i <= catalog.getNumOfPages(); i++) {
            PdfPage p = catalog.getPage(i);
            PdfDictionary pageResources = p.getPdfObject().getAsDictionary(PdfName.Resources);
            checkResources(pageResources);
            PdfArray annots = p.getPdfObject().getAsArray(PdfName.Annots);
            if (annots != null) {
                checkAnnotations(annots);
            }
        }


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

    protected abstract void checkAction(PdfDictionary action);
    protected abstract void checkExtGState(PdfDictionary extGState);
    protected abstract void checkImageXObject(PdfStream image);
    protected abstract void checkFormXObject(PdfStream form);
    protected abstract void checkFont(PdfDictionary font);
    protected abstract void checkPdfNumber(PdfNumber number);
    protected abstract void checkPdfStream(PdfStream stream);
    protected abstract void checkPdfString(PdfString string);
    protected abstract void checkAnnotations(PdfArray annotations);
    protected abstract void checkForm(PdfDictionary form);
    protected abstract void checkCatalog(PdfDictionary catalog);

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

    protected static boolean checkFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    protected static boolean checkStructure(PdfAConformanceLevel conformanceLevel) {
        return conformanceLevel == PdfAConformanceLevel.PDF_A_1A
                || conformanceLevel == PdfAConformanceLevel.PDF_A_2A
                || conformanceLevel == PdfAConformanceLevel.PDF_A_3A;
    }
}
