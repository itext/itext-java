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
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.geom.Rectangle;

import java.util.Calendar;

/**
 * Properties to be used in signing operations.
 */
public class SignerProperties {
    public static final String IGNORED_ID = "";

    private PdfSigFieldLock fieldLock;
    private SignatureFieldAppearance appearance;
    private Calendar signDate = DateTimeUtil.getCurrentTimeCalendar();
    private AccessPermissions certificationLevel = AccessPermissions.UNSPECIFIED;
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
     * @return calendar set to the signature date
     */
    public java.util.Calendar getClaimedSignDate() {
        return signDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signDate the signature date
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setClaimedSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        return this;
    }

    /**
     * Sets the signature field layout element to customize the appearance of the signature.
     * ID specified for {@link SignatureFieldAppearance} will be ignored and won't override field name, so
     * {@link #IGNORED_ID} could be used. To specify signature name use {@link SignerProperties#setFieldName}.
     *
     * <p>
     * Note that if {@link SignedAppearanceText} was set as the content (or part of the content)
     * for {@link SignatureFieldAppearance} object, {@link PdfSigner} properties such as signing date, reason, location
     * and signer name could be set automatically.
     *
     * <p>
     * In case you create new signature field (either using {@link SignerProperties#setFieldName} with the name
     * that doesn't exist in the document or do not specifying it at all) then the signature is invisible by default.
     * Use {@link SignerProperties#setPageRect(Rectangle)} and {@link SignerProperties#setPageNumber(int)} to provide
     * the rectangle that represent the position and dimension of the signature field in the specified page.
     *
     * <p>
     * It is possible to set other appearance related properties such as
     * {@link PdfSignatureFormField#setReuseAppearance}, {@link PdfSignatureFormField#setBackgroundLayer} (n0 layer) and
     * {@link PdfSignatureFormField#setSignatureAppearanceLayer} (n2 layer) for the signature field using
     * {@link PdfSigner#getSignatureField()}. Page, rectangle and other properties could be also set up via
     * {@link SignerProperties}.
     *
     * @param appearance the {@link SignatureFieldAppearance} layout element representing signature appearance
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setSignatureAppearance(SignatureFieldAppearance appearance) {
        this.appearance = appearance;
        return this;
    }

    /**
     * Gets signature field appearance object representing the appearance of the signature.
     *
     * <p>
     * To customize the signature appearance, create new {@link SignatureFieldAppearance} object and set it
     * using {@link SignerProperties#setSignatureAppearance(SignatureFieldAppearance)}.
     *
     * @return {@link SignatureFieldAppearance} object representing signature appearance
     */
    public SignatureFieldAppearance getSignatureAppearance() {
        return this.appearance;
    }

    /**
     * Returns the document's certification level.
     * For possible values see {@link AccessPermissions}.
     *
     * @return {@link AccessPermissions} enum which specifies which certification level shall be used
     */
    public AccessPermissions getCertificationLevel() {
        return this.certificationLevel;
    }

    /**
     * Sets the document's certification level.
     *
     * @param accessPermissions {@link AccessPermissions} enum which specifies which certification level shall be used
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setCertificationLevel(AccessPermissions accessPermissions) {
        this.certificationLevel = accessPermissions;
        return this;
    }

    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the name indicating the field to be signed. The field can already be presented in the
     * document but shall not be signed. If the field is not presented in the document, it will be created.
     *
     * <p>
     * Note that ID specified for {@link SignatureFieldAppearance} set by {@link #setSignatureAppearance} will be
     * ignored and won't override the field name.
     *
     * @param fieldName the name indicating the field to be signed
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setFieldName(String fieldName) {
        if (fieldName != null) {
            this.fieldName = fieldName;
        }
        return this;
    }

    /**
     * Provides the page number of the signature field which this signature appearance is associated with.
     *
     * @return the page number of the signature field which this signature appearance is associated with
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    /**
     * Sets the page number of the signature field which this signature appearance is associated with.
     *
     * @param pageNumber the page number of the signature field which this signature appearance is associated with
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    /**
     * Provides the rectangle that represent the position and dimension of the signature field in the page.
     *
     * @return the rectangle that represent the position and dimension of the signature field in the page
     */
    public Rectangle getPageRect() {
        return this.pageRect;
    }

    /**
     * Sets the rectangle that represent the position and dimension of the signature field in the page.
     *
     * @param pageRect the rectangle that represents the position and dimension of the signature field in the page
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setPageRect(Rectangle pageRect) {
        this.pageRect = pageRect;
        return this;
    }

    /**
     * Getter for the field lock dictionary.
     *
     * @return field lock dictionary
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
     * @param fieldLock field lock dictionary
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setFieldLockDict(PdfSigFieldLock fieldLock) {
        this.fieldLock = fieldLock;
        return this;
    }

    /**
     * Returns the signature creator.
     *
     * @return the signature creator
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
     * @return the signing contact
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the signing contact.
     *
     * @param contact a new signing contact
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setContact(String contact) {
        this.contact = contact;
        return this;
    }

    /**
     * Returns the signing reason.
     *
     * @return the signing reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the signing reason.
     *
     * @param reason a new signing reason
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return the signing location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the signing location.
     *
     * @param location a new signing location
     *
     * @return this instance to support fluent interface
     */
    public SignerProperties setLocation(String location) {
        this.location = location;
        return this;
    }
}
