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

