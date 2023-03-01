/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.form.element.InputButton;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * The {@link AbstractOneLineTextFieldRenderer} implementation for buttons with no kids.
 */
public class InputButtonRenderer extends AbstractOneLineTextFieldRenderer {

    /** Indicates of the content was split. */
    private boolean isSplit = false;

    /**
     * Creates a new {@link InputButtonRenderer} instance.
     *
     * @param modelElement the model element
     */
    public InputButtonRenderer(InputButton modelElement) {
        super(modelElement);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.IRenderer#getNextRenderer()
     */
    @Override
    public IRenderer getNextRenderer() {
        return new InputButtonRenderer((InputButton) modelElement);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#adjustFieldLayout()
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        List<LineRenderer> flatLines = ((ParagraphRenderer) flatRenderer).getLines();
        Rectangle flatBBox = flatRenderer.getOccupiedArea().getBBox();
        updatePdfFont((ParagraphRenderer) flatRenderer);
        if (flatLines.isEmpty() || font == null) {
            LoggerFactory.getLogger(getClass()).error(
                    MessageFormatUtil.format(
                            FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE,
                            "button"));
            setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flatBBox.setY(flatBBox.getTop()).setHeight(0);
        } else {
            if (flatLines.size() != 1) {
                isSplit = true;
            }
            cropContentLines(flatLines, flatBBox);
            Float width = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
            if (width == null) {
                LineRenderer drawnLine = flatLines.get(0);
                drawnLine.move(flatBBox.getX() - drawnLine.getOccupiedArea().getBBox().getX(), 0);
                flatBBox.setWidth(drawnLine.getOccupiedArea().getBBox().getWidth());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#createFlatRenderer()
     */
    @Override
    protected IRenderer createFlatRenderer() {
        return createParagraphRenderer(getDefaultValue());
    }

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#applyAcroField(com.itextpdf.layout.renderer.DrawContext)
     */
    @Override
    protected void applyAcroField(DrawContext drawContext) {
        String value = getDefaultValue();
        String name = getModelId();
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(InputButtonRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        PdfDocument doc = drawContext.getDocument();
        Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();
        applyPaddings(area, true);
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());

        Background background = this.<Background>getProperty(Property.BACKGROUND);
        final Color backgroundColor = background == null ? null : background.getColor();
        final float fontSizeValue = fontSize.getValue();

        final PdfButtonFormField button = new PushButtonFormFieldBuilder(doc, name)
                .setWidgetRectangle(area).setCaption(value).createPushButton();
        button.setFont(font).setFontSize(fontSizeValue);
        if (backgroundColor != null) {
            button.getFirstFormAnnotation().setBackgroundColor(backgroundColor);
        }
        applyDefaultFieldProperties(button);
        PdfAcroForm.getAcroForm(doc, true).addField(button, page);

        writeAcroFormFieldLangAttribute(doc);
    }

    /* (non-Javadoc)
     * @see AbstractFormFieldRenderer#isRendererFit(float, float)
     */
    @Override
    protected boolean isRendererFit(float availableWidth, float availableHeight) {
        return !isSplit && super.isRendererFit(availableWidth, availableHeight);
    }
}

