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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import org.junit.jupiter.api.Assertions;

public class SignatureIdentifierTest extends AbstractIdentifiableObjectTest {
    @Override
    protected void performTestHashForEqualInstances() {
        AbstractIdentifiableObject sut1 = getIdentifiableObjectUnderTest();
        AbstractIdentifiableObject sut2 = getIdentifiableObjectUnderTest();
        // CMS Containers have not equal hashes.
        Assertions.assertNotEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Override
    protected void performTestEqualsForEqualInstances() {
        AbstractIdentifiableObject sut1 = getIdentifiableObjectUnderTest();
        AbstractIdentifiableObject sut2 = getIdentifiableObjectUnderTest();
        // CMS Containers are not equal.
        Assertions.assertNotEquals(sut1, sut2);
    }

    @Override
    protected void performTestEqualsForDifferentInstances() {
        AbstractIdentifiableObject sut1 = getIdentifiableObjectUnderTest();
        AbstractIdentifiableObject sut2 = new SignatureIdentifier(new ValidationObjects(),
                new CMSContainer(), "other test", TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertNotEquals(sut1, sut2);
    }

    @Override
    protected void performTestHashForDifferentInstances() {
        AbstractIdentifiableObject sut1 = getIdentifiableObjectUnderTest();
        AbstractIdentifiableObject sut2 = new SignatureIdentifier(new ValidationObjects(),
                new CMSContainer(), "other test", TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertNotEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Override
    AbstractIdentifiableObject getIdentifiableObjectUnderTest() {
        return new SignatureIdentifier(new ValidationObjects(), new CMSContainer(), "test",
                TimeTestUtil.TEST_DATE_TIME);
    }
}
