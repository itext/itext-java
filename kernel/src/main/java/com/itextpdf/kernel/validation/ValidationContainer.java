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
package com.itextpdf.kernel.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a container for one or more {@link IValidationChecker} implementations.
 * <p>
 *
 * It is used in the {@link com.itextpdf.kernel.pdf.PdfDocument} to check for additional conformance requirements.
 */
public class ValidationContainer {

    private final List<IValidationChecker> validationCheckers;

    /**
     * Create a new {@link ValidationContainer} instance.
     * <p>
     *
     * By default, no {@link IValidationChecker} implementations are added.
     */
    public ValidationContainer() {
        this.validationCheckers = new ArrayList<>();
    }

    /**
     * Validate the provided {@link IValidationContext} with all the {@link IValidationChecker} implementations.
     *
     * @param context the {@link IValidationContext} to validate
     */
    public void validate(IValidationContext context) {
        for (IValidationChecker checker : validationCheckers) {
            checker.validate(context);
        }
    }

    /**
     * Add an {@link IValidationChecker} implementation to the container.
     *
     * @param checker the {@link IValidationChecker} implementation to add
     */
    public void addChecker(IValidationChecker checker) {
        validationCheckers.add(checker);
    }


    /**
     * Check if the container contains the provided {@link IValidationChecker} implementation.
     *
     * @param checker the {@link IValidationChecker} implementation to check
     * @return {@code true} if the container contains the provided {@link IValidationChecker} implementation,
     * {@code false} otherwise
     */
    public boolean containsChecker(IValidationChecker checker) {
        return validationCheckers.contains(checker);
    }

}
