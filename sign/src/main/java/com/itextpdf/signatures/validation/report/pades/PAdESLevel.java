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
