package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import org.bouncycastle.cms.SignerInformationVerifier;

public class SignerInformationVerifierBCFips implements ISignerInformationVerifier {
    private final SignerInformationVerifier verifier;
    
    public SignerInformationVerifierBCFips(SignerInformationVerifier verifier) {
        this.verifier = verifier;
    }

    public SignerInformationVerifier getVerifier() {
        return verifier;
    }
}
