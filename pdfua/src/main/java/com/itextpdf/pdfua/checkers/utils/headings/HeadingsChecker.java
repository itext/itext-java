/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.pdfua.checkers.utils.headings;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class which performs headings check according to PDF/UA specification.
 */
public final class HeadingsChecker {
    private static final Pattern Hn_PATTERN = Pattern.compile("^H([1-6])$");
    private final PdfUAValidationContext context;
    private final Set<IRenderer> hRendererParents = new HashSet<>();
    private final Set<PdfDictionary> hPdfDictParents = new HashSet<>();
    private int previousHn = -1;
    private boolean wasAtLeastOneH = false;

    /**
     * Creates a new instance of {@link HeadingsChecker}.
     *
     * @param context The validation context.
     */
    public HeadingsChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if layout element has correct heading.
     *
     * @param rendererObj layout element to check
     *
     * @throws PdfUAConformanceException if headings sequence is incorrect
     */
    public void checkLayoutElement(Object rendererObj) {
        IRenderer renderer = (IRenderer) rendererObj;
        IPropertyContainer element = renderer.getModelElement();
        if (element instanceof IAccessibleElement) {
            IAccessibleElement accessibleElement = (IAccessibleElement) element;
            String role = context.resolveToStandardRole(accessibleElement.getAccessibilityProperties().getRole());

            checkHnSequence(role);

            if (StandardRoles.H.equals(role)) {
                IRenderer parent = renderer.getParent();
                if (hRendererParents.contains(parent)) {
                    // Matterhorn-protocol checkpoint 14-006
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
                } else if (parent != null) {
                    hRendererParents.add(parent);
                }
            }

            checkHAndHnUsing(role);
        }
    }

    /**
     * Checks if structure element has correct heading.
     *
     * @param structNode structure element to check
     *
     * @throws PdfUAConformanceException if headings sequence is incorrect
     */
    public void checkStructElement(IStructureNode structNode) {
        final String role = context.resolveToStandardRole(structNode);
        if (role == null) {
            return;
        }
        checkHnSequence(role);

        if (StandardRoles.H.equals(role)) {
            PdfDictionary parent = extractPdfDictFromNode(structNode.getParent());
            if (hPdfDictParents.contains(parent)) {
                // Matterhorn-protocol checkpoint 14-006
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MORE_THAN_ONE_H_TAG);
            } else if (parent != null) {
                hPdfDictParents.add(parent);
            }
        }

        checkHAndHnUsing(role);
    }

    private void checkHnSequence(String role) {
        int currHn = extractNumber(role);
        if (currHn != -1) {
            if (previousHn == -1) {
                if (currHn != 1) {
                    // Matterhorn-protocol checkpoint 14-002
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.H1_IS_SKIPPED);
                }
            } else if (currHn - previousHn > 1) {
                // Matterhorn-protocol checkpoint 14-003
                throw new PdfUAConformanceException(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.HN_IS_SKIPPED, previousHn + 1));
            }
            previousHn = currHn;
        }
    }

    private void checkHAndHnUsing(String role) {
        if (StandardRoles.H.equals(role)) {
            wasAtLeastOneH = true;
        }

        if (wasAtLeastOneH && previousHn != -1) {
            // Matterhorn-protocol checkpoint 14-007
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_USES_BOTH_H_AND_HN);
        }
    }

    private static int extractNumber(String heading) {
        if (heading == null) {
            return -1;
        }
        final Matcher matcher = Hn_PATTERN.matcher(heading);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    private static PdfDictionary extractPdfDictFromNode(IStructureNode node) {
        if (node instanceof PdfStructTreeRoot) {
            return ((PdfStructTreeRoot) node).getPdfObject();
        } else if (node instanceof PdfStructElem) {
            return ((PdfStructElem) node).getPdfObject();
        }
        return null;
    }

    /**
     * Handler class that checks heading tags while traversing the tag tree.
     */
    public static class HeadingHandler extends ContextAwareTagTreeIteratorHandler {
        private final HeadingsChecker checker;

        /**
         * Creates a new instance of {@link HeadingsChecker}.
         *
         * @param context The validation context.
         */
        public HeadingHandler(PdfUAValidationContext context) {
            super(context);
            checker = new HeadingsChecker(context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean nextElement(IStructureNode elem) {
            checker.checkStructElement(elem);
            return true;
        }
    }
}
