/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
    private IRenderer minMaxWidthRenderer;

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
    protected IRenderer createFlatRenderer() {
        return createFlatRenderer(false);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        // TODO DEVSIX-1901
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
