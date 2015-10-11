package com.itextpdf.signatures;

import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;

public interface ExternalDecryptionProcess {

    RecipientId getCmsRecipientId();

    Recipient getCmsRecipient();

}
