/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Category(UnitTest.class)
public class ResourceResolverTest extends ExtendedITextTest {
    private final String baseUri = "./src/test/resources/com/itextpdf/styledxmlparser/resolver/retrieveStreamTest/";
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, count = 1))
    public void malformedResourceNameTest() {
        String fileName = "resourceResolverTest .png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.retrieveStream(fileName);
    }

    @Test
    public void malformedResourceNameTest01() throws IOException {
        junitExpectedException.expect(IOException.class);
        String fileName = "retrieveStyl eSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.retrieveStyleSheet(fileName);
    }

    @Test
    public void retrieveStreamTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        byte[] expected = Files.readAllBytes(new File(baseUri + fileName).toPath());
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] stream = resourceResolver.retrieveStream(fileName);
        Assert.assertNotNull(resourceResolver.retrieveStream(fileName));
        Assert.assertEquals(expected.length, stream.length);
    }

    @Test
    public void retrieveStyleSheetTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        InputStream expected = new FileInputStream(baseUri + fileName);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveStyleSheet(fileName);
        Assert.assertNotNull(stream);
        Assert.assertEquals(expected.read(), stream.read());
    }


    @Test
    public void retrieveImageTest()  {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfImageXObject image = resourceResolver.retrieveImage(fileName);
        Assert.assertNotNull(image);
        Assert.assertTrue(image.identifyImageFileExtension().equalsIgnoreCase("png"));
    }

    @Test
    public void absolutePathTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        String absolutePath = UrlUtil.toNormalizedURI(baseUri).getPath() + fileName;
        InputStream expected = new FileInputStream(absolutePath);

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveStyleSheet(absolutePath);
        Assert.assertNotNull(stream);
        Assert.assertEquals(expected.read(), stream.read());
    }

    @Test
    public void absolutePathTest2() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        String absolutePath = UrlUtil.toNormalizedURI(baseUri).toString() + fileName;
        //this constructor will fail.
        //InputStream expected = new FileInputStream(absolutePath);

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveStyleSheet(absolutePath);
        Assert.assertNotNull(stream);
        //Assert.assertEquals(expected.read(), stream.read());
    }
}
