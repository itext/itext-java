package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.RespID;

/**
 * Wrapper class for {@link RespID}.
 */
public class RespIDBC implements IRespID {
    private final RespID respID;

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param respID {@link RespID} to be wrapped
     */
    public RespIDBC(RespID respID) {
        this.respID = respID;
    }

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param x500Name X500Name wrapper to create {@link RespID}
     */
    public RespIDBC(IX500Name x500Name) {
        this(new RespID(((X500NameBC) x500Name).getX500Name()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RespID}.
     */
    public RespID getRespID() {
        return respID;
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
        RespIDBC that = (RespIDBC) o;
        return Objects.equals(respID, that.respID);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(respID);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return respID.toString();
    }
}
