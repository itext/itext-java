package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import org.bouncycastle.cms.SignerInformationVerifier;

public class SignerInformationVerifierBC implements ISignerInformationVerifier {
    private final SignerInformationVerifier verifier;

    public SignerInformationVerifierBC(SignerInformationVerifier verifier) {
        this.verifier = verifier;
    }

    public SignerInformationVerifier getVerifier() {
        return verifier;
    }
}
