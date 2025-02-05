/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.test.ExtendedITextTest;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class UriResolverTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdfstyledxmlparser/resolver/resource/UriResolverTest/";

    @Test
    public void uriResolverTest01() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        testPaths(resolver, absolutePathRoot);
    }

    @Test
    public void uriResolverTest01A() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().toUri().toURL().toExternalForm();
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        testPaths(resolver, absolutePathRoot);
    }

    @Test
    public void uriResolverTest02() throws MalformedURLException {
        UriResolver resolver = new UriResolver("test/folder/index.html");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        testPaths(resolver, runFolder);
    }

    @Test
    public void uriResolverTest03() throws MalformedURLException {
        UriResolver resolver = new UriResolver("/test/folder/index.html");
        String rootFolder = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        testPaths(resolver, rootFolder);
    }

    @Test
    public void uriResolverTest04() throws MalformedURLException {
        UriResolver resolver = new UriResolver("index.html");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        Assertions.assertEquals(runFolder + "index.html", resolver.getBaseUri());
        Assertions.assertEquals(runFolder + "innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assertions.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assertions.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest05() throws MalformedURLException {
        UriResolver resolver = new UriResolver("/../test/folder/index.html");
        String rootFolder = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        testPaths(resolver, rootFolder);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7369 fix tests which rely on Paths.get("").toAbsolutePath().getParent())
    public void uriResolverTest06() throws MalformedURLException {
        UriResolver resolver = new UriResolver("../test/folder/index.html");
        String parentFolder = Paths.get("").toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        testPaths(resolver, parentFolder);
    }

    @Test
    public void resolveAgainstBaseUriTest() throws MalformedURLException {
        String baseUrl = "https://test";
        UriResolver resolver = new UriResolver(SOURCE_FOLDER);
        resolver.resolveAgainstBaseUri(baseUrl);
        Assertions.assertTrue(resolver.isLocalBaseUri());
        Assertions.assertTrue(resolver.getBaseUri().startsWith("file:"));
    }

    @Test
    public void uriResolverTest07() throws MalformedURLException {
        UriResolver resolver = new UriResolver("http://itextpdf.com/itext7");
        Assertions.assertEquals("http://itextpdf.com/itext7", resolver.getBaseUri());
        Assertions.assertEquals("http://itextpdf.com/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assertions.assertEquals("http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assertions.assertEquals("http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri("//folder2.com/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest07A() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "%23r%e%2525s@o%25urces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "%23r%25e%2525s@o%25urces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest07B() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "#r%e%25s@o%urces/folder/index.html");
        String malformedPath = absolutePathRoot;
        Assertions.assertEquals(malformedPath + "#r%25e%25s@o%25urces/folder/index.html", resolver.getBaseUri());
        Assertions.assertEquals(malformedPath + "test/folder/innerTest", resolver.resolveAgainstBaseUri("test/folder/innerTest").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assertions.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assertions.assertEquals("file://folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("//folder/folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest08() throws MalformedURLException {
        UriResolver resolver = new UriResolver("http://itextpdf.com/itext7/");
        Assertions.assertEquals("http://itextpdf.com/itext7/", resolver.getBaseUri());
        Assertions.assertEquals("http://itextpdf.com/itext7/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assertions.assertEquals("http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assertions.assertEquals("http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri("//folder2.com/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest09() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "test/folder/index.html");
        String uriRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        testPaths(resolver, uriRoot);
    }

    @Test
    public void uriResolverTest10A() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path%with%spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%25with%25spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest10B() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path%25with%25spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%25with%25spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest10C() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path%2525with%2525spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%2525with%2525spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest10D() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path with spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%20with%20spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest10E() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path%20with%20spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%20with%20spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest10F() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        UriResolver resolver = new UriResolver(absolutePathRoot + "path%2520with%2520spaces/test/folder/index.html");
        String malformedPath = absolutePathRoot + "path%2520with%2520spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7369 fix tests which rely on Paths.get("").toAbsolutePath().getParent())
    public void uriResolverTest13() throws MalformedURLException {
        UriResolver resolver = new UriResolver("");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        Assertions.assertEquals(runFolder, resolver.getBaseUri());
        Assertions.assertEquals(runFolder + "innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        String parentToRunFolder = Paths.get("").toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        Assertions.assertEquals(parentToRunFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assertions.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assertions.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest14() throws MalformedURLException {
        UriResolver resolver = new UriResolver("base/uri/index.html");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        Assertions.assertEquals(runFolder + "base/uri/index.html", resolver.getBaseUri());

        final String firstUriResolvingResult = resolver.resolveAgainstBaseUri("file:/c:/test/folder/img.txt")
                .toExternalForm();
        final String expectedUriWithSingleSlash = "file:/c:/test/folder/img.txt";
        final String expectedUriWithTripleSlash = "file:///c:/test/folder/img.txt";

        // Both variants(namely with triple and single slashes) are valid.
        Assertions.assertTrue(expectedUriWithSingleSlash.equals(firstUriResolvingResult)
                || expectedUriWithTripleSlash.equals(firstUriResolvingResult));

        Assertions.assertEquals("file://c:/test/folder/img.txt",
                resolver.resolveAgainstBaseUri("file://c:/test/folder/img.txt").toExternalForm());

        final String thirdUriResolvingResult = resolver.resolveAgainstBaseUri("file:///c:/test/folder/img.txt")
                .toExternalForm();
        // Result of resolving uri with triple slash should be the same as if it contained single slash.
        Assertions.assertEquals(firstUriResolvingResult, thirdUriResolvingResult);

        // It is windows specific to assume this to work. On unix it shall fail, as it will assume that it is
        // an absolute URI with scheme 'c', and will not recognize this scheme.
        // Assertions.assertEquals("file:/c:/test/folder/data.jpg", resolver.resolveAgainstBaseUri("c:/test/folder/data
        // .jpg").toExternalForm());
    }

    @Test
    public void uriResolverTest15() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        testPaths(resolver, absolutePathRoot);
    }

    @Test
    public void uriResolverTest16() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        testPaths(resolver, absolutePathRoot);
    }

    @Test
    public void uriResolverTest16A() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        String absoluteBaseUri = absolutePathRoot + "path/with/spaces/test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        String malformedPath = absolutePathRoot + "path/with/spaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest16B() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();

        String absoluteBaseUri = absolutePathRoot + "path%2Fwith%2Fspaces/test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        String malformedPath = absolutePathRoot + "path%2Fwith%2Fspaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest16C() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        String absoluteBaseUri = absolutePathRoot + "path%252Fwith%252Fspaces/test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        String malformedPath = absolutePathRoot + "path%252Fwith%252Fspaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest16D() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        String absoluteBaseUri = absolutePathRoot + "path%25252Fwith%25252Fspaces/test/folder/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        String malformedPath = absolutePathRoot + "path%25252Fwith%25252Fspaces/";
        testPaths(resolver, malformedPath);
    }

    @Test
    public void uriResolverTest17() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        String absoluteBaseUri = absolutePathRoot + "test/fol ders/wi@th/diffe#rent/$characters/test/folder/index.html\t\t\t\t\t\t";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        String malformedPath = absolutePathRoot + "test/fol%20ders/wi@th/diffe#rent/$characters/";
        Assertions.assertNotNull(malformedPath + "test/folder/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assertions.assertNotNull(malformedPath + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());
        Assertions.assertNotNull(malformedPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assertions.assertNotNull(malformedPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest18() {
        String absolutePathRoot ="http://";
        String absoluteBaseUri = absolutePathRoot + "test/folders/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        Assertions.assertFalse(resolver.isLocalBaseUri());
    }

    @Test
    public void singleQuoteRelativePath() throws MalformedURLException {
        String expectedUrl = "https://he.wikipedia.org/wiki/%D7%90%D7%91%D7%92'%D7%93";
        String baseUri = "https://he.wikipedia.org/wiki/";
        String relativePath = "%D7%90%D7%91%D7%92'%D7%93";
        UriResolver resolver = new UriResolver(baseUri);

        Assertions.assertEquals(expectedUrl, resolver.resolveAgainstBaseUri(relativePath).toExternalForm());
    }

    @Test
    @Disabled("DEVSIX-2880: single quote character isn't encoded in Java and .NET 4.0, but it's encoded in .NETCoreApp 1.0" +
            " from single quote to %27")
    public void quoteInPercentsRelativePath() throws MalformedURLException {
        String expectedUrl = "https://he.wikipedia.org/wiki/%D7%90%D7%91%D7%92%27%D7%93";
        String baseUri = "https://he.wikipedia.org/wiki/";
        String relativePath = "%D7%90%D7%91%D7%92%27%D7%93";
        UriResolver resolver = new UriResolver(baseUri);

        Assertions.assertEquals(expectedUrl, resolver.resolveAgainstBaseUri(relativePath).toExternalForm());
    }

    @Test
    public void singleQuoteBasePath() throws MalformedURLException {
        String expectedUrl = "https://he.wikipedia.org/wiki'/%D7%90%D7%91%D7%92%D7%93";
        String baseUri = "https://he.wikipedia.org/wiki'/";
        String relativePath = "%D7%90%D7%91%D7%92%D7%93";
        UriResolver resolver = new UriResolver(baseUri);

        Assertions.assertEquals(expectedUrl, resolver.resolveAgainstBaseUri(relativePath).toExternalForm());
    }

    @Test
    @Disabled("DEVSIX-2880: single quote character isn't encoded in Java and .NET 4.0, but it's encoded in .NETCoreApp 1.0" +
            " from single quote to %27")
    public void quoteInPercentsBasePath() throws MalformedURLException {
        String expectedUrl = "https://he.wikipedia.org/wiki%27/%D7%90%D7%91%D7%92%D7%93";
        String baseUri = "https://he.wikipedia.org/wiki%27/";
        String relativePath = "%D7%90%D7%91%D7%92%D7%93";
        UriResolver resolver = new UriResolver(baseUri);

        Assertions.assertEquals(expectedUrl, resolver.resolveAgainstBaseUri(relativePath).toExternalForm());
    }

    @Test
    public void uriResolverPercentSignTest() throws MalformedURLException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toString();
        
        UriResolver resolver = new UriResolver(absolutePathRoot + "%homepath%");

        Assertions.assertEquals(absolutePathRoot + "%25homepath%25", resolver.getBaseUri());
    }

    private void testPaths(UriResolver resolver, String path) throws MalformedURLException {
        Assertions.assertEquals(path + "test/folder/index.html", resolver.getBaseUri());
        Assertions.assertEquals(path + "test/folder/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assertions.assertEquals(path + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());
        /*
            Resolving relative paths like "/folder" works correct on Linux and .NET, but fails on Java because of strong
            corresponding with URI standard RFC3986.

            Look to this memo for specifying "file" URI scheme:
            https://tools.ietf.org/id/draft-ietf-appsawg-file-scheme-12.html

            Expected results after resolving "/folder2/innerTest2":
                - .NET: "file:///C:/folder2/innerTest2"
                - Java (Windows): "file:/folder2/innerTest2" - incorrect
                - Java (Linux): "file:/folder2/innerTest2" - correct
        */
//        Assertions.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assertions.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }
}
