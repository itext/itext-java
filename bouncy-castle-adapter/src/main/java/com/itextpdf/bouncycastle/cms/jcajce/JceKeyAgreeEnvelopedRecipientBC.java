/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.RecipientBC;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJceKeyAgreeEnvelopedRecipient;

import org.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipient;

/**
 * Wrapper class for {@link JceKeyAgreeEnvelopedRecipient}.
 */
public class JceKeyAgreeEnvelopedRecipientBC extends RecipientBC implements IJceKeyAgreeEnvelopedRecipient {
    /**
     * Creates new wrapper instance for {@link JceKeyAgreeEnvelopedRecipient}.
     *
     * @param jceKeyAgreeEnvelopedRecipient {@link JceKeyAgreeEnvelopedRecipient} to be wrapped
     */
    public JceKeyAgreeEnvelopedRecipientBC(JceKeyAgreeEnvelopedRecipient jceKeyAgreeEnvelopedRecipient) {
        super(jceKeyAgreeEnvelopedRecipient);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JceKeyAgreeEnvelopedRecipient}.
     */
    public JceKeyAgreeEnvelopedRecipient getJceKeyAgreeEnvelopedRecipient() {
        return (JceKeyAgreeEnvelopedRecipient) getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceKeyAgreeEnvelopedRecipient setProvider(String provider) {
        getJceKeyAgreeEnvelopedRecipient().setProvider(provider);
        return this;
    }
}
