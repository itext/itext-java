package com.itextpdf.kernel.security;

import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;

/**
 * Interface to externalize the retrieval of the CMS recipient info.
 */
public interface ExternalDecryptionProcess {

    /**
     * Returns the ID of the CMS recipient.
     *
     * @return ID of the CMS Recipient
     */
    RecipientId getCmsRecipientId();

    /**
     * Returns the CMS recipient
     *
     * @return CMS Recipient
     */
    Recipient getCmsRecipient();

}
