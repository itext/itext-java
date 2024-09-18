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

import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.form.element.SelectFieldItem;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link BlockRenderer} for select form fields.
 */
public abstract class AbstractSelectFieldRenderer extends BlockRenderer {

    /**
     * Creates a new {@link AbstractSelectFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    protected AbstractSelectFieldRenderer(AbstractSelectField modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        childRenderers.clear();
        addChild(createFlatRenderer());

        // Resolve width here in case it's relative, while parent width is still intact.
        // If it's inline-block context, relative width is already resolved.
        Float width = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
        if (width != null) {
            updateWidth(UnitValue.createPointValue((float) width));
        }

        float childrenMaxWidth = getMinMaxWidth().getMaxWidth();

        LayoutArea area = layoutContext.getArea().clone();
        area.getBBox().moveDown(INF - area.getBBox().getHeight()).setHeight(INF).setWidth(childrenMaxWidth + EPS);
        // A workaround for the issue that super.layout clears Property.FORCED_PLACEMENT,
        // but we need it later in this function
        final boolean isForcedPlacement = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        LayoutResult layoutResult = super.layout(new LayoutContext(area, layoutContext.getMarginsCollapseInfo(),
                layoutContext.getFloatRendererAreas(), layoutContext.isClippedHeight()));
        if (isForcedPlacement) {
            // Restore the Property.FORCED_PLACEMENT value as it was before super.layout
            setProperty(Property.FORCED_PLACEMENT, true);
        }
        if (layoutResult.getStatus() != LayoutResult.FULL) {
            if (isForcedPlacement) {
                layoutResult = makeLayoutResultFull(layoutContext.getArea(), layoutResult);
            } else {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
            }
        }

        float availableHeight = layoutContext.getArea().getBBox().getHeight();
        boolean isClippedHeight = layoutContext.isClippedHeight();

        Rectangle dummy = new Rectangle(0, 0);
        applyMargins(dummy, true);
        applyBorderBox(dummy, true);
        applyPaddings(dummy, true);
        float additionalHeight = dummy.getHeight();

        availableHeight -= additionalHeight;
        availableHeight = Math.max(availableHeight, 0);
        float actualHeight = getOccupiedArea().getBBox().getHeight() - additionalHeight;

        float finalSelectFieldHeight = getFinalSelectFieldHeight(availableHeight, actualHeight, isClippedHeight);
        if (finalSelectFieldHeight < 0) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
        }

        float delta = finalSelectFieldHeight - actualHeight;
        if (Math.abs(delta) > EPS) {
            getOccupiedArea().getBBox().increaseHeight(delta).moveDown(delta);
        }

        return layoutResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        if (isFlatten()) {
            super.draw(drawContext);
        } else {
            drawChildren(drawContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        if (isFlatten()) {
            super.drawChildren(drawContext);
        } else {
            applyAcroField(drawContext);
            writeAcroFormFieldLangAttribute(drawContext.getDocument());
        }
    }

    /**
     * Gets the accessibility language using {@link IAccessibleElement#getAccessibilityProperties()}.
     *
     * @return the accessibility language.
     */
    protected String getLang() {
        String language = null;
        if (this.getModelElement() instanceof IAccessibleElement) {
            language = ((IAccessibleElement) this.getModelElement()).getAccessibilityProperties().getLanguage();
        }
        return language;
    }

    /**
     * Sets the form accessibility language identifier of the form element in case the document is tagged.
     *
     * @param pdfDoc the document which contains form field.
     */
    protected void writeAcroFormFieldLangAttribute(PdfDocument pdfDoc) {
        if (pdfDoc.isTagged()) {
            TagTreePointer formParentPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            List<String> kidsRoles = formParentPointer.getKidsRoles();
            int lastFormIndex = kidsRoles.lastIndexOf(StandardRoles.FORM);
            TagTreePointer formPointer = formParentPointer.moveToKid(lastFormIndex);

            if (getLang() != null) {
                formPointer.getProperties().setLanguage(getLang());
            }
            formParentPointer.moveToParent();
        }
    }

    /**
     * Applies the accessibility properties to the form field.
     *
     * @param formField The form field to which the accessibility properties should be applied.
     * @param pdfDocument The document to which the form field belongs.
     */
    protected void applyAccessibilityProperties(PdfFormField formField, PdfDocument pdfDocument) {
        if (!pdfDocument.isTagged()) {
            return;
        }
        final AccessibilityProperties properties = ((IAccessibleElement) this.modelElement)
                .getAccessibilityProperties();
        final String alternativeDescription = properties.getAlternateDescription();
        if (alternativeDescription != null && !alternativeDescription.isEmpty()) {
            formField.setAlternativeName(alternativeDescription);
        }
    }

    /**
     * Creates the flat renderer instance.
     *
     * @return {@link IRenderer} instance.
     */
    protected abstract IRenderer createFlatRenderer();

    /**
     * Applies the AcroField widget.
     *
     * @param drawContext the draw context
     */
    protected abstract void applyAcroField(DrawContext drawContext);

    /**
     * Checks if form fields need to be flattened.
     *
     * @return true, if fields need to be flattened.
     */
    protected boolean isFlatten() {
        return (boolean) getPropertyAsBoolean(FormProperty.FORM_FIELD_FLATTEN);
    }

    /**
     * Gets the model id.
     *
     * @return the model id.
     */
    protected String getModelId() {
        return ((IFormField) getModelElement()).getId();
    }

    /**
     * Retrieve the options from select field (can be combo box or list box field) and set them
     * to the form field builder.
     *
     * @param builder {@link ChoiceFormFieldBuilder} to set options to
     * @param field   {@link AbstractSelectField} to retrieve the options from
     */
    protected void setupBuilderValues(ChoiceFormFieldBuilder builder, AbstractSelectField field) {
        List<SelectFieldItem> options = field.getOptions();
        if (options.isEmpty()) {
            builder.setOptions(new String[0]);
            return;
        }

        final boolean supportExportValueAndDisplayValue = field.hasExportAndDisplayValues();
        // If one element has export value and display value, then all elements must have export value and display value
        if (supportExportValueAndDisplayValue) {
            String[][] exportValuesAndDisplayValues = new String[options.size()][];
            for (int i = 0; i < options.size(); i++) {
                SelectFieldItem option = options.get(i);
                String[] exportValues = new String[2];
                exportValues[0] = option.getExportValue();
                exportValues[1] = option.getDisplayValue();
                exportValuesAndDisplayValues[i] = exportValues;
            }
            builder.setOptions(exportValuesAndDisplayValues);
        } else {
            // In normal case we just use display values as this will correctly give the one value that we need
            String[] displayValues = new String[options.size()];
            for (int i = 0; i < options.size(); i++) {
                SelectFieldItem option = options.get(i);
                displayValues[i] = option.getDisplayValue();
            }
            builder.setOptions(displayValues);
        }
    }

    /**
     * Returns final height of the select field.
     *
     * @param availableHeight available height of the layout area
     * @param actualHeight    actual occupied height of the select field
     * @param isClippedHeight indicates whether the layout area's height is clipped or not
     *
     * @return final height of the select field.
     */
    protected float getFinalSelectFieldHeight(float availableHeight, float actualHeight, boolean isClippedHeight) {
        boolean isForcedPlacement = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        if (!isClippedHeight && actualHeight > availableHeight) {
            if (isForcedPlacement) {
                return availableHeight;
            }
            return -1;
        }
        return actualHeight;
    }

    /**
     * Gets the conformance. If the conformance is not set, the conformance of the document is used.
     *
     * @param document the document
     *
     * @return the conformance or null if the conformance is not set.
     */
    protected PdfConformance getConformance(PdfDocument document) {
        final PdfConformance conformance = this.<PdfConformance>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL);
        if (conformance != null) {
            return conformance;
        }
        if (document == null) {
            return null;
        }
        return document.getConformance();
    }

    /**
     * Gets options that are marked as selected from the select field options subtree.
     *
     * @param optionsSubTree options subtree to get selected options
     *
     * @return selected options list.
     */
    protected List<IRenderer> getOptionsMarkedSelected(IRenderer optionsSubTree) {
        List<IRenderer> selectedOptions = new ArrayList<>();
        for (IRenderer option : optionsSubTree.getChildRenderers()) {
            if (isOptionRenderer(option)) {
                if (Boolean.TRUE.equals(option.<Boolean>getProperty(FormProperty.FORM_FIELD_SELECTED))) {
                    selectedOptions.add(option);
                }
            } else {
                List<IRenderer> subSelectedOptions = getOptionsMarkedSelected(option);
                selectedOptions.addAll(subSelectedOptions);
            }
        }
        return selectedOptions;
    }

    static boolean isOptGroupRenderer(IRenderer renderer) {
        return renderer.hasProperty(FormProperty.FORM_FIELD_LABEL) &&
                !renderer.hasProperty(FormProperty.FORM_FIELD_SELECTED);
    }

    static boolean isOptionRenderer(IRenderer child) {
        return child.hasProperty(FormProperty.FORM_FIELD_SELECTED);
    }

    private LayoutResult makeLayoutResultFull(LayoutArea layoutArea, LayoutResult layoutResult) {
        IRenderer splitRenderer = layoutResult.getSplitRenderer() == null ? this : layoutResult.getSplitRenderer();
        if (occupiedArea == null) {
            occupiedArea = new LayoutArea(layoutArea.getPageNumber(),
                    new Rectangle(layoutArea.getBBox().getLeft(), layoutArea.getBBox().getTop(), 0, 0));
        }
        layoutResult = new LayoutResult(LayoutResult.FULL, occupiedArea, splitRenderer, null);
        return layoutResult;
    }
}
