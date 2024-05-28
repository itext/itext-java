package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class EncryptionUtilsTest extends ExtendedITextTest {

    @Test
    public void fetchEnvelopedDataThrows() {
        Assert.assertThrows(Exception.class, () -> EncryptionUtils.fetchEnvelopedData(null, null, null, null, null));
    }

}