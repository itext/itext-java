package com.itextpdf.kernel;

import java.lang.reflect.Method;

/**
 * This class contains version information about iText.
 * DO NOT CHANGE THE VERSION INFORMATION WITHOUT PERMISSION OF THE COPYRIGHT HOLDERS OF ITEXT.
 * Changing the version makes it extremely difficult to debug an application.
 * Also, the nature of open source software is that you honor the copyright of the original creators of the software.
 */
public final class Version {

    /** The iText version instance. */
    private static Version version = null;
    /**
     * This String contains the name of the product.
     * iText is a registered trademark by iText Group NV.
     * Please don't change this constant.
     */
    private String iText = "iText\u00ae";
    /**
     * This String contains the version number of this iText release.
     * For debugging purposes, we request you NOT to change this constant.
     */
    private String release = "7.0.0-SNAPSHOT";
    /**
     * This String contains the iText version as shown in the producer line.
     * iText is a product developed by iText Group NV.
     * iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     */
    private String iTextVersion = iText + " " + release + " \u00a92000-2016 iText Group NV";
    /**
     * The license key.
     */
    private String key = null;

    /**
     * Gets an instance of the iText version that is currently used.
     * Note that iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     */
    public static Version getInstance() {
        if (version == null) {
            version = new Version();
            synchronized ( version ) {
                try {
                    Class<?> klass = Class.forName("com.itextpdf.licensekey.LicenseKey");
                    Method m = klass.getMethod("getLicenseeInfo");
                    String[] info = (String[])m.invoke(klass.newInstance());
                    if (info[3] != null && info[3].trim().length() > 0) {
                        version.key = info[3];
                    } else {
                        version.key = "Trial version";
                        if (info[5] == null) {
                            version.key += "unauthorised";
                        } else {
                            version.key += info[5];
                        }
                    }

                    if (info[4] != null && info[4].trim().length() > 0) {
                        version.iTextVersion = info[4];
                    }  else if (info[2] != null && info[2].trim().length() > 0) {
                        version.iTextVersion += " (" + info[2];
                        if (!version.key.toLowerCase().startsWith("trial")) {
                            version.iTextVersion += "; licensed version)";
                        } else {
                            version.iTextVersion += "; " + version.key + ")";
                        }
                    } else if (info[0] != null && info[0].trim().length() > 0) {
                        // fall back to contact name, if company name is unavailable
                        version.iTextVersion += " (" + info[0];
                        if (!version.key.toLowerCase().startsWith("trial")) {
                            // we shouldn't have a licensed version without company name,
                            // but let's account for it anyway
                            version.iTextVersion += "; licensed version)";
                        } else {
                            version.iTextVersion += "; " + version.key + ")";
                        }
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    version.iTextVersion += "; AGPL";
                }
            }
        }
        return version;
    }

    /**
     * Gets the product name.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     * @return the product name
     */
    public String getProduct() {
        return iText;
    }

    /**
     * Gets the release number.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
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
     * @return iText version
     */
    public String getVersion() {
        return iTextVersion;
    }

    /**
     * Returns a license key if one was provided, or null if not.
     * @return a license key.
     */
    public String getKey() {
        return key;
    }
}

