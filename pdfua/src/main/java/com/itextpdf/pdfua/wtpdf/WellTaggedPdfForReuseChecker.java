package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
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
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2DestinationsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2EmbeddedFilesChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2FormChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2FormulaChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2HeadingsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2LinkChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2ListChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2NotesChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2TableOfContentsChecker;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2XfaChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

public class WellTaggedPdfForReuseChecker extends WellTaggedPdfForAccessibilityChecker {
    /**
     * Creates {@link WellTaggedPdfForReuseChecker} instance which will be validated against WTPDF For Reuse standard.
     *
     * @param pdfDocument the document to validate
     */
    public WellTaggedPdfForReuseChecker(PdfDocument pdfDocument) {
        super(pdfDocument);
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
                break;
            case FONT:
                FontValidationContext fontContext = (FontValidationContext) context;
                checkText(fontContext.getText(), fontContext.getFont());
                break;
            case CANVAS_BEGIN_MARKED_CONTENT:
                CanvasBmcValidationContext bmcContext = (CanvasBmcValidationContext) context;
                checkLogicalStructureInBMC(bmcContext.getTagStructureStack(), bmcContext.getCurrentBmc(),
                        getPdfDocument());
                break;
            case CANVAS_WRITING_CONTENT:
                CanvasWritingContentValidationContext writingContext = (CanvasWritingContentValidationContext) context;
                checkContentInCanvas(writingContext.getTagStructureStack(), getPdfDocument());
                break;
            case LAYOUT:
                LayoutValidationContext layoutContext = (LayoutValidationContext) context;
                new WellTaggedPdfForReuseLayoutChecker(getUAValidationContext()).checkRenderer(layoutContext.getRenderer());
                new PdfUA2HeadingsChecker(getUAValidationContext()).checkLayoutElement(layoutContext.getRenderer());
                break;
            case DESTINATION_ADDITION:
                PdfDestinationAdditionContext destinationAdditionContext = (PdfDestinationAdditionContext) context;
                new PdfUA2DestinationsChecker(destinationAdditionContext, getPdfDocument()).checkDestinationsOnCreation();
                break;
            case PDF_OBJECT:
                PdfObjectValidationContext validationContext = (PdfObjectValidationContext) context;
                checkPdfObject(validationContext.getObject());
                break;
            case ANNOTATION:
                PdfAnnotationContext annotationContext = (PdfAnnotationContext) context;
                new WellTaggedPdfForReuseAnnotationChecker().checkAnnotation(
                        annotationContext.getAnnotation(), getUAValidationContext());
                break;
        }
    }

    /**
     * Validates document catalog dictionary against PDF/UA-2 standard.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary to check
     */
    @Override
    protected void checkCatalog(PdfCatalog catalog) {
        checkLang(catalog);
        checkMetadata(catalog);
        checkFormFieldsAndAnnotations(catalog);
        PdfUA2EmbeddedFilesChecker.checkEmbeddedFiles(catalog);
    }

    /**
     * Validates all annotations and form fields present in the document against PDF/UA-2 standard.
     *
     * @param catalog {@link PdfCatalog} to check form fields present in the acroform
     */
    @Override
    protected void checkFormFieldsAndAnnotations(PdfCatalog catalog) {
        PdfUA2FormChecker formChecker = new PdfUA2FormChecker(getUAValidationContext());
        formChecker.checkFormFields(catalog.getPdfObject().getAsDictionary(PdfName.AcroForm));
        formChecker.checkWidgetAnnotations(getPdfDocument());
        PdfUA2LinkChecker.checkLinkAnnotations(getPdfDocument());
        new WellTaggedPdfForReuseAnnotationChecker().checkAnnotations(getPdfDocument());
    }

    @Override
    protected TagTreeIterator createTagTreeIterator(PdfStructTreeRoot structTreeRoot) {
        TagTreeIterator tagTreeIterator = new TagTreeIterator(structTreeRoot);
        tagTreeIterator.addHandler(new PdfUA2HeadingsChecker.PdfUA2HeadingHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new TableCheckUtil.TableHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2FormChecker.PdfUA2FormTagHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new WellTaggedPdfForReuseAnnotationChecker.WellTaggedPdfForReuseAnnotationHandler(
                getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2ListChecker.PdfUA2ListHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2NotesChecker.PdfUA2NotesHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2TableOfContentsChecker.PdfUA2TableOfContentsHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2FormulaChecker.PdfUA2FormulaTagHandler(getUAValidationContext()));
        tagTreeIterator.addHandler(new PdfUA2LinkChecker.PdfUA2LinkAnnotationHandler(
                getUAValidationContext(), getPdfDocument()));
        return tagTreeIterator;
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file contains the {@code Metadata} key whose value is
     * a metadata stream as defined in ISO 32000-2:2020.
     *
     * <p>
     * Checks that the {@code Metadata} stream as specified in ISO 32000-2:2020, 14.3 in the document catalog dictionary
     * includes a {@code dc: title} entry reflecting the title of the document.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    @Override
    protected void checkMetadata(PdfCatalog catalog) {
        PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.WELL_TAGGED_PDF_FOR_REUSE,
                msg -> new PdfUAConformanceException(msg));
        try {
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (XMPException e) {
            throw new PdfUAConformanceException(e.getMessage(), e);
        }
    }
}
