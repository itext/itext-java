package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Objects;
import org.bouncycastle.operator.ContentSigner;

public class ContentSignerBCFips implements IContentSigner {
    private final ContentSigner contentSigner;

    public ContentSignerBCFips(ContentSigner contentSigner) {
        this.contentSigner = contentSigner;
    }

    public ContentSigner getContentSigner() {
        return contentSigner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentSignerBCFips that = (ContentSignerBCFips) o;
        return Objects.equals(contentSigner, that.contentSigner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentSigner);
    }

    @Override
    public String toString() {
        return contentSigner.toString();
    }
}
