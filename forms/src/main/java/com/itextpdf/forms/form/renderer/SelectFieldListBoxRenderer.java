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
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.element.ListBoxField;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.util.BorderStyleUtil;
import com.itextpdf.forms.util.FormFieldRendererUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SelectFieldListBoxRenderer} implementation for select field renderer.
 */
public class SelectFieldListBoxRenderer extends AbstractSelectFieldRenderer {

    /**
     * Creates a new {@link SelectFieldListBoxRenderer} instance.
     *
     * @param modelElement the model element
     */
    public SelectFieldListBoxRenderer(AbstractSelectField modelElement) {
        super(modelElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new SelectFieldListBoxRenderer((AbstractSelectField) modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutResult layoutResult = super.layout(layoutContext);
        // options container is the only kid of the select field renderer by design
        IRenderer optionsContainer = childRenderers.size() == 1 ? childRenderers.get(0) : null;

        if (!isFlatten() || layoutResult.getStatus() != LayoutResult.FULL ||
                optionsContainer == null || optionsContainer.getOccupiedArea() == null) {
            return layoutResult;
        }

        if (isOverflowProperty(OverflowPropertyValue.HIDDEN, this, Property.OVERFLOW_Y)) {
            List<IRenderer> selectedOptions = getSelectedOptions(this);
            IRenderer firstSelectedOption;
            if (!selectedOptions.isEmpty() &&
                    (firstSelectedOption = selectedOptions.get(0)).getOccupiedArea() != null) {
                Rectangle borderAreaBBox = getBorderAreaBBox();
                Rectangle optionBBox = firstSelectedOption.getOccupiedArea().getBBox().clone();
                if (firstSelectedOption instanceof AbstractRenderer) {
                    ((AbstractRenderer) firstSelectedOption).applyMargins(optionBBox, false);
                }
                if (optionBBox.getHeight() < borderAreaBBox.getHeight()) {
                    float selectedBottom = optionBBox.getBottom();
                    float borderAreaBBoxBottom = borderAreaBBox.getBottom();
                    if (selectedBottom < borderAreaBBoxBottom) {
                        optionsContainer.move(0, borderAreaBBoxBottom - selectedBottom);
                    }
                } else {
                    optionsContainer.move(0, borderAreaBBox.getTop() - optionBBox.getTop());
                }
            }
        }

        return layoutResult;
    }

    @Override
    protected boolean allowLastYLineRecursiveExtraction() {
        return false;
    }

    @Override
    protected IRenderer createFlatRenderer() {
        AbstractSelectField selectField = (AbstractSelectField) modelElement;
        List<SelectFieldItem> options = selectField.getOptions();

        Div optionsContainer = new Div();
        int topIndex = (int) this.<Integer>getProperty(FormProperty.LIST_BOX_TOP_INDEX, 0);
        List<SelectFieldItem> visibleOptions = topIndex > 0 ? options.subList(topIndex, options.size()) : options;
        for (SelectFieldItem option : visibleOptions) {
            optionsContainer.add(option.getElement());
        }
        String lang = getLang();
        if (lang != null) {
            AccessibilityProperties properties = optionsContainer.getAccessibilityProperties();
            if (properties.getLanguage() == null) {
                properties.setLanguage(lang);
            }
        }

        IRenderer rendererSubTree;
        if (optionsContainer.getChildren().isEmpty()) {
            Paragraph pStub = new Paragraph("\u00A0").setMargin(0);
            pStub.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            pStub.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            // applying this property for the sake of finding this element as option
            pStub.setProperty(FormProperty.FORM_FIELD_SELECTED, false);
            optionsContainer.add(pStub);
            rendererSubTree = optionsContainer.createRendererSubTree();
        } else {
            rendererSubTree = optionsContainer.createRendererSubTree();

            List<IRenderer> selectedOptions = getSelectedOptions(rendererSubTree);
            for (IRenderer selectedOption : selectedOptions) {
                applySelectedStyle(selectedOption);
            }
        }
        return rendererSubTree;
    }

    @Override
    protected float getFinalSelectFieldHeight(float availableHeight, float actualHeight, boolean isClippedHeight) {
        Float height = retrieveHeight();
        float calculatedHeight;
        if (height == null) {
            calculatedHeight = getCalculatedHeight(this);

            Float maxHeight = retrieveMaxHeight();
            if (maxHeight != null && maxHeight < calculatedHeight) {
                calculatedHeight = (float) maxHeight;
            }
            Float minHeight = retrieveMinHeight();
            if (minHeight != null && minHeight > calculatedHeight) {
                calculatedHeight = (float) minHeight;
            }
        } else {
            calculatedHeight = height.floatValue();
        }
        return super.getFinalSelectFieldHeight(availableHeight, calculatedHeight, isClippedHeight);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        // Retrieve font properties
        PdfFont font = getResolvedFont(drawContext.getDocument());
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(SelectFieldListBoxRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }

        final PdfDocument doc = drawContext.getDocument();
        final Rectangle area = this.getOccupiedArea().getBBox().clone();
        final PdfPage page = doc.getPage(occupiedArea.getPageNumber());

        applyMargins(area, false);
        final Map<Integer, Object> properties = FormFieldRendererUtil.removeProperties(this.modelElement);
        // Some properties are set to the HtmlDocumentRenderer, which is root renderer for this ButtonRenderer, but
        // in forms logic root renderer is CanvasRenderer, and these properties will have default values. So
        // we get them from renderer and set these properties to model element, which will be passed to forms logic.
        modelElement.setProperty(Property.FONT_PROVIDER, this.<FontProvider>getProperty(Property.FONT_PROVIDER));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));

        ListBoxField lbModelElement = (ListBoxField) modelElement;
        List<String> selectedOptions = lbModelElement.getSelectedStrings();
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(doc, getModelId())
                .setConformance(getConformance(doc))
                .setFont(font)
                .setWidgetRectangle(area);
        setupBuilderValues(builder, lbModelElement);
        PdfChoiceFormField choiceField = builder.createList();
        choiceField.disableFieldRegeneration();
        applyAccessibilityProperties(choiceField,drawContext.getDocument());
        choiceField.setFontSize(fontSize.getValue());
        choiceField.setMultiSelect(isMultiple());
        choiceField.setListSelected(selectedOptions.toArray(new String[selectedOptions.size()]));

        Integer topIndex = modelElement.<Integer>getOwnProperty(FormProperty.LIST_BOX_TOP_INDEX);
        if (topIndex != null) {
            choiceField.setTopIndex((int) topIndex);
        }

        TransparentColor color = getPropertyAsTransparentColor(Property.FONT_COLOR);
        if (color != null) {
            choiceField.setColor(color.getColor());
        }
        choiceField.setJustification(this.<TextAlignment>getProperty(Property.TEXT_ALIGNMENT));

        BorderStyleUtil.applyBorderProperty(this, choiceField.getFirstFormAnnotation());

        Background background = this.<Background>getProperty(Property.BACKGROUND);
        if (background != null) {
            choiceField.getFirstFormAnnotation().setBackgroundColor(background.getColor());
        }

        choiceField.getFirstFormAnnotation().setFormFieldElement(lbModelElement);
        choiceField.enableFieldRegeneration();
        PdfFormCreator.getAcroForm(doc, true).addField(choiceField, page);
        FormFieldRendererUtil.reapplyProperties(this.modelElement, properties);

    }

    private float getCalculatedHeight(IRenderer flatRenderer) {
        Integer sizeProp = this.<Integer>getProperty(FormProperty.FORM_FIELD_SIZE);
        int size;
        if (sizeProp == null || sizeProp <= 0) {
            // Ensure height will not be negative or zero.
            // There is no particular reason for setting specifically 4.
            size = 4;
        } else {
            size = (int)sizeProp;
        }
        float maxOptionActualHeight = getMaxOptionActualHeight(flatRenderer);
        if (maxOptionActualHeight == Float.MIN_VALUE) {
            UnitValue fontSize = flatRenderer.<UnitValue>getProperty(Property.FONT_SIZE);
            if (fontSize != null && fontSize.isPointValue()) {
                // according to default styles for options (min-height: 1.2em)
                maxOptionActualHeight = fontSize.getValue() * 1.2f;
            } else {
                maxOptionActualHeight = 0;
            }
        }

        return size * maxOptionActualHeight;
    }

    private float getMaxOptionActualHeight(IRenderer flatRenderer) {
        float maxActualHeight = Float.MIN_VALUE;
        for (IRenderer child : flatRenderer.getChildRenderers()) {
            if (isOptionRenderer(child)) {
                float childHeight;
                if (child instanceof AbstractRenderer) {
                    AbstractRenderer abstractChild = (AbstractRenderer) child;
                    childHeight = abstractChild.applyMargins(abstractChild.getOccupiedAreaBBox(), false).getHeight();
                } else {
                    childHeight = child.getOccupiedArea().getBBox().getHeight();
                }
                if (childHeight > maxActualHeight) {
                    maxActualHeight = childHeight;
                }
            } else {
                float maxNestedHeight = getMaxOptionActualHeight(child);
                if (maxNestedHeight > maxActualHeight) {
                    maxActualHeight = maxNestedHeight;
                }
            }
        }
        return maxActualHeight;
    }

    private List<IRenderer> getSelectedOptions(IRenderer rendererSubTree) {
        List<IRenderer> selectedOptions = new ArrayList<>();
        List<IRenderer> optionsWhichMarkedSelected = getOptionsMarkedSelected(rendererSubTree);
        if (!optionsWhichMarkedSelected.isEmpty()) {
            if (isMultiple()) {
                selectedOptions.addAll(optionsWhichMarkedSelected);
            } else {
                selectedOptions.add(optionsWhichMarkedSelected.get(optionsWhichMarkedSelected.size() - 1));
            }
        }
        return selectedOptions;
    }

    private boolean isMultiple() {
        Boolean propertyAsBoolean = getPropertyAsBoolean(FormProperty.FORM_FIELD_MULTIPLE);
        return propertyAsBoolean != null && (boolean) propertyAsBoolean;
    }

    private void applySelectedStyle(IRenderer selectedOption) {
        RenderingMode mode = this.<RenderingMode>getProperty(Property.RENDERING_MODE);
        if (RenderingMode.HTML_MODE.equals(mode) && isFlatten() &&
                selectedOption.<Background>getProperty(Property.BACKGROUND) == null) {
            selectedOption.setProperty(Property.BACKGROUND, new Background(new DeviceRgb(206,206,206)));
        } else {
            selectedOption.setProperty(Property.BACKGROUND, new Background(new DeviceRgb(169, 204, 225)));
        }
        setFontColorRecursively(selectedOption);
    }

    /**
     * The `select` tag has default color css property,
     * therefore it makes sense to explicitly override this property to all children,
     * otherwise it will be not applied due to the css resolving mechanism.
     */
    private void setFontColorRecursively(IRenderer selectedOption) {
        selectedOption.setProperty(Property.FONT_COLOR, new TransparentColor(ColorConstants.BLACK));
        for (IRenderer renderer : selectedOption.getChildRenderers()) {
            setFontColorRecursively(renderer);
        }
    }
}
