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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.element.ComboBoxField;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.forms.util.BorderStyleUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SelectFieldComboBoxRenderer} implementation for select field renderer.
 */
public class SelectFieldComboBoxRenderer extends AbstractSelectFieldRenderer {
    private final IRenderer minMaxWidthRenderer;

    /**
     * Creates a new {@link SelectFieldComboBoxRenderer} instance.
     *
     * @param modelElement the model element
     */
    public SelectFieldComboBoxRenderer(AbstractSelectField modelElement) {
        super(modelElement);
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.MIDDLE);
        setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
        minMaxWidthRenderer = createFlatRenderer(true);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new SelectFieldComboBoxRenderer((AbstractSelectField) modelElement);
    }

    @Override
    public MinMaxWidth getMinMaxWidth() {
        List<IRenderer> realChildRenderers = childRenderers;
        childRenderers = new ArrayList<>();
        childRenderers.add(minMaxWidthRenderer);
        MinMaxWidth minMaxWidth = super.getMinMaxWidth();
        childRenderers = realChildRenderers;
        return minMaxWidth;
    }

    @Override
    protected boolean allowLastYLineRecursiveExtraction() {
        return true;
    }

    @Override
    protected IRenderer createFlatRenderer() {
        return createFlatRenderer(false);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        final ComboBoxField comboBoxFieldModelElement = (ComboBoxField) this.modelElement;
        final String name = getModelId();
        final PdfDocument doc = drawContext.getDocument();
        final Rectangle area = getOccupiedAreaBBox();
        final PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        PdfFont font = getResolvedFont(doc);

        final ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setFont(font)
                .setGenericConformanceLevel(getGenericConformanceLevel(doc));

        modelElement.setProperty(Property.FONT_PROVIDER, this.<FontProvider>getProperty(Property.FONT_PROVIDER));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));
        setupBuilderValues(builder, comboBoxFieldModelElement);
        final PdfChoiceFormField comboBoxField = builder.createComboBox();
        comboBoxField.disableFieldRegeneration();
        applyAccessibilityProperties(comboBoxField, doc);
        final Background background = this.modelElement.<Background>getProperty(Property.BACKGROUND);
        if (background != null) {
            comboBoxField.getFirstFormAnnotation().setBackgroundColor(background.getColor());
        }
        BorderStyleUtil.applyBorderProperty(this, comboBoxField.getFirstFormAnnotation());

        UnitValue fontSize = getFontSize();
        if (fontSize != null) {
            comboBoxField.setFontSize(fontSize.getValue());
        }
        SelectFieldItem selectedLabel = comboBoxFieldModelElement.getSelectedOption();
        if (selectedLabel != null) {
            comboBoxField.setValue(selectedLabel.getDisplayValue());
        } else {
            String exportValue = comboBoxFieldModelElement.getSelectedExportValue();
            if (exportValue == null) {
                RenderingMode renderingMode = comboBoxFieldModelElement.<RenderingMode>getProperty(
                        Property.RENDERING_MODE);
                if (RenderingMode.HTML_MODE == renderingMode && comboBoxFieldModelElement.hasOptions()) {
                    comboBoxFieldModelElement.setSelected(0);
                    comboBoxField.setValue(comboBoxFieldModelElement.getSelectedExportValue());
                }
            } else {
                comboBoxField.setValue(comboBoxFieldModelElement.getSelectedExportValue());
            }
        }

        comboBoxField.getFirstFormAnnotation().setFormFieldElement(comboBoxFieldModelElement);
        comboBoxField.enableFieldRegeneration();

        PdfFormCreator.getAcroForm(doc, true).addField(comboBoxField, page);
    }


    private UnitValue getFontSize() {
        if (!this.hasProperty(Property.FONT_SIZE)) {
            return null;
        }
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(SelectFieldComboBoxRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        return fontSize;
    }

    private IRenderer createFlatRenderer(boolean addAllOptionsToChildren) {
        AbstractSelectField selectField = (AbstractSelectField) modelElement;
        List<SelectFieldItem> options = selectField.getItems();

        Div pseudoContainer = new Div();
        for (SelectFieldItem option : options) {
            pseudoContainer.add(option.getElement());
        }

        List<Paragraph> allOptions;
        IRenderer pseudoRendererSubTree = pseudoContainer.createRendererSubTree();
        if (addAllOptionsToChildren) {
            allOptions = getAllOptionsFlatElements(pseudoRendererSubTree);
        } else {
            allOptions = getSingleSelectedOptionFlatRenderer(pseudoRendererSubTree);
        }

        if (allOptions.isEmpty()) {
            allOptions.add(createComboBoxOptionFlatElement());
        }
        pseudoContainer.getChildren().clear();
        for (Paragraph option : allOptions) {
            pseudoContainer.add(option);
        }
        String lang = getLang();
        if (lang != null) {
            AccessibilityProperties properties = pseudoContainer.getAccessibilityProperties();
            if (properties.getLanguage() == null) {
                properties.setLanguage(lang);
            }
        }

        IRenderer rendererSubTree = pseudoContainer.createRendererSubTree();

        return rendererSubTree;
    }

    private List<Paragraph> getSingleSelectedOptionFlatRenderer(IRenderer optionsSubTree) {
        List<Paragraph> selectedOptionFlatRendererList = new ArrayList<>();
        List<IRenderer> selectedOptions = getOptionsMarkedSelected(optionsSubTree);
        IRenderer selectedOption;
        if (selectedOptions.isEmpty()) {
            selectedOption = getFirstOption(optionsSubTree);
        } else {
            selectedOption = selectedOptions.get(selectedOptions.size() - 1);
        }
        if (selectedOption != null) {
            String label = selectedOption.<String>getProperty(FormProperty.FORM_FIELD_LABEL);
            Paragraph p = createComboBoxOptionFlatElement(label, false);
            processLangAttribute(p, selectedOption);
            selectedOptionFlatRendererList.add(p);
        } else {
            ComboBoxField modelElement = (ComboBoxField) getModelElement();
            SelectFieldItem selectedOptionItem = modelElement.getSelectedOption();
            String label = modelElement.getSelectedExportValue();
            if (selectedOptionItem != null) {
                label = selectedOptionItem.getDisplayValue();
            }
            if (label != null) {
                Paragraph p = createComboBoxOptionFlatElement(label, false);
                p.setProperty(FormProperty.FORM_FIELD_SELECTED, true);
                processLangAttribute(p, p.getRenderer());
                selectedOptionFlatRendererList.add(p);
            }

        }
        return selectedOptionFlatRendererList;
    }

    private IRenderer getFirstOption(IRenderer renderer) {
        IRenderer firstOption = null;
        for (IRenderer child : renderer.getChildRenderers()) {
            if (isOptionRenderer(child)) {
                firstOption = child;
                break;
            }
            firstOption = getFirstOption(child);
            if (firstOption != null) {
                break;
            }
        }
        return firstOption;
    }

    private List<Paragraph> getAllOptionsFlatElements(IRenderer renderer) {
        return getAllOptionsFlatElements(renderer, false);
    }

    private List<Paragraph> getAllOptionsFlatElements(IRenderer renderer, boolean isInOptGroup) {
        List<Paragraph> options = new ArrayList<>();
        for (IRenderer child : renderer.getChildRenderers()) {
            if (isOptionRenderer(child)) {
                String label = child.<String>getProperty(FormProperty.FORM_FIELD_LABEL);
                options.add(createComboBoxOptionFlatElement(label, isInOptGroup));
            } else {
                options.addAll(getAllOptionsFlatElements(child, isInOptGroup || isOptGroupRenderer(child)));
            }
        }
        return options;
    }

    private void processLangAttribute(Paragraph optionFlatElement, IRenderer originalOptionRenderer) {
        IPropertyContainer propertyContainer = originalOptionRenderer.getModelElement();
        if (propertyContainer instanceof IAccessibleElement) {
            String lang = ((IAccessibleElement) propertyContainer).getAccessibilityProperties().getLanguage();
            AccessibilityProperties properties = ((IAccessibleElement) optionFlatElement).getAccessibilityProperties();
            if (properties.getLanguage() == null) {
                properties.setLanguage(lang);
            }
        }
    }

    private Paragraph createComboBoxOptionFlatElement() {
        return createComboBoxOptionFlatElement(null, false);
    }

    private Paragraph createComboBoxOptionFlatElement(String label, boolean simulateOptGroupMargin) {
        Paragraph paragraph = new Paragraph().setMargin(0);
        if (simulateOptGroupMargin) {
            paragraph.add("\u200d    ");
        }

        if (label == null || label.isEmpty()) {
            label = "\u00A0";
        }

        paragraph.add(label);
        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        paragraph.setFontColor(modelElement.<TransparentColor>getProperty(Property.FONT_COLOR));
        UnitValue fontSize = modelElement.<UnitValue>getProperty(Property.FONT_SIZE);
        if (fontSize != null) {
            paragraph.setFontSize(fontSize.getValue());
        }

        PdfFont font = getResolvedFont(null);
        if (font != null) {
            paragraph.setFont(font);
        }

        final float paddingTop = 0f;
        final float paddingBottom = 0.75f;
        final float paddingLeft = 1.5f;

        float paddingRight = 1.5f;
        if (!isFlatten()) {
            final float extraPaddingChrome = 10f;
            paddingRight += extraPaddingChrome;
        }
        paragraph.setPaddings(paddingTop, paddingRight, paddingBottom, paddingLeft);
        return paragraph;
    }

}
