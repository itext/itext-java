package com.itextpdf.pdfa;

import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.xmp.*;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.properties.XMPProperty;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;

import java.io.IOException;

public class PdfADocument extends PdfDocument {
    protected PdfAChecker checker;

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

        byte[] existingXmpMetadata = getXmpMetadata();
        if (existingXmpMetadata == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }
        XMPMeta meta = XMPMetaFactory.parseFromBuffer(existingXmpMetadata);
        XMPProperty conformanceXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE);
        XMPProperty partXmpProperty = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART);
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
        checkIsoConformance(obj, key, null);
    }

    @Override
    public void checkShowTextIsoConformance(Object obj, PdfResources resources) {
        CanvasGraphicsState gState = (CanvasGraphicsState) obj;
        boolean fill = false;
        boolean stroke = false;

        switch (gState.getTextRenderingMode()) {
            case PdfCanvasConstants.TextRenderingMode.STROKE:
            case PdfCanvasConstants.TextRenderingMode.STROKE_CLIP:
                stroke = true;
                break;
            case PdfCanvasConstants.TextRenderingMode.FILL:
            case PdfCanvasConstants.TextRenderingMode.FILL_CLIP:
                fill = true;
                break;
            case PdfCanvasConstants.TextRenderingMode.FILL_STROKE:
            case PdfCanvasConstants.TextRenderingMode.FILL_STROKE_CLIP:
                stroke = true;
                fill = true;
                break;
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
                    gState = (CanvasGraphicsState) obj;
                    checker.checkExtGState(gState);
                break;
            case DRAWMODE_FILL:
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                    checker.checkExtGState(gState);
                break;
            case DRAWMODE_STROKE:
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                    checker.checkExtGState(gState);
                break;
            case DRAWMODE_FILL_STROKE:
                    gState = (CanvasGraphicsState) obj;
                    checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                    checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                    checker.checkExtGState(gState);
                break;
            case PAGE:
                checker.checkSinglePage((PdfPage)obj);
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    @Override
    public void createXmpMetadata() throws XMPException {
        createXmpMetadata(checker.getConformanceLevel());
    }

        @Override
    protected void checkIsoConformance() {
        checker.checkDocument(catalog);
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        markObjectAsMustBeFlushed(pdfObject);
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

    protected void setChecker(PdfAConformanceLevel conformanceLevel) {
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

    protected void addRdfDescription(XMPMeta xmpMeta, PdfAConformanceLevel conformanceLevel) throws XMPException {
        switch (conformanceLevel) {
            case PDF_A_1A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "1");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_1B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "1");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_2A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_2B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_2U:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "U");
                break;
            case PDF_A_3A:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "3");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "A");
                break;
            case PDF_A_3B:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "3");
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, PdfAXMPUtil.CONFORMANCE, "B");
                break;
            case PDF_A_3U:
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "3");
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

    public void createXmpMetadata(PdfAConformanceLevel conformanceLevel) throws XMPException {
        checkClosingStatus();
        XMPMeta xmpMeta = XMPMetaFactory.create();
        xmpMeta.setObjectName(XMPConst.TAG_XMPMETA);
        xmpMeta.setObjectName("");
        try {
            xmpMeta.setProperty(XMPConst.NS_DC, PdfConst.Format, "application/pdf");
            xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, Version.getInstance().getVersion());
        } catch (XMPException ignored) {
        }
        PdfDictionary docInfo = info.getPdfObject();
        if (docInfo != null) {
            PdfName key;
            PdfObject obj;
            String value;
            for (PdfName pdfName : docInfo.keySet()) {
                key = pdfName;
                obj = docInfo.get(key);
                if (obj == null)
                    continue;
                if (obj.getType() != PdfObject.String)
                    continue;
                value = ((PdfString) obj).toUnicodeString();
                if (PdfName.Title.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Title, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Author.equals(key)) {
                    xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Creator, new PropertyOptions(PropertyOptions.ARRAY_ORDERED), value, null);
                } else if (PdfName.Subject.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Description, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Keywords.equals(key)) {
                    for (String v : value.split(",|;"))
                        if (v.trim().length() > 0)
                            xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, new PropertyOptions(PropertyOptions.ARRAY), v.trim(), null);
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Keywords, value);
                } else if (PdfName.Producer.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, value);
                } else if (PdfName.Creator.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreatorTool, value);
                } else if (PdfName.CreationDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreateDate, PdfDate.getW3CDate(value));
                } else if (PdfName.ModDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.ModifyDate, PdfDate.getW3CDate(value));
                }
            }
        }
        if (isTagged()) {
            xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART, 1, new PropertyOptions(PropertyOptions.SEPARATE_NODE));
        }
        if (conformanceLevel != null) {
            addRdfDescription(xmpMeta, conformanceLevel);
        }
        setXmpMetadata(xmpMeta);
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
