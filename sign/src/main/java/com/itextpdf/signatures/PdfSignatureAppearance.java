package com.itextpdf.signatures;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.image.Image;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
    private Image signatureGraphic = null;

    /**
     * A background image for the text in layer 2.
     */
    private Image image;

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
    private float layer2FontSize = 12;

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
     * appearance is associated with.
     *
     * @param pageNumber The page number of the signature field which
     *                   this signature appearance is associated with.
     */
    public void setPageNumber(int pageNumber) {
        this.page = pageNumber;
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
    public void setPageRect(Rectangle pageRect) {
        this.pageRect = new Rectangle(pageRect);
        this.rect = new Rectangle(pageRect.getWidth(), pageRect.getHeight());
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
    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
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
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Sets the caption for the signing reason.
     *
     * @param reasonCaption A new signing reason caption
     */
    public void setReasonCaption(String reasonCaption) {
        this.reasonCaption = reasonCaption;
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
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the caption for the signing location.
     *
     * @param locationCaption A new signing location caption
     */
    public void setLocationCaption(String locationCaption) {
        this.locationCaption = locationCaption;
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
    public void setSignatureCreator(String signatureCreator){
        this.signatureCreator = signatureCreator;
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
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Sets the certificate used to provide the text in the appearance.
     * This certificate doesn't take part in the actual signing process.
     *
     * @param signCertificate the certificate
     */
    public void setCertificate(Certificate signCertificate) {
        this.signCertificate = signCertificate;
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
    public Image getSignatureGraphic() {
        return signatureGraphic;
    }

    /**
     * Sets the Image object to render when Render is set to RenderingMode.GRAPHIC or RenderingMode.GRAPHIC_AND_DESCRIPTION.
     *
     * @param signatureGraphic image rendered. If null the mode is defaulted to RenderingMode.DESCRIPTION
     */
    public void setSignatureGraphic(Image signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
    }

    /**
     * Indicates that the existing appearances needs to be reused as layer 0.
     */
    public void setReuseAppearance(boolean reuseAppearance) {
        this.reuseAppearance = reuseAppearance;
    }

    // layer 2

    /**
     * Gets the background image for the layer 2.
     *
     * @return the background image for the layer 2
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Sets the background image for the layer 2.
     *
     * @param image the background image for the layer 2
     */
    public void setImage(Image image) {
        this.image = image;
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
    public void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }

    /**
     * Sets the signature text identifying the signer.
     *
     * @param text the signature text identifying the signer. If null or not set
     * a standard description will be used
     */
    public void setLayer2Text(String text) {
        layer2Text = text;
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
    public void setLayer2Font(PdfFont layer2Font) {
        this.layer2Font = layer2Font;
    }

    /**
     * Sets the n2 and n4 layer font size.
     *
     * @param fontSize font size
     */
    public void setLayer2FontSize(float fontSize) {
        this.layer2FontSize = fontSize;
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
     */
    protected PdfFormXObject getAppearance() throws IOException {
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
                SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                buf.append("Date: ").append(sd.format(signDate.getTime()));
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
                    PdfCanvas canvas = new PdfCanvas(n2, document);
                    canvas.addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), 0, 0);
                } else {
                    float usableScale = imageScale;

                    if (imageScale < 0) {
                        usableScale = Math.min(rect.getWidth() / image.getWidth(), rect.getHeight() / image.getHeight());
                    }

                    float w = image.getWidth() * usableScale;
                    float h = image.getHeight() * usableScale;
                    float x = (rect.getWidth() - w) / 2;
                    float y = (rect.getHeight() - h) / 2;

                    PdfCanvas canvas = new PdfCanvas(n2, document);
                    canvas.addImage(image, w, 0, 0, h, x, y);
                }
            }

            PdfFont font;

            if (layer2Font == null) {
                font = PdfFont.getDefaultFont();
            } else {
                font = layer2Font;
            }

            float size = layer2FontSize;

            Rectangle dataRect = null;
            Rectangle signatureRect = null;

            if (renderingMode == RenderingMode.NAME_AND_DESCRIPTION ||
                renderingMode == RenderingMode.GRAPHIC_AND_DESCRIPTION && this.signatureGraphic != null) {
                if (rect.getHeight() > rect.getWidth()) {
                    signatureRect = new Rectangle(
                            MARGIN,
                            rect.getHeight() / 2,
                            rect.getWidth() - 2 * MARGIN,
                            rect.getHeight() / 2);
                    dataRect = new Rectangle(
                            MARGIN,
                            MARGIN,
                            rect.getWidth() - 2 * MARGIN,
                            rect.getHeight() / 2 - 2 * MARGIN);
                } else {
                    // origin is the bottom-left
                    signatureRect = new Rectangle(
                            MARGIN,
                            MARGIN,
                            rect.getWidth() / 2 - 2 * MARGIN,
                            rect.getHeight() - 2 * MARGIN);
                    dataRect = new Rectangle(
                            rect.getWidth() / 2 + MARGIN / 2,
                            MARGIN,
                            rect.getWidth() / 2 - MARGIN,
                            rect.getHeight() - 2 * MARGIN);
                }
            } else if (renderingMode == RenderingMode.GRAPHIC) {
                if (signatureGraphic == null) {
                    throw new IllegalStateException(/*MessageLocalization.getComposedMessage("a.signature.image.should.be.present.when.rendering.mode.is.graphic.only")*/);
                }

                signatureRect = new Rectangle(
                        MARGIN,
                        MARGIN,
                        rect.getWidth() - 2 * MARGIN, // take all space available
                        rect.getHeight() - 2 * MARGIN);
            } else {
                dataRect = new Rectangle(
                        MARGIN,
                        MARGIN,
                        rect.getWidth() - 2 * MARGIN,
                        rect.getHeight() * (1 - TOP_SECTION) - 2 * MARGIN);
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

                    List<String> splittedText = font.splitString(signedBy, (int) layer2FontSize, signatureRect.getWidth());
                    PdfCanvas canvas = new PdfCanvas(n2, document);

                    canvas.setFontAndSize(font, layer2FontSize);
                    canvas.beginText();
                    canvas.setTextMatrix(signatureRect.getLeft(), signatureRect.getTop() - layer2FontSize);

                    for (String str : splittedText) {
                        canvas.newlineText();
                        canvas.showText(str);
                    }

                    canvas.endText();
                    break;
                case GRAPHIC_AND_DESCRIPTION: {
                    if (signatureGraphic == null) {
                        throw new IllegalStateException(/*MessageLocalization.getComposedMessage("a.signature.image.should.be.present.when.rendering.mode.is.graphic.and.description")*/);
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
                List<String> splittedText = font.splitString(text, (int) layer2FontSize, dataRect.getWidth());
                PdfCanvas canvas = new PdfCanvas(n2, document);

                float x = dataRect.getLeft();
                float y = dataRect.getTop();

                canvas.setFontAndSize(font, layer2FontSize);
                canvas.beginText();
                canvas.setTextMatrix(x, y);

                for (String str : splittedText) {
                    canvas.moveText(0, -layer2FontSize);
                    canvas.showText(str);
                }

                canvas.endText();
            }
        }

        int rotation = document.getPage(page).getRotation();
        Rectangle rotated = new Rectangle(rect);

        if (topLayer == null) {
            topLayer = new PdfFormXObject(rotated);
            topLayer.makeIndirect(document);

            float scale = Math.min(rect.getWidth(), rect.getHeight()) * 0.9f;
            float x = (rect.getWidth() - scale) / 2;
            float y = (rect.getHeight() - scale) / 2;
            scale /= 100;

            PdfCanvas canvas = new PdfCanvas(topLayer, document);

            if (rotation == 90) {
                canvas.concatMatrix(0, 1, -1, 0, rect.getHeight(), 0);
            } else if (rotation == 180) {
                canvas.concatMatrix(-1, 0, 0, -1, rect.getWidth(), rect.getHeight());
            } else if (rotation == 270) {
                canvas.concatMatrix(0, -1, 1, 0, 0, rect.getWidth());
            }

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

        PdfCanvas canvas = new PdfCanvas(napp, document);
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
    protected void setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
    }

    /**
     * Set the field name of the appearance.
     *
     * @param fieldName name of the field
     */
    protected void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    private void createBlankN0() {
        n0 = new PdfFormXObject(new Rectangle(100, 100));
        n0.makeIndirect(document);

        PdfCanvas canvas = new PdfCanvas(n0, document);
        canvas.writeLiteral("% DSBlank\n");
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