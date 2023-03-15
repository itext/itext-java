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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.MetaInfoContainer;

import java.util.List;

/**
 * The {@link AbstractOneLineTextFieldRenderer} implementation for buttons with kids.
 */
public class ButtonRenderer extends BlockRenderer {

    private static final float DEFAULT_FONT_SIZE = 12f;

    public ButtonRenderer(Button modelElement) {
        super(modelElement);
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        if (!isFlatten()) {
            String value = getDefaultValue();
            String name = getModelId();
            UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
            if (!fontSize.isPointValue()) {
                fontSize = UnitValue.createPointValue(DEFAULT_FONT_SIZE);
            }
            PdfDocument doc = drawContext.getDocument();
            Rectangle area = getOccupiedArea().getBBox().clone();
            applyMargins(area, false);
            PdfPage page = doc.getPage(occupiedArea.getPageNumber());

            final TransparentColor transparentColor = getPropertyAsTransparentColor(Property.FONT_COLOR);
            final Color color = transparentColor == null ? null : transparentColor.getColor();
            final float fontSizeValue = fontSize.getValue();
            final PdfFont font = doc.getDefaultFont();

            final PdfButtonFormField button = new PushButtonFormFieldBuilder(doc, name)
                    .setWidgetRectangle(area).setCaption(value).createPushButton();
            button.setFont(font).setFontSize(fontSizeValue);
            button.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_NONE);
            button.getFirstFormAnnotation().setBorderWidth(0);
            button.getFirstFormAnnotation().setBackgroundColor(null);
            if (color != null) {
                button.setColor(color);
            }
            PdfAcroForm forms = PdfAcroForm.getAcroForm(doc, true);
            //Add fields only if it isn't already added. This can happen on split.
            if (forms.getField(name) == null) {
                forms.addField(button, page);
            }

            if (doc.isTagged()) {
                TagTreePointer formParentPointer = doc.getTagStructureContext().getAutoTaggingPointer();
                List<String> kidsRoles = formParentPointer.getKidsRoles();
                int lastFormIndex = kidsRoles.lastIndexOf(StandardRoles.FORM);
                TagTreePointer formPointer = formParentPointer.moveToKid(lastFormIndex);

                String lang = this.<String>getProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE);
                if (lang != null) {
                    formPointer.getProperties().setLanguage(lang);
                }
                formParentPointer.moveToParent();
            }
        }
    }

    @Override
    protected Float getLastYLineRecursively() {
        return super.getFirstYLineRecursively();
    }

    @Override
    public IRenderer getNextRenderer() {
        return new ButtonRenderer((Button) modelElement);
    }

    //NOTE: Duplicates methods from AbstractFormFieldRenderer should be changed in next major version

    /**
     * Gets the model id.
     *
     * @return the model id
     */
    protected String getModelId() {
        return ((IFormField) getModelElement()).getId();
    }

    /**
     * Checks if form fields need to be flattened.
     *
     * @return true, if fields need to be flattened
     */
    public boolean isFlatten() {
        Boolean flatten = getPropertyAsBoolean(FormProperty.FORM_FIELD_FLATTEN);
        return flatten == null ?
                (boolean) modelElement.<Boolean>getDefaultProperty(FormProperty.FORM_FIELD_FLATTEN) : (boolean) flatten;
    }

    /**
     * Gets the default value of the form field.
     *
     * @return the default value of the form field
     */
    public String getDefaultValue() {
        String defaultValue = this.<String>getProperty(FormProperty.FORM_FIELD_VALUE);
        return defaultValue == null ?
                modelElement.<String>getDefaultProperty(FormProperty.FORM_FIELD_VALUE) : defaultValue;
    }
}
