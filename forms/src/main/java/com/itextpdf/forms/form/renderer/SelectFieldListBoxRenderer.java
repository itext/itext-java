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

import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import java.util.ArrayList;
import java.util.List;

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

        if (!isFlatten() || layoutResult.getStatus() != LayoutResult.FULL || optionsContainer == null || optionsContainer.getOccupiedArea() == null) {
            return layoutResult;
        }

        if (isOverflowProperty(OverflowPropertyValue.HIDDEN, this, Property.OVERFLOW_Y)) {
            List<IRenderer> selectedOptions = getSelectedOptions(this);
            IRenderer firstSelectedOption;
            if (!selectedOptions.isEmpty() && (firstSelectedOption = selectedOptions.get(0)).getOccupiedArea() != null) {
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
        List<IBlockElement> options = selectField.getOptions();

        Div optionsContainer = new Div();
        for (IBlockElement option : options) {
            optionsContainer.add(option);
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
            pStub.setProperty(FormProperty.FORM_FIELD_SELECTED, false); // applying this property for the sake of finding this element as option
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
            calculatedHeight = actualHeight;
        }
        return super.getFinalSelectFieldHeight(availableHeight, calculatedHeight, isClippedHeight);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        // TODO DEVSIX-1901
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
                maxOptionActualHeight = fontSize.getValue() * 1.2f; // according to default styles for options (min-height: 1.2em)
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
        selectedOption.setProperty(Property.BACKGROUND, new Background(new DeviceRgb(0, 120, 215)));
        setFontColorRecursively(selectedOption);
    }

    /**
     * The `select` tag has default color css property, therefore it makes sense to explicitly override this property to all children,
     * otherwise it will be not applied due to the css resolving mechanism.
     */
    private void setFontColorRecursively(IRenderer selectedOption) {
        selectedOption.setProperty(Property.FONT_COLOR, new TransparentColor(ColorConstants.WHITE));
        for (IRenderer renderer : selectedOption.getChildRenderers()) {
            setFontColorRecursively(renderer);
        }
    }
}
