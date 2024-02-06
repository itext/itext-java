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
package com.itextpdf.signatures;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.geom.Rectangle;

import java.util.Calendar;

/**
 * Properties to be used in signing operations.
 */
public class SignerProperties {

    private PdfSigFieldLock fieldLock;
    private SignatureFieldAppearance appearance;
    private Calendar signDate = DateTimeUtil.getCurrentTimeCalendar();
    private int certificationLevel = PdfSigner.NOT_CERTIFIED;
    private String fieldName;
    private int pageNumber = 1;
    private Rectangle pageRect = new Rectangle(0, 0);
    private String signatureCreator = "";
    private String contact = "";
    private String reason = "";
    private String location = "";

    /**
     * Create instance of {@link SignerProperties}.
     */
    public SignerProperties() {
        // Empty constructor.
    }

    /**
     * Gets the signature date.
     *
     * @return Calendar set to the signature date.
     */
    public java.util.Calendar getSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate the signature date.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        return this;
    }

    /**
     * Sets the signature field layout element to customize the appearance of the signature. Signer's sign date will
     * be set.
     *
     * @param appearance the {@link SignatureFieldAppearance} layout element.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setSignatureAppearance(SignatureFieldAppearance appearance) {
        this.appearance = appearance;
        return this;
    }

    /**
     * Gets signature field layout element, which customizes the appearance of a signature.
     *
     * @return {@link SignatureFieldAppearance} layout element.
     */
    public SignatureFieldAppearance getSignatureAppearance() {
        return this.appearance;
    }

    /**
     * Returns the document's certification level.
     * For possible values see {@link #setCertificationLevel(int)}.
     *
     * @return The certified status.
     */
    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    /**
     * Sets the document's certification level.
     *
     * @param certificationLevel a new certification level for a document.
     *                           Possible values are: <ul>
     *                           <li>{@link PdfSigner#NOT_CERTIFIED}
     *                           <li>{@link PdfSigner#CERTIFIED_NO_CHANGES_ALLOWED}
     *                           <li>{@link PdfSigner#CERTIFIED_FORM_FILLING}
     *                           <li>{@link PdfSigner#CERTIFIED_FORM_FILLING_AND_ANNOTATIONS}
     *                           </ul>
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setCertificationLevel(int certificationLevel) {
        this.certificationLevel = certificationLevel;
        return this;
    }

    /**
     * Gets the field name.
     *
     * @return the field name.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the name indicating the field to be signed. The field can already be presented in the
     * document but shall not be signed. If the field is not presented in the document, it will be created.
     *
     * @param fieldName The name indicating the field to be signed.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    /**
     * Provides the page number of the signature field which this signature
     * appearance is associated with.
     *
     * @return The page number of the signature field which this signature
     * appearance is associated with.
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    /**
     * Sets the page number of the signature field which this signature
     * appearance is associated with. Implicitly calls {@link PdfSigner#setPageRect}
     * which considers page number to process the rectangle correctly.
     *
     * @param pageNumber The page number of the signature field which
     *                   this signature appearance is associated with.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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
        return this.pageRect;
    }

    /**
     * Sets the rectangle that represent the position and dimension of
     * the signature field in the page.
     *
     * @param pageRect The rectangle that represents the position and
     *                 dimension of the signature field in the page.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setPageRect(Rectangle pageRect) {
        this.pageRect = pageRect;
        return this;
    }

    /**
     * Getter for the field lock dictionary.
     *
     * @return Field lock dictionary.
     */
    public PdfSigFieldLock getFieldLockDict() {
        return fieldLock;
    }

    /**
     * Setter for the field lock dictionary.
     * <p>
     * <strong>Be aware:</strong> if a signature is created on an existing signature field,
     * then its /Lock dictionary takes the precedence (if it exists).
     *
     * @param fieldLock Field lock dictionary.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setFieldLockDict(PdfSigFieldLock fieldLock) {
        this.fieldLock = fieldLock;
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return The signature creator.
     */
    public String getSignatureCreator() {
        return this.signatureCreator;
    }

    /**
     * Sets the name of the application used to create the signature.
     *
     * @param signatureCreator A new name of the application signing a document.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setSignatureCreator(String signatureCreator) {
        this.signatureCreator = signatureCreator;
        return this;
    }

    /**
     * Returns the signing contact.
     *
     * @return The signing contact.
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the signing contact.
     *
     * @param contact A new signing contact.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setContact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return The signing reason.
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the signing reason.
     *
     * @param reason A new signing reason.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return The signing location.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the signing location.
     *
     * @param location A new signing location.
     *
     * @return this instance to support fluent interface.
     */
    public SignerProperties setLocation(String location) {
        this.location = location;
        return this;
    }
}
