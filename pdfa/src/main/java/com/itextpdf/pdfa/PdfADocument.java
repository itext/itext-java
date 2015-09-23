package com.itextpdf.pdfa;

import com.itextpdf.basics.color.IccProfile;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.canvas.PdfGraphicsState;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.xmp.*;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;
import com.itextpdf.pdfa.xmp.PdfAXMPUtil;

import java.io.IOException;

public class PdfADocument extends PdfDocument {
    private PdfAChecker checker;

    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(writer);
        setChecker(conformanceLevel);
        addOutputIntent(outputIntent);
    }

    public PdfADocument(PdfReader reader, PdfWriter writer, PdfAConformanceLevel conformanceLevel) {
        this(reader, writer, false, conformanceLevel);
    }
    public PdfADocument(PdfReader reader, PdfWriter writer, boolean append, PdfAConformanceLevel conformanceLevel) {
        super(reader, writer, append);

        //todo check document conformance level compatibility with one passed to constructor
//        XMPMeta meta = XMPMetaFactory.parseFromBuffer(getXmpMetadata().getBytes());
//        meta.getProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART).getValue();
//        String prop = meta.getProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE).getValue();

        setChecker(conformanceLevel);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key) {
        checkIsoConformance(obj, key, null);
    }

    @Override
    public void checkShowTextIsoConformance(Object obj, PdfResources resources) {
        PdfGraphicsState gState = (PdfGraphicsState) obj;
        boolean fill = false;
        boolean stroke = false;

        if (gState.getTextRenderingMode() != null) {
            switch (gState.getTextRenderingMode()) {
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_STROKE:
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_STROKE_CLIP:
                    stroke = true;
                    break;
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_FILL:
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_FILL_CLIP:
                    fill = true;
                    break;
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE:
                case PdfCanvasConstants.TextRenderingMode.TEXT_RENDERING_MODE_FILL_STROKE_CLIP:
                    stroke = true;
                    fill = true;
                    break;
            }
        } else {
            fill = true;
        }

        IsoKey drawMode = null;
        if (fill && stroke) {
            drawMode = IsoKey.DRAWMODE_FILL_STROKE;
        } else if (fill) {
            drawMode = IsoKey.DRAWMODE_FILL;
        } else if (stroke) {
            drawMode = IsoKey.DRAWMODE_STROKE;
        }

        if (fill || stroke) {
            checkIsoConformance(gState, drawMode, resources);
        }
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources) {
        PdfGraphicsState gState;
        PdfDictionary currentColorSpaces = null;
        if (resources != null) {
            currentColorSpaces = resources.getPdfObject().getAsDictionary(PdfName.ColorSpace);
        }
        switch (key) {
            case CANVAS_STACK:
                checker.checkCanvasStack((Character) obj);
                break;
            case PDF_OBJECT:
                checker.checkPdfObject((PdfObject) obj);
                break;
            case RENDERING_INTENT:
                checker.checkRenderingIntent((PdfName) obj);
                break;
            case INLINE_IMAGE:
                checker.checkInlineImage((PdfStream) obj, currentColorSpaces);
                break;
            case GRAPHIC_STATE_ONLY:
                gState = (PdfGraphicsState) obj;
                checker.checkExtGState(gState);
                break;
            case DRAWMODE_FILL:
                gState = (PdfGraphicsState) obj;
                checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                checker.checkExtGState(gState);
                break;
            case DRAWMODE_STROKE:
                gState = (PdfGraphicsState) obj;
                checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                checker.checkExtGState(gState);
                break;
            case DRAWMODE_FILL_STROKE:
                gState = (PdfGraphicsState) obj;
                checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                checker.checkExtGState(gState);
                break;
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    public void addOutputIntent(PdfOutputIntent outputIntent) {
        if (outputIntent == null)
            return;

        PdfArray outputIntents = catalog.getPdfObject().getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            outputIntents = new PdfArray();
            catalog.put(PdfName.OutputIntents, outputIntents);
        }
        outputIntents.add(outputIntent.getPdfObject());
    }

    @Override
    protected void checkIsoConformance() {
        checker.checkDocument(catalog);
    }

    protected void addRdfDescription(XMPMeta xmpMeta) throws XMPException {
        switch (checker.getConformanceLevel()) {
            case PDF_A_1A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "1");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_1B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "1");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_2A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_2B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_2U:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "U");
                break;
            case PDF_A_3A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "3");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_3B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "3");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_3U:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART, "3");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "U");
                break;
            default:
                break;
        }
        if (this.isTagged()) {
            XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PdfAXMPUtil.PDF_UA_EXTENSION);
            XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
        }
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        if (isClosing) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else {
            //suppress the call
        }
    }

    @Override
    protected void flushFonts() {
        for (PdfFont pdfFont: getDocumentFonts()) {
            if (!pdfFont.isEmbedded()) {
                throw new PdfAConformanceException(PdfAConformanceException.AllFontsMustBeEmbeddedThisOneIsnt1)
                        .setMessageParams(pdfFont.getFontProgram().getFontNames().getFontName());
            }
        }
        super.flushFonts();
    }

    private void setChecker(PdfAConformanceLevel conformanceLevel) {
        switch (conformanceLevel) {
            case PDF_A_1A:
            case PDF_A_1B:
                checker = new PdfA1Checker(conformanceLevel);
                break;
            case PDF_A_2A:
            case PDF_A_2B:
            case PDF_A_2U:
                checker = new PdfA2Checker(conformanceLevel);
                break;
            case PDF_A_3A:
            case PDF_A_3B:
            case PDF_A_3U:
                checker = new PdfA3Checker(conformanceLevel);
                break;
        }
    }
}
