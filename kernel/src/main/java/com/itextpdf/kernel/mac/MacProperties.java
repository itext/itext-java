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
package com.itextpdf.kernel.mac;

/**
 * Class which contains configurable properties for MAC integrity protection mechanism.
 */
public class MacProperties {
    private final MacDigestAlgorithm macDigestAlgorithm;
    private final MacAlgorithm macAlgorithm;
    private final KeyWrappingAlgorithm keyWrappingAlgorithm;

    /**
     * Creates {@link MacProperties} class containing provided {@link MacDigestAlgorithm}.
     * For other properties default values are used.
     *
     * @param macDigestAlgorithm {@link MacDigestAlgorithm} to be used in MAC integrity protection algorithm
     */
    public MacProperties(MacDigestAlgorithm macDigestAlgorithm) {
        this(macDigestAlgorithm, MacAlgorithm.HMAC_WITH_SHA_256, KeyWrappingAlgorithm.AES_256_NO_PADD);
    }

    /**
     * Creates {@link MacProperties} class containing provided properties.
     *
     * @param macDigestAlgorithm {@link MacDigestAlgorithm} to be used in MAC integrity protection algorithm
     * @param macAlgorithm {@link MacAlgorithm} to be used in MAC integrity protection algorithm
     * @param keyWrappingAlgorithm {@link KeyWrappingAlgorithm} to be used in MAC integrity protection algorithm
     */
    public MacProperties(MacDigestAlgorithm macDigestAlgorithm, MacAlgorithm macAlgorithm,
            KeyWrappingAlgorithm keyWrappingAlgorithm) {
        this.macDigestAlgorithm = macDigestAlgorithm;
        this.macAlgorithm = macAlgorithm;
        this.keyWrappingAlgorithm = keyWrappingAlgorithm;
    }

    /**
     * Gets {@link MacDigestAlgorithm} to be used in MAC integrity protection algorithm.
     *
     * @return {@link MacDigestAlgorithm} to be used in MAC integrity protection algorithm
     */
    public MacDigestAlgorithm getMacDigestAlgorithm() {
        return macDigestAlgorithm;
    }

    /**
     * Gets {@link MacAlgorithm} to be used in MAC integrity protection algorithm.
     *
     * @return {@link MacAlgorithm} to be used in MAC integrity protection algorithm
     */
    public MacAlgorithm getMacAlgorithm() {
        return macAlgorithm;
    }

    /**
     * Gets {@link KeyWrappingAlgorithm} to be used in MAC integrity protection algorithm.
     *
     * @return {@link KeyWrappingAlgorithm} to be used in MAC integrity protection algorithm
     */
    public KeyWrappingAlgorithm getKeyWrappingAlgorithm() {
        return keyWrappingAlgorithm;
    }

    /**
     * Message digest algorithms, which can be used in MAC integrity protection algorithm.
     */
    public enum MacDigestAlgorithm {
        SHA_256,
        SHA_384,
        SHA_512,
        SHA3_256,
        SHA3_384,
        SHA3_512
    }

    /**
     * MAC algorithms, which can be used during integrity protection operation.
     */
    public enum MacAlgorithm {
        HMAC_WITH_SHA_256
    }

    /**
     * Key wrapping algorithms, which can be used in MAC integrity protection algorithm.
     */
    public enum KeyWrappingAlgorithm {
        AES_256_NO_PADD
    }
}
