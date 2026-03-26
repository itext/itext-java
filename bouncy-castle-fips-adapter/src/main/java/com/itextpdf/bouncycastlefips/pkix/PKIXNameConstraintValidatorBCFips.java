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
package com.itextpdf.bouncycastlefips.pkix;

import com.itextpdf.bouncycastlefips.asn1.ASN1SequenceBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.GeneralNameBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.GeneralSubtreeBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.pkix.AbstractPKIXNameConstraintValidatorException;
import com.itextpdf.commons.bouncycastle.asn1.pkix.IPKIXConstraintValidator;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralSubtree;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.pkix.PKIXNameConstraintValidator;
import org.bouncycastle.pkix.PKIXNameConstraintValidatorException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Wrapper class for {@link PKIXNameConstraintValidator}.
 */
public class PKIXNameConstraintValidatorBCFips implements IPKIXConstraintValidator {
    private final PKIXNameConstraintValidator constraintValidator;

    /**
     * Creates new wrapper instance for {@link PKIXNameConstraintValidator}.
     *
     * @param constraintValidator {@link PKIXNameConstraintValidator} to be wrapped
     */
    public PKIXNameConstraintValidatorBCFips(PKIXNameConstraintValidator constraintValidator) {
        this.constraintValidator = constraintValidator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkPermittedDN(IASN1Sequence dns) throws AbstractPKIXNameConstraintValidatorException {
        try {
            constraintValidator.checkPermittedDN(((ASN1SequenceBCFips) dns).getASN1Sequence());
        } catch (PKIXNameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkExcludedDN(IASN1Sequence dns) throws AbstractPKIXNameConstraintValidatorException {
        try {
            constraintValidator.checkExcludedDN(((ASN1SequenceBCFips) dns).getASN1Sequence());
        } catch (PKIXNameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkPermitted(IGeneralName name) throws AbstractPKIXNameConstraintValidatorException {
        try {
            constraintValidator.checkPermitted(((GeneralNameBCFips) name).getGeneralName());
        } catch (PKIXNameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkExcluded(IGeneralName name) throws AbstractPKIXNameConstraintValidatorException {
        try {
            constraintValidator.checkExcluded(((GeneralNameBCFips) name).getGeneralName());
        } catch (PKIXNameConstraintValidatorException e) {
            throw new PKIXNameConstraintValidatorExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void intersectPermittedSubtree(IGeneralSubtree[] permitted) {
        List<GeneralSubtree> generalSubtreeList = Arrays.stream(permitted).map(
                subtree -> ((GeneralSubtreeBCFips) subtree).getGeneralSubtree()).collect(Collectors.toList());
        constraintValidator.intersectPermittedSubtree(generalSubtreeList.toArray(new GeneralSubtree[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addExcludedSubtree(IGeneralSubtree subtree) {
        constraintValidator.addExcludedSubtree(((GeneralSubtreeBCFips) subtree).getGeneralSubtree());
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PKIXNameConstraintValidatorBCFips that = (PKIXNameConstraintValidatorBCFips) o;
        return Objects.equals(constraintValidator, that.constraintValidator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(constraintValidator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return constraintValidator.toString();
    }
}