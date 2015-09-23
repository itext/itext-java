/*
 * $Id$
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2015 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.RASInputStream;
import com.itextpdf.basics.io.RandomAccessSource;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.Version;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigLockDictionary;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Takes care of the cryptographic options and appearances
 * that form a signature.
 */
public class PdfSignatureAppearance {

    /** Approval signature. */
    public static final int NOT_CERTIFIED = 0;

    /** Author signature, no changes allowed. */
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;

    /** Author signature, form filling allowed. */
    public static final int CERTIFIED_FORM_FILLING = 2;

    /** Author signature, form filling and annotations allowed. */
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;

    /** extra space at the top. */
    private static final float TOP_SECTION = 0.3f;

    /** margin for the content inside the signature rectangle. */
    private static final float MARGIN = 2;

    /** The certification level. */
    private int certificationLevel = NOT_CERTIFIED;

    /** The reason for signing. */
    private String reason = "";

    /** The caption for the reason for signing. */
    private String reasonCaption = "Reason: ";

    /** Holds value of property location. */
    private String location = "";

    /** The caption for the location of signing. */
    private String locationCaption = "Location: ";

    /** Holds value of the application that creates the signature. */
    private String signatureCreator = "";

    /** The contact name of the signer. */
    private String contact = "";

    /** Holds value of property signDate. */
    private Calendar signDate;

    /** The name of the field. */
    private String fieldName;

    /** The file right before the signature is added (can be null). */
    private RandomAccessFile raf;

    /** The bytes of the file right before the signature is added (if raf is null) */
    private byte[] bout;

    /** Array containing the byte positions of the bytes that need to be hashed. */
    private long[] range;

    private PdfDocument document;

    /** The crypto dictionary */
    private PdfSignature cryptoDictionary;

    /** The signing certificate */
    private Certificate signCertificate;

    /** Holds value of property signatureEvent. */
    private SignatureEvent signatureEvent;

    /** The page where the signature will appear. */
    private int page = 1;

    /**
     * The coordinates of the rectangle for a visible signature,
     * or a zero-width, zero-height rectangle for an invisible signature.
     */
    private Rectangle rect;

    /** rectangle that represent the position and dimension of the signature in the page. */
    private Rectangle pageRect;

    /** OutputStream for the bytes of the stamper. */
    private OutputStream originalOS;

    private ByteArrayOutputStream temporaryOS;

    private File tempFile;

    /** Name and content of keys that can only be added in the close() method. */
    private HashMap<PdfName, PdfLiteral> exclusionLocations;

    /** The rendering mode chosen for visible signatures */
    private RenderingMode renderingMode = RenderingMode.DESCRIPTION;

    /** The image that needs to be used for a visible signature */
    private Image signatureGraphic = null;

    /** Layers for a visible signature. */
    private PdfFormXObject n0;

    private PdfFormXObject n2;

    /** Indicates if we need to reuse the existing appearance as layer 0. */
    private boolean reuseAppearance = false;

    /** A background image for the text in layer 2. */
    private Image image;

    /** the scaling to be applied to the background image.t  */
    private float imageScale;

    /** The text that goes in Layer 2 of the signature appearance. */
    private String layer2Text;

    private float layer2FontSize = 12;

    /** Font for the text in Layer 2. */
    private PdfFont layer2Font;

    /** Template containing all layers drawn on top of each other. */
    private PdfFormXObject frm;

    /** Indicates if the stamper has already been pre-closed. */
    private boolean preClosed = false;

    /** Signature field lock dictionary */
    private PdfSigLockDictionary fieldLock;

    public PdfSignatureAppearance(PdfReader reader, PdfWriter writer, File tempFile) throws IOException {
        if (tempFile == null) {
            temporaryOS = new ByteArrayOutputStream();
            document = new PdfDocument(reader, new PdfWriter(temporaryOS), false);
        } else {
            if (tempFile.isDirectory()) {
                tempFile = File.createTempFile("pdf", null, tempFile);
            }

            OutputStream os = new FileOutputStream(tempFile);
            this.tempFile = tempFile;
            document = new PdfDocument(reader, new PdfWriter(os), false);
        }

        originalOS = writer == null ? null : writer.getOutputStream();
        signDate = new GregorianCalendar();
        fieldName = getNewSigFieldName();
        signatureCreator = Version.getInstance().getVersion();
    }

    public PdfSignatureAppearance(PdfReader reader, PdfWriter writer) throws IOException {
        this(reader, writer, null);
    }

    /**
     * Gets the rendering mode for this signature.
     * @return the rendering mode for this signature
     * @since 5.0.1
     */
    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    /**
     * Sets the rendering mode for this signature.
     * @param renderingMode the rendering mode
     * @since 5.0.1
     */
    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    /**
     * Gets the Image object to render.
     * @return the image
     */
    public Image getSignatureGraphic() {
        return signatureGraphic;
    }

    /**
     * Sets the Image object to render when Render is set to <CODE>RenderingMode.GRAPHIC</CODE>
     * or <CODE>RenderingMode.GRAPHIC_AND_DESCRIPTION</CODE>.
     * @param signatureGraphic image rendered. If <CODE>null</CODE> the mode is defaulted
     * to <CODE>RenderingMode.DESCRIPTION</CODE>
     */
    public void setSignatureGraphic(Image signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
    }

    public PdfFormXObject getLayer0() {
        if (n0 == null) {
            n0 = new PdfFormXObject(rect);
            n0.makeIndirect(document);
        }

        return n0;
    }

    public PdfFormXObject getLayer2() {
        if (n2 == null) {
            n2 = new PdfFormXObject(rect);
            n2.makeIndirect(document);
        }

        return n2;
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
     * @return the background image for the layer 2
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Sets the background image for the layer 2.
     * @param image the background image for the layer 2
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Gets the scaling to be applied to the background image.
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
     * @param imageScale the scaling to be applied to the background image
     */
    public void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }

    /**
     * Sets the signature text identifying the signer.
     * @param text the signature text identifying the signer. If <CODE>null</CODE> or not set
     * a standard description will be used
     */
    public void setLayer2Text(String text) {
        layer2Text = text;
    }

    /**
     * Gets the signature text identifying the signer if set by setLayer2Text().
     * @return the signature text identifying the signer
     */
    public String getLayer2Text() {
        return layer2Text;
    }

    /**
     * Gets the n2 and n4 layer font.
     * @return the n2 and n4 layer font
     */
    public PdfFont getLayer2Font() {
        return this.layer2Font;
    }

    /**
     * Sets the n2 and n4 layer font. If the font size is zero, auto-fit will be used.
     * @param layer2Font the n2 and n4 font
     */
    public void setLayer2Font(PdfFont layer2Font) {
        this.layer2Font = layer2Font;
    }

    public void setLayer2FontSize(float fontSize) {
        this.layer2FontSize = fontSize;
    }

    /**
     * Gets the template that aggregates all appearance layers. This corresponds to the /FRM resource.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     * @return the template that aggregates all appearance layers
     */
    public PdfFormXObject getTopLayer() {
        if (frm == null) {
            frm = new PdfFormXObject(rect);
            frm.makeIndirect(document);
        }

        return frm;
    }

    /**
     * Constructs appearance (top-level) for a signature.
     * <p>
     * Consult <A HREF="http://partners.adobe.com/asn/developer/pdfs/tn/PPKAppearances.pdf">PPKAppearances.pdf</A>
     * for further details.
     * @return a top-level signature appearance
     */
    public PdfFormXObject getAppearance() throws IOException {
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
                font = PdfFont.getDefaultFont(document);
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

            if(renderingMode != RenderingMode.GRAPHIC) {
                List<String> splittedText = font.splitString(text, (int) layer2FontSize, dataRect.getWidth());
                PdfCanvas canvas = new PdfCanvas(n2, document);

                canvas.setFontAndSize(font, layer2FontSize);
                canvas.beginText();
                canvas.setTextMatrix(dataRect.getLeft(), dataRect.getTop() - layer2FontSize);

                for (String str : splittedText) {
                    canvas.newlineText();
                    canvas.showText(str);
                }

                canvas.endText();
            }
        }

        int rotation = document.getPage(page).getRotation();
        Rectangle rotated = new Rectangle(rect);

        if (frm == null) {
            frm = new PdfFormXObject(rotated);
            frm.makeIndirect(document);

            float scale = Math.min(rect.getWidth(), rect.getHeight()) * 0.9f;
            float x = (rect.getWidth() - scale) / 2;
            float y = (rect.getHeight() - scale) / 2;
            scale /= 100;

            PdfCanvas canvas = new PdfCanvas(frm, document);

            if (rotation == 90) {
                canvas.concatMatrix(0, 1, -1, 0, rect.getHeight(), 0);
            } else if (rotation == 180) {
                canvas.concatMatrix(-1, 0, 0, -1, rect.getWidth(), rect.getHeight());
            } else if (rotation == 270) {
                canvas.concatMatrix(0, -1, 1, 0, 0, rect.getWidth());
            }

            if (reuseAppearance) {
                PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
                PdfFormField field = acroForm.getField(getFieldName());
                PdfStream stream = field.getWidgets().get(0).getAppearanceDictionary().getAsStream(PdfName.N);
                PdfFormXObject xobj = new PdfFormXObject(stream);

                if (stream != null) {
                    frm.getResources().addForm(xobj, new PdfName("n0"));
                    PdfCanvas canvas1 = new PdfCanvas(frm, document);
                    canvas1.addXObject(xobj, 1, 0, 0, 1, 0, 0);
                } else {
                    reuseAppearance = false;

                    if (n0 == null) {
                        createBlankN0();
                    }
                }
            }

            if (!reuseAppearance) {
                frm.getResources().addForm(n0, new PdfName("n0"));
                PdfCanvas canvas1 = new PdfCanvas(frm, document);
                canvas1.addXObject(n0, 1, 0, 0, 1, 0, 0);
            }

            frm.getResources().addForm(n2, new PdfName("n2"));
            PdfCanvas canvas1 = new PdfCanvas(frm, document);
            canvas1.addXObject(n2, 1, 0, 0, 1, 0, 0);
        }

        PdfFormXObject napp = new PdfFormXObject(rotated);
        napp.makeIndirect(document);
        napp.getResources().addForm(frm, new PdfName("FRM"));

        PdfCanvas canvas = new PdfCanvas(napp, document);
        canvas.addXObject(frm, 0, 0);

        return napp;
    }

    /**
     * Constructs a new {@link PdfSignatureAppearance} object.
     * @param document The document which is going to be signed.
     */
    public PdfSignatureAppearance(PdfDocument document) {
        this.document = document;
        signDate = new GregorianCalendar();
        fieldName = getNewSigFieldName();
        signatureCreator = Version.getInstance().getVersion();
    }

    /**
     * Returns the document's certification level.
     * For possible values see {@link #setCertificationLevel(int)}.
     * @return The certified status.
     */
    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    /**
     * Sets the document's certification level.
     * @param certificationLevel a new certification level for a document.
     *                           Possible values are: <ul>
     *                              <li>{@link #NOT_CERTIFIED}</li>
     *                              <li>{@link #CERTIFIED_NO_CHANGES_ALLOWED}</li>
     *                              <li>{@link #CERTIFIED_FORM_FILLING}</li>
     *                              <li>{@link #CERTIFIED_FORM_FILLING_AND_ANNOTATIONS}</li>
     *                           </ul>
     */
    public void setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    /**
     * Returns the signing reason.
     * @return The signing reason.
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the signing reason.
     * @param reason A new signing reason.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Sets the caption for the signing reason.
     * @param reasonCaption A new signing reason caption.
     */
    public void setReasonCaption(String reasonCaption) {
        this.reasonCaption = reasonCaption;
    }

    /**
     * Returns the signing location.
     * @return The signing location.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the signing location.
     * @param location A new signing location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the caption for the signing location.
     * @param locationCaption A new signing location caption.
     */
    public void setLocationCaption(String locationCaption) {
        this.locationCaption = locationCaption;
    }

    /**
     * Returns the signature creator.
     * @return The signature creator.
     */
    public String getSignatureCreator(){
        return signatureCreator;
    }

    /**
     * Sets the name of the application used to create the signature.
     * @param signatureCreator A new name of the application signing a document.
     */
    public void setSignatureCreator(String signatureCreator){
        this.signatureCreator = signatureCreator;
    }

    /**
     * Returns the signing contact.
     * @return The signing contact.
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the signing contact.
     * @param contact A new signing contact.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Returns the signature date.
     * @return the signature date
     */
    public java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     * @param signDate A new signature date.
     */
    public void setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
    }

    /**
     * Gets the field name.
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the document bytes that are hashable when using external signatures.
     * The general sequence is:
     * {@link #preClose(HashMap)}, {@link #getRangeStream()} and {@link #close(PdfDictionary)}.
     * @return The {@link InputStream} of bytes to be signed.
     */
    public InputStream getRangeStream() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return new RASInputStream(fac.createRanged(getUnderlyingSource(), range));
    }

    /**
     * Returns the user made signature dictionary. This is the dictionary at the /V key
     * of the signature field.
     * @return The user made signature dictionary.
     */
    public PdfSignature getSignatureDictionary() {
        return cryptoDictionary;
    }

    /**
     * Sets a user made signature dictionary. This is the dictionary at the /V key
     * of the signature field.
     * @param cryptoDictionary A new user made signature dictionary.
     */
    public void setCryptoDictionary(PdfSignature cryptoDictionary) {
        this.cryptoDictionary = cryptoDictionary;
    }

    /**
     * Sets the certificate used to provide the text in the appearance.
     * This certificate doesn't take part in the actual signing process.
     * @param signCertificate the certificate
     */
    public void setCertificate(Certificate signCertificate) {
        this.signCertificate = signCertificate;
    }

    public Certificate getCertificate() {
        return signCertificate;
    }

    /**
     * Getter for property signatureEvent.
     * @return Value of property signatureEvent.
     */
    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }

    /**
     * Sets the signature event to allow modification of the signature dictionary.
     * @param signatureEvent the signature event
     */
    public void setSignatureEvent(SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }

    /**
     * Gets a new signature field name that doesn't clash with any existing name.
     * @return A new signature field name.
     */
    public String getNewSigFieldName() {
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        String name = "Signature";
        int step = 1;

        while (acroForm.getField(name + step) != null) {
            ++step;
        }

        return name + step;
    }

    /**
     * Gets the page number of the field.
     * @return the page number of the field
     */
    public int getPageNumber() {
        return page;
    }

    /**
     * Gets the rectangle representing the signature dimensions.
     * @return the rectangle representing the signature dimensions. It may be <CODE>null</CODE>
     * or have zero width or height for invisible signatures
     */
    public Rectangle getRect() {
        return rect;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the rectangle that represent the position and dimension of the signature in the page.
     * @return the rectangle that represent the position and dimension of the signature in the page
     */
    public Rectangle getPageRect() {
        return pageRect;
    }

    /**
     * Gets the visibility status of the signature.
     * @return the visibility status of the signature
     */
    public boolean isInvisible() {
        return rect == null || rect.getWidth() == 0 || rect.getHeight() == 0;
    }

    /**
     * Sets the signature to be visible. It creates a new visible signature field.
     * @param pageRect the position and dimension of the field in the page
     * @param page the page to place the field. The fist page is 1
     * @param fieldName the field name or <CODE>null</CODE> to generate automatically a new field name
     */
    public void setVisibleSignature(Rectangle pageRect, int page, String fieldName) {
        if (fieldName != null) {
            if (fieldName.indexOf('.') >= 0) {
                throw new IllegalArgumentException("field.names.cannot.contain.a.dot"); // TODO: correct the message
            }

            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);

            if (acroForm.getField(fieldName) != null) {
                throw new IllegalArgumentException("the.field.1.already.exists");
            }

            this.fieldName = fieldName;
        }

        if (page < 1 || page > document.getNumOfPages()) {
            throw new IllegalArgumentException("invalid.page.number.1");
        }

        this.pageRect = new Rectangle(pageRect);
        rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
        this.page = page;
    }

    /**
     * Gets the <CODE>PdfStamper</CODE> associated with this instance.
     * @return the <CODE>PdfStamper</CODE> associated with this instance
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Sets the PdfStamper
     */
    void setDocument(PdfDocument document) {
        this.document = document;
    }

    /**
     * Setter for the OutputStream.
     */
    public void setOriginalOutputStream(OutputStream originalOS) {
        this.originalOS = originalOS;
    }

    /**
     * Getter for the field lock dictionary.
     * @return Field lock dictionary.
     */
    public PdfSigLockDictionary getFieldLockDict() {
        return fieldLock;
    }

    /**
     * Setter for the field lock dictionary.
     * <p><strong>Be aware:</strong> if a signature is created on an existing signature field,
     * then its /Lock dictionary takes the precedence (if it exists).</p>
     *
     * @param fieldLock Field lock dictionary.
     */
    public void setFieldLockDict(PdfSigLockDictionary fieldLock) {
        this.fieldLock = fieldLock;
    }

    public void addDeveloperExtension(PdfDeveloperExtension extension) {
        document.getCatalog().addDeveloperExtension(extension);
    }

    /**
     * Checks if the document is in the process of closing.
     * @return <CODE>true</CODE> if the document is in the process of closing,
     * <CODE>false</CODE> otherwise
     */
    public boolean isPreClosed() {
        return preClosed;
    }

    /**
     * This is the first method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * If calling preClose() <B>dont't</B> call PdfStamper.close().
     * <p>
     * <CODE>exclusionSizes</CODE> must contain at least
     * the <CODE>PdfName.CONTENTS</CODE> key with the size that it will take in the
     * document. Note that due to the hex string coding this size should be
     * byte_size*2+2.
     * @param exclusionSizes a <CODE>HashMap</CODE> with names and sizes to be excluded in the signature
     * calculation. The key is a <CODE>PdfName</CODE> and the value an
     * <CODE>Integer</CODE>. At least the <CODE>PdfName.CONTENTS</CODE> must be present
     * @throws IOException on error
     */
    public void preClose(HashMap<PdfName, Integer> exclusionSizes) throws IOException {
        if (preClosed) {
            throw new RuntimeException("document.already.pre.closed");
        }

        preClosed = true;
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        String name = getFieldName();
        boolean fieldExist = acroForm.getField(fieldName) != null;
        acroForm.setSignatureFlags(PdfAcroForm.SIGNATURE_EXIST | PdfAcroForm.APPEND_ONLY);
        PdfSigLockDictionary fieldLock = null;

        cryptoDictionary.getPdfObject().makeIndirect(document);

        if (fieldExist) {
            PdfSignatureFormField sigField = (PdfSignatureFormField) acroForm.getField(fieldName);
            sigField.put(PdfName.V, cryptoDictionary);

            if (sigField.getSigFieldLockDictionary() == null && this.fieldLock != null) {
                this.fieldLock.getPdfObject().makeIndirect(document);
                sigField.put(PdfName.Lock, fieldLock);
                fieldLock = this.fieldLock;
            }

            sigField.put(PdfName.P, document.getPage(getPageNumber()));
            sigField.put(PdfName.V, cryptoDictionary);
            PdfObject obj = sigField.getPdfObject().get(PdfName.F);
            int flags = 0;

            if (obj != null && obj.isNumber()) {
                flags = ((PdfNumber) obj).getIntValue();
            }

            flags |= PdfAnnotation.Locked;
            sigField.put(PdfName.F, new PdfNumber(flags));
            PdfDictionary ap = new PdfDictionary();
            ap.put(PdfName.N, getAppearance().getPdfObject());
            sigField.put(PdfName.AP, ap);
        } else {
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(document, !isInvisible() ? getPageRect() : new Rectangle(0, 0));
            widget.setFlags(PdfAnnotation.Print | PdfAnnotation.Locked);

            PdfSignatureFormField sigField = PdfFormField.createSignature(document);
            sigField.setFieldName(name);
            sigField.put(PdfName.V, cryptoDictionary);
            sigField.addKid(widget);

            if (this.fieldLock != null) {
                this.fieldLock.getPdfObject().makeIndirect(document);
                sigField.put(PdfName.Lock, fieldLock);
                fieldLock = this.fieldLock;
            }

            int pagen = getPageNumber();
            widget.setPage(document.getPage(pagen));
            PdfDictionary ap = widget.getAppearanceDictionary();

            if (ap == null) {
                ap = new PdfDictionary();
                widget.put(PdfName.AP, ap);
            }

            ap.put(PdfName.N, getAppearance().getPdfObject());
            acroForm.addField(sigField, document.getPage(pagen));
        }

        exclusionLocations = new HashMap<PdfName, PdfLiteral>();
        if (cryptoDictionary == null) {
            throw new /*DocumentException*/RuntimeException("No crypto dictionary defined.");
        }
        else {
            PdfLiteral lit = new PdfLiteral(80);
            exclusionLocations.put(PdfName.ByteRange, lit);
            cryptoDictionary.put(PdfName.ByteRange, lit);
            for (Map.Entry<PdfName, Integer> entry: exclusionSizes.entrySet()) {
                PdfName key = entry.getKey();
                Integer v = entry.getValue();
                lit = new PdfLiteral(v.intValue());
                exclusionLocations.put(key, lit);
                cryptoDictionary.put(key, lit);
            }
            if (certificationLevel > 0)
                addDocMDP(cryptoDictionary);
            if (fieldLock != null)
                addFieldMDP(cryptoDictionary, fieldLock);
            if (signatureEvent != null)
                signatureEvent.getSignatureDictionary(cryptoDictionary);
        }
        if (certificationLevel > 0) {
            // add DocMDP entry to root
            PdfDictionary docmdp = new PdfDictionary();
            docmdp.put(new PdfName("DocMDP"), cryptoDictionary.getPdfObject());
            document.getCatalog().put(new PdfName("Perms"), docmdp);
        }

        document.close();

        range = new long[exclusionLocations.size() * 2];
        long byteRangePosition = exclusionLocations.get(PdfName.ByteRange).getPosition();
        exclusionLocations.remove(PdfName.ByteRange);
        int idx = 1;
        for (PdfLiteral lit: exclusionLocations.values()) {
            long n = lit.getPosition();
            range[idx++] = n;
            range[idx++] = lit.getBytesCount() + n;
        }
        Arrays.sort(range, 1, range.length - 1);
        for (int k = 3; k < range.length - 2; k += 2)
            range[k] -= range[k - 1];

        if (tempFile == null) {
            bout = temporaryOS.toByteArray();
            range[range.length - 1] = bout.length - range[range.length - 2];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bos);
            os.write('[');
            for (int k = 0; k < range.length; ++k)
                os.writeLong(range[k]).write(' ');
            os.write(']');
            System.arraycopy(bos.toByteArray(), 0, bout, (int) byteRangePosition, bos.size());
        } else {
            try {
                raf = new RandomAccessFile(tempFile, "rw");
                long len = raf.length();
                range[range.length - 1] = len - range[range.length - 2];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfOutputStream os = new PdfOutputStream(bos);
                os.write('[');
                for (int k = 0; k < range.length; ++k)
                    os.writeLong(range[k]).write(' ');
                os.write(']');
                raf.seek(byteRangePosition);
                raf.write(bos.toByteArray(), 0, bos.size());
            }
            catch (IOException e) {
                try{raf.close();}catch(Exception ee){}
                try{tempFile.delete();}catch(Exception ee){}
                throw e;
            }
        }
    }

    /**
     * This is the last method to be called when using external signatures. The general sequence is:
     * preClose(), getDocumentBytes() and close().
     * <p>
     * <CODE>update</CODE> is a <CODE>PdfDictionary</CODE> that must have exactly the
     * same keys as the ones provided in {@link #preClose(HashMap)}.
     * @param update a <CODE>PdfDictionary</CODE> with the key/value that will fill the holes defined
     * in {@link #preClose(HashMap)}
     * @throws IOException on error
     */
    public void close(PdfDictionary update) throws IOException {
        try {
            if (!preClosed)
                throw new RuntimeException("Document must be preclosed"); // TODO: correct the message
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            PdfOutputStream os = new PdfOutputStream(bous);

            for (PdfName key: update.keySet()) {
                PdfObject obj = update.get(key);
                PdfLiteral lit = exclusionLocations.get(key);
                if (lit == null)
                    throw new IllegalArgumentException("the.key.1.didn.t.reserve.space.in.preclose");
                bous.reset();
                os.write(obj);
                if (bous.size() > lit.getBytesCount())
                    throw new IllegalArgumentException("the.key.1.is.too.big.is.2.reserved.3");
                if (tempFile == null) {
                    System.arraycopy(bous.toByteArray(), 0, bout, (int) lit.getPosition(), bous.size());
                } else {
                    raf.seek(lit.getPosition());
                    raf.write(bous.toByteArray(), 0, bous.size());
                }
            }
            if (update.size() != exclusionLocations.size())
                throw new IllegalArgumentException("the.update.dictionary.has.less.keys.than.required");
            if (tempFile == null) {
                originalOS.write(bout, 0, bout.length);
            } else {
                if (originalOS != null) {
                    raf.seek(0);
                    long length = raf.length();
                    byte buf[] = new byte[8192];
                    while (length > 0) {
                        int r = raf.read(buf, 0, (int)Math.min((long)buf.length, length));
                        if (r < 0)
                            throw new EOFException("unexpected.eof");
                        originalOS.write(buf, 0, r);
                        length -= r;
                    }
                }
            }
        }
        finally {
            // TODO: should I close reader?
            if (tempFile != null) {
                raf.close();

                if (originalOS != null) {
                    tempFile.delete();
                }
            }

            if (originalOS != null) {
                try {
                    originalOS.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void createBlankN0() {
        n0 = new PdfFormXObject(new Rectangle(100, 100));
        n0.makeIndirect(document);

        PdfCanvas canvas = new PdfCanvas(n0, document);
        canvas.writeLiteral("% DSBlank\n");
    }

    /**
     * Returns the underlying source.
     * @return The underlying source.
     * @throws IOException
     */
    private RandomAccessSource getUnderlyingSource() throws IOException {
        RandomAccessSourceFactory fac = new RandomAccessSourceFactory();
        return raf == null ? fac.createSource(bout) : fac.createSource(raf);
    }

    /**
     * Adds keys to the signature dictionary that define
     * the certification level and the permissions.
     * This method is only used for Certifying signatures.
     * @param crypto the signature dictionary
     */
    private void addDocMDP(PdfSignature crypto) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.put(PdfName.P, new PdfNumber(certificationLevel));
        transformParams.put(PdfName.V, new PdfName("1.2"));
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        reference.put(PdfName.TransformMethod, PdfName.DocMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);
        if (document.getPdfVersion().compareTo(PdfVersion.PDF_1_6) < 0) { // TODO: refactor
            reference.put(new PdfName("DigestValue"), new PdfString("aa"));
            PdfArray loc = new PdfArray();
            loc.add(new PdfNumber(0));
            loc.add(new PdfNumber(0));
            reference.put(new PdfName("DigestLocation"), loc);
            reference.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        }
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.Reference, types);
    }

    /**
     * Adds keys to the signature dictionary that define
     * the field permissions.
     * This method is only used for signatures that lock fields.
     * @param crypto the signature dictionary
     */
    private void addFieldMDP(PdfSignature crypto, PdfSigLockDictionary fieldLock) {
        PdfDictionary reference = new PdfDictionary();
        PdfDictionary transformParams = new PdfDictionary();
        transformParams.putAll(fieldLock.getPdfObject());
        transformParams.put(PdfName.Type, PdfName.TransformParams);
        transformParams.put(PdfName.V, new PdfName("1.2"));
        reference.put(PdfName.TransformMethod, PdfName.FieldMDP);
        reference.put(PdfName.Type, PdfName.SigRef);
        reference.put(PdfName.TransformParams, transformParams);
        reference.put(new PdfName("DigestValue"), new PdfString("aa"));
        PdfArray loc = new PdfArray();
        loc.add(new PdfNumber(0));
        loc.add(new PdfNumber(0));
        reference.put(new PdfName("DigestLocation"), loc);
        reference.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        reference.put(PdfName.Data, document.getTrailer().get(PdfName.Root));
        PdfArray types = crypto.getPdfObject().getAsArray(PdfName.Reference);
        if (types == null)
            types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.Reference, types);
    }

    /**
     * An interface to retrieve the signature dictionary for modification.
     */
    public interface SignatureEvent {

        /**
         * Allows modification of the signature dictionary.
         * @param sig The signature dictionary.
         */
        void getSignatureDictionary(PdfSignature sig);
    }

    /**
     * Signature rendering modes
     * @since 5.0.1
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