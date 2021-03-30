/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
