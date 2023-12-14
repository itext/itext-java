/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class contains version information about iText.
 * DO NOT CHANGE THE VERSION INFORMATION WITHOUT PERMISSION OF THE COPYRIGHT HOLDERS OF ITEXT.
 * Changing the version makes it extremely difficult to debug an application.
 * Also, the nature of open source software is that you honor the copyright of the original creators of the software.
 */
public final class Version {

    /**
     * Lock object used for synchronization
     */
    private static final Object staticLock = new Object();

    /**
     * String that will indicate if the AGPL version is used.
     */
    private static final String AGPL = " (AGPL-version)";

    /**
     * The iText version instance.
     */
    private static volatile Version version = null;
    /**
     * This String contains the name of the product.
     * iText is a registered trademark by iText Group NV.
     * Please don't change this constant.
     */
    private static final String iTextProductName = "iText\u00ae";
    /**
     * This String contains the version number of this iText release.
     * For debugging purposes, we request you NOT to change this constant.
     */
    private static final String release = "7.1.19";
    /**
     * This String contains the iText version as shown in the producer line.
     * iText is a product developed by iText Group NV.
     * iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     */
    private static final String producerLine = iTextProductName + " " + release + " \u00a92000-2022 iText Group NV";

    /**
     * The version info;
     */
    private final VersionInfo info;

    private boolean expired;

    /**
     * @deprecated Use {@link Version#getInstance()} instead. Will be removed in next major release.
     */
    @Deprecated
    public Version() {
        this.info = new VersionInfo(iTextProductName, release, producerLine, null);
    }

    Version(VersionInfo info, boolean expired) {
        this.info = info;
        this.expired = expired;
    }

    /**
     * Gets an instance of the iText version that is currently used.
     *
     * <p>
     * Note that iText Group requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     * @return an instance of {@link Version}.
     */
    public static Version getInstance() {
        Version localVersion = version;
        // It's crucial to work with 'localVersion' local variable, because 'version' field can be
        // changed by some other thread. We also don't want to block Version class lock when calling
        // for scheduled check in order to avoid synchronization issues with parallel loading license.
        if (localVersion != null) {
            try {
                licenseScheduledCheck(localVersion);
                return localVersion;
            } catch (Exception e) {
                // If any exception occurs during scheduled check of core license it means that
                // license is not valid, in this particular case we want to reset to AGPL Version,
                // however "normal" initialization logic will not switch to AGPL unless license is
                // unloaded.

                // not saving this AGPL version in order to avoid race condition with loaded proper license
                return initAGPLVersion(e, null);
            }
        }
        String key = null;
        try {
            String coreVersion = release;
            String[] info = getLicenseeInfoFromLicenseKey(coreVersion);
            if(info != null){
                if (info[3] != null && info[3].trim().length() > 0) {
                    key = info[3];
                } else {
                    key = "Trial version ";
                    if (info[5] == null) {
                        key += "unauthorised";
                    } else {
                        key += info[5];
                    }
                }

                if (info.length > 6) {
                    if (info[6] != null && info[6].trim().length() > 0) {
                        //Compare versions with this release versions
                        checkLicenseVersion(coreVersion, info[6]);
                    }
                }

                if (info[4] != null && info[4].trim().length() > 0) {
                    localVersion = initVersion(info[4], key, false);
                } else if (info[2] != null && info[2].trim().length() > 0) {
                    localVersion = initDefaultLicensedVersion(info[2], key);
                } else if (info[0] != null && info[0].trim().length() > 0) {
                    // fall back to contact name, if company name is unavailable.
                    // we shouldn't have a licensed version without company name,
                    // but let's account for it anyway
                    localVersion = initDefaultLicensedVersion(info[0], key);
                } else {
                    localVersion = initAGPLVersion(null, key);
                }
            } else {
                localVersion = initAGPLVersion(null, key);
            }
            //Catch the exception
        } catch(LicenseVersionException lve) {
            //Rethrow license version exceptions
            throw lve;
        }catch(ClassNotFoundException cnfe){
            //License key library not on classpath, switch to AGPL
            localVersion = initAGPLVersion(null, key);
        } catch (Exception e) {
            //Check if an iText5 license is loaded
            if(e.getCause() != null && e.getCause().getMessage().equals(LicenseVersionException.LICENSE_FILE_NOT_LOADED)) {
                if (isiText5licenseLoaded()) {
                    throw new LicenseVersionException(LicenseVersionException.NO_I_TEXT7_LICENSE_IS_LOADED_BUT_AN_I_TEXT5_LICENSE_IS_LOADED);
                }
            }
            localVersion = initAGPLVersion(e.getCause(), key);
        }
        return atomicSetVersion(localVersion);
    }

    /**
     * Checks if the AGPL version is used.
     * @return returns true if the AGPL version is used.
     */
    public static boolean isAGPLVersion() {
        return getInstance().isAGPL();
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
        return info.getProduct();
    }

    /**
     * Gets the release number.
     * iText Group NV requests that you retain the iText producer line
     * in every PDF that is created or manipulated using iText.
     *
     * @return the release number
     */
    public String getRelease() {
        return info.getRelease();
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
        return info.getVersion();
    }

    /**
     * Returns a license key if one was provided, or null if not.
     *
     * @return a license key.
     */
    public String getKey() {
        return info.getKey();
    }

    /**
     * Returns a version info in one class
     *
     * @return a version info.
     */
    public VersionInfo getInfo() {
        return info;
    }

    static String[] parseVersionString(String version) {
        String splitRegex = "\\.";
        String[] split = version.split(splitRegex);
        //Guard for empty versions and throw exceptions
        if (split.length == 0) {
            throw new LicenseVersionException(LicenseVersionException.VERSION_STRING_IS_EMPTY_AND_CANNOT_BE_PARSED);
        }
        //Desired Format: X.Y.Z-....
        //Also catch X, X.Y-...
        String major = split[0];
        //If no minor version is present, default to 0
        String minor = "0";
        if (split.length > 1) {
            minor = split[1].substring(0);
        }
        //Check if both values are numbers
        if (!isVersionNumeric(major)) {
            throw new LicenseVersionException(LicenseVersionException.MAJOR_VERSION_IS_NOT_NUMERIC);
        }
        if (!isVersionNumeric(minor)) {
            throw new LicenseVersionException(LicenseVersionException.MINOR_VERSION_IS_NOT_NUMERIC);
        }
        return new String[] {major, minor};
    }

    static boolean isVersionNumeric(String version) {
        try {
            int value = (int) Integer.parseInt(version);
            // parseInt accepts numbers which start with a plus sign, but for a version it's unacceptable
            return value >= 0 && !version.contains("+");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the current object has been initialized with AGPL license.
     *
     * @return returns true if the current object has been initialized with AGPL license.
     */
    boolean isAGPL() {
        return getVersion().indexOf(AGPL) > 0;
    }

    private static Version initDefaultLicensedVersion(String ownerName, String key) {
        String producer = producerLine + " (" + ownerName;
        if (! key.toLowerCase().startsWith("trial")) {
            producer += "; licensed version)";
        } else {
            producer += "; " + key + ")";
        }
        return initVersion(producer, key, false);
    }

    private static Version initAGPLVersion(Throwable cause, String key) {
        String producer = producerLine + AGPL;

        boolean expired = cause != null && cause.getMessage() != null && cause.getMessage().contains("expired");

        return initVersion(producer, key, expired);
    }

    private static Version initVersion(String producer, String key, boolean expired) {
        return new Version(new VersionInfo(iTextProductName, release, producer, key), expired);
    }

    private static Class<?> getLicenseKeyClass() throws ClassNotFoundException {
        String licenseKeyClassFullName = "com.itextpdf.licensekey.LicenseKey";
        return getClassFromLicenseKey(licenseKeyClassFullName);
    }

    private static Class<?> getClassFromLicenseKey(String classFullName) throws ClassNotFoundException {
        return Class.forName(classFullName);
    }

    private static void checkLicenseVersion(String coreVersionString, String licenseVersionString){
        String[] coreVersions = parseVersionString(coreVersionString);
        String[] licenseVersions = parseVersionString(licenseVersionString);

        int coreMajor = Integer.parseInt(coreVersions[0]);
        int coreMinor = Integer.parseInt(coreVersions[1]);

        int licenseMajor = Integer.parseInt(licenseVersions[0]);
        int licenseMinor = Integer.parseInt(licenseVersions[1]);
        //Major version check
        if(licenseMajor < coreMajor){
            throw new LicenseVersionException(LicenseVersionException.THE_MAJOR_VERSION_OF_THE_LICENSE_0_IS_LOWER_THAN_THE_MAJOR_VERSION_1_OF_THE_CORE_LIBRARY).setMessageParams(licenseMajor,coreMajor);
        }
        if(licenseMajor>coreMajor){
            throw new LicenseVersionException(LicenseVersionException.THE_MAJOR_VERSION_OF_THE_LICENSE_0_IS_HIGHER_THAN_THE_MAJOR_VERSION_1_OF_THE_CORE_LIBRARY).setMessageParams(licenseMajor,coreMajor);

        }
        //Minor version check
        if(licenseMinor < coreMinor){
            throw new LicenseVersionException(LicenseVersionException.THE_MINOR_VERSION_OF_THE_LICENSE_0_IS_LOWER_THAN_THE_MINOR_VERSION_1_OF_THE_CORE_LIBRARY).setMessageParams(licenseMinor,coreMinor);
        }

    }

    private static String[] getLicenseeInfoFromLicenseKey(String validatorKey) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String licenseeInfoMethodName = "getLicenseeInfoForVersion";
        Class<?> klass = getLicenseKeyClass();
        if (klass != null) {
            Class[] cArg = {String.class};
            Method m = klass.getMethod(licenseeInfoMethodName, cArg);
            Object[] args = {validatorKey};
            String[] info = (String[]) m.invoke(klass.newInstance(), args);
            return info;
        }
        return null;
    }

    private static boolean isiText5licenseLoaded() {
        String validatorKey5 = "5";
        boolean result = false;
        try {
            String[] info = getLicenseeInfoFromLicenseKey(validatorKey5);
            result = true;
        } catch (Exception ignore) {

        }
        return result;
    }

    private static Version atomicSetVersion(Version newVersion) {
        synchronized (staticLock) {
            version = newVersion;
            return version;
        }
    }

    private static void licenseScheduledCheck(Version localVersion) {
        if (localVersion.isAGPL()) {
            return;
        }

        String licenseKeyProductFullName = "com.itextpdf.licensekey.LicenseKeyProduct";
        String checkLicenseKeyMethodName = "scheduledCheck";
        try {
            Class<?> licenseKeyClass = getLicenseKeyClass();
            Class<?> licenseKeyProductClass = getClassFromLicenseKey(licenseKeyProductFullName);

            Class[] cArg = {licenseKeyProductClass};
            Method method = licenseKeyClass.getMethod(checkLicenseKeyMethodName, cArg);
            method.invoke(null, new Object[]{null});
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
