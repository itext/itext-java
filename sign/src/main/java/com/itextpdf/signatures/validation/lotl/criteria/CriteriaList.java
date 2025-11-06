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
package com.itextpdf.signatures.validation.lotl.criteria;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria List which holds other Criteria or other Criteria Lists.
 */
public class CriteriaList implements Criteria {
    private final List<Criteria> criterias = new ArrayList<>();
    private final String assertValue;

    /**
     * Creates a new instance of a Criteria List with a provided assert value.
     *
     * @param assertValue assert value. Possible value are "all", "atLeastOne" and "none".
     */
    public CriteriaList(String assertValue) {
        this.assertValue = assertValue;
    }

    /**
     * Gets assert value for this Criteria List.
     *
     * @return assert value
     */
    public String getAssertValue() {
        return assertValue;
    }

    /**
     * Adds {@link Criteria} to this Criteria List.
     *
     * @param criteria {@link Criteria} to be added
     */
    public void addCriteria(Criteria criteria) {
        criterias.add(criteria);
    }

    /**
     * Gets Criteria List.
     *
     * @return Criteria List
     */
    public List<Criteria> getCriteriaList() {
        return new ArrayList<>(criterias);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkCriteria(X509Certificate certificate) {
        switch (assertValue) {
            case "all":
                for (Criteria criteria : criterias) {
                    if (!criteria.checkCriteria(certificate)) {
                        return false;
                    }
                }
                return true;
            case "atLeastOne":
                for (Criteria criteria : criterias) {
                    if (criteria.checkCriteria(certificate)) {
                        return true;
                    }
                }
                return false;
            case "none":
                for (Criteria criteria : criterias) {
                    if (criteria.checkCriteria(certificate)) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
