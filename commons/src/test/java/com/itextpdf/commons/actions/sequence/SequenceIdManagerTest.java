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
package com.itextpdf.commons.actions.sequence;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SequenceIdManagerTest extends ExtendedITextTest {

    @Test
    public void setIdentifier() {
        IdentifiableElement element = new IdentifiableElement();
        Assertions.assertNull(SequenceIdManager.getSequenceId(element));

        SequenceId sequenceId = new SequenceId();
        SequenceIdManager.setSequenceId(element, sequenceId);
        Assertions.assertEquals(sequenceId, SequenceIdManager.getSequenceId(element));
    }

    @Test
    public void overrideIdentifierTest() {
        IdentifiableElement element = new IdentifiableElement();
        SequenceId sequenceId1 = new SequenceId();
        SequenceId sequenceId2 = new SequenceId();
        SequenceIdManager.setSequenceId(element, sequenceId1);

        Exception e = Assertions.assertThrows(IllegalStateException.class,
                () -> SequenceIdManager.setSequenceId(element, sequenceId2));

        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.ELEMENT_ALREADY_HAS_IDENTIFIER,
                sequenceId1.getId(), sequenceId2.getId()), e.getMessage());
    }

    private static class IdentifiableElement extends AbstractIdentifiableElement {

    }
}
