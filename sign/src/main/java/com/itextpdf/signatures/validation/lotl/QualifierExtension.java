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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing Qualifications entry from a country specific Trusted List.
 */
public class QualifierExtension {
    private final List<String> qualifiers = new ArrayList<>();
    private CriteriaList criteriaList;

    QualifierExtension() {
    }

    /**
     * Gets list of qualifiers from this extension.
     *
     * @return list of qualifiers
     */
    public List<String> getQualifiers() {
        return Collections.unmodifiableList(qualifiers);
    }

    /**
     * Checks criteria for this Qualifier extension.
     *
     * @param certificate {@link X509Certificate} for which criteria shall be meet
     *
     * @return {@code true} if criteria were meet, {@code false} otherwise
     */
    public boolean checkCriteria(X509Certificate certificate) {
        return criteriaList.checkCriteria(certificate);
    }

    void setCriteriaList(CriteriaList criteriaList) {
        this.criteriaList = criteriaList;
    }

    CriteriaList getCriteriaList() {
        return criteriaList;
    }

    void addQualifier(String qualifier) {
        this.qualifiers.add(qualifier);
    }
}
