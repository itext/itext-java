/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Category(UnitTest.class)
public class ResourceResolverTest extends ExtendedITextTest {
    private final String baseUri = "./src/test/resources/com/itextpdf/styledxmlparser/resolver/resources/retrieveStreamTest/";
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void resourceResolverTest01() throws MalformedURLException {
        String absolutePathRoot = Paths.get( "" ).toAbsolutePath().getRoot().toUri().toURL().toExternalForm().replace( '\\', '/' ).replaceFirst( "^/", "" );
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        ResourceResolver resolver = new ResourceResolver( absoluteBaseUri );
        Assert.assertEquals( absolutePathRoot + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest01A() throws MalformedURLException {
        String absolutePathRoot = Paths.get( "" ).toAbsolutePath().toUri().toURL().toExternalForm().replace( '\\', '/' ).replaceFirst( "^/", "" );
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        ResourceResolver resolver = new ResourceResolver( absoluteBaseUri );
        Assert.assertEquals( absolutePathRoot + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest02() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "test/folder/index.html" );
        String runFolder = Paths.get( "" ).toUri().toURL().toExternalForm();
        Assert.assertEquals( runFolder + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( runFolder + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( runFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( runFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest03() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "/test/folder/index.html" );
        String rootFolder = Paths.get( "" ).toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        Assert.assertEquals( rootFolder + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest04() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "index.html" );
        String runFolder = Paths.get( "" ).toUri().toURL().toExternalForm();
        Assert.assertEquals( runFolder + "innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( runFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( runFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest05() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "/../test/folder/index.html" );
        String rootFolder = Paths.get( "" ).toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        Assert.assertEquals( rootFolder + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( rootFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest06() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "../test/folder/index.html" );
        String parentFolder = Paths.get( "" ).toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        Assert.assertEquals( parentFolder + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( parentFolder + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( parentFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( parentFolder + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest07() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "http://itextpdf.com/itext7" );
        Assert.assertEquals( "http://itextpdf.com/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( "http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( "http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri( "//folder2.com/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest08() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "http://itextpdf.com/itext7/" );
        Assert.assertEquals( "http://itextpdf.com/itext7/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( "http://itextpdf.com/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( "http://folder2.com/innerTest2", resolver.resolveAgainstBaseUri( "//folder2.com/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest09() throws MalformedURLException {
        String absolutePathRoot = Paths.get( "" ).toAbsolutePath().getRoot().toString().replace( '\\', '/' );
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        ResourceResolver resolver = new ResourceResolver( absoluteBaseUri );
        String uriRoot = Paths.get( "" ).toAbsolutePath().getRoot().toUri().toURL().toExternalForm();
        Assert.assertEquals( uriRoot + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( uriRoot + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( uriRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( uriRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest13() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "" );
        String runFolder = Paths.get( "" ).toUri().toURL().toExternalForm();
        Assert.assertEquals( runFolder + "innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        String parentToRunFolder = Paths.get( "" ).toAbsolutePath().getParent().toUri().toURL().toExternalForm();
        Assert.assertEquals( parentToRunFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( runFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( runFolder + "folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest14() throws MalformedURLException {
        ResourceResolver resolver = new ResourceResolver( "base/uri/index.html" );
        String runFolder = Paths.get( "" ).toUri().toURL().toExternalForm();
        Assert.assertEquals( "file:/c:/test/folder/img.txt", resolver.resolveAgainstBaseUri( "file:/c:/test/folder/img.txt" ).toExternalForm() );
        Assert.assertEquals( "file://c:/test/folder/img.txt", resolver.resolveAgainstBaseUri( "file://c:/test/folder/img.txt" ).toExternalForm() );
        Assert.assertEquals( "file:/c:/test/folder/data.jpg", resolver.resolveAgainstBaseUri( "file:///c:/test/folder/data.jpg" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest15() throws MalformedURLException {
        String absolutePathRoot = "file:/" + Paths.get( "" ).toAbsolutePath().getRoot().toString().replace( '\\', '/' ).replaceFirst( "^/", "" );
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        ResourceResolver resolver = new ResourceResolver( absoluteBaseUri );
        Assert.assertEquals( absolutePathRoot + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( absolutePathRoot + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void resourceResolverTest16() throws MalformedURLException {
        String absolutePathRoot = "file:///" + Paths.get( "" ).toAbsolutePath().getRoot().toString().replace( '\\', '/' ).replaceFirst( "^/", "" );
        String absoluteBaseUri = absolutePathRoot + "test/folder/index.html";
        ResourceResolver resolver = new ResourceResolver( absoluteBaseUri );
        String singleSlashRootPath = absolutePathRoot.replace( "///", "/" );
        Assert.assertEquals( singleSlashRootPath + "test/folder/innerTest", resolver.resolveAgainstBaseUri( "innerTest" ).toExternalForm() );
        Assert.assertEquals( singleSlashRootPath + "test/folder2/innerTest2", resolver.resolveAgainstBaseUri( "../folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( singleSlashRootPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "/folder2/innerTest2" ).toExternalForm() );
        Assert.assertEquals( singleSlashRootPath + "test/folder/folder2/innerTest2", resolver.resolveAgainstBaseUri( "//folder2/innerTest2" ).toExternalForm() );
    }

    @Test
    public void retrieveStreamTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        byte[] expected = Files.readAllBytes( new File( baseUri+fileName ).toPath());
        ResourceResolver resourceResolver = new ResourceResolver( baseUri );
        byte[] stream = resourceResolver.retrieveStream( "resourceResolverTest.png" );
        Assert.assertNotNull( stream );
        Assert.assertEquals( expected.length, stream.length );
    }

    @Test
    public void retrieveStyleSheetTest() throws IOException {
        String fileName = "retrieveStyleSheetTest.css";

      InputStream expected = new FileInputStream(baseUri+fileName);
        ResourceResolver resourceResolver = new ResourceResolver( baseUri );
        InputStream stream = resourceResolver.retrieveStyleSheet( "retrieveStyleSheetTest.css" );
        Assert.assertNotNull( stream );
        Assert.assertEquals( expected.read(), stream.read() );
    }

    @Test
    public void retrieveImageTest() throws IOException {
        String fileName = "resourceResolverTest.png";
        ResourceResolver resourceResolver = new ResourceResolver( baseUri );
        PdfImageXObject image = resourceResolver.retrieveImage( fileName );
        Assert.assertNotNull( image );
        Assert.assertTrue( image.identifyImageFileExtension().equalsIgnoreCase( "png" ) );
    }


}
