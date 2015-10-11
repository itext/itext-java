package com.itextpdf.pdfa;

import com.itextpdf.canvas.CanvasGraphicsState;
import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.IsoKey;
import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.xmp.PdfAXMPUtil;
import com.itextpdf.core.xmp.XMPConst;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.properties.XMPProperty;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;

import java.io.IOException;

public class PdfADocument extends PdfDocument {
    private PdfAChecker checker;
    private int gStateIndex = -1;

    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(writer);
        setChecker(conformanceLevel);
        addOutputIntent(outputIntent);
    }

    public PdfADocument(PdfReader reader, PdfWriter writer) throws XMPException {
        this(reader, writer, false);
    }
    public PdfADocument(PdfReader reader, PdfWriter writer, boolean append) throws XMPException {
        super(reader, writer, append);

        PdfStream existingXmpMetadata = getXmpMetadata();
        if (existingXmpMetadata == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }
        XMPMeta meta = XMPMetaFactory.parseFromBuffer(existingXmpMetadata.getBytes());
        XMPProperty conformanceXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE);
        XMPProperty partXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.PART);
        if (conformanceXmpProperty == null || partXmpProperty == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }
        String conformance = conformanceXmpProperty.getValue();
        String part = partXmpProperty.getValue();
        PdfAConformanceLevel conformanceLevel = getConformanceLevel(conformance, part);

        setChecker(conformanceLevel);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key) {
        checkIsoConformance(obj, key, null, 0);
    }

    @Override
    public void checkShowTextIsoConformance(Object obj, PdfResources resources, int gStateIndex) {
        CanvasGraphicsState gState = (CanvasGraphicsState) obj;
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
            checkIsoConformance(gState, drawMode, resources, gStateIndex);
        }
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources, int gStateIndex) {
        CanvasGraphicsState gState;
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
                if (this.gStateIndex != gStateIndex) {
                    gState = (CanvasGraphicsState) obj;
                    checker.checkExtGState(gState);
                    this.gStateIndex = gStateIndex;
                }
                break;
            case DRAWMODE_FILL:
                if (this.gStateIndex != gStateIndex) {
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                    checker.checkExtGState(gState);
                    this.gStateIndex = gStateIndex;
                }
                break;
            case DRAWMODE_STROKE:
                if (this.gStateIndex != gStateIndex) {
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                    checker.checkExtGState(gState);
                    this.gStateIndex = gStateIndex;
                }
                break;
            case DRAWMODE_FILL_STROKE:
                if (this.gStateIndex != gStateIndex) {
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                    checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                    checker.checkExtGState(gState);
                    this.gStateIndex = gStateIndex;
                }
                break;
            case PAGE:
                checker.checkSinglePage((PdfPage)obj);
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    @Override
    public void setXmpMetadata() throws XMPException {
        setXmpMetadata(checker.getConformanceLevel());
    }

        @Override
    protected void checkIsoConformance() {
        checker.checkDocument(catalog);
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        if (isClosing || checker.objectIsChecked(pdfObject)) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else {
            //suppress the call
            //TODO log unsuccessful call
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

    private PdfAConformanceLevel getConformanceLevel(String conformance, String part) {
        String lowLetter = part.toUpperCase();
        boolean aLevel = lowLetter.equals("A");
        boolean bLevel = lowLetter.equals("B");
        boolean uLevel = lowLetter.equals("U");

        if (conformance.equals("1")) {
            if (aLevel)
                return PdfAConformanceLevel.PDF_A_1A;
            if (bLevel)
                return PdfAConformanceLevel.PDF_A_1B;
        } else if (conformance.equals("2")) {
            if (aLevel)
                return PdfAConformanceLevel.PDF_A_2A;
            if (bLevel)
                return PdfAConformanceLevel.PDF_A_2B;
            if (uLevel)
                return PdfAConformanceLevel.PDF_A_2U;
        } else if (conformance.equals("3")) {
            if (aLevel)
                return PdfAConformanceLevel.PDF_A_3A;
            if (bLevel)
                return PdfAConformanceLevel.PDF_A_3B;
            if (uLevel)
                return PdfAConformanceLevel.PDF_A_3U;
        }
        return null;
    }
}
