package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.forms.form.renderer.SignatureAppearanceRenderer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;

/**
 * Performs layout checks for a PDF document being validated against the Well Tagged PDF for Reuse standard.
 *
 */
public class WellTaggedPdfForReuseLayoutChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link WellTaggedPdfForReuseLayoutChecker} instance.
     *
     * @param context the validation context
     */
    public WellTaggedPdfForReuseLayoutChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks renderer for PDF UA compliance.
     *
     * @param renderer the renderer to check
     */
    public void checkRenderer(IRenderer renderer) {
        if (renderer == null) {
            return;
        }
        if (isPartOfSignatureAppearance(renderer)) {
            // Tagging of the current layout element will be skipped in that case.
            return;
        }
        IPropertyContainer layoutElement = renderer.getModelElement();
        if (layoutElement instanceof Table) {
            new TableCheckUtil(context).checkTable((Table) layoutElement);
        }
    }

    private static boolean isPartOfSignatureAppearance(IRenderer renderer) {
        IRenderer parent = renderer.getParent();
        while (parent != null) {
            if (parent instanceof SignatureAppearanceRenderer) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
