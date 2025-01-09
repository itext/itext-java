/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.TextLayoutResult;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom implementation for rendering form field values. It makes sure that text value
 * trimming strategy matches Acrobat's behavior
 */

class FormFieldValueNonTrimmingTextRenderer extends TextRenderer {
    // Determines whether we want to trim leading space. In particular we don't want to trim
    // the very first leading spaces of the text value. When text overflows to the next lines,
    // whether we should trim the text depends on why the overflow happened
    private boolean callTrimFirst = false;

    public FormFieldValueNonTrimmingTextRenderer(Text textElement) {
        super(textElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new FormFieldValueNonTrimmingTextRenderer((Text) getModelElement());
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutResult baseLayoutResult = super.layout(layoutContext);
        if (baseLayoutResult instanceof TextLayoutResult &&
                baseLayoutResult.getOverflowRenderer() instanceof FormFieldValueNonTrimmingTextRenderer &&
                !((TextLayoutResult) baseLayoutResult).isSplitForcedByNewline()) {
            // In case the overflow to the next line happened naturally (without a forced line break),
            // we don't want to preserve the extra spaces at the beginning of the next line
            ((FormFieldValueNonTrimmingTextRenderer) baseLayoutResult.getOverflowRenderer()).setCallTrimFirst(true);
        }
        return baseLayoutResult;
    }

    @Override
    public void trimFirst() {
       if (callTrimFirst) {
           super.trimFirst();
       }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextRenderer createCopy(GlyphLine gl, PdfFont font) {
        if (FormFieldValueNonTrimmingTextRenderer.class != this.getClass()) {
            Logger logger = LoggerFactory.getLogger(FormFieldValueNonTrimmingTextRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.CREATE_COPY_SHOULD_BE_OVERRIDDEN));
        }
        FormFieldValueNonTrimmingTextRenderer copy = new FormFieldValueNonTrimmingTextRenderer((Text)this.modelElement);
        copy.setProcessedGlyphLineAndFont(gl, font);
        return copy;
    }

    private void setCallTrimFirst(boolean callTrimFirst) {
        this.callTrimFirst = callTrimFirst;
    }
}
