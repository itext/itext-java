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
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
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

import java.io.*;
import java.net.MalformedURLException;
import java.nio.channels.FileLock;
import java.nio.file.Files;

@Category(UnitTest.class)
public class ResourceResolverTest extends ExtendedITextTest {

    private final String baseUri = "./src/test/resources/com/itextpdf/styledxmlparser/resolver/retrieveStreamTest/";

    private final String bLogoIncorrect = "data:image/png;base,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

    private final String bLogoCorruptedData = "data:image/png;base64,,,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

    private final String bLogo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

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
    public void resourceResolverConstructorTest() throws MalformedURLException {
        ResourceResolver rr = new ResourceResolver(null);
        UriResolver ur = new UriResolver("");
        String rrUrl = rr.resolveAgainstBaseUri("").toString();
        String urUrl = ur.resolveAgainstBaseUri("").toString();
        Assert.assertEquals(rrUrl, urUrl);
    }

    @Test
    public void malformedResourceNameTest01() throws IOException {
        junitExpectedException.expect(IOException.class);
        String fileName = "retrieveStyl eSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.retrieveStyleSheet(fileName);
    }

    @Test
    public void isDataSrcCheckTest() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        Assert.assertTrue(resourceResolver.isDataSrc(bLogoCorruptedData));
        Assert.assertTrue(resourceResolver.isDataSrc(bLogoIncorrect));
        Assert.assertFalse(resourceResolver.isDataSrc("https://data.com/data"));
    }

    @Test
    public void incorrectBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject pdfXObject = resourceResolver.tryResolveBase64ImageSource(bLogoIncorrect);
        Assert.assertNull(pdfXObject);
    }

    @Test
    public void retrieveAsStreamBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(bLogo);
        Assert.assertNotNull(stream);
    }

    @Test
    public void retrieveBytesTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        byte[] expected = Files.readAllBytes(new File(baseUri + fileName).toPath());
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] stream = resourceResolver.retrieveBytesFromResource(fileName);
        Assert.assertNotNull(resourceResolver.retrieveBytesFromResource(fileName));
        Assert.assertEquals(expected.length, stream.length);
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
    public void retrieveImageTest() {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfImageXObject image = resourceResolver.retrieveImage(fileName);
        Assert.assertNotNull(image);
        Assert.assertTrue(image.identifyImageFileExtension().equalsIgnoreCase("png"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, count = 1))
    public void retrieveBytesMalformedResourceNameTest() {
        String fileName = "resourceResolverTest .png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes =resourceResolver.retrieveBytesFromResource(fileName);
        Assert.assertNull(bytes);
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
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI, count = 1))
    public void retrieveImageExtendedNullTest() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImageExtended(null);
        Assert.assertNull(image);
    }

    @Test
    public void retrieveImageExtendedBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImageExtended(bLogo);
        Assert.assertNotNull(image);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI, count = 1))
    public void retrieveImageExtendedIncorrectBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImageExtended(bLogoCorruptedData);
        Assert.assertNull(image);
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

    @Test
    public void isImageTypeSupportedTest() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        String fileName = "resourceResolverTest.png";
        boolean res = resourceResolver.isImageTypeSupportedByImageDataFactory(fileName);
        Assert.assertTrue(res);
        res = resourceResolver.isImageTypeSupportedByImageDataFactory("test.txt");
        Assert.assertFalse(res);
    }

    @Test
    public void isImageTypeSupportedMalformedURLTest() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        boolean res = resourceResolver.isImageTypeSupportedByImageDataFactory("htt://test.png");
        Assert.assertFalse(res);
        res = resourceResolver.isImageTypeSupportedByImageDataFactory("htt://test.png");
        Assert.assertFalse(res);
    }
}
