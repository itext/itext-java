package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.RespID;

public class RespIDBC implements IRespID {
    private final RespID respID;

    public RespIDBC(RespID respID) {
        this.respID = respID;
    }

    public RespIDBC(IX500Name x500Name) {
        this(new RespID(((X500NameBC) x500Name).getX500Name()));
    }

    public RespID getRespID() {
        return respID;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(respID);
    }

    @Override
    public String toString() {
        return respID.toString();
    }
}
