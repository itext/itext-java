package com.itextpdf.signatures;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Category(UnitTest.class)
public class CertificateInfoTest extends ExtendedITextTest {
    @Test
    public void X500InvalidDirectoryConstructorTest() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new CertificateInfo.X500Name("some_dir"));
    }

    @Test
    public void X500ValidDirectoryConstructorTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=dir,another=dir,some=value");

        Map<String, List<String>> values = name.getFields();
        Assert.assertEquals(2, values.size());

        Assert.assertEquals(Arrays.asList("dir", "value"), values.get("SOME"));
        Assert.assertEquals(Collections.singletonList("dir"), values.get("ANOTHER"));
    }

    @Test
    public void X500GetFieldTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assert.assertEquals("value", name.getField("SOME"));
        Assert.assertEquals("dir", name.getField("ANOTHER"));
    }

    @Test
    public void X500GetFieldArrayTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assert.assertEquals(Arrays.asList("value", "dir"), name.getFieldArray("SOME"));
        Assert.assertEquals(Collections.singletonList("dir"), name.getFieldArray("ANOTHER"));
    }

    @Test
    public void X509NameTokenizerNextTokenComplicatedTest() {
        CertificateInfo.X509NameTokenizer tokenizer = new CertificateInfo.X509NameTokenizer("quoted\",\"comma=escaped\\,comma_escaped\\\"quote");
        String token = tokenizer.nextToken();

        Assert.assertEquals("quoted,comma=escaped,comma_escaped\"quote", token);
        Assert.assertNull(tokenizer.nextToken());
    }

    @Test
    public void getIssuerFieldsExceptionTest() {
        Exception exception =
                Assert.assertThrows(PdfException.class, () -> CertificateInfo.getIssuer(new byte[] {4, 8, 15, 16, 23, 42}));
        Assert.assertEquals("corrupted stream - out of bounds length found: 8 >= 6", exception.getCause().getMessage());
    }

    @Test
    public void getSubjectExceptionTest() {
        Exception exception =
                Assert.assertThrows(PdfException.class, () -> CertificateInfo.getSubject(new byte[] {4, 8, 15, 16, 23, 42}));
        Assert.assertEquals("corrupted stream - out of bounds length found: 8 >= 6", exception.getCause().getMessage());
    }
}
