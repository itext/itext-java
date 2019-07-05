/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.signatures;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;

/**
 * Provides convenient methods to make a signature appearance. Use it in conjunction with {@link PdfSigner}.
 */
public class PdfSignatureAppearance {

    /**
     * Extra space at the top.
     */
    private static final float TOP_SECTION = 0.3f;

    /**
     * Margin for the content inside the signature rectangle.
     */
    private static final float MARGIN = 2;

    /**
     * The document to be signed.
     */
    private PdfDocument document;

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
     * Zero level of the signature appearance.
     */
    private PdfFormXObject n0;

    /**
     * Second level of the signature appearance.
     */
    private PdfFormXObject n2;

    /**
     * Form containing all layers drawn on top of each other.
     */
    private PdfFormXObject topLayer;

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
    private String layer2Text;

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
     * Indicates the field to be signed if it is already presented in the document
     * (signing existing field). Required for {@link #reuseAppearance} option.
     */
    private String fieldName;

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
     */
    public PdfSignatureAppearance setPageRect(Rectangle pageRect) {
        this.pageRect = new Rectangle(pageRect);
        this.rect = new Rectangle(pageRect.getWidth(), pageRect.getHeight());
        return this;
    }

    /**
     * Get Layer 0 of the appearance.
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
     */
    public PdfSignatureAppearance setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return reason for signing
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the signing reason.
     *
     * @param reason signing reason.
     */
    public PdfSignatureAppearance setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Sets the caption for the signing reason.
     *
     * @param reasonCaption A new signing reason caption
     */
    public PdfSignatureAppearance setReasonCaption(String reasonCaption) {
        this.reasonCaption = reasonCaption;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return signing location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the signing location.
     *
     * @param location A new signing location
     */
    public PdfSignatureAppearance setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the caption for the signing location.
     *
     * @param locationCaption A new signing location caption
     */
    public PdfSignatureAppearance setLocationCaption(String locationCaption) {
        this.locationCaption = locationCaption;
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return The signature creator
     */
    public String getSignatureCreator(){
        return signatureCreator;
    }

    /**
     * Sets the name of the application used to create the signature.
     *
     * @param signatureCreator A new name of the application signing a document
     */
    public PdfSignatureAppearance setSignatureCreator(String signatureCreator){
        this.signatureCreator = signatureCreator;
        return this;
    }

    /**
     * Returns the signing contact.
     *
     * @return The signing contact
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the signing contact.
     *
     * @param contact A new signing contact
     */
    public PdfSignatureAppearance setContact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Sets the certificate used to provide the text in the appearance.
     * This certificate doesn't take part in the actual signing process.
     *
     * @param signCertificate the certificate
     */
    public PdfSignatureAppearance setCertificate(Certificate signCertificate) {
        this.signCertificate = signCertificate;
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
        return signatureGraphic;
    }

    /**
     * Sets the Image object to render when Render is set to RenderingMode.GRAPHIC or RenderingMode.GRAPHIC_AND_DESCRIPTION.
     *
     * @param signatureGraphic image rendered. If null the mode is defaulted to RenderingMode.DESCRIPTION
     */
    public PdfSignatureAppearance setSignatureGraphic(ImageData signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
        return this;
    }

    /**
     * Indicates that the existing appearances needs to be reused as layer 0.
     *
     * @param reuseAppearance
     */
    public PdfSignatureAppearance setReuseAppearance(boolean reuseAppearance) {
        this.reuseAppearance = reuseAppearance;
        return this;
    }

    // layer 2

    /**
     * Gets the background image for the layer 2.
     *
     * @return the background image for the layer 2
     */
    public ImageData getImage() {
        return this.image;
    }

    /**
     * Sets the background image for the layer 2.
     *
     * @param image the background image for the layer 2
     */
    public PdfSignatureAppearance setImage(ImageData image) {
        this.image = image;
        return this;
    }

    /**
     * Gets the scaling to be applied to the background image.
     *
     * @return the scaling to be applied to the background image
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
     * @param imageScale the scaling to be applied to the background image
     */
    public PdfSignatureAppearance setImageScale(float imageScale) {
        this.imageScale = imageScale;
        return this;
    }

    /**
     * Sets the signature text identifying the signer.
     *
     * @param text the signature text identifying the signer. If null or not set
     * a standard description will be used
     */
    public PdfSignatureAppearance setLayer2Text(String text) {
        layer2Text = text;
        return this;
    }

    /**
     * Gets the signature text identifying the signer if set by setLayer2Text().
     *
     * @return the signature text identifying the signer
     */
    public String getLayer2Text() {
        return layer2Text;
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
     */
    public PdfSignatureAppearance setLayer2Font(PdfFont layer2Font) {
        this.layer2Font = layer2Font;
        return this;
    }

    /**
     * Sets the n2 and n4 layer font size.
     *
     * @param fontSize font size
     */
    public PdfSignatureAppearance setLayer2FontSize(float fontSize) {
        this.layer2FontSize = fontSize;
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
     */
    public PdfSignatureAppearance setLayer2FontColor(Color color) {
        this.layer2FontColor = color;
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
     * Gets the visibility status of the signature.
     *
     * @return the visibility status of the signature
     */
    public boolean isInvisible() {
        return rect == null || rect.getWidth() == 0 || rect.getHeight() == 0;
    }

    /**
     * Constructs appearance (top-level) for a signature.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     *
     * @return a top-level signature appearance
     * @throws IOException
     */
    protected PdfFormXObject getAppearance() throws IOException {
        PdfCanvas canvas;
        if (isInvisible()) {
            PdfFormXObject appearance = new PdfFormXObject(new Rectangle(0, 0));
            appearance.makeIndirect(document);
            return appearance;
        }

        if (n0 == null && !reuseAppearance) {
            createBlankN0();
        }

        if (n2 == null) {
            n2 = new PdfFormXObject(rect);
            n2.makeIndirect(document);

            canvas = new PdfCanvas(n2, document);
            int rotation = document.getPage(page).getRotation();

            if (rotation == 90) {
                canvas.concatMatrix(0, 1, -1, 0, rect.getWidth(), 0);
            } else if (rotation == 180) {
                canvas.concatMatrix(-1, 0, 0, -1, rect.getWidth(), rect.getHeight());
            } else if (rotation == 270) {
                canvas.concatMatrix(0, -1, 1, 0, 0, rect.getHeight());
            }

            Rectangle rotatedRect = rotateRectangle(this.rect, document.getPage(page).getRotation());

            String text;

            if (layer2Text == null) {
                StringBuilder buf = new StringBuilder();
                buf.append("Digitally signed by ");
                String name = null;
                CertificateInfo.X500Name x500name = CertificateInfo.getSubjectFields((X509Certificate)signCertificate);
                if (x500name != null) {
                    name = x500name.getField("CN");
                    if (name == null)
                        name = x500name.getField("E");
                }
                if (name == null)
                    name = "";
                buf.append(name).append('\n');
                buf.append("Date: ").append(SignUtils.dateToString(signDate));
                if (reason != null)
                    buf.append('\n').append(reasonCaption).append(reason);
                if (location != null)
                    buf.append('\n').append(locationCaption).append(location);
                text = buf.toString();
            } else {
                text = layer2Text;
            }

            if (image != null) {
                if (imageScale == 0) {
                    canvas = new PdfCanvas(n2, document);
                    canvas.addImage(image, rotatedRect.getWidth(), 0, 0, rotatedRect.getHeight(), 0, 0);
                } else {
                    float usableScale = imageScale;

                    if (imageScale < 0) {
                        usableScale = Math.min(rotatedRect.getWidth() / image.getWidth(), rotatedRect.getHeight() / image.getHeight());
                    }

                    float w = image.getWidth() * usableScale;
                    float h = image.getHeight() * usableScale;
                    float x = (rotatedRect.getWidth() - w) / 2;
                    float y = (rotatedRect.getHeight() - h) / 2;

                    canvas = new PdfCanvas(n2, document);
                    canvas.addImage(image, w, 0, 0, h, x, y);
                }
            }

            PdfFont font;

            if (layer2Font == null) {
                font = PdfFontFactory.createFont();
            } else {
                font = layer2Font;
            }

            Rectangle dataRect = null;
            Rectangle signatureRect = null;

            if (renderingMode == RenderingMode.NAME_AND_DESCRIPTION ||
                renderingMode == RenderingMode.GRAPHIC_AND_DESCRIPTION && this.signatureGraphic != null) {
                if (rotatedRect.getHeight() > rotatedRect.getWidth()) {
                    signatureRect = new Rectangle(
                            MARGIN,
                            rotatedRect.getHeight() / 2,
                            rotatedRect.getWidth() - 2 * MARGIN,
                            rotatedRect.getHeight() / 2);
                    dataRect = new Rectangle(
                            MARGIN,
                            MARGIN,
                            rotatedRect.getWidth() - 2 * MARGIN,
                            rotatedRect.getHeight() / 2 - 2 * MARGIN);
                } else {
                    // origin is the bottom-left
                    signatureRect = new Rectangle(
                            MARGIN,
                            MARGIN,
                            rotatedRect.getWidth() / 2 - 2 * MARGIN,
                            rotatedRect.getHeight() - 2 * MARGIN);
                    dataRect = new Rectangle(
                            rotatedRect.getWidth() / 2 + MARGIN / 2,
                            MARGIN,
                            rotatedRect.getWidth() / 2 - MARGIN,
                            rotatedRect.getHeight() - 2 * MARGIN);
                }
            } else if (renderingMode == RenderingMode.GRAPHIC) {
                if (signatureGraphic == null) {
                    throw new IllegalStateException("A signature image must be present when rendering mode is graphic. Use setSignatureGraphic()");
                }

                signatureRect = new Rectangle(
                        MARGIN,
                        MARGIN,
                        rotatedRect.getWidth() - 2 * MARGIN, // take all space available
                        rotatedRect.getHeight() - 2 * MARGIN);
            } else {
                dataRect = new Rectangle(
                        MARGIN,
                        MARGIN,
                        rotatedRect.getWidth() - 2 * MARGIN,
                        rotatedRect.getHeight() * (1 - TOP_SECTION) - 2 * MARGIN);
            }

            switch (renderingMode) {
                case NAME_AND_DESCRIPTION:
                    String signedBy = CertificateInfo.getSubjectFields((X509Certificate) signCertificate).getField("CN");

                    if (signedBy == null) {
                        signedBy = CertificateInfo.getSubjectFields((X509Certificate) signCertificate).getField("E");
                    }

                    if (signedBy == null) {
                        signedBy = "";
                    }

                    addTextToCanvas(signedBy, font, signatureRect);
                    break;
                case GRAPHIC_AND_DESCRIPTION: {
                    if (signatureGraphic == null) {
                        throw new IllegalStateException("A signature image must be present when rendering mode is graphic and description. Use setSignatureGraphic()");
                    }

                    float imgWidth = signatureGraphic.getWidth();

                    if (imgWidth == 0) {
                        imgWidth = signatureRect.getWidth();
                    }

                    float imgHeight = signatureGraphic.getHeight();

                    if (imgHeight == 0) {
                        imgHeight = signatureRect.getHeight();
                    }

                    float multiplierH = signatureRect.getWidth() / signatureGraphic.getWidth();
                    float multiplierW = signatureRect.getHeight() / signatureGraphic.getHeight();
                    float multiplier = Math.min(multiplierH, multiplierW);
                    imgWidth *= multiplier;
                    imgHeight *= multiplier;

                    float x = signatureRect.getRight() - imgWidth;
                    float y = signatureRect.getBottom() + (signatureRect.getHeight() - imgHeight) / 2;

                    canvas = new PdfCanvas(n2, document);
                    canvas.addImage(signatureGraphic, imgWidth, 0, 0, imgHeight, x, y);
                    break;
                }
                case GRAPHIC:
                    float imgWidth = signatureGraphic.getWidth();

                    if (imgWidth == 0) {
                        imgWidth = signatureRect.getWidth();
                    }

                    float imgHeight = signatureGraphic.getHeight();

                    if (imgHeight == 0) {
                        imgHeight = signatureRect.getHeight();
                    }

                    float multiplierH = signatureRect.getWidth() / signatureGraphic.getWidth();
                    float multiplierW = signatureRect.getHeight() / signatureGraphic.getHeight();
                    float multiplier = Math.min(multiplierH, multiplierW);
                    imgWidth *= multiplier;
                    imgHeight *= multiplier;

                    float x = signatureRect.getLeft() + (signatureRect.getWidth() - imgWidth) / 2;
                    float y = signatureRect.getBottom() + (signatureRect.getHeight() - imgHeight) / 2;

                    canvas = new PdfCanvas(n2, document);
                    canvas.addImage(signatureGraphic, imgWidth, 0, 0, imgHeight, x, y);
                    break;
            }

            if (renderingMode != RenderingMode.GRAPHIC) {
                addTextToCanvas(text, font, dataRect);
            }
        }

        Rectangle rotated = new Rectangle(rect);

        if (topLayer == null) {
            topLayer = new PdfFormXObject(rotated);
            topLayer.makeIndirect(document);

            if (reuseAppearance) {
                PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
                PdfFormField field = acroForm.getField(fieldName);
                PdfStream stream = field.getWidgets().get(0).getAppearanceDictionary().getAsStream(PdfName.N);
                PdfFormXObject xobj = new PdfFormXObject(stream);

                if (stream != null) {
                    topLayer.getResources().addForm(xobj, new PdfName("n0"));
                    PdfCanvas canvas1 = new PdfCanvas(topLayer, document);
                    canvas1.addXObject(xobj, 1, 0, 0, 1, 0, 0);
                } else {
                    reuseAppearance = false;

                    if (n0 == null) {
                        createBlankN0();
                    }
                }
            }

            if (!reuseAppearance) {
                topLayer.getResources().addForm(n0, new PdfName("n0"));
                PdfCanvas canvas1 = new PdfCanvas(topLayer, document);
                canvas1.addXObject(n0, 1, 0, 0, 1, 0, 0);
            }

            topLayer.getResources().addForm(n2, new PdfName("n2"));
            PdfCanvas canvas1 = new PdfCanvas(topLayer, document);
            canvas1.addXObject(n2, 1, 0, 0, 1, 0, 0);
        }

        PdfFormXObject napp = new PdfFormXObject(rotated);
        napp.makeIndirect(document);
        napp.getResources().addForm(topLayer, new PdfName("FRM"));

        canvas = new PdfCanvas(napp, document);
        canvas.addXObject(topLayer, 0, 0);

        return napp;
    }

    /**
     * Returns the signature date.
     *
     * @return the signature date
     */
    protected java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate A new signature date
     */
    protected PdfSignatureAppearance setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        return this;
    }

    /**
     * Set the field name of the appearance.
     *
     * @param fieldName name of the field
     */
    protected PdfSignatureAppearance setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    private static Rectangle rotateRectangle(Rectangle rect, int angle) {
        if (0 == (angle / 90) % 2) {
            return new Rectangle(rect.getWidth(), rect.getHeight());
        } else {
            return new Rectangle(rect.getHeight(), rect.getWidth());
        }
    }

    private void createBlankN0() {
        n0 = new PdfFormXObject(new Rectangle(100, 100));
        n0.makeIndirect(document);

        PdfCanvas canvas = new PdfCanvas(n0, document);
        canvas.writeLiteral("% DSBlank\n");
    }

    private void addTextToCanvas(String text, PdfFont font, Rectangle dataRect) {
        PdfCanvas canvas;
        canvas = new PdfCanvas(n2, document);
        Paragraph paragraph = new Paragraph(text).setFont(font).setMargin(0).setMultipliedLeading(0.9f);
        Canvas layoutCanvas = new Canvas(canvas, document, dataRect);
        paragraph.setFontColor(layer2FontColor);
        if (layer2FontSize == 0) {
            applyCopyFittingFontSize(paragraph, dataRect, layoutCanvas.getRenderer());
        } else {
            paragraph.setFontSize(layer2FontSize);
        }
        layoutCanvas.add(paragraph);
    }

    private void applyCopyFittingFontSize(Paragraph paragraph, Rectangle rect, IRenderer parentRenderer) {
        IRenderer renderer = paragraph.createRendererSubTree().setParent(parentRenderer);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(1, rect));
        float lFontSize = 0.1f, rFontSize = 100;
        int numberOfIterations = 15; // 15 iterations with lFontSize = 0.1 and rFontSize = 100 should result in ~0.003 precision
        for (int i = 0; i < numberOfIterations; i++) {
            float mFontSize = (lFontSize + rFontSize) / 2;
            paragraph.setFontSize(mFontSize);
            LayoutResult result = renderer.layout(layoutContext);
            if (result.getStatus() == LayoutResult.FULL) {
                lFontSize = mFontSize;
            } else {
                rFontSize = mFontSize;
            }
        }
        paragraph.setFontSize(lFontSize);
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
