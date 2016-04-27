/*
    $Id$

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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.log.Counter;
import com.itextpdf.kernel.log.CounterFactory;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;

import java.io.IOException;

public class PdfADocument extends PdfDocument {

    private static final long serialVersionUID = -5908390625367471894L;
    protected PdfAChecker checker;

    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        super(writer);
        setChecker(conformanceLevel);
        addOutputIntent(outputIntent);
    }

    public PdfADocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    public PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties);

        byte[] existingXmpMetadata = getXmpMetadata();
        if (existingXmpMetadata == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }
        XMPMeta meta;
        try {
            meta = XMPMetaFactory.parseFromBuffer(existingXmpMetadata);
        } catch (XMPException exc) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.getConformanceLevel(meta);
        if (conformanceLevel == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DocumentToReadFromShallBeAPdfAConformantFileWithValidXmpMetadata);
        }

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
                checker.checkSinglePage((PdfPage) obj);
        }
    }

    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    @Override
    public void createXmpMetadata() throws XMPException {
        createXmpMetadata(checker.getConformanceLevel());
    }

    public void createXmpMetadata(PdfAConformanceLevel conformanceLevel) throws XMPException {
        super.createXmpMetadata();
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(getXmpMetadata());
        if (conformanceLevel != null) {
            addRdfDescription(xmpMeta, conformanceLevel);
        }
        setXmpMetadata(xmpMeta);
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
        for (PdfFont pdfFont : getDocumentFonts()) {
            if (!pdfFont.isEmbedded()) {
                throw new PdfAConformanceException(PdfAConformanceException.AllFontsMustBeEmbeddedThisOneIsnt1)
                        .setMessageParams(pdfFont.getFontProgram().getFontNames().getFontName());
            }
        }
        super.flushFonts();
    }

    protected void setChecker(PdfAConformanceLevel conformanceLevel) {
        switch (conformanceLevel.getPart()) {
            case "1":
                checker = new PdfA1Checker(conformanceLevel);
                break;
            case "2":
                checker = new PdfA2Checker(conformanceLevel);
                break;
            case "3":
                checker = new PdfA3Checker(conformanceLevel);
                break;
        }
    }

    protected void addRdfDescription(XMPMeta xmpMeta, PdfAConformanceLevel conformanceLevel) throws XMPException {
        xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, conformanceLevel.getPart());
        xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, conformanceLevel.getConformance());
        if (this.isTagged()) {
            XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PdfAXMPUtil.PDF_UA_EXTENSION);
            XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
        }
    }

    protected void initTagStructureContext() {
        tagStructureContext = new TagStructureContext(this, getPdfVersionForPdfA(checker.getConformanceLevel()));
    }

    @Override
    protected Counter getCounter() {
        return CounterFactory.getCounter(PdfADocument.class);
    }

    private static PdfVersion getPdfVersionForPdfA(PdfAConformanceLevel conformanceLevel) {
        PdfVersion version;
        switch (conformanceLevel.getPart()) {
            case "1":
                version = PdfVersion.PDF_1_4;
                break;
            case "2":
                version = PdfVersion.PDF_1_7;
                break;
            case "3":
                version = PdfVersion.PDF_1_7;
                break;
            default:
                version = PdfVersion.PDF_1_4;
                break;
        }
        return version;
    }
}
