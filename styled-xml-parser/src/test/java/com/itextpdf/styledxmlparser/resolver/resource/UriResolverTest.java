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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.nio.file.Paths;

@Category(UnitTest.class)
public class UriResolverTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        Assert.assertEquals(runFolder + "index.html", resolver.getBaseUri());
        Assert.assertEquals(runFolder + "innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assert.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assert.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest05() throws MalformedURLException {
        UriResolver resolver = new UriResolver("/../test/folder/index.html");
        String rootFolder = Paths.get("").toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        testPaths(resolver, rootFolder);
    }

    @Test
    public void uriResolverTest06() throws MalformedURLException {
        UriResolver resolver = new UriResolver("../test/folder/index.html");
        String parentFolder = Paths.get("").toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        testPaths(resolver, parentFolder);
    }

    @Test
    public void uriResolverTest07() throws MalformedURLException {
        UriResolver resolver = new UriResolver("http://itextpdf.com/itext7");
        Assert.assertEquals("http://itextpdf.com/itext7", resolver.getBaseUri());
        Assert.assertEquals("http://itextpdf.com/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assert.assertEquals("http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assert.assertEquals("http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri("//folder2.com/innerTest2").toExternalForm());
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
        Assert.assertEquals(malformedPath + "#r%25e%25s@o%25urces/folder/index.html", resolver.getBaseUri());
        Assert.assertEquals(malformedPath + "test/folder/innerTest", resolver.resolveAgainstBaseUri("test/folder/innerTest").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assert.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assert.assertEquals("file://folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("//folder/folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest08() throws MalformedURLException {
        UriResolver resolver = new UriResolver("http://itextpdf.com/itext7/");
        Assert.assertEquals("http://itextpdf.com/itext7/", resolver.getBaseUri());
        Assert.assertEquals("http://itextpdf.com/itext7/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assert.assertEquals("http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assert.assertEquals("http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri("//folder2.com/innerTest2").toExternalForm());
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
    public void uriResolverTest13() throws MalformedURLException {
        UriResolver resolver = new UriResolver("");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        Assert.assertEquals(runFolder, resolver.getBaseUri());
        Assert.assertEquals(runFolder + "innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        String parentToRunFolder = Paths.get("").toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        Assert.assertEquals(parentToRunFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());

//        Look to commentary in testPaths() method.
//        Assert.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assert.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest14() throws MalformedURLException {
        UriResolver resolver = new UriResolver("base/uri/index.html");
        String runFolder = Paths.get("").toUri().toURL().toExternalForm();
        Assert.assertEquals(runFolder + "base/uri/index.html", resolver.getBaseUri());
        Assert.assertEquals("file:/c:/test/folder/img.txt", resolver.resolveAgainstBaseUri("file:/c:/test/folder/img.txt").toExternalForm());
        Assert.assertEquals("file://c:/test/folder/img.txt", resolver.resolveAgainstBaseUri("file://c:/test/folder/img.txt").toExternalForm());
        Assert.assertEquals("file:/c:/test/folder/data.jpg", resolver.resolveAgainstBaseUri("file:///c:/test/folder/data.jpg").toExternalForm());

        // It is windows specific to assume this to work. On unix it shall fail, as it will assume that it is
        // an absolute URI with scheme 'c', and will not recognize this scheme.
        // Assert.assertEquals("file:/c:/test/folder/data.jpg", resolver.resolveAgainstBaseUri("c:/test/folder/data.jpg").toExternalForm());
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
    //TODO RND-1019 this test should fail in .Net version when RND-1019 is resolved this method produces a behavior that is not consistant in java vs .Net
    // the whitespace characters are
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
        Assert.assertNotNull(malformedPath + "test/folder/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assert.assertNotNull(malformedPath + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());
        Assert.assertNotNull(malformedPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
        Assert.assertNotNull(malformedPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }

    @Test
    public void uriResolverTest18() {
        String absolutePathRoot ="http://";
        String absoluteBaseUri = absolutePathRoot + "test/folders/index.html";
        UriResolver resolver = new UriResolver(absoluteBaseUri);
        Assert.assertFalse(resolver.isLocalBaseUri());
    }

    private void testPaths(UriResolver resolver, String path) throws MalformedURLException {
        Assert.assertEquals(path + "test/folder/index.html", resolver.getBaseUri());
        Assert.assertEquals(path + "test/folder/innerTest", resolver.resolveAgainstBaseUri("innerTest").toExternalForm());
        Assert.assertEquals(path + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri("../folder2/innerTest2").toExternalForm());
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
//        Assert.assertEquals("file:/folder2/innerTest2", resolver.resolveAgainstBaseUri("/folder2/innerTest2").toExternalForm());
//        Assert.assertEquals("file://folder2/innerTest2/", resolver.resolveAgainstBaseUri("//folder2/innerTest2").toExternalForm());
    }
}
