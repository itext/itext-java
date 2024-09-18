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
package com.itextpdf.commons.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

/**
 * This interface represents the wrapper for ASN1EncodableVector that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1EncodableVector {
    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param primitive ASN1Primitive wrapper.
     */
    void add(IASN1Primitive primitive);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param attribute Attribute wrapper.
     */
    void add(IAttribute attribute);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param element AlgorithmIdentifier wrapper.
     */
    void add(IAlgorithmIdentifier element);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object if the primitive is not null.
     *
     * @param primitive ASN1Primitive wrapper.
     */
    void addOptional(IASN1Primitive primitive);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object if the attribute is not null.
     *
     * @param attribute Attribute wrapper.
     */
    void addOptional(IAttribute attribute);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object if the element is not null.
     *
     * @param element AlgorithmIdentifier wrapper.
     */
    void addOptional(IAlgorithmIdentifier element);

    /**
     * Calls actual {@code size} method for the wrapped ASN1EncodableVector object.
     *
     * @return {@code int} representing current vector size
     */
    int size();
}
