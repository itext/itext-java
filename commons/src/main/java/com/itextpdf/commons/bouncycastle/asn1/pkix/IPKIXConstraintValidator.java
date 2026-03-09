/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.commons.bouncycastle.asn1.pkix;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralSubtree;

/**
 * This interface represents the wrapper for PKIXConstraintValidator that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IPKIXConstraintValidator {
    /**
     * Calls actual {@code checkPermittedDN} method for the wrapped PKIXConstraintValidator object.
     *
     * @param dns {@link IASN1Sequence} direct name sequence wrapper to be checked
     *
     * @throws AbstractPKIXNameConstraintValidatorException which wraps PKIXNameConstraintValidatorException
     */
    void checkPermittedDN(IASN1Sequence dns) throws AbstractPKIXNameConstraintValidatorException;

    /**
     * Calls actual {@code checkExcludedDN} method for the wrapped PKIXConstraintValidator object.
     *
     * @param dns {@link IASN1Sequence} direct name sequence wrapper to be checked
     *
     * @throws AbstractPKIXNameConstraintValidatorException which wraps PKIXNameConstraintValidatorException
     */
    void checkExcludedDN(IASN1Sequence dns) throws AbstractPKIXNameConstraintValidatorException;

    /**
     * Calls actual {@code checkPermitted} method for the wrapped PKIXConstraintValidator object.
     *
     * @param name {@link IGeneralName} general name wrapper to be checked
     *
     * @throws AbstractPKIXNameConstraintValidatorException which wraps PKIXNameConstraintValidatorException
     */
    void checkPermitted(IGeneralName name) throws AbstractPKIXNameConstraintValidatorException;

    /**
     * Calls actual {@code checkExcluded} method for the wrapped PKIXConstraintValidator object.
     *
     * @param name {@link IGeneralName} general name wrapper to be checked
     *
     * @throws AbstractPKIXNameConstraintValidatorException which wraps PKIXNameConstraintValidatorException
     */
    void checkExcluded(IGeneralName name) throws AbstractPKIXNameConstraintValidatorException;

    /**
     * Calls actual {@code intersectPermittedSubtree} method for the wrapped PKIXConstraintValidator object.
     *
     * @param permitted sequence of GeneralSubtree wrappers
     */
    void intersectPermittedSubtree(IGeneralSubtree[] permitted);


    /**
     * Calls actual {@code addExcludedSubtree} method for the wrapped PKIXConstraintValidator object.
     *
     * @param subtree {@link IGeneralSubtree} wrapper
     */
    void addExcludedSubtree(IGeneralSubtree subtree);
}
