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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.kernel.crypto.OID;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * Class representing "Key Usage" extenstion.
 */
public class KeyUsageExtension extends CertificateExtension {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    public static final String EXPECTED_VALUE =
            "Key usage expected: ({0})";
    public static final String ACTUAL_VALUE = "\nbut found {0}";
    public static final String MISSING_VALUE = "\nbut nothing found.";

    private final int keyUsage;
    private final boolean resultOnMissingExtension;
    private String messagePreAmble;
    private String message;

    /**
     * Create new {@link KeyUsageExtension} instance using provided {@code int} flag.
     *
     * @param keyUsage {@code int} flag which represents bit values for key usage value
     *                             bit strings are stored with the big-endian byte order and padding on the end,
     *                             the big endian notation causes a shift in actual integer values for
     *                             bits 1-8 becoming 0-7 and bit 1
     *                             the 7 bits padding makes for bit 0 to become bit 7 of the first byte
     */
    public KeyUsageExtension(int keyUsage) {
        this(keyUsage, false);
    }

    /**
     * Create new {@link KeyUsageExtension} instance using provided {@code int} flag.
     *
     * @param keyUsage                 {@code int} flag which represents bit values for key usage value
     *                                 bit strings are stored with the big-endian byte order and padding on the end,
     *                                 the big endian notation causes a shift in actual integer values for bits 1-8
     *                                 becoming 0-7 and bit 1
     *                                 the 7 bits padding makes for bit 0 to become bit 7 of the first byte
     * @param resultOnMissingExtension parameter which represents return value for
     * {@link #existsInCertificate(X509Certificate)} method in case of the extension not being present in a certificate
     */
    public KeyUsageExtension(int keyUsage, boolean resultOnMissingExtension) {
        super(OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(keyUsage).toASN1Primitive());
        this.keyUsage = keyUsage;
        this.resultOnMissingExtension = resultOnMissingExtension;
        messagePreAmble =MessageFormatUtil.format(EXPECTED_VALUE , convertKeyUsageMaskToString(keyUsage));
        message = messagePreAmble;
    }

    /**
     * Create new {@link KeyUsageExtension} instance using provided key usage enum list.
     *
     * @param keyUsages key usages {@link List} which represents key usage values
     */
    public KeyUsageExtension(List<KeyUsage> keyUsages) {
        this(keyUsages, false);
    }

    /**
     * Create new {@link KeyUsageExtension} instance using provided key usage enum list.
     *
     * @param keyUsages                key usages {@link List} which represents key usage values
     * @param resultOnMissingExtension parameter which represents return value for
     * {@link #existsInCertificate(X509Certificate)} method in case of the extension not being present in a certificate
     */
    public KeyUsageExtension(List<KeyUsage> keyUsages, boolean resultOnMissingExtension) {
        this(convertKeyUsageSetToInt(keyUsages), resultOnMissingExtension);
    }

    /**
     * Create new {@link KeyUsageExtension} instance using provided single key usage enum value.
     *
     * @param keyUsageValue {@link KeyUsage} which represents single key usage enum value
     */
    public KeyUsageExtension(KeyUsage keyUsageValue) {
        this(Collections.singletonList(keyUsageValue), false);
    }

    /**
     * Create new {@link KeyUsageExtension} instance using provided single key usage enum value.
     *
     * @param keyUsageValue {@link KeyUsage} which represents single key usage enum value
     * @param resultOnMissingExtension parameter which represents return value for
     * {@link #existsInCertificate(X509Certificate)} method in case of the extension not being present in a certificate
     */
    public KeyUsageExtension(KeyUsage keyUsageValue, boolean resultOnMissingExtension) {
        this(Collections.singletonList(keyUsageValue), resultOnMissingExtension);
    }

    /**
     * Check if this extension is present in the provided certificate. In case of {@link KeyUsageExtension},
     * check if this key usage bit values are present in certificate. Other values may be present as well.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if this key usage bit values are present in certificate, {@code false} otherwise
     */
    @Override
    public boolean existsInCertificate(X509Certificate certificate) {
        boolean[] providedKeyUsageFlags = certificate.getKeyUsage();
        if (providedKeyUsageFlags == null) {
            // By default, we want to return true if extension is not specified. Configurable.
            message = messagePreAmble + MISSING_VALUE;
            return resultOnMissingExtension;
        }
        int bitmap = 0;
        // bit strings are stored with the big-endian byte order and padding on the end,
        // the big endian notation causes a shift in actual integer values for bits 1-8 becoming 0-7 and bit 1
        // the 7 bits padding makes for bit 0 to become bit 7 of the first byte
        for (int i = 0; i < providedKeyUsageFlags.length - 1; ++i) {
            if (providedKeyUsageFlags[i]) {
                bitmap +=  1 << (8-i-1);
            }
        }
        if (providedKeyUsageFlags[8]) {
            bitmap +=  0x8000;
        }
        if ((bitmap & keyUsage) != keyUsage) {
            message = new StringBuilder(messagePreAmble).append(
                    MessageFormatUtil.format(ACTUAL_VALUE, convertKeyUsageMaskToString(bitmap)))
                    .toString();
            return false;
        }
        return true;
    }
    @Override
    public String getMessage() {
        return message;
    }

    private static String convertKeyUsageMaskToString(int keyUsageMask) {
        StringBuilder result = new StringBuilder();
        String separator = "";
        // bit strings are stored with the big-endian byte order and padding on the end,
        // the big endian notation causes a shift in actual integer values for bits 1-8 becoming 0-7 and bit 1
        // the 7 bits padding makes for bit 0 to become bit 7 of the first byte
        for (KeyUsage usage: EnumUtil.getAllValuesOfEnum(KeyUsage.class)) {
            if (((1 << (8-usage.ordinal()-1)) & keyUsageMask) > 0 ||
                    (usage == KeyUsage.DECIPHER_ONLY &&  (keyUsageMask & 0x8000) == 0x8000)) {
                result.append(separator)
                        .append(usage);
                separator = ", ";
            }
        }
        return result.toString();
    }
    private static int convertKeyUsageSetToInt(Iterable<KeyUsage> keyUsages) {
        int keyUsageMask = 0;
        // bit strings are stored with the big-endian byte order and padding on the end,
        // the big endian notation causes a shift in actual integer values for bits 1-8 becoming 0-7 and bit 1
        // the 7 bits padding makes for bit 0 to become bit 7 of the first byte
        for (KeyUsage usage: keyUsages) {
            if (usage == KeyUsage.DECIPHER_ONLY) {
                keyUsageMask += 0x8000;
                continue;
            }
            keyUsageMask +=  1 << ( 8 - usage.ordinal() - 1);
        }
        return keyUsageMask;
    }
}
