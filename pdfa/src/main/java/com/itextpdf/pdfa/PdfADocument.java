/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
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
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
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
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class extends {@link PdfDocument} and is in charge of creating files
 * that comply with the PDF/A standard.
 *
 * Client code is still responsible for making sure the file is actually PDF/A
 * compliant: multiple steps must be undertaken (depending on the
 * {@link PdfAConformanceLevel}) to ensure that the PDF/A standard is followed.
 *
 * This class will throw exceptions, mostly {@link PdfAConformanceException},
 * and thus refuse to output a PDF/A file if at any point the document does not
 * adhere to the PDF/A guidelines specified by the {@link PdfAConformanceLevel}.
 */
public class PdfADocument extends PdfDocument {


    private static IPdfPageFactory pdfAPageFactory = new PdfAPageFactory();

    protected PdfAChecker checker;

    private boolean alreadyLoggedThatObjectFlushingWasNotPerformed = false;

    private boolean alreadyLoggedThatPageFlushingWasNotPerformed = false;

    private boolean isPdfADocument = true;

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance level, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param conformanceLevel the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     */
    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        this(writer, conformanceLevel, outputIntent, new DocumentProperties());
    }

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance level, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param conformanceLevel the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     * @param properties a {@link com.itextpdf.kernel.pdf.DocumentProperties}
     */
    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent, DocumentProperties properties) {
        super(writer, properties);
        setChecker(conformanceLevel);
        addOutputIntent(outputIntent);
    }

    /**
     * Opens a PDF/A document in the stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfADocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param properties properties of the stamping process
     */
    public PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        this(reader, writer, properties, false);
    }

    PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties, boolean tolerant) {
        super(reader, writer, properties);

        PdfAConformanceLevel conformanceLevel = reader.getPdfAConformanceLevel();
        if (conformanceLevel == null) {
            if (tolerant) {
                isPdfADocument = false;
            } else {
                throw new PdfAConformanceException(
                        PdfAConformanceException.
                                DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);
            }
        }

        setChecker(conformanceLevel);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key) {
        checkIsoConformance(obj, key, null, null);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources, PdfStream contentStream) {
        if (!isPdfADocument) {
            super.checkIsoConformance(obj, key, resources, contentStream);
            return;
        }

        CanvasGraphicsState gState;
        PdfDictionary currentColorSpaces = null;
        if (resources != null) {
            currentColorSpaces = resources.getPdfObject().getAsDictionary(PdfName.ColorSpace);
        }
        switch (key) {
            case CANVAS_STACK:
                checker.checkCanvasStack((char) obj);
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
            case EXTENDED_GRAPHICS_STATE:
                gState = (CanvasGraphicsState) obj;
                checker.checkExtGState(gState, contentStream);
                break;
            case FILL_COLOR:
                gState = (CanvasGraphicsState) obj;
                checker.checkColor(gState.getFillColor(), currentColorSpaces, true, contentStream);
                break;
            case PAGE:
                checker.checkSinglePage((PdfPage) obj);
                break;
            case STROKE_COLOR:
                gState = (CanvasGraphicsState) obj;
                checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false, contentStream);
                break;
            case TAG_STRUCTURE_ELEMENT:
                checker.checkTagStructureElement((PdfObject) obj);
                break;
            case FONT_GLYPHS:
                checker.checkFontGlyphs(((CanvasGraphicsState) obj).getFont(), contentStream);
                break;
            case XREF_TABLE:
                checker.checkXrefTable((PdfXrefTable) obj);
                break;
            case SIGNATURE:
                checker.checkSignature((PdfDictionary) obj);
                break;
        }
    }

    /**
     * Gets the PdfAConformanceLevel set in the constructor or in the metadata
     * of the {@link PdfReader}.
     *
     * @return a {@link PdfAConformanceLevel}
     */
    public PdfAConformanceLevel getConformanceLevel() {
        if (isPdfADocument) {
            return checker.getConformanceLevel();
        } else {
            return null;
        }
    }

    void logThatPdfAPageFlushingWasNotPerformed() {
        if (!alreadyLoggedThatPageFlushingWasNotPerformed) {
            alreadyLoggedThatPageFlushingWasNotPerformed = true;
            // This log message will be printed once for one instance of the document.
            LoggerFactory.getLogger(PdfADocument.class).warn(PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED);
        }
    }

    @Override
    protected void addCustomMetadataExtensions(XMPMeta xmpMeta) {
        if (!isPdfADocument) {
            super.addCustomMetadataExtensions(xmpMeta);
            return;
        }

        if (this.isTagged()) {
            try {
                if (xmpMeta.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART) != null) {
                    XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PdfAXMPUtil.PDF_UA_EXTENSION);
                    XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
                }
            } catch (XMPException exc) {
                Logger logger = LoggerFactory.getLogger(PdfADocument.class);
                logger.error(IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, exc);
            }
        }
    }

    @Override
    protected void updateXmpMetadata() {
        if (!isPdfADocument) {
            super.updateXmpMetadata();
            return;
        }

        try {
            XMPMeta xmpMeta = updateDefaultXmpMetadata();
            xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, checker.getConformanceLevel().getPart());
            xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE,
                    checker.getConformanceLevel().getConformance());
            addCustomMetadataExtensions(xmpMeta);
            setXmpMetadata(xmpMeta);
        } catch (XMPException e) {
            Logger logger = LoggerFactory.getLogger(PdfADocument.class);
            logger.error(IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, e);
        }
    }

    @Override
    protected void checkIsoConformance() {
        if (isPdfADocument) {
            checker.checkDocument(catalog);
        } else {
            super.checkIsoConformance();
        }
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        if (!isPdfADocument) {
            super.flushObject(pdfObject, canBeInObjStm);
            return;
        }

        markObjectAsMustBeFlushed(pdfObject);
        if (isClosing || checker.objectIsChecked(pdfObject)) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else if (!alreadyLoggedThatObjectFlushingWasNotPerformed) {
            alreadyLoggedThatObjectFlushingWasNotPerformed = true;
            // This log message will be printed once for one instance of the document.
            LoggerFactory.getLogger(PdfADocument.class)
                    .warn(PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED);
        }
    }

    @Override
    protected void flushFonts() {
        if (isPdfADocument) {
            for (PdfFont pdfFont : getDocumentFonts()) {
                checker.checkFont(pdfFont);
            }
        }

        super.flushFonts();
    }

    /**
     * Sets the checker that defines the requirements of the PDF/A standard
     * depending on conformance level.
     *
     * @param conformanceLevel {@link PdfAConformanceLevel}
     */
    protected void setChecker(PdfAConformanceLevel conformanceLevel) {
        if (!isPdfADocument) {
            return;
        }

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

    /**
     * Initializes tagStructureContext to track necessary information of document's tag structure.
     */
    @Override
    protected void initTagStructureContext() {
        if (isPdfADocument) {
            tagStructureContext = new TagStructureContext(this, getPdfVersionForPdfA(checker.getConformanceLevel()));
        } else {
            super.initTagStructureContext();
        }
    }

    @Override
    protected IPdfPageFactory getPageFactory() {
        if (isPdfADocument) {
            return pdfAPageFactory;
        } else {
            return super.getPageFactory();
        }
    }

    boolean isClosing() {
        return isClosing;
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
