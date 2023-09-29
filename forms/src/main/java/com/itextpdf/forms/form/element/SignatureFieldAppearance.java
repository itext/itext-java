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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.forms.form.renderer.SignatureAppearanceRenderer;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.Calendar;

/**
 * Extension of the {@link FormField} class representing a signature field in PDF.
 */
public class SignatureFieldAppearance extends FormField<SignatureFieldAppearance> {
    /**
     * Default paddings for the signature field.
     */
    private static final float DEFAULT_PADDING = 2;

    /**
     * The rendering mode chosen for visible signatures.
     */
    private RenderingMode renderingMode = RenderingMode.DESCRIPTION;

    /**
     * The reason for signing.
     */
    private String reason = "";

    /**
     * The caption for the reason for signing.
     */
    private String reasonCaption = "Reason: ";

    /**
     * Holds value of property location.
     */
    private String location = "";

    /**
     * The caption for the location of signing.
     */
    private String locationCaption = "Location: ";

    /**
     * Holds value of the application that creates the signature.
     */
    private String signatureCreator = "";

    /**
     * The contact name of the signer.
     */
    private String contact = "";

    /**
     * The name of the signer from the certificate.
     */
    private String signedBy = "";

    /**
     * Holds value of property signDate.
     */
    private Calendar signDate;
    private boolean isSignDateSet = false;

    /**
     * The image that needs to be used for a visible signature.
     */
    private ImageData signatureGraphic = null;

    /**
     * A background image for the text.
     */
    private ImageData image;

    /**
     * The scaling to be applied to the background image.
     */
    private float imageScale = 0;

    /**
     * The text that represents the description of the signature.
     */
    private String description;

    /**
     * Indicates if we need to reuse the existing appearance as a background.
     */
    private boolean reuseAppearance = false;

    /**
     * Background level of the signature appearance.
     */
    private PdfFormXObject n0;

    /**
     * Signature appearance layer that contains information about the signature.
     */
    private PdfFormXObject n2;

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
     * Gets the rendering mode for this signature model element.
     *
     * @return the rendering mode for this signature.
     */
    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    /**
     * Sets the rendering mode for this signature.
     *
     * @param renderingMode the rendering mode.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return reason for signing.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the signing reason.
     *
     * @param reason signing reason.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Sets the caption for the signing reason.
     *
     * @param reasonCaption new signing reason caption.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setReasonCaption(String reasonCaption) {
        this.reasonCaption = reasonCaption;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return signing location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the signing location.
     *
     * @param location new signing location.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the caption for the signing location.
     *
     * @param locationCaption new signing location caption.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setLocationCaption(String locationCaption) {
        this.locationCaption = locationCaption;
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return the signature creator.
     */
    public String getSignatureCreator() {
        return signatureCreator;
    }

    /**
     * Sets the name of the application used to create the signature.
     *
     * @param signatureCreator new name of the application signing a document.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setSignatureCreator(String signatureCreator) {
        this.signatureCreator = signatureCreator;
        return this;
    }

    /**
     * Returns the signing contact.
     *
     * @return the signing contact.
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the signing contact.
     *
     * @param contact new signing contact.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setContact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Gets the Image object to render.
     *
     * @return the image.
     */
    public ImageData getSignatureGraphic() {
        return signatureGraphic;
    }

    /**
     * Sets the Image object to render.
     *
     * @param signatureGraphic image rendered.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setSignatureGraphic(ImageData signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
        return this;
    }

    /**
     * Indicates if the existing appearances needs to be reused as a background.
     *
     * @return appearances reusing flag value.
     */
    public boolean isReuseAppearance() {
        return reuseAppearance;
    }

    /**
     * Indicates that the existing appearances needs to be reused as a background.
     *
     * @param reuseAppearance is an appearances reusing flag value to set.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setReuseAppearance(boolean reuseAppearance) {
        this.reuseAppearance = reuseAppearance;
        return this;
    }

    /**
     * Gets the background image for the text.
     *
     * @return the background image.
     */
    public ImageData getImage() {
        return this.image;
    }

    /**
     * Sets the background image for the text.
     *
     * @param image the background image.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setImage(ImageData image) {
        this.image = image;
        return this;
    }

    /**
     * Gets the scaling to be applied to the background image.
     *
     * @return the scaling to be applied to the background image.
     */
    public float getImageScale() {
        return this.imageScale;
    }

    /**
     * Sets the scaling to be applied to the background image. If it's zero the image
     * will fully fill the rectangle. If it's less than zero the image will fill the rectangle but
     * will keep the proportions. If it's greater than zero that scaling will be applied.
     * In any of the cases the image will always be centered. It's zero by default.
     *
     * @param imageScale the scaling to be applied to the background image.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setImageScale(float imageScale) {
        this.imageScale = imageScale;
        return this;
    }

    /**
     * Sets the signature text identifying the signer.
     *
     * @param text the signature text identifying the signer. If null or not set
     *             a standard description will be used.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setDescription(String text) {
        description = text;
        return this;
    }

    /**
     * Gets the signature text identifying the signer if set by setDescription().
     *
     * @param generate if true and description wasn't set by user, description will be generated.
     *
     * @return the signature text identifying the signer.
     */
    public String getDescription(boolean generate) {
        return generate && description == null ? generateDescriptionText() : description;
    }

    /**
     * Sets the name of the signer from the certificate.
     *
     * @param signedBy name of the signer.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setSignedBy(String signedBy) {
        this.signedBy = signedBy;
        return this;
    }

    /**
     * Gets the name of the signer from the certificate.
     *
     * @return signedBy name of the signer.
     */
    public String getSignedBy() {
        return signedBy;
    }

    /**
     * Returns the signature date.
     *
     * @return the signature date
     */
    public java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate new signature date.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        this.isSignDateSet = true;
        return this;
    }

    /**
     * Gets the background layer that is present when creating the signature field if it was set.
     *
     * @return n0 layer xObject.
     */
    public PdfFormXObject getBackgroundLayer() {
        return n0;
    }

    /**
     * Sets the background layer that is present when creating the signature field.
     *
     * @param n0 layer xObject.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setBackgroundLayer(PdfFormXObject n0) {
        this.n0 = n0;
        return this;
    }

    /**
     * Gets the signature appearance layer that contains information about the signature if it was set.
     *
     * @return n2 layer xObject.
     */
    public PdfFormXObject getSignatureAppearanceLayer() {
        return n2;
    }

    /**
     * Sets the signature appearance layer that contains information about the signature, e.g. the line art for the
     * handwritten signature, the text giving the signer’s name, date, reason, location and so on.
     *
     * @param n2 layer xObject.
     *
     * @return this same {@link SignatureFieldAppearance} instance.
     */
    public SignatureFieldAppearance setSignatureAppearanceLayer(PdfFormXObject n2) {
        this.n2 = n2;
        return this;
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

    private String generateDescriptionText() {
        StringBuilder buf = new StringBuilder();
        if (!signedBy.isEmpty()) {
            buf.append("Digitally signed by ").append(signedBy);
        }
        if (isSignDateSet) {
            buf.append('\n').append("Date: ").append(DateTimeUtil.dateToString(signDate));
        }
        if (reason != null) {
            buf.append('\n').append(reasonCaption).append(reason);
        }
        if (location != null) {
            buf.append('\n').append(locationCaption).append(location);
        }
        return buf.toString();
    }

    /**
     * Signature rendering modes.
     */
    public enum RenderingMode {
        /**
         * The rendering mode is just the description.
         */
        DESCRIPTION,
        /**
         * The rendering mode is the name of the signer and the description.
         */
        NAME_AND_DESCRIPTION,
        /**
         * The rendering mode is an image and the description.
         */
        GRAPHIC_AND_DESCRIPTION,
        /**
         * The rendering mode is just an image.
         */
        GRAPHIC
    }
}
