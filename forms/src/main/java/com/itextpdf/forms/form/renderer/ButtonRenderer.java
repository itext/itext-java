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
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.FormFieldRendererUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractTextFieldRenderer} implementation for buttons.
 */
public class ButtonRenderer extends AbstractOneLineTextFieldRenderer {

    /**
     * Default padding Y offset for an input button.
     */
    private static final float DEFAULT_Y_OFFSET = 4;

    /**
     * Relative value is quite big in order to preserve visible padding on small field sizes.
     * This constant is taken arbitrary, based on visual similarity to Acrobat behaviour.
     */
    private static final float RELATIVE_PADDING_FOR_SMALL_SIZES = 0.15f;

    /**
     * Indicates if the one line caption was split.
     */
    private boolean isSplit = false;

    /**
     * Creates a new {@link ButtonRenderer} instance.
     *
     * @param modelElement the model element
     */
    public ButtonRenderer(Button modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     *
     * @param layoutContext {@inheritDoc}
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        if (((Button) modelElement).isSingleLine()) {
            ParagraphRenderer renderer = (ParagraphRenderer) flatRenderer.getChildRenderers().get(0);
            List<LineRenderer> flatLines = renderer.getLines();
            Rectangle buttonBBox = getOccupiedArea().getBBox();
            Rectangle flatBBox = flatRenderer.getOccupiedArea().getBBox();
            updatePdfFont(renderer);
            if (flatLines.isEmpty() || font == null) {
                LoggerFactory.getLogger(getClass()).error(
                        MessageFormatUtil.format(
                                FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE,
                                "button"));
                setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
                flatBBox.setY(flatBBox.getTop()).setHeight(0);
            } else {
                if (flatLines.size() != 1) {
                    // Used to check if renderer fit,
                    // happens when caption contains '\n' symbol.
                    isSplit = true;
                }
                cropContentLines(flatLines, flatBBox);
                Float width = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
                if (width == null) {
                    LineRenderer drawnLine = flatLines.get(0);
                    drawnLine.move(flatBBox.getX() - drawnLine.getOccupiedArea().getBBox().getX(), 0);
                    flatBBox.setWidth(drawnLine.getOccupiedArea().getBBox().getWidth());
                    buttonBBox.setWidth(flatBBox.getWidth() + 2 * (flatBBox.getX() - buttonBBox.getX()));
                }
            }
        } else {
            if (this.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT) == null) {
                // Apply middle vertical alignment for children including floats.
                float lowestChildBottom = getLowestChildBottom(flatRenderer,
                        flatRenderer.getOccupiedArea().getBBox().getBottom());
                float deltaY = lowestChildBottom - getInnerAreaBBox().getY();
                if (deltaY > 0) {
                    flatRenderer.move(0, -deltaY / 2);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean isLayoutBasedOnFlatRenderer() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected Float getLastYLineRecursively() {
        return super.getFirstYLineRecursively();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer createFlatRenderer() {
        Div div = new Div();
        for (IElement child : ((Button) modelElement).getChildren()) {
            if (child instanceof Image) {
                // Renderer for the image with fixed position will be added to positionedRenderers of the root renderer,
                // so occupiedArea of div renderer can have zero or wrong sizes.
                div.add((Image) child);
            } else {
                div.add((IBlockElement) child);
            }
            child.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        }

        setProperty(Property.APPEARANCE_STREAM_LAYOUT, Boolean.TRUE);
        setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        if (((Button) modelElement).isSingleLine()) {
            setProperty(Property.NO_SOFT_WRAP_INLINE, Boolean.TRUE);
        }
        Float height = retrieveHeight();
        if (height == null) {
            float buttonPadding = DEFAULT_Y_OFFSET;
            if (this.<UnitValue>getProperty(Property.FONT_SIZE) != null) {
                float fontSize = (this.<UnitValue>getProperty(Property.FONT_SIZE)).getValue();
                buttonPadding = Math.min(DEFAULT_Y_OFFSET, RELATIVE_PADDING_FOR_SMALL_SIZES * fontSize);
            }
            // 0 - top, 2 - bottom
            UnitValue[] paddings = getPaddings();
            if (paddings[0] == null || paddings[0].getValue() == 0) {
                setProperty(Property.PADDING_TOP, UnitValue.createPointValue(buttonPadding));
            }
            if (paddings[2] == null || paddings[2].getValue() == 0) {
                setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(buttonPadding));
            }
        }

        return div.createRendererSubTree();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new ButtonRenderer((Button) modelElement);
    }

    /**
     * Gets the default value of the form field.
     *
     * @return the default value of the form field.
     */
    @Override
    public String getDefaultValue() {
        // FormProperty.FORM_FIELD_VALUE is not supported for Button element.
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * @param availableWidth {@inheritDoc}
     * @param availableHeight {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected boolean isRendererFit(float availableWidth, float availableHeight) {
        return !isSplit && super.isRendererFit(availableWidth, availableHeight);
    }

    /**
     * {@inheritDoc}
     *
     * @param drawContext {@inheritDoc}
     */
    @Override
    protected void applyAcroField(DrawContext drawContext) {
        String name = getModelId();
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(ButtonRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        PdfDocument doc = drawContext.getDocument();
        Rectangle area = getOccupiedArea().getBBox().clone();
        applyMargins(area, false);
        final Map<Integer, Object> properties = FormFieldRendererUtil.removeProperties(modelElement);
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());

        Background background = this.<Background>getProperty(Property.BACKGROUND);
        // Background is light gray by default, but can be set to null by user.
        final Color backgroundColor = background == null ? null : background.getColor();

        final float fontSizeValue = fontSize.getValue();

        if (font == null) {
            font = getResolvedFont(doc);
        }

        // Some properties are set to the HtmlDocumentRenderer, which is root renderer for this ButtonRenderer, but
        // in forms logic root renderer is CanvasRenderer, and these properties will have default values. So
        // we get them from renderer and set these properties to model element, which will be passed to forms logic.
        modelElement.setProperty(Property.FONT_PROVIDER, this.<FontProvider>getProperty(Property.FONT_PROVIDER));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));
        final PdfButtonFormField button = new PushButtonFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setFont(font)
                .setConformance(getConformance(doc))
                .createPushButton();
        button.disableFieldRegeneration();
        button.setFontSize(fontSizeValue);
        button.getFirstFormAnnotation().setBackgroundColor(backgroundColor);
        applyDefaultFieldProperties(button);
        applyAccessibilityProperties(button,doc);
        button.getFirstFormAnnotation().setFormFieldElement((Button) modelElement);
        button.enableFieldRegeneration();
        PdfAcroForm forms = PdfFormCreator.getAcroForm(doc, true);
        // Fields can be already added on split, e.g. when button split into multiple pages. But now we merge fields
        // with the same names (and add all the widgets as kids to that merged field), so we can add it anyway.
        forms.addField(button, page);

        FormFieldRendererUtil.reapplyProperties(modelElement, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setContentHeight(IRenderer flatRenderer, float height) {
        Rectangle bBox = flatRenderer.getOccupiedArea().getBBox();
        Border border = getBorders()[0];
        if (border != null) {
            height += border.getWidth() * 2;
        }
        UnitValue[] paddings = getPaddings();
        if (paddings[0] != null) {
            height += paddings[0].getValue();
        }
        if (paddings[2] != null) {
            height += paddings[2].getValue();
        }
        UnitValue[] margins = getMargins();
        if (margins[0] != null) {
            height += margins[0].getValue();
        }
        if (margins[2] != null) {
            height += margins[2].getValue();
        }
        float newY = getOccupiedArea().getBBox().getBottom() + height / 2 - bBox.getHeight() / 2;
        float dy = bBox.getBottom() - newY;
        bBox.moveDown(dy);
        bBox.setHeight(height);
        flatRenderer.move(0, -dy);
    }
}
