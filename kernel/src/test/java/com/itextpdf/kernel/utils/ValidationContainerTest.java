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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ValidationContainerTest extends ExtendedITextTest {
    @Test
    public void validateObjectTest() {
        ValidationContainer container = new ValidationContainer();
        container.validate(new FontValidationContext(null, null));
        final CustomValidationChecker checker = new CustomValidationChecker();
        container.addChecker(checker);
        Assertions.assertTrue(container.containsChecker(checker));

        Assertions.assertFalse(checker.objectValidationPerformed);
        container.validate(new FontValidationContext(null, null));
        Assertions.assertTrue(checker.objectValidationPerformed);
    }

    @Test
    public void validateDocumentTest() {
        ValidationContainer container = new ValidationContainer();
        PdfDocumentValidationContext context = new PdfDocumentValidationContext(null, null);
        container.validate(context);
        final CustomValidationChecker checker = new CustomValidationChecker();
        container.addChecker(checker);

        Assertions.assertFalse(checker.documentValidationPerformed);
        container.validate(context);
        Assertions.assertTrue(checker.documentValidationPerformed);
    }

    private static class CustomValidationChecker implements IValidationChecker {
        public boolean documentValidationPerformed = false;
        public boolean objectValidationPerformed = false;

        @Override
        public void validate(IValidationContext validationContext) {
            if (validationContext.getType() == ValidationType.PDF_DOCUMENT) {
                documentValidationPerformed = true;
            } else {
                objectValidationPerformed = true;
            }
        }

        @Override
        public boolean isPdfObjectReadyToFlush(PdfObject object) {
            return true;
        }
    }
}
