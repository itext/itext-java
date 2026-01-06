/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
import com.itextpdf.kernel.validation.context.CanvasTextAdditionContext;
import com.itextpdf.kernel.validation.context.CanvasWritingContentValidationContext;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.context.PdfAnnotationContext;
import com.itextpdf.kernel.validation.context.PdfDestinationAdditionContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.validation.context.PdfObjectValidationContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.layout.validation.context.LayoutValidationContext;
import com.itextpdf.pdfua.checkers.utils.GraphicsCheckUtil;
import com.itextpdf.pdfua.checkers.utils.LayoutCheckUtil;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2AnnotationChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2CanvasTextChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2DestinationsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2EmbeddedFilesChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2FormChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2FormulaChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2HeadingsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2LinkChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2ListChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2NotesChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2StringChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2TableOfContentsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2XfaChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * The class defines the requirements of the PDF/UA-2 standard and contains
 * method implementations from the abstract {@link PdfUAChecker} class.
 *
 * <p>
 * The specification implemented by this class is ISO 14289-2.
 */
public class PdfUA2Checker extends PdfUAChecker {

    private final PdfDocument pdfDocument;
    private final PdfUAValidationContext context;
    private final PdfUA2CanvasTextChecker textChecker = new PdfUA2CanvasTextChecker();

    /**
     * Creates {@link PdfUA2Checker} instance with PDF document which will be validated against PDF/UA-2 standard.
     *
     * @param pdfDocument the document to validate
     */
    public PdfUA2Checker(PdfDocument pdfDocument) {
        super();
        this.pdfDocument = pdfDocument;
        this.context = new PdfUAValidationContext(this.pdfDocument);
    }

    @Override
    public void validate(IValidationContext context) {
        switch (context.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) context;
                checkCatalog(pdfDocContext.getPdfDocument().getCatalog());
                checkStructureTreeRoot(pdfDocContext.getPdfDocument().getStructTreeRoot());
                checkFonts(pdfDocContext.getDocumentFonts());
                new PdfUA2DestinationsChecker(pdfDocContext.getPdfDocument()).checkDestinations();
                PdfUA2XfaChecker.check(pdfDocContext.getPdfDocument());
                textChecker.checkCollectedContexts(pdfDocContext.getPdfDocument());
                break;
            case FONT:
                FontValidationContext fontContext = (FontValidationContext) context;
                checkText(fontContext.getText(), fontContext.getFont());
                break;
            case CANVAS_BEGIN_MARKED_CONTENT:
                CanvasBmcValidationContext bmcContext = (CanvasBmcValidationContext) context;
                checkLogicalStructureInBMC(bmcContext.getTagStructureStack(), bmcContext.getCurrentBmc(),
                        this.pdfDocument);
                break;
            case CANVAS_WRITING_CONTENT:
                CanvasWritingContentValidationContext writingContext = (CanvasWritingContentValidationContext) context;
                checkContentInCanvas(writingContext.getTagStructureStack(), this.pdfDocument);
                break;
            case LAYOUT:
                LayoutValidationContext layoutContext = (LayoutValidationContext) context;
                new LayoutCheckUtil(this.context).checkRenderer(layoutContext.getRenderer());
                new PdfUA2HeadingsChecker(this.context).checkLayoutElement(layoutContext.getRenderer());
                break;
            case DESTINATION_ADDITION:
                PdfDestinationAdditionContext destinationAdditionContext = (PdfDestinationAdditionContext) context;
                new PdfUA2DestinationsChecker(destinationAdditionContext, pdfDocument).checkDestinationsOnCreation();
                break;
            case PDF_OBJECT:
                PdfObjectValidationContext validationContext = (PdfObjectValidationContext) context;
                checkPdfObject(validationContext.getObject());
                break;
            case ANNOTATION:
                PdfAnnotationContext annotationContext = (PdfAnnotationContext) context;
                PdfUA2AnnotationChecker.checkAnnotation(annotationContext.getAnnotation(), this.context);
                break;
            case CANVAS_TEXT_ADDITION:
                CanvasTextAdditionContext canvasTextAdditionContext = (CanvasTextAdditionContext) context;
                textChecker.collectTextAdditionContext(canvasTextAdditionContext);
                break;
        }
    }

    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        return false;
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file contains the {@code Metadata} key whose value is
     * a metadata stream as defined in ISO 32000-2:2020. Also checks that the value of {@code pdfuaid:part} is 2 for
     * conforming PDF files and validates required {@code pdfuaid:rev} value.
     *
     * <p>
     * Checks that the {@code Metadata} stream as specified in ISO 32000-2:2020, 14.3 in the document catalog dictionary
     * includes a {@code dc: title} entry reflecting the title of the document.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    protected void checkMetadata(PdfCatalog catalog) {
        PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER);
        try {
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (XMPException e) {
            throw new PdfUAConformanceException(e.getMessage());
        }
    }

    /**
     * For all non-symbolic TrueType fonts used for rendering, the embedded TrueType font program shall contain
     * at least the Microsoft Unicode (3, 1 – Platform ID = 3, Encoding ID = 1),
     * or the Macintosh Roman (1, 0 – Platform ID = 1, Encoding ID = 0) “cmap” subtable.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    @Override
    void checkNonSymbolicCmapSubtable(TrueTypeFont fontProgram) {
        if (!fontProgram.isCmapPresent(3, 1) && !fontProgram.isCmapPresent(1, 0)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.NON_SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_UNI_CMAP);
        }
    }

    /**
     * Checks cmap entries present in the embedded TrueType font program of the symbolic TrueType font.
     *
     * <p>
     * The “cmap” subtable in the embedded font program shall either contain the Microsoft Symbol
     * (3, 0 – Platform ID = 3, Encoding ID = 0) or the Mac Roman (1, 0 – Platform ID = 1, Encoding ID = 1) encoding.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    @Override
    void checkSymbolicCmapSubtable(TrueTypeFont fontProgram) {
        if (!fontProgram.isCmapPresent(3, 0) && !fontProgram.isCmapPresent(1, 0)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.SYMBOLIC_TTF_SHALL_CONTAIN_MAC_ROMAN_OR_MICROSOFT_SYMBOL_CMAP);
        }
    }

    private void checkPdfObject(PdfObject obj) {
        switch (obj.getType()) {
            case PdfObject.STRING:
                PdfUA2StringChecker.checkPdfString((PdfString) obj);
                break;
            case PdfObject.ARRAY:
                checkArrayRecursively((PdfArray) obj);
                break;
            case PdfObject.DICTIONARY:
            case PdfObject.STREAM:
                checkDictionaryRecursively((PdfDictionary) obj);
                break;
        }
    }

    private void checkArrayRecursively(PdfArray array) {
        for (int i = 0; i < array.size(); i++) {
            PdfObject object = array.get(i, false);
            if (object != null && !object.isIndirect()) {
                checkPdfObject(object);
            }
        }
    }

    private void checkDictionaryRecursively(PdfDictionary dictionary) {
        for (PdfName name : dictionary.keySet()) {
            PdfObject object = dictionary.get(name, false);
            if (object != null && !object.isIndirect()) {
                checkPdfObject(object);
            }
        }
    }

    /**
     * Validates document catalog dictionary against PDF/UA-2 standard.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary to check
     */
    private void checkCatalog(PdfCatalog catalog) {
        checkLang(catalog);
        checkMetadata(catalog);
        checkViewerPreferences(catalog);
        checkOCProperties(catalog.getPdfObject().getAsDictionary(PdfName.OCProperties));
        checkFormFieldsAndAnnotations(catalog);
        PdfUA2EmbeddedFilesChecker.checkEmbeddedFiles(catalog);
    }

    /**
     * Validates all annotations and form fields present in the document against PDF/UA-2 standard.
     *
     * @param catalog {@link PdfCatalog} to check form fields present in the acroform
     */
    private void checkFormFieldsAndAnnotations(PdfCatalog catalog) {
        PdfUA2FormChecker formChecker = new PdfUA2FormChecker(context);
        formChecker.checkFormFields(catalog.getPdfObject().getAsDictionary(PdfName.AcroForm));
        formChecker.checkWidgetAnnotations(this.pdfDocument);
        PdfUA2LinkChecker.checkLinkAnnotations(this.pdfDocument);
        PdfUA2AnnotationChecker.checkAnnotations(this.pdfDocument);
    }

    /**
     * Validates structure tree root dictionary against PDF/UA-2 standard.
     *
     * <p>
     * Additionally, checks that within a given explicitly provided namespace, structure types are not role mapped to
     * other structure types in the same namespace. In the StructTreeRoot RoleMap there is no explicitly provided
     * namespace, that's why it is not checked.
     *
     * @param structTreeRoot {@link PdfStructTreeRoot} structure tree root dictionary to check
     */
    private void checkStructureTreeRoot(PdfStructTreeRoot structTreeRoot) {
        List<PdfNamespace> namespaces = structTreeRoot.getNamespaces();
        for (PdfNamespace namespace : namespaces) {
            PdfDictionary roleMap = namespace.getNamespaceRoleMap();
            if (roleMap != null) {
                for (Map.Entry<PdfName, PdfObject> entry : roleMap.entrySet()) {
                    final String role = entry.getKey().getValue();
                    final IRoleMappingResolver roleMappingResolver = pdfDocument.getTagStructureContext()
                            .getRoleMappingResolver(role, namespace);

                    int i = 0;
                    // Reasonably large arbitrary number that will help to avoid a possible infinite loop.
                    int maxIters = 100;
                    while (roleMappingResolver.resolveNextMapping()) {
                        if (++i > maxIters) {
                            Logger logger = LoggerFactory.getLogger(PdfUA2Checker.class);
                            logger.error(MessageFormatUtil.format(PdfUALogMessageConstants.
                                    CANNOT_RESOLVE_ROLE_IN_NAMESPACE_TOO_MUCH_TRANSITIVE_MAPPINGS, role, namespace));
                            break;
                        }
                        final PdfNamespace roleMapToNamespace = roleMappingResolver.getNamespace();
                        if (namespace.getNamespaceName().equals(roleMapToNamespace.getNamespaceName())) {
                            throw new PdfUAConformanceException(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                                            STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                                    namespace.getNamespaceName(), role));
                        }
                    }
                }
            }
        }

        TagTreeIterator tagTreeIterator = new TagTreeIterator(structTreeRoot);
        tagTreeIterator.addHandler(new GraphicsCheckUtil.GraphicsHandler(context));
        tagTreeIterator.addHandler(new PdfUA2HeadingsChecker.PdfUA2HeadingHandler(context));
        tagTreeIterator.addHandler(new TableCheckUtil.TableHandler(context));
        tagTreeIterator.addHandler(new PdfUA2FormChecker.PdfUA2FormTagHandler(context));
        tagTreeIterator.addHandler(new PdfUA2AnnotationChecker.PdfUA2AnnotationHandler(context));
        tagTreeIterator.addHandler(new PdfUA2ListChecker.PdfUA2ListHandler(context));
        tagTreeIterator.addHandler(new PdfUA2NotesChecker.PdfUA2NotesHandler(context));
        tagTreeIterator.addHandler(new PdfUA2TableOfContentsChecker.PdfUA2TableOfContentsHandler(context));
        tagTreeIterator.addHandler(new PdfUA2FormulaChecker.PdfUA2FormulaTagHandler(context));
        tagTreeIterator.addHandler(new PdfUA2LinkChecker.PdfUA2LinkAnnotationHandler(context, pdfDocument));
        tagTreeIterator.traverse();
    }
}
