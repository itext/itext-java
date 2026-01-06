/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.report.pades;

/**
 * This enumeration holds all possible PAdES levels plus none and indeterminate, needed for when
 * none if the levels is reached or a signature is invalid.
 */
public enum PAdESLevel {
    /**
     * None of the levels criteria where met
     */
    NONE,
    /**
     * Unable to establish the PAdES level
     */
    INDETERMINATE,
    /**
     * B-B level provides requirements for the incorporation of signed and some unsigned attributes when the
     * signature is generated.
     */
    B_B,
    /**
     * B-T level provides requirements for the generation and inclusion, for an existing signature, of a trusted token
     * proving that the signature itself actually existed at a certain date and time.
     */
    B_T,
    /**
     * B-LT level provides requirements for the incorporation of all the material required for validating the signature
     * in the signature document. This level aims to tackle the long term availability of the validation material.
     */
    B_LT,
    /**
     * B-LTA level provides requirements for the incorporation of electronic timestamps that allow validation of the
     * signature long time after its generation. This level aims to tackle the long term availability and integrity of
     * the validation material.
     */
    B_LTA
}
