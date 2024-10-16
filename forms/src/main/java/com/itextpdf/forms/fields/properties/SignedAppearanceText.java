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
package com.itextpdf.forms.fields.properties;

import com.itextpdf.commons.utils.DateTimeUtil;

import java.util.Calendar;

/**
 * Class representing the signature text identifying the signer.
 */
public class SignedAppearanceText {
    /**
     * The reason for signing.
     */
    private String reason = "";

    /**
     * Holds value of property location.
     */
    private String location = "";

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
     * Creates a new {@link SignedAppearanceText} instance.
     */
    public SignedAppearanceText() {
        // Empty constructor.
    }

    /**
     * Returns the signing reason.
     *
     * @return reason for signing.
     */
    public String getReasonLine() {
        return reason;
    }

    /**
     * Sets the signing reason.
     *
     * <p>
     * Note, that this reason won't be passed to the signature dictionary. If none is set, value set by
     * {@code PdfSigner#setReason} will be used.
     *
     * @param reason signing reason.
     *
     * @return this same {@link SignedAppearanceText} instance.
     */
    public SignedAppearanceText setReasonLine(String reason) {
        if (reason != null) {
            reason = reason.trim();
        }
        this.reason = reason;
        return this;
    }

    /**
     * Returns the signing location.
     *
     * @return signing location.
     */
    public String getLocationLine() {
        return location;
    }

    /**
     * Sets the signing location.
     *
     * <p>
     * Note, that this location won't be passed to the signature dictionary. If none is set, value set by
     * {@code PdfSigner#setLocation} will be used.
     *
     * @param location new signing location
     *
     * @return this same {@link SignedAppearanceText} instance
     */
    public SignedAppearanceText setLocationLine(String location) {
        if (location != null) {
            location = location.trim();
        }
        this.location = location;
        return this;
    }

    /**
     * Sets the name of the signer from the certificate.
     *
     * <p>
     * Note, that the signer name will be replaced by the one from the signing certificate during the actual signing.
     *
     * @param signedBy name of the signer
     *
     * @return this same {@link SignedAppearanceText} instance
     */
    public SignedAppearanceText setSignedBy(String signedBy) {
        if (signedBy != null) {
            signedBy = signedBy.trim();
        }
        this.signedBy = signedBy;
        return this;
    }

    /**
     * Gets the name of the signer from the certificate.
     *
     * @return signedBy name of the signer
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
     * <p>
     * Note, that the signing date will be replaced by the one from the {@code PdfSigner} during the signing.
     *
     * @param signDate new signature date
     *
     * @return this same {@link SignedAppearanceText} instance
     */
    public SignedAppearanceText setSignDate(java.util.Calendar signDate) {
        this.signDate = signDate;
        this.isSignDateSet = true;
        return this;
    }

    /**
     * Generates the signature description text based on the provided parameters.
     *
     * @return signature description
     */
    public String generateDescriptionText() {
        final StringBuilder buf = new StringBuilder();
        if (signedBy != null && !signedBy.isEmpty()) {
            buf.append("Digitally signed by ").append(signedBy);
        }
        if (isSignDateSet) {
            buf.append('\n').append("Date: ").append(DateTimeUtil.dateToString(signDate));
        }
        if (reason != null && !reason.isEmpty()) {
            buf.append('\n').append(reason);
        }
        if (location != null && !location.isEmpty()) {
            buf.append('\n').append(location);
        }
        return buf.toString();
    }
}
