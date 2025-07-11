package com.itextpdf.signatures.validation.lotl;

import java.util.HashSet;
import java.util.Set;

class AdditionalServiceInformationExtension {

    private static final Set<String> invalidScopes = new HashSet<>();

    static {
        invalidScopes.add("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication");
    }

    private String uri;

    AdditionalServiceInformationExtension() {
        // Empty constructor.
    }

    AdditionalServiceInformationExtension (String uri) {
        this.uri = uri;
    }

    String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    boolean isScopeValid() {
        return !invalidScopes.contains(uri);
    }
}
