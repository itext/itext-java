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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SignaturePermissionsTest extends ExtendedITextTest {

    @Test
    public void defaultValuesTest() {
        SignaturePermissions permissions = new SignaturePermissions(new PdfDictionary(), null);
        Assertions.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assertions.assertTrue(permissions.isAnnotationsAllowed());
        Assertions.assertFalse(permissions.isCertification());
        Assertions.assertTrue(permissions.isFillInAllowed());
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

        Assertions.assertTrue(permissions.isCertification());

        Assertions.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assertions.assertTrue(permissions.isAnnotationsAllowed());
        Assertions.assertTrue(permissions.isFillInAllowed());
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

        Assertions.assertEquals(1, permissions.getFieldLocks().size());
        FieldLock fieldLock = permissions.getFieldLocks().get(0);
        Assertions.assertEquals(action, fieldLock.getAction());
        Assertions.assertEquals(fields, fieldLock.getFields());

        Assertions.assertTrue(permissions.isAnnotationsAllowed());
        Assertions.assertFalse(permissions.isCertification());
        Assertions.assertTrue(permissions.isFillInAllowed());
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

        Assertions.assertEquals(3, permissions.getFieldLocks().size());
        for(FieldLock fieldLock: permissions.getFieldLocks()) {
            Assertions.assertEquals(action, fieldLock.getAction());
            Assertions.assertEquals(fields, fieldLock.getFields());
        }

        Assertions.assertTrue(permissions.isAnnotationsAllowed());
        Assertions.assertFalse(permissions.isCertification());
        Assertions.assertTrue(permissions.isFillInAllowed());
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

        Assertions.assertFalse(permissions.isFillInAllowed());
        Assertions.assertFalse(permissions.isAnnotationsAllowed());

        Assertions.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assertions.assertFalse(permissions.isCertification());
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

        Assertions.assertFalse(permissions.isAnnotationsAllowed());

        Assertions.assertEquals(new ArrayList<>(), permissions.getFieldLocks());
        Assertions.assertTrue(permissions.isFillInAllowed());
        Assertions.assertFalse(permissions.isCertification());
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

        Assertions.assertEquals(1, permissions.getFieldLocks().size());
        FieldLock fieldLock = permissions.getFieldLocks().get(0);
        Assertions.assertEquals(action, fieldLock.getAction());
        Assertions.assertEquals(fields, fieldLock.getFields());

        Assertions.assertFalse(permissions.isAnnotationsAllowed());
        Assertions.assertFalse(permissions.isCertification());
        Assertions.assertFalse(permissions.isFillInAllowed());
    }
}
