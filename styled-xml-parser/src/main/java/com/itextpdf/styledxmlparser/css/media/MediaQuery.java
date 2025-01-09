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
package com.itextpdf.styledxmlparser.css.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that bundles all the media query properties.
 */
public class MediaQuery {

    /**
     * The logical "only" value.
     */
    private boolean only;

    /**
     * The logical "not" value.
     */
    private boolean not;

    /**
     * The type.
     */
    private String type;

    /**
     * The expressions.
     */
    private List<MediaExpression> expressions;

    /**
     * Creates a new {@link MediaQuery} instance.
     *
     * @param type        the type
     * @param expressions the expressions
     * @param only        logical "only" value
     * @param not         logical "not" value
     */
    MediaQuery(String type, List<MediaExpression> expressions, boolean only, boolean not) {
        this.type = type;
        this.expressions = new ArrayList<>(expressions);
        this.only = only;
        this.not = not;
    }

    /**
     * Tries to match a device description with the media query.
     *
     * @param deviceDescription the device description
     * @return true, if successful
     */
    public boolean matches(MediaDeviceDescription deviceDescription) {
        boolean typeMatches = type == null || MediaType.ALL.equals(type) || Objects.equals(type, deviceDescription.getType());

        boolean matchesExpressions = true;
        for (MediaExpression expression : expressions) {
            if (!expression.matches(deviceDescription)) {
                matchesExpressions = false;
                break;
            }
        }

        boolean expressionResult = typeMatches && matchesExpressions;
        if (not) {
            expressionResult = !expressionResult;
        }

        return expressionResult;
    }

}
