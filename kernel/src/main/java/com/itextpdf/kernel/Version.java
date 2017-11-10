/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel;

import java.lang.reflect.Method;

/**
 * This class contains version information about iText.
 * DO NOT CHANGE THE VERSION INFORMATION WITHOUT PERMISSION OF THE COPYRIGHT HOLDERS OF ITEXT.
 * Changing the version makes it extremely difficult to debug an application.
 * Also, the nature of open source software is that you honor the copyright of the original creators of the software.
 */
public final class Version {

    /**
     * String that will indicate if the AGPL version is used.
     */
    private static String AGPL = " (AGPL-version)";

    /**
     * The iText version instance.
     */
    private static Version version = null;
    /**
     * This String contains the name of the product.
     * iText is a registered trademark by iText Group NV.
     * Please don't change this constant.
     */
    private static String iText = "iText\u00ae";
    /**
     * This String contains the version number of this iText release.
     * For debugging purposes, we request you NOT to change this constant.
     */
    private static String release = "7.1.0-SNAPSHOT";
    /**
     * This String contains the iText version as shown in the producer line.
     * iText is a product developed by iText Group NV.
     * iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     */
    private String iTextVersion = iText + " " + release + " \u00a92000-2017 iText Group NV";

    /**
     * The license key.
     */
    private String key = null;

    private boolean expired;

    /**
     * Gets an instance of the iText version that is currently used.
     * Note that iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     */
    public static Version getInstance() {
        if (version == null) {
            version = new Version();
            synchronized (version) {
                try {
                    String licenseeInfoMethodName = "getLicenseeInfoForVersion";
                    Class<?> klass = getLicenseKeyClass();
                    if (klass != null) {
                        Class[] cArg  = {String.class};
                        Method m = klass.getMethod(licenseeInfoMethodName,cArg);
                        String coreVersion = release;
                        Object[] args = {coreVersion};
                        String[] info = (String[]) m.invoke(klass.newInstance(), args);
                        if (info[3] != null && info[3].trim().length() > 0) {
                            version.key = info[3];
                        } else {
                            version.key = "Trial version ";
                            if (info[5] == null) {
                                version.key += "unauthorised";
                            } else {
                                version.key += info[5];
                            }
                        }

                        if (info.length > 6) {
                            if (info[6] != null && info[6].trim().length() > 0) {
                                if (! release.startsWith(info[6])) {
                                    throw new IllegalArgumentException("Your license key version doesn't match the iText version.");
                                }
                            }
                        }

                        if (info[4] != null && info[4].trim().length() > 0) {
                            version.iTextVersion = info[4];
                        } else if (info[2] != null && info[2].trim().length() > 0) {
                            version.addLicensedPostfix(info[2]);
                        } else if (info[0] != null && info[0].trim().length() > 0) {
                            // fall back to contact name, if company name is unavailable.
                            // we shouldn't have a licensed version without company name,
                            // but let's account for it anyway
                            version.addLicensedPostfix(info[0]);
                        } else {
                            version.addAGPLPostfix(null);
                        }
                    } else {
                        version.addAGPLPostfix(null);
                    }
                } catch (IllegalArgumentException iae) {
                    version.addAGPLPostfix(iae.getCause());
                } catch (Exception e) {
                    version.addAGPLPostfix(e.getCause());
                }
            }
        }
        return version;
    }

    /**
     * Checks if the AGPL version is used.
     * @return returns true if the AGPL version is used.
     */
    public static boolean isAGPLVersion() {
        return getInstance().getVersion().indexOf(AGPL) > 0;
    }

    /**
     * Is the license expired?
     * @return true if expired
     */
    public static boolean isExpired() {
        return getInstance().expired;
    }

    /**
     * Gets the product name.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return the product name
     */
    public String getProduct() {
        return iText;
    }

    /**
     * Gets the release number.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return the release number
     */
    public String getRelease() {
        return release;
    }

    /**
     * Returns the iText version as shown in the producer line.
     * iText is a product developed by iText Group NV.
     * iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return iText version
     */
    public String getVersion() {
        return iTextVersion;
    }

    /**
     * Returns a license key if one was provided, or null if not.
     *
     * @return a license key.
     */
    public String getKey() {
        return key;
    }

    private void addLicensedPostfix(String ownerName) {
        iTextVersion += " (" + ownerName;
        if (! key.toLowerCase().startsWith("trial")) {
            iTextVersion += "; licensed version)";
        } else {
            iTextVersion += "; " + key + ")";
        }
    }

    private void addAGPLPostfix(Throwable cause) {
        iTextVersion += AGPL;

        if (cause != null && cause.getMessage() != null && cause.getMessage().contains("expired")) {
            expired = true;
        }
    }

    private static Class<?> getLicenseKeyClass() throws ClassNotFoundException {
        String licenseKeyClassFullName = "com.itextpdf.licensekey.LicenseKey";
        return Class.forName(licenseKeyClassFullName);
    }

}
