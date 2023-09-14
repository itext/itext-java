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
package com.itextpdf.signatures;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.form.element.SigField;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Provides convenient methods to make a signature appearance. Use it in conjunction with {@link PdfSigner}.
 */
public class PdfSignatureAppearance {

    /**
     * The document to be signed.
     */
    private final PdfDocument document;

    /**
     * Signature model element.
     */
    private SigField modelElement = new SigField("");

    /**
     * The page where the signature will appear.
     */
    private int page = 1;

    /**
     * The coordinates of the rectangle for a visible signature,
     * or a zero-width, zero-height rectangle for an invisible signature.
     */
    private Rectangle rect;

    /**
     * Rectangle that represent the position and dimension of the signature in the page.
     */
    private Rectangle pageRect;

    /**
     * The rendering mode chosen for visible signatures.
     */
    private RenderingMode renderingMode = RenderingMode.DESCRIPTION;

    /**
     * The signing certificate.
     */
    private Certificate signCertificate;

    /**
     * Font for the text in Layer 2.
     */
    private PdfFont layer2Font;

    /**
     * Font size for the font of Layer 2.
     */
    private float layer2FontSize = 0;

    /**
     * Font color for the font of Layer 2.
     */
    private Color layer2FontColor;

    /**
     * Zero level of the signature appearance.
     */
    private PdfFormXObject n0;

    /**
     * Second level of the signature appearance.
     */
    private PdfFormXObject n2;

    /**
     * Creates a PdfSignatureAppearance.
     *
     * @param document PdfDocument
     * @param pageRect Rectangle of the appearance
     * @param pageNumber Number of the page the appearance should be on
     */
    protected PdfSignatureAppearance(PdfDocument document, Rectangle pageRect, int pageNumber) {
        this.document = document;
        this.pageRect = new Rectangle(pageRect);
        this.rect = new Rectangle(pageRect.getWidth(), pageRect.getHeight());
        this.page = pageNumber;
    }

    /**
     * Provides the page number of the signature field which this signature
     * appearance is associated with.
     *
     * @return The page number of the signature field which this signature
     * appearance is associated with.
     */
    public int getPageNumber() {
        return page;
    }

    /**
     * Sets the page number of the signature field which this signature
     * appearance is associated with. Implicitly calls {@link PdfSignatureAppearance#setPageRect}
     * which considers page number to process the rectangle correctly.
     *
     * @param pageNumber The page number of the signature field which
     *                   this signature appearance is associated with.
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setPageNumber(int pageNumber) {
        this.page = pageNumber;
        setPageRect(pageRect);
        return this;
    }

    /**
     * Provides the rectangle that represent the position and dimension
     * of the signature field in the page.
     *
     * @return the rectangle that represent the position and dimension
     * of the signature field in the page
     */
    public Rectangle getPageRect() {
        return pageRect;
    }

    /**
     * Sets the rectangle that represent the position and dimension of
     * the signature field in the page.
     *
     * @param pageRect The rectangle that represents the position and
     *                 dimension of the signature field in the page.
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setPageRect(Rectangle pageRect) {
        this.pageRect = new Rectangle(pageRect);
        this.rect = new Rectangle(pageRect.getWidth(), pageRect.getHeight());
        return this;
    }

    /**
     * Get Layer 0 of the appearance.
     *
     * <p>
     * The size of the layer is determined by the rectangle set via
     * {@link PdfSignatureAppearance#setPageRect(Rectangle)}
     *
     * @return layer 0
     */
    public PdfFormXObject getLayer0() {
        if (n0 == null) {
            n0 = new PdfFormXObject(rect);
            n0.makeIndirect(document);
        }
        return n0;
    }

    /**
     * Get Layer 2 of the appearance.
     *
     * <p>
     * The size of the layer is determined by the rectangle set via
     * {@link PdfSignatureAppearance#setPageRect(Rectangle)}
     *
     * @return layer 2
     */
    public PdfFormXObject getLayer2() {
        if (n2 == null) {
            n2 = new PdfFormXObject(rect);
            n2.makeIndirect(document);
        }
        return n2;
    }

    /**
     * Gets the rendering mode for this signature.
     *
     * @return the rendering mode for this signature
     */
    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    /**
     * Sets the rendering mode for this signature.
     *
     * @param renderingMode the rendering mode
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
        switch (renderingMode) {
            case NAME_AND_DESCRIPTION:
                modelElement.setRenderingMode(SigField.RenderingMode.NAME_AND_DESCRIPTION);
                break;
            case GRAPHIC_AND_DESCRIPTION:
                modelElement.setRenderingMode(SigField.RenderingMode.GRAPHIC_AND_DESCRIPTION);
                break;
            case GRAPHIC:
                modelElement.setRenderingMode(SigField.RenderingMode.GRAPHIC);
                break;
            default:
                modelElement.setRenderingMode(SigField.RenderingMode.DESCRIPTION);
                break;
        }
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return reason for signing
     */
    public String getReason() {
        return modelElement.getReason();
    }

    /**
     * Sets the signing reason.
     *
     * @param reason signing reason.
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setReason(String reason) {
        modelElement.setReason(reason);
        return this;
    }

    /**
     * Sets the caption for the signing reason.
     *
     * @param reasonCaption A new signing reason caption
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setReasonCaption(String reasonCaption) {
        modelElement.setReasonCaption(reasonCaption);
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return signing location
     */
    public String getLocation() {
        return modelElement.getLocation();
    }

    /**
     * Sets the signing location.
     *
     * @param location A new signing location
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLocation(String location) {
        modelElement.setLocation(location);
        return this;
    }

    /**
     * Sets the caption for the signing location.
     *
     * @param locationCaption A new signing location caption
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLocationCaption(String locationCaption) {
        modelElement.setLocationCaption(locationCaption);
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return The signature creator
     */
    public String getSignatureCreator(){
        return modelElement.getSignatureCreator();
    }

    /**
     * Sets the name of the application used to create the signature.
     *
     * @param signatureCreator A new name of the application signing a document
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setSignatureCreator(String signatureCreator) {
        modelElement.setSignatureCreator(signatureCreator);
        return this;
    }

    /**
     * Returns the signing contact.
     *
     * @return The signing contact
     */
    public String getContact() {
        return modelElement.getContact();
    }

    /**
     * Sets the signing contact.
     *
     * @param contact A new signing contact
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setContact(String contact) {
        modelElement.setContact(contact);
        return this;
    }

    /**
     * Sets the certificate used to provide the text in the appearance.
     * This certificate doesn't take part in the actual signing process.
     *
     * @param signCertificate the certificate
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setCertificate(Certificate signCertificate) {
        this.signCertificate = signCertificate;
        String signedBy =
                CertificateInfo.getSubjectFields((X509Certificate) signCertificate).getField("CN");

        if (signedBy == null) {
            signedBy = CertificateInfo.getSubjectFields((X509Certificate) signCertificate).getField("E");
        }

        if (signedBy == null) {
            signedBy = "";
        }
        modelElement.setSignedBy(signedBy);
        return this;
    }

    /**
     * Get the signing certificate.
     *
     * @return the signing certificate
     */
    public Certificate getCertificate() {
        return signCertificate;
    }

    /**
     * Gets the Image object to render.
     *
     * @return the image
     */
    public ImageData getSignatureGraphic() {
        return modelElement.getSignatureGraphic();
    }

    /**
     * Sets the Image object to render when Render is set to RenderingMode.GRAPHIC or RenderingMode.GRAPHIC_AND_DESCRIPTION.
     *
     * @param signatureGraphic image rendered. If null the mode is defaulted to RenderingMode.DESCRIPTION
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setSignatureGraphic(ImageData signatureGraphic) {
        modelElement.setSignatureGraphic(signatureGraphic);
        return this;
    }

    /**
     * Indicates that the existing appearances needs to be reused as layer 0.
     *
     * @param reuseAppearance is an appearances reusing flag value to set
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setReuseAppearance(boolean reuseAppearance) {
        modelElement.setReuseAppearance(reuseAppearance);
        return this;
    }

    // layer 2

    /**
     * Gets the background image for the layer 2.
     *
     * @return the background image for the layer 2
     */
    public ImageData getImage() {
        return modelElement.getImage();
    }

    /**
     * Sets the background image for the text in the layer 2.
     *
     * @param image the background image for the layer 2
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setImage(ImageData image) {
        modelElement.setImage(image);
        return this;
    }

    /**
     * Gets the scaling to be applied to the background image.
     *
     * @return the scaling to be applied to the background image
     */
    public float getImageScale() {
        return modelElement.getImageScale();
    }

    /**
     * Sets the scaling to be applied to the background image. If it's zero the image
     * will fully fill the rectangle. If it's less than zero the image will fill the rectangle but
     * will keep the proportions. If it's greater than zero that scaling will be applied.
     * In any of the cases the image will always be centered. It's zero by default.
     *
     * @param imageScale the scaling to be applied to the background image
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setImageScale(float imageScale) {
        modelElement.setImageScale(imageScale);
        return this;
    }

    /**
     * Sets the signature text identifying the signer.
     *
     * @param text the signature text identifying the signer. If null or not set
     * a standard description will be used
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLayer2Text(String text) {
        modelElement.setDescription(text);
        return this;
    }

    /**
     * Gets the signature text identifying the signer if set by setLayer2Text().
     *
     * @return the signature text identifying the signer
     */
    public String getLayer2Text() {
        return modelElement.getDescription(false);
    }

    /**
     * Gets the n2 and n4 layer font.
     *
     * @return the n2 and n4 layer font
     */
    public PdfFont getLayer2Font() {
        return this.layer2Font;
    }

    /**
     * Sets the n2 and n4 layer font. If the font size is zero, auto-fit will be used.
     *
     * @param layer2Font the n2 and n4 font
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLayer2Font(PdfFont layer2Font) {
        this.layer2Font = layer2Font;
        modelElement.setFont(layer2Font);
        return this;
    }

    /**
     * Sets the n2 and n4 layer font size.
     *
     * @param fontSize font size
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLayer2FontSize(float fontSize) {
        this.layer2FontSize = fontSize;
        modelElement.setFontSize(fontSize);
        return this;
    }

    /**
     * Gets the n2 and n4 layer font size.
     *
     * @return the n2 and n4 layer font size
     */
    public float getLayer2FontSize() {
        return layer2FontSize;
    }

    /**
     * Sets the n2 and n4 layer font color.
     *
     * @param color font color
     *
     * @return this instance to support fluent interface
     */
    public PdfSignatureAppearance setLayer2FontColor(Color color) {
        this.layer2FontColor = color;
        modelElement.setFontColor(color);
        return this;
    }

    /**
     * Gets the n2 and n4 layer font color.
     *
     * @return the n2 and n4 layer font color
     */
    public Color getLayer2FontColor() {
        return layer2FontColor;
    }

    /**
     * Gets the signature layout element.
     *
     * @return the signature layout element.
     */
    public SigField getModelElement() {
        modelElement.setBackgroundLayer(n0);
        modelElement.setSignatureAppearanceLayer(n2);
        return modelElement;
    }

    /**
     * Sets the signature layout element.
     *
     * @param modelElement the signature layout element.
     */
    public void setModelElement(SigField modelElement) {
        this.modelElement = modelElement;
    }

    /**
     * Gets the visibility status of the signature.
     *
     * @return the visibility status of the signature
     */
    public boolean isInvisible() {
        return rect == null || rect.getWidth() == 0 || rect.getHeight() == 0;
    }

    /**
     * Constructs appearance (top-level) for a signature.
     *
     * @return a top-level signature appearance
     * @throws IOException if font cannot be created
     * @see <a href="https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/PPKAppearances.pdf">Adobe Pdf Digital
     * Signature Appearances</a>
     */
    protected PdfFormXObject getAppearance() throws IOException {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        String name = modelElement.getId();
        boolean fieldExist = signatureUtil.doesSignatureFieldExist(name);
        PdfSignatureFormField sigField;
        if (fieldExist) {
            sigField = (PdfSignatureFormField) PdfFormCreator.getAcroForm(document, true).getField(name);
        } else {
            sigField = new SignatureFormFieldBuilder(document, modelElement.getId())
                    .setWidgetRectangle(rect).createSignature();
        }
        sigField.getFirstFormAnnotation().setFormFieldElement(modelElement);
        sigField.regenerateField();
        return new PdfFormXObject(sigField.getFirstFormAnnotation().getPdfObject()
                .getAsDictionary(PdfName.AP).getAsStream(PdfName.N));
    }

    /**
     * Returns the signature date.
     *
     * @return the signature date
     */
    protected java.util.Calendar getSignDate() {
        return modelElement.getSignDate();
    }

    /**
     * Sets the signature date.
     *
     * @param signDate A new signature date
     *
     * @return this instance to support fluent interface
     */
    protected PdfSignatureAppearance setSignDate(java.util.Calendar signDate) {
        modelElement.setSignDate(signDate);
        return this;
    }

    /**
     * Set the field name of the appearance. Field name indicates the field to be signed if it is already presented
     * in the document (signing existing field). Required for reuseAppearance option.
     *
     * @param fieldName name of the field
     *
     * @return this instance to support fluent interface
     */
    protected PdfSignatureAppearance setFieldName(String fieldName) {
        SigField newModelElement = new SigField(fieldName);
        newModelElement.setRenderingMode(modelElement.getRenderingMode());
        newModelElement.setReason(modelElement.getReason());
        newModelElement.setLocation(modelElement.getLocation());
        newModelElement.setSignatureCreator(modelElement.getSignatureCreator());
        newModelElement.setContact(modelElement.getContact());
        newModelElement.setSignatureGraphic(modelElement.getSignatureGraphic());
        newModelElement.setReuseAppearance(modelElement.isReuseAppearance());
        newModelElement.setImage(modelElement.getImage());
        newModelElement.setImageScale(modelElement.getImageScale());
        newModelElement.setDescription(modelElement.getDescription(false));
        newModelElement.setSignedBy(modelElement.getSignedBy());
        newModelElement.setSignDate(modelElement.getSignDate());
        newModelElement.setSignedBy(modelElement.getSignedBy());
        newModelElement.setFont(layer2Font);
        newModelElement.setFontSize(layer2FontSize);
        newModelElement.setFontColor(layer2FontColor);
        modelElement = newModelElement;
        return this;
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
