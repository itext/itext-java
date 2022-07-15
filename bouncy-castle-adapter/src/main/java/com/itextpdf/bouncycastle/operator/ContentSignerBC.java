package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Objects;
import org.bouncycastle.operator.ContentSigner;

/**
 * Wrapper class for {@link ContentSigner}.
 */
public class ContentSignerBC implements IContentSigner {
    private final ContentSigner contentSigner;

    /**
     * Creates new wrapper instance for {@link ContentSigner}.
     *
     * @param contentSigner {@link ContentSigner} to be wrapped
     */
    public ContentSignerBC(ContentSigner contentSigner) {
        this.contentSigner = contentSigner;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ContentSigner}.
     */
    public ContentSigner getContentSigner() {
        return contentSigner;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentSignerBC that = (ContentSignerBC) o;
        return Objects.equals(contentSigner, that.contentSigner);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(contentSigner);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return contentSigner.toString();
    }
}
