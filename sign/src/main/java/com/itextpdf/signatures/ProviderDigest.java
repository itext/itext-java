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
package com.itextpdf.signatures;

import com.itextpdf.kernel.crypto.DigestAlgorithms;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * This class will return the {@link java.security.MessageDigest} associated with
 * a certain hashing algorithm returned by the specified provider.
 */
public class ProviderDigest implements IExternalDigest {
    private String provider;

    /**
     * Creates a ProviderDigest.
     *
     * @param provider String name of the provider that you want to use to create the hash
     */
    public ProviderDigest(String provider) {
        this.provider = provider;
    }

    @Override
    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException{
        return DigestAlgorithms.getMessageDigest(hashAlgorithm, provider);
    }
}
