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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.renderer.SignatureAppearanceRenderer;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extension of the {@link FormField} class representing a signature field in PDF.
 */
public class SignatureFieldAppearance extends FormField<SignatureFieldAppearance> {
    /**
     * Default paddings for the signature field.
     */
    private static final float DEFAULT_PADDING = 2;

    /**
     * Collection of the layout elements which will be rendered as a signature content.
     */
    private final List<IElement> contentElements = new ArrayList<IElement>();

    /**
     * We should support signing of existing fields with dots in name, but dots are now allowed in model element id.
     * So it is a placeholder for such cases.
     */
    private String idWithDots = null;

    /**
     * Creates a new {@link SignatureFieldAppearance} instance.
     *
     * @param id the id.
     */
    public SignatureFieldAppearance(String id) {
        // We should support signing of existing fields with dots in name.
        super(id != null && id.contains(".") ? "" : id);
        if (id.contains(".")) {
            idWithDots = id;
        }
        // Draw the borders inside the element by default
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        setProperty(Property.PADDING_TOP, UnitValue.createPointValue(DEFAULT_PADDING));
        setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(DEFAULT_PADDING));
        setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(DEFAULT_PADDING));
        setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(DEFAULT_PADDING));
    }

    /**
     * Sets the content for this signature.
     *
     * @param description {@link SignedAppearanceText} instance representing the signature text identifying the signer.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(SignedAppearanceText description) {
        addTextContent(description.generateDescriptionText());
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param description the signature text identifying the signer.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(String description) {
        addTextContent(description);
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param description {@link SignedAppearanceText} instance representing the signature text identifying the signer.
     * @param image       the Image object to render.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(SignedAppearanceText description, ImageData image) {
        addImageContent(image);
        addTextContent(description.generateDescriptionText());
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param description the signature text identifying the signer.
     * @param image       the Image object to render.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(String description, ImageData image) {
        addImageContent(image);
        addTextContent(description);
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param image the Image object to render.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(ImageData image) {
        addImageContent(image);
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param signerName  the name of the signer from the certificate.
     * @param description {@link SignedAppearanceText} instance representing the signature text identifying the signer.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(String signerName, SignedAppearanceText description) {
        addTextContent(signerName);
        addTextContent(description.generateDescriptionText());
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param signerName  the name of the signer from the certificate.
     * @param description {@link SignedAppearanceText} instance representing the signature text identifying the signer.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(String signerName, String description) {
        addTextContent(signerName);
        addTextContent(description);
        return this;
    }

    /**
     * Sets the content for this signature.
     *
     * @param data the custom signature data which will be rendered.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContent(Div data) {
        contentElements.add(data);
        return this;
    }

    /**
     * Gets the content for this signature.
     *
     * @return collection of the layout elements which will be rendered as a signature content.
     */
    public List<IElement> getContentElements() {
        return Collections.unmodifiableList(contentElements);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getId() {
        return idWithDots == null? super.getId() : idWithDots;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new SignatureAppearanceRenderer(this);
    }

    private void addTextContent(String text) {
        contentElements.add(new Paragraph(text).setMargin(0).setMultipliedLeading(0.9f));
    }

    private void addImageContent(ImageData imageData) {
        contentElements.add(new Image(imageData));
    }
}
