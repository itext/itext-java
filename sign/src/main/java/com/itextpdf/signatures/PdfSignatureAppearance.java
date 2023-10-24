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
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BackgroundPosition;
import com.itextpdf.layout.properties.BackgroundRepeat;
import com.itextpdf.layout.properties.BackgroundSize;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;

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
    private SignatureFieldAppearance modelElement;

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
     * Holds value of property signDate.
     */
    private Calendar signDate;

    /**
     * The signing certificate.
     */
    private Certificate signCertificate;

    /**
     * The image that needs to be used for a visible signature.
     */
    private ImageData signatureGraphic = null;

    /**
     * A background image for the text in layer 2.
     */
    private ImageData image;

    /**
     * The scaling to be applied to the background image.
     */
    private float imageScale;

    /**
     * The text that goes in Layer 2 of the signature appearance.
     */
    private String description;

    /**
     * Font for the text in Layer 2.
     */
    private PdfFont font;

    /**
     * Font provider for the text.
     */
    private FontProvider fontProvider;

    /**
     * Font family for the text.
     */
    private String[] fontFamilyNames;

    /**
     * Font size for the font of Layer 2.
     */
    private float fontSize = 0;

    /**
     * Font color for the font of Layer 2.
     */
    private Color fontColor;

    /**
     * Zero level of the signature appearance.
     */
    private PdfFormXObject n0;

    /**
     * Second level of the signature appearance.
     */
    private PdfFormXObject n2;

    /**
     * Indicates the field to be signed.
     */
    private String fieldName = "";

    /**
     * Indicates if we need to reuse the existing appearance as layer 0.
     */
    private boolean reuseAppearance = false;

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
     *
     * @deprecated won't be public in the next major release. Use {@link PdfSigner#getPageNumber()} instead.
     */
    @Deprecated
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
     * @return this instance to support fluent interface.
     *
     * @deprecated won't be public in the next major release. Use {@link PdfSigner#setPageNumber(int)} instead.
     */
    @Deprecated
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
     * of the signature field in the page.
     *
     * @deprecated won't be public in the next major release. Use {@link PdfSigner#getPageRect()} instead.
     */
    @Deprecated
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
     * @return this instance to support fluent interface.
     *
     * @deprecated won't be public in the next major release. Use {@link PdfSigner#setPageRect(Rectangle)} instead.
     */
    @Deprecated
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
     * @return layer 0.
     *
     * @deprecated will be deleted in the next major release.
     * See {@link PdfSignatureFormField#setBackgroundLayer(PdfFormXObject)}.
     */
    @Deprecated
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
     * @return layer 2.
     *
     * @deprecated will be deleted in the next major release.
     * See {@link PdfSignatureFormField#setSignatureAppearanceLayer(PdfFormXObject)}.
     */
    @Deprecated
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
     * @return the rendering mode for this signature.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}, rendering mode will be detected depending on specified
     * {@code setContent} method parameters.
     */
    @Deprecated
    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    /**
     * Sets the rendering mode for this signature.
     *
     * @param renderingMode the rendering mode.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}, rendering mode will be detected depending on specified
     * {@code setContent} method parameters.
     */
    @Deprecated
    public PdfSignatureAppearance setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return reason for signing.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public String getReason() {
        return reason;
    }

    /**
     * Sets the signing reason.
     *
     * @param reason signing reason.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfSignatureAppearance setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Sets the caption for the signing reason.
     *
     * @param reasonCaption A new signing reason caption.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfSignatureAppearance setReasonCaption(String reasonCaption) {
        this.reasonCaption = reasonCaption;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return signing location.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public String getLocation() {
        return location;
    }

    /**
     * Sets the signing location.
     *
     * @param location A new signing location.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfSignatureAppearance setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the caption for the signing location.
     *
     * @param locationCaption A new signing location caption.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfSignatureAppearance setLocationCaption(String locationCaption) {
        this.locationCaption = locationCaption;
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return The signature creator.
     *
     * @deprecated Use {@link PdfSigner#getSignatureCreator()} instead.
     */
    @Deprecated
    public String getSignatureCreator() {
        return signatureCreator;
    }

    /**
     * Sets the name of the application used to create the signature.
     *
     * @param signatureCreator A new name of the application signing a document.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated Use {@link PdfSigner#setSignatureCreator(String)} instead.
     */
    @Deprecated
    public PdfSignatureAppearance setSignatureCreator(String signatureCreator) {
        this.signatureCreator = signatureCreator;
        return this;
    }

    /**
     * Returns the signing contact.
     *
     * @return The signing contact.
     *
     * @deprecated Use {@link PdfSigner#getContact()} instead.
     */
    @Deprecated
    public String getContact() {
        return contact;
    }

    /**
     * Sets the signing contact.
     *
     * @param contact A new signing contact.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated Use {@link PdfSigner#setContact(String)} instead.
     */
    @Deprecated
    public PdfSignatureAppearance setContact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Sets the certificate used to provide the text in the appearance.
     * This certificate doesn't take part in the actual signing process.
     *
     * @param signCertificate the certificate.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     * Specified certificate provides signer name.
     */
    @Deprecated
    public PdfSignatureAppearance setCertificate(Certificate signCertificate) {
        this.signCertificate = signCertificate;
        return this;
    }

    /**
     * Get the signing certificate.
     *
     * @return the signing certificate.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public Certificate getCertificate() {
        return signCertificate;
    }

    /**
     * Gets the Image object to render.
     *
     * @return the image.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public ImageData getSignatureGraphic() {
        return signatureGraphic;
    }

    /**
     * Sets the Image object to render when Render is set to RenderingMode.GRAPHIC or RenderingMode.GRAPHIC_AND_DESCRIPTION.
     *
     * @param signatureGraphic image rendered. If null the mode is defaulted to RenderingMode.DESCRIPTION
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setContent(ImageData)} or
     * {@link SignatureFieldAppearance#setContent(String, ImageData)}.
     */
    @Deprecated
    public PdfSignatureAppearance setSignatureGraphic(ImageData signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
        return this;
    }

    /**
     * Indicates that the existing appearances needs to be reused as a background layer.
     *
     * @param reuseAppearance is an appearances reusing flag value to set.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link PdfSignatureFormField#setReuseAppearance(boolean)}.
     */
    @Deprecated
    public PdfSignatureAppearance setReuseAppearance(boolean reuseAppearance) {
        this.reuseAppearance = reuseAppearance;
        return this;
    }

    /**
     * Gets the background image for the layer 2.
     *
     * @return the background image for the layer 2.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setBackgroundImage(BackgroundImage)}.
     */
    @Deprecated
    public ImageData getImage() {
        return image;
    }

    /**
     * Sets the background image for the text in the layer 2.
     *
     * @param image the background image for the layer 2.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setBackgroundImage(BackgroundImage)}.
     */
    @Deprecated
    public PdfSignatureAppearance setImage(ImageData image) {
        this.image = image;
        return this;
    }

    /**
     * Gets the scaling to be applied to the background image.
     *
     * @return the scaling to be applied to the background image.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setBackgroundImage(BackgroundImage)}.
     */
    @Deprecated
    public float getImageScale() {
        return imageScale;
    }

    /**
     * Sets the scaling to be applied to the background image. If it's zero the image
     * will fully fill the rectangle. If it's less than zero the image will fill the rectangle but
     * will keep the proportions. If it's greater than zero that scaling will be applied.
     * In any of the cases the image will always be centered. It's zero by default.
     *
     * @param imageScale the scaling to be applied to the background image.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setBackgroundImage(BackgroundImage)}.
     */
    @Deprecated
    public PdfSignatureAppearance setImageScale(float imageScale) {
        this.imageScale = imageScale;
        return this;
    }

    /**
     * Sets the signature text identifying the signer.
     *
     * @param text the signature text identifying the signer. If null or not set
     * a standard description will be used.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfSignatureAppearance setLayer2Text(String text) {
        this.description = text;
        return this;
    }

    /**
     * Gets the signature text identifying the signer if set by setLayer2Text().
     *
     * @return the signature text identifying the signer.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public String getLayer2Text() {
        return description;
    }

    /**
     * Gets the n2 and n4 layer font.
     *
     * @return the n2 and n4 layer font.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public PdfFont getLayer2Font() {
        return this.font;
    }

    /**
     * Sets the n2 layer font. If the font size is zero, auto-fit will be used.
     *
     * @param font the n2 font.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setFont(PdfFont)}.
     */
    @Deprecated
    public PdfSignatureAppearance setLayer2Font(PdfFont font) {
        this.font = font;
        return this;
    }

    /**
     * Sets the n2 and n4 layer font size.
     *
     * @param fontSize font size.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setFontSize(float)}.
     */
    @Deprecated
    public PdfSignatureAppearance setLayer2FontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * Gets the n2 and n4 layer font size.
     *
     * @return the n2 and n4 layer font size.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public float getLayer2FontSize() {
        return fontSize;
    }

    /**
     * Sets the n2 and n4 layer font color.
     *
     * @param color font color.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setFontColor(Color)}.
     */
    @Deprecated
    public PdfSignatureAppearance setLayer2FontColor(Color color) {
        this.fontColor = color;
        return this;
    }

    /**
     * Gets the n2 layer font color.
     *
     * @return the n2 layer font color.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}.
     */
    @Deprecated
    public Color getLayer2FontColor() {
        return fontColor;
    }

    /**
     * Gets the signature layout element.
     *
     * @return the signature layout element.
     */
    public SignatureFieldAppearance getSignatureAppearance() {
        if (modelElement == null) {
            modelElement = new SignatureFieldAppearance(fieldName);
            setContent();
            setFontRelatedProperties();
            applyBackgroundImage();
        }
        return modelElement;
    }

    /**
     * Sets the signature layout element.
     *
     * @param modelElement the signature layout element.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated Use {@link PdfSigner#setSignatureAppearance(SignatureFieldAppearance)} instead.
     */
    @Deprecated
    public PdfSignatureAppearance setSignatureAppearance(SignatureFieldAppearance modelElement) {
        this.modelElement = modelElement;
        return this;
    }

    /**
     * Sets {@link FontProvider}. Note, font provider is inherited property.
     *
     * @param fontProvider the instance of {@link FontProvider}.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setProperty(int, Object)}.
     */
    @Deprecated
    public PdfSignatureAppearance setFontProvider(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        return this;
    }

    /**
     * Sets the preferable font families for the signature content.
     * Note that {@link com.itextpdf.layout.font.FontProvider} shall be set as well.
     *
     * @param fontFamilyNames defines an ordered list of preferable font families for the signature element.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignatureFieldAppearance#setFontFamily(String...)}.
     */
    @Deprecated
    public PdfSignatureAppearance setFontFamily(String... fontFamilyNames) {
        this.fontFamilyNames = fontFamilyNames;
        return this;
    }

    /**
     * Gets the visibility status of the signature.
     *
     * @return the visibility status of the signature.
     *
     * @deprecated won't be public in the next major release.
     */
    @Deprecated
    public boolean isInvisible() {
        return rect == null || rect.getWidth() == 0 || rect.getHeight() == 0;
    }

    /**
     * Constructs appearance (top-level) for a signature.
     *
     * @return a top-level signature appearance.
     *
     * @throws IOException if font cannot be created.
     *
     * @see <a href="https://www.adobe.com/content/dam/acom/en/devnet/acrobat/pdfs/PPKAppearances.pdf">Adobe Pdf Digital
     * Signature Appearances</a>
     *
     * @deprecated in favour of {@link SignatureFieldAppearance}. Shouldn't be used.
     */
    @Deprecated
    protected PdfFormXObject getAppearance() throws IOException {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        boolean fieldExist = signatureUtil.doesSignatureFieldExist(fieldName);
        PdfSignatureFormField sigField;
        if (fieldExist) {
            sigField = (PdfSignatureFormField) PdfFormCreator.getAcroForm(document, true).getField(fieldName);
        } else {
            sigField = new SignatureFormFieldBuilder(document, fieldName)
                    .setWidgetRectangle(rect).createSignature();
        }
        sigField.getFirstFormAnnotation().setFormFieldElement(getSignatureAppearance());
        return new PdfFormXObject(sigField.getFirstFormAnnotation().getPdfObject()
                .getAsDictionary(PdfName.AP).getAsStream(PdfName.N));
    }

    /**
     * Returns the signature date.
     *
     * @return the signature date.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    protected java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate A new signature date.
     *
     * @return this instance to support fluent interface.
     *
     * @deprecated in favour of {@link SignedAppearanceText} that should be used for {@link SignatureFieldAppearance}.
     */
    @Deprecated
    protected PdfSignatureAppearance setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
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
        this.fieldName = fieldName;
        return this;
    }

    /**
     * Returns reuseAppearance value which indicates that the existing appearances needs to be reused
     * as a background layer.
     *
     * @return an appearances reusing flag value.
     */
    boolean isReuseAppearance() {
        return reuseAppearance;
    }

    /**
     * Gets the background layer that is present when creating the signature field if it was set.
     *
     * @return n0 layer xObject.
     */
    PdfFormXObject getBackgroundLayer() {
        return n0;
    }

    /**
     * Gets the signature appearance layer that contains information about the signature if it was set.
     *
     * @return n2 layer xObject.
     */
    PdfFormXObject getSignatureAppearanceLayer() {
        return n2;
    }

    void applyBackgroundImage() {
        if (image != null) {
            BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeat.BackgroundRepeatValue.NO_REPEAT);
            BackgroundPosition position = new BackgroundPosition()
                    .setPositionX(BackgroundPosition.PositionX.CENTER)
                    .setPositionY(BackgroundPosition.PositionY.CENTER);
            BackgroundSize size = new BackgroundSize();
            final float EPS = 1e-5f;
            if (Math.abs(imageScale) < EPS) {
                size.setBackgroundSizeToValues(UnitValue.createPercentValue(100),
                        UnitValue.createPercentValue(100));
            } else {;
                if (imageScale < 0) {
                    size.setBackgroundSizeToContain();
                } else {
                    size.setBackgroundSizeToValues(
                            UnitValue.createPointValue(imageScale * image.getWidth()),
                            UnitValue.createPointValue(imageScale * image.getHeight()));
                }
            }
            modelElement.setBackgroundImage(new BackgroundImage.Builder()
                    .setImage(new PdfImageXObject(image))
                    .setBackgroundSize(size)
                    .setBackgroundRepeat(repeat)
                    .setBackgroundPosition(position)
                    .build());
        }
    }

    SignedAppearanceText generateSignatureText() {
        return new SignedAppearanceText()
                .setSignedBy(getSignerName())
                .setSignDate(signDate)
                .setReasonLine(reasonCaption + reason)
                .setLocationLine(locationCaption + location);
    }

    private void setFontRelatedProperties() {
        if (fontProvider != null) {
            modelElement.setProperty(Property.FONT_PROVIDER, fontProvider);
            modelElement.setFontFamily(fontFamilyNames);
        } else {
            modelElement.setFont(font);
        }
        modelElement.setFontSize(fontSize);
        modelElement.setFontColor(fontColor);
    }

    private void setContent() {
        if (isInvisible()) {
            return;
        }
        switch (renderingMode) {
            case GRAPHIC: {
                if (signatureGraphic == null) {
                    throw new IllegalStateException("A signature image must be present when rendering mode is " +
                            "graphic and description. Use setSignatureGraphic()");
                }
                modelElement.setContent(signatureGraphic);
                break;
            }
            case GRAPHIC_AND_DESCRIPTION: {
                if (signatureGraphic == null) {
                    throw new IllegalStateException("A signature image must be present when rendering mode is " +
                            "graphic and description. Use setSignatureGraphic()");
                }
                if (description != null) {
                    modelElement.setContent(description, signatureGraphic);
                } else {
                    modelElement.setContent(generateSignatureText(), signatureGraphic);
                }
                break;
            }
            case NAME_AND_DESCRIPTION: {
                if (description != null) {
                    modelElement.setContent(getSignerName(), description);
                } else {
                    modelElement.setContent(getSignerName(), generateSignatureText());
                }
                break;
            }
            default: {
                if (description != null) {
                    modelElement.setContent(description);
                } else {
                    modelElement.setContent(generateSignatureText());
                }
            }
        }
    }

    private String getSignerName() {
        String name = null;
        CertificateInfo.X500Name x500name = CertificateInfo.getSubjectFields((X509Certificate)signCertificate);
        if (x500name != null) {
            name = x500name.getField("CN");
            if (name == null) {
                name = x500name.getField("E");
            }
        }
        return name == null? "" : name;
    }

    /**
     * Signature rendering modes.
     */
    @Deprecated
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
