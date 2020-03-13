/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class VersionTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void parseCurrentVersionTest() {
        Version instance = Version.getInstance();

        // expected values
        String release = instance.getRelease();
        String major = "7";
        String minor = release.split("\\.")[1];

        String[] parseResults = Version.parseVersionString(release);

        Assert.assertEquals(2, parseResults.length);
        Assert.assertEquals(major, parseResults[0]);
        Assert.assertEquals(minor, parseResults[1]);
    }

    @Test
    public void parseCustomCorrectVersionTest() {
        Version customVersion = new Version(new VersionInfo("iText®", "7.5.1-SNAPSHOT",
                "iText® 7.5.1-SNAPSHOT ©2000-2090 iText Group NV (AGPL-version)", null), false);

        // expected values
        String major = "7";
        String minor = "5";

        String[] parseResults = Version.parseVersionString(customVersion.getRelease());

        Assert.assertEquals(2, parseResults.length);
        Assert.assertEquals(major, parseResults[0]);
        Assert.assertEquals(minor, parseResults[1]);
    }

    @Test
    public void parseVersionIncorrectMajorTest() {
        junitExpectedException.expect(LicenseVersionException.class);
        junitExpectedException.expectMessage(LicenseVersionException.MAJOR_VERSION_IS_NOT_NUMERIC);

        // the line below is expected to produce an exception
        String[] parseResults = Version.parseVersionString("a.9.11");
    }

    @Test
    public void parseVersionIncorrectMinorTest() {
        junitExpectedException.expect(LicenseVersionException.class);
        junitExpectedException.expectMessage(LicenseVersionException.MINOR_VERSION_IS_NOT_NUMERIC);

        // the line below is expected to produce an exception
        Version.parseVersionString("1.a.11");
    }

    @Test
    public void isVersionNumericPositiveIntegerTest() {
        Assert.assertTrue(Version.isVersionNumeric("7"));
    }

    @Test
    public void isVersionNumericNegativeIntegerTest() {
        Assert.assertFalse(Version.isVersionNumeric("-7"));
    }

    @Test
    public void isVersionNumericPositiveFloatTest() {
        Assert.assertFalse(Version.isVersionNumeric("5.973"));
    }

    @Test
    public void isVersionNumericNegativeFloatTest() {
        Assert.assertFalse(Version.isVersionNumeric("-5.973"));
    }

    @Test
    public void isVersionNumericLetterTest() {
        Assert.assertFalse(Version.isVersionNumeric("a"));
    }

    @Test
    public void isAGPLVersionTest() {
        Assert.assertTrue(Version.isAGPLVersion());
    }

    @Test
    public void isAGPLTrueTest() {
        Version customVersion = new Version(new VersionInfo("iText®", "7.5.1-SNAPSHOT",
                "iText® 7.5.1-SNAPSHOT ©2000-2090 iText Group NV (AGPL-version)", null), false);
        Assert.assertTrue(customVersion.isAGPL());
    }

    @Test
    public void isAGPLFalseTest() {
        Version customVersion = new Version(
                new VersionInfo("iText®", "7.5.1-SNAPSHOT", "iText® 7.5.1-SNAPSHOT ©2000-2090 iText Group NV", null),
                false);
        Assert.assertFalse(customVersion.isAGPL());
    }

    @Test
    public void isExpiredTest() {
        Assert.assertFalse(Version.isExpired());
    }

    @Test
    public void getInstanceTest() {
        Version instance = Version.getInstance();
        checkVersionInstance(instance);
    }

    @Test
    public void customVersionCorrectTest() {
        Version customVersion = new Version(
                new VersionInfo("iText®", "7.5.1-SNAPSHOT", "iText® 7.5.1-SNAPSHOT ©2000-2090 iText Group NV", null),
                false);
        checkVersionInstance(customVersion);
    }

    @Test
    public void customVersionIncorrectMajorTest() {
        Version customVersion = new Version(
                new VersionInfo("iText®", "8.5.1-SNAPSHOT", "iText® 8.5.1-SNAPSHOT ©2000-2090 iText Group NV", null),
                false);
        Assert.assertFalse(checkVersion(customVersion.getVersion()));
    }

    @Test
    public void customVersionIncorrectMinorTest() {
        Version customVersion = new Version(
                new VersionInfo("iText®", "7.a.1-SNAPSHOT", "iText® 7.a.1-SNAPSHOT ©2000-2090 iText Group NV", null),
                false);
        Assert.assertFalse(checkVersion(customVersion.getVersion()));
    }

    @Test
    public void customVersionIncorrectPatchTest() {
        Version customVersion = new Version(
                new VersionInfo("iText®", "7.50.a-SNAPSHOT", "iText® 7.50.a-SNAPSHOT ©2000-2090 iText Group NV", null),
                false);
        Assert.assertFalse(checkVersion(customVersion.getVersion()));
    }

    private static void checkVersionInstance(Version instance) {
        String product = instance.getProduct();
        String release = instance.getRelease();
        String version = instance.getVersion();
        String key = instance.getKey();
        VersionInfo info = instance.getInfo();

        Assert.assertEquals(product, info.getProduct());
        Assert.assertEquals("iText®", product);

        Assert.assertEquals(release, info.getRelease());
        Assert.assertTrue(release.matches("[7]\\.[0-9]+\\.[0-9]+(-SNAPSHOT)?$"));

        Assert.assertEquals(version, info.getVersion());

        Assert.assertTrue(checkVersion(version));

        Assert.assertNull(key);
    }

    private static boolean checkVersion(String version) {
        String regexp = "iText\\u00ae [7]\\.[0-9]+\\.[0-9]+(-SNAPSHOT)? \\u00a92000-20([2-9][0-9]) "
                + "iText Group NV( \\(AGPL-version\\))?";
        return version.matches(regexp);
    }
}
