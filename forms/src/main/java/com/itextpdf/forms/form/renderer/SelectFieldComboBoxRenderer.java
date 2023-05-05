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

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

import java.util.ArrayList;
import java.util.List;

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
    protected void applyAcroField(DrawContext drawContext) {
        // TODO DEVSIX-1901
    }

    @Override
    protected IRenderer createFlatRenderer() {
        return createFlatRenderer(false);
    }

    private IRenderer createFlatRenderer(boolean addAllOptionsToChildren) {
        AbstractSelectField selectField = (AbstractSelectField) modelElement;
        List<IBlockElement> options = selectField.getOptions();

        Div pseudoContainer = new Div();
        for (IBlockElement option : options) {
            pseudoContainer.add(option);
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

    private static Paragraph createComboBoxOptionFlatElement() {
        return createComboBoxOptionFlatElement(null, false);
    }

    private static Paragraph createComboBoxOptionFlatElement(String label, boolean simulateOptGroupMargin) {
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
        // These constants are defined according to values in default.css.
        // At least in Chrome paddings of options in comboboxes cannot be altered through css styles.
        float leftRightPaddingVal = 2 * 0.75f;
        float bottomPaddingVal = 0.75f;
        float topPaddingVal = 0;
        paragraph.setPaddings(topPaddingVal, leftRightPaddingVal, bottomPaddingVal, leftRightPaddingVal);
        return paragraph;
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
}
