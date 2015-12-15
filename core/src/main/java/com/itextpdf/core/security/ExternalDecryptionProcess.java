package com.itextpdf.core.security;

import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;

public interface ExternalDecryptionProcess {

    RecipientId getCmsRecipientId();

    Recipient getCmsRecipient();

}

