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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.exceptions.ReadingByteLimitException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ResourceResolverTest extends ExtendedITextTest {

    private final String baseUri = "./src/test/resources/com/itextpdf/styledxmlparser/resolver/retrieveStreamTest/";

    private final String bLogoIncorrect = "data:image/png;base,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

    private final String bLogoCorruptedData = "data:image/png;base64,,,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

    private final String bLogo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAVoAAAAxCAMAAACsy5FpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAqUExURQAAAPicJAdJdQdJdQdJdficJjBUbPicJgdJdQdJdficJficJQdJdficJlrFe50AAAAMdFJOUwCBe8I/Phe+65/saIJg0K4AAAMOSURBVHja7ZvbmqsgDIU5Bo/v/7q7/WZXsQYNuGy1muuZFH7DIiSglFLU6pZUbGQQNvXpNcC4caoNRvNxOuDUdf80HXk3VYewKp516DHWxuOc/0ye/U00duAwU+/qkWzfh9F9hzIHJxuzNa+fsa4I7Ihx+H+qUFN/sKVhzP7lH+a+qwY1gJHtmwFDPBHK1wLLjLOGTb2jIWhHScAF7RgOGod2CAGTFB8J2JodJ3Dq5kNow95oH3BdtsjGHE6LVu+P9iG5UlVwNjXOndGeRWuZEBBJLtWcMMK11nFoDfDL4TOEMUu0K/leIpNNpUrYFVsrDi2Mbb1DXqv5PV4quWzKHikJKq99utTsoI1dsMjBkr2dctoAMO3XQS2ogrNrJ5vH1OvtU6/ddIPR0k1g9K++bcSKo6Htf8wbdxpK2rnRigJRqAU3WiEylzzVlubCF0TLb/pTyZXH9o1WoKLVoKK8yBbUHS6IdjksZYpxo82WXIzIXhptYtmDRPbQaDXiPBZaaQl26ZBI6pfQ+gZ00A3CxkH6COo2rIwjom12KM/IJRehBUdF2wLrtUWS+56P/Q7aPUrheYnYRpE9LtrwSbSp7cxuJnv1qCWzk9AeEy3t0MAp2ccq93NogWHry3QWowqHPDK0mPSr8aXZAWQzO+hB17ebb9P5ZbDCu2obJPeiNQQWbAUse10VbbKqSLm9yRutQGT/8wO0G6+LdvV2Aaq0eDW0kmI3SHKvhZZkESnoTd5o5SIr+gb0A2g9wGQi67KUw5wdLajNEHymyCqo5B4RLawWHp10XcEC528suBOjJVwDZ2iOca9lBNsSl4jZE6Ntd6jXmtKVzeiIOy/aDzwTydmPZpJrzov2A89EsrKod8mVoq1y0LbsE02Zf/sVQSAObXa5ZSq5UkGoZw9LlqwRNkai5ZT7rRXyHkJgQqioSBipgjhGHPdMYy3hbLx8UDbDPTatndyeeW1HpaXtodxYyUO+zmoDUWjeUnHRB7d5E/KQnazRs0VdbWjI/EluloPnb26+KXIGI+e+7CBt/wAetDeCKwxY6QAAAABJRU5ErkJggg==";

    // Constructor tests block

    @Test
    public void constructorWithBaseUriTest() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver(null);

        UriResolver uriResolver = new UriResolver("");
        String resolveUrl = resolver.resolveAgainstBaseUri("").toString();
        String expectedUrl = uriResolver.resolveAgainstBaseUri("").toString();

        Assertions.assertEquals(resolveUrl, expectedUrl);
        Assertions.assertEquals(DefaultResourceRetriever.class, resolver.getRetriever().getClass());
    }

    @Test
    public void constructorWithBaseUriAndResourceRetrieverTest() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver("folder", new CustomResourceRetriever());

        UriResolver uriResolver = new UriResolver("folder");
        String resolveUrl = resolver.resolveAgainstBaseUri("").toString();
        String expectedUrl = uriResolver.resolveAgainstBaseUri("").toString();

        Assertions.assertEquals(resolveUrl, expectedUrl);
        Assertions.assertEquals(CustomResourceRetriever.class, resolver.getRetriever().getClass());
    }

    private static class CustomResourceRetriever extends DefaultResourceRetriever {

    }

    // Malformed resource name tests block

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveStreamByMalformedResourceNameTest() {
        String fileName = "resourceResolverTest .png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNull(bytes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveStyleSheetByMalformedResourceNameTest() throws IOException {
        String fileName = "retrieveStyl eSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.retrieveResourceAsInputStream(fileName);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveResourceAsInputStreamByMalformedResourceNameTest() {
        String fileName = "retrieveStyl eSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);
        Assertions.assertNull(stream);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveBytesFromResourceByMalformedResourceNameTest() {
        String fileName = "retrieveStyl eSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNull(bytes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveImageByMalformedResourceNameTest() {
        String fileName = "retrieveStyl eSheetTest.css";

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject pdfXObject = resourceResolver.retrieveImage(fileName);
        Assertions.assertNull(pdfXObject);
    }

    @Test
    public void malformedResourceNameWithoutSharpSymbolTest07() throws IOException {
        String fileName = "%5B%5D@!$&'()+,;=._~-/styles09.css";

        InputStream expected = FileUtil.getInputStreamForFile(baseUri + "[]@!$&'()+,;=._~-/styles09.css");

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);

        Assertions.assertNotNull(stream);
        Assertions.assertEquals(expected.read(), stream.read());
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6612 Unignore tests related to "#" symbol in URL path)
    public void malformedResourceNameTest07() throws IOException {
        String fileName = "%23%5B%5D@!$&'()+,;=._~-/styles09.css";

        InputStream expected = FileUtil.getInputStreamForFile(baseUri + "#[]@!$&'()+,;=._~-/styles09.css");

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);

        Assertions.assertNotNull(stream);
        Assertions.assertEquals(expected.read(), stream.read());
    }

    // Boolean method tests block

    @Test
    public void isDataSrcTest() {
        Assertions.assertTrue(ResourceResolver.isDataSrc(bLogo));
        Assertions.assertTrue(ResourceResolver.isDataSrc(bLogoCorruptedData));
        Assertions.assertTrue(ResourceResolver.isDataSrc(bLogoIncorrect));
        Assertions.assertFalse(ResourceResolver.isDataSrc("https://data.com/data"));
    }

    // Retrieve pdfXObject tests block

    @Test
    public void retrieveImageBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(bLogo);
        Assertions.assertNotNull(image);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_DATA_URI))
    public void retrieveImageIncorrectBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(bLogoCorruptedData);
        Assertions.assertNull(image);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_DATA_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveImageCorruptedDataBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(bLogoCorruptedData);
        Assertions.assertNull(image);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveImageNullTest() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(null);
        Assertions.assertNull(image);
    }

    @Test
    public void retrieveImageTest() {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(fileName);
        Assertions.assertNotNull(image);
    }

    // Retrieve byte array tests block

    @Test
    public void retrieveBytesFromResourceBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(bLogo);
        Assertions.assertNotNull(bytes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveBytesFromResourceIncorrectBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(bLogoIncorrect);
        Assertions.assertNull(bytes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR))
    public void retrieveBytesFromResourceCorruptedDataBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(bLogoCorruptedData);
        Assertions.assertNull(bytes);
    }

    @Test
    public void retrieveBytesFromResourcePngImageTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] expected = Files.readAllBytes(new File(baseUri + fileName).toPath());
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNotNull(bytes);
        Assertions.assertEquals(expected.length, bytes.length);
    }

    @Test
    public void retrieveStreamPngImageTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] expected = Files.readAllBytes(new File(baseUri + fileName).toPath());
        byte[] stream = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNotNull(resourceResolver.retrieveBytesFromResource(fileName));
        Assertions.assertEquals(expected.length, stream.length);
    }

    @Test
    public void retrieveBytesFromResourceStyleSheetTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] expected = Files.readAllBytes(new File(baseUri + fileName).toPath());
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNotNull(bytes);
        Assertions.assertEquals(expected.length, bytes.length);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT, logLevel = LogLevelConstants.WARN))
    public void attemptToRetrieveBytesFromResourceStyleSheetWithFilterRetrieverTest() {
        String fileName = "retrieveStyleSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.setRetriever(new FilterResourceRetriever());
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNull(bytes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI))
    public void retrieveImageWrongPathTest() {
        String fileName = "/itextpdf.com/itis.jpg";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(fileName);
        Assertions.assertNull(image);
    }

    @Test
    public void retrieveImageRightPathTest() {
        String fileName = "itextpdf.com/itis.jpg";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        PdfXObject image = resourceResolver.retrieveImage(fileName);
        Assertions.assertNotNull(image);
    }

    @Test
    public void retrieveImagePathWithSpacesTest() {
        String fileName = "retrieveImagePathWithSpaces.jpg";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri + "path with spaces/");
        PdfXObject image = resourceResolver.retrieveImage(fileName);
        Assertions.assertNotNull(image);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI))
    public void retrieveBytesMalformedResourceNameTest() {
        String fileName = "resourceResolverTest .png";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        byte[] bytes =resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNull(bytes);
    }

    @Test
    public void retrieveBytesFromResourceWithRetryRetrieverTest() throws IOException {
        String fileName = "!invalid! StyleSheetName.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri, new RetryResourceRetriever(baseUri));
        byte[] expected = Files.readAllBytes(new File(baseUri + "retrieveStyleSheetTest.css").toPath());
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNotNull(bytes);
        Assertions.assertEquals(expected.length, bytes.length);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_RESOURCE_WITH_GIVEN_RESOURCE_SIZE_BYTE_LIMIT, logLevel = LogLevelConstants.WARN))
    public void attemptToRetrieveBytesFromLocalWithResourceSizeByteLimitTest() {
        String fileName = "retrieveStyleSheetTest.css.dat";
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        IResourceRetriever retriever = new DefaultResourceRetriever().setResourceSizeByteLimit(88);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri, retriever);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNull(bytes);
    }

    @Test
    public void retrieveBytesFromLocalWithResourceSizeByteLimitTest() {
        String fileName = "retrieveStyleSheetTest.css.dat";
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        IResourceRetriever retriever = new DefaultResourceRetriever().setResourceSizeByteLimit(89);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri, retriever);
        byte[] bytes = resourceResolver.retrieveBytesFromResource(fileName);
        Assertions.assertNotNull(bytes);
        Assertions.assertEquals(((DefaultResourceRetriever) retriever).getResourceSizeByteLimit(), bytes.length);
    }

    // Retrieve input stream tests block

    @Test
    public void attemptToReadBytesFromLimitedInputStreamTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css.dat";
        // retrieveStyleSheetTest.css.dat size is 89 bytes
        IResourceRetriever retriever = new DefaultResourceRetriever().setResourceSizeByteLimit(40);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri, retriever);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);

        for (int i = 0; i < 40; i++) {
            stream.read();
        }

        Assertions.assertThrows(ReadingByteLimitException.class, () -> stream.read());
    }

    @Test
    public void retrieveResourceAsInputStreamBase64Test() {
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(bLogo);
        Assertions.assertNotNull(stream);
    }

    @Test
    public void retrieveStyleSheetTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        InputStream expected = FileUtil.getInputStreamForFile(baseUri + fileName);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);
        Assertions.assertNotNull(stream);
        Assertions.assertEquals(expected.read(), stream.read());
    }

    @Test
    public void retrieveResourceAsInputStreamStyleSheetTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        InputStream expected = FileUtil.getInputStreamForFile(baseUri + fileName);
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);
        Assertions.assertNotNull(stream);
        Assertions.assertEquals(expected.read(), stream.read());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT, logLevel = LogLevelConstants.WARN))
    public void attemptToRetrieveInputStreamWithFilterRetrieverTest() {
        String fileName = "retrieveStyleSheetTest.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        resourceResolver.setRetriever(new FilterResourceRetriever());
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);
        Assertions.assertNull(stream);
    }

    private static class FilterResourceRetriever extends DefaultResourceRetriever {
        @Override
        protected boolean urlFilter(URL url) {
            return url.getPath().startsWith("/MyFolderWithUniqName");
        }
    }

    @Test
    public void retrieveInputStreamWithRetryRetrieverTest() throws IOException {
        String fileName = "!invalid! StyleSheetName.css";
        ResourceResolver resourceResolver = new ResourceResolver(baseUri, new RetryResourceRetriever(baseUri));
        InputStream expected = FileUtil.getInputStreamForFile(baseUri + "retrieveStyleSheetTest.css");
        InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileName);
        Assertions.assertNotNull(stream);
        Assertions.assertEquals(expected.read(), stream.read());
    }

    private static class RetryResourceRetriever extends DefaultResourceRetriever {
        private String baseUri;

        public RetryResourceRetriever(String baseUri) {
            this.baseUri = baseUri;
        }

        @Override
        public InputStream getInputStreamByUrl(URL url) throws IOException {
            InputStream stream = null;
            try {
                stream = super.getInputStreamByUrl(url);
            } catch (Exception ignored) {
            }

            if (stream == null) {
                URL newUrl = new UriResolver(this.baseUri).resolveAgainstBaseUri("retrieveStyleSheetTest.css");
                stream = super.getInputStreamByUrl(newUrl);
            }
            return stream;
        }
    }

    // Absolute path tests block

    @Test
    public void retrieveStyleSheetAbsolutePathTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        String absolutePath = Paths.get(baseUri, fileName).toFile().getAbsolutePath();

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(absolutePath);
                InputStream expected = FileUtil.getInputStreamForFile(absolutePath);) {
            Assertions.assertNotNull(stream);
            Assertions.assertEquals(expected.read(), stream.read());
        }
    }

    @Test
    public void retrieveResourceAsInputStreamAbsolutePathTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        String absolutePath = Paths.get(baseUri, fileName).toFile().getAbsolutePath();

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(absolutePath);
                InputStream expected = FileUtil.getInputStreamForFile(absolutePath);) {
            Assertions.assertNotNull(stream);
            Assertions.assertEquals(expected.read(), stream.read());
        }
    }

    @Test
    public void retrieveStyleSheetFileUrlTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        URL url = Paths.get(baseUri, fileName).toUri().toURL();
        String fileUrlString = url.toExternalForm();

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileUrlString);
                InputStream expected = url.openStream()) {
            Assertions.assertNotNull(stream);
            Assertions.assertEquals(expected.read(), stream.read());
        }
    }

    @Test
    public void retrieveResourceAsInputStreamFileUrlTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";
        URL url = Paths.get(baseUri, fileName).toUri().toURL();
        String fileUrlString = url.toExternalForm();

        ResourceResolver resourceResolver = new ResourceResolver(baseUri);
        try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(fileUrlString);
                InputStream expected = url.openStream()) {
            Assertions.assertNotNull(stream);
            Assertions.assertEquals(expected.read(), stream.read());
        }
    }
}
