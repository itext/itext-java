/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.SignaturePermissions.FieldLock;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SignaturePermissionsTest extends ExtendedITextTest {

    @Test
    public void defaultValuesTest() {
        SignaturePermissions permissions = new SignaturePermissions(new PdfDictionary(), null);
        Assert.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assert.assertTrue(permissions.isAnnotationsAllowed());
        Assert.assertFalse(permissions.isCertification());
        Assert.assertTrue(permissions.isFillInAllowed());
    }

    @Test
    public void transformedMethodDocMDPIsPresentedTest() {
        PdfDictionary dict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithDocMDP = new PdfDictionary();
        dictWithDocMDP.put(PdfName.TransformMethod, PdfName.DocMDP);
        dictWithDocMDP.put(PdfName.TransformParams, new PdfDictionary());

        references.add(dictWithDocMDP);
        dict.put(PdfName.Reference, references);

        SignaturePermissions permissions = new SignaturePermissions(dict, null);

        Assert.assertTrue(permissions.isCertification());

        Assert.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assert.assertTrue(permissions.isAnnotationsAllowed());
        Assert.assertTrue(permissions.isFillInAllowed());
    }

    @Test
    public void actionIsPresentedTest() {
        PdfDictionary dict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithAction = new PdfDictionary();
        PdfDictionary params = new PdfDictionary();

        PdfName action = new PdfName("Name");
        PdfArray fields = new PdfArray();
        fields.add(new PdfString("Value1"));
        fields.add(new PdfString("Value2"));

        params.put(PdfName.Action, action);
        params.put(PdfName.Fields, fields);

        dictWithAction.put(PdfName.TransformParams, params);

        references.add(dictWithAction);
        dict.put(PdfName.Reference, references);

        SignaturePermissions permissions = new SignaturePermissions(dict, null);

        Assert.assertEquals(1, permissions.getFieldLocks().size());
        FieldLock fieldLock = permissions.getFieldLocks().get(0);
        Assert.assertEquals(action, fieldLock.getAction());
        Assert.assertEquals(fields, fieldLock.getFields());

        Assert.assertTrue(permissions.isAnnotationsAllowed());
        Assert.assertFalse(permissions.isCertification());
        Assert.assertTrue(permissions.isFillInAllowed());
    }

    @Test
    public void multipleActionsArePresentedTest() {
        PdfDictionary dict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithAction = new PdfDictionary();
        PdfDictionary params = new PdfDictionary();

        PdfName action = new PdfName("Name");
        PdfArray fields = new PdfArray();
        fields.add(new PdfString("Value1"));
        fields.add(new PdfString("Value2"));

        params.put(PdfName.Action, action);
        params.put(PdfName.Fields, fields);

        dictWithAction.put(PdfName.TransformParams, params);

        references.add(dictWithAction);
        references.add(dictWithAction);
        references.add(dictWithAction);

        dict.put(PdfName.Reference, references);

        SignaturePermissions permissions = new SignaturePermissions(dict, null);

        Assert.assertEquals(3, permissions.getFieldLocks().size());
        for(FieldLock fieldLock: permissions.getFieldLocks()) {
            Assert.assertEquals(action, fieldLock.getAction());
            Assert.assertEquals(fields, fieldLock.getFields());
        }

        Assert.assertTrue(permissions.isAnnotationsAllowed());
        Assert.assertFalse(permissions.isCertification());
        Assert.assertTrue(permissions.isFillInAllowed());
    }

    @Test
    public void pParamEqualsTo1Test() {
        PdfDictionary dict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithAction = new PdfDictionary();
        PdfDictionary params = new PdfDictionary();
        params.put(PdfName.P, new PdfNumber(1));

        dictWithAction.put(PdfName.TransformParams, params);

        references.add(dictWithAction);
        dict.put(PdfName.Reference, references);

        SignaturePermissions permissions = new SignaturePermissions(dict, null);

        Assert.assertFalse(permissions.isFillInAllowed());
        Assert.assertFalse(permissions.isAnnotationsAllowed());

        Assert.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assert.assertFalse(permissions.isCertification());
    }

    @Test
    public void pParamEqualsTo2Test() {
        PdfDictionary dict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithAction = new PdfDictionary();
        PdfDictionary params = new PdfDictionary();
        params.put(PdfName.P, new PdfNumber(2));

        dictWithAction.put(PdfName.TransformParams, params);

        references.add(dictWithAction);
        dict.put(PdfName.Reference, references);

        SignaturePermissions permissions = new SignaturePermissions(dict, null);

        Assert.assertFalse(permissions.isAnnotationsAllowed());

        Assert.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assert.assertTrue(permissions.isFillInAllowed());
        Assert.assertFalse(permissions.isCertification());
    }

    @Test
    public void previousIsSetTest() {
        PdfDictionary previousDict = new PdfDictionary();
        PdfArray references = new PdfArray();

        PdfDictionary dictWithAction = new PdfDictionary();
        PdfDictionary params = new PdfDictionary();
        params.put(PdfName.P, new PdfNumber(1));

        PdfName action = new PdfName("Name");
        PdfArray fields = new PdfArray();
        fields.add(new PdfString("Value1"));
        fields.add(new PdfString("Value2"));

        params.put(PdfName.Action, action);
        params.put(PdfName.Fields, fields);

        dictWithAction.put(PdfName.TransformParams, params);

        references.add(dictWithAction);
        previousDict.put(PdfName.Reference, references);

        SignaturePermissions previousPermissions = new SignaturePermissions(previousDict, null);
        SignaturePermissions permissions = new SignaturePermissions(new PdfDictionary(), previousPermissions);

        Assert.assertEquals(1, permissions.getFieldLocks().size());
        FieldLock fieldLock = permissions.getFieldLocks().get(0);
        Assert.assertEquals(action, fieldLock.getAction());
        Assert.assertEquals(fields, fieldLock.getFields());

        Assert.assertFalse(permissions.isAnnotationsAllowed());
        Assert.assertFalse(permissions.isCertification());
        Assert.assertFalse(permissions.isFillInAllowed());
    }
}
