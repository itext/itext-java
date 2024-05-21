package com.itextpdf.signatures;

/**
 * Access permissions value to be set to certification signature as a part of DocMDP configuration.
 */
public enum AccessPermissions {
    /**
     * Unspecified access permissions value which makes signature "approval" rather than "certification".
     */
    UNSPECIFIED,
    /**
     * Access permissions level 1 which indicates that no changes are permitted except for DSS and DTS creation.
     */
    NO_CHANGES_PERMITTED,
    /**
     * Access permissions level 2 which indicates that permitted changes, with addition to level 1, are:
     * filling in forms, instantiating page templates, and signing.
     */
    FORM_FIELDS_MODIFICATION,
    /**
     * Access permissions level 3 which indicates that permitted changes, with addition to level 2, are:
     * annotation creation, deletion and modification.
     */
    ANNOTATION_MODIFICATION
}
