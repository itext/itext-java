package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public interface ExternalDigest {
    MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException;
}
