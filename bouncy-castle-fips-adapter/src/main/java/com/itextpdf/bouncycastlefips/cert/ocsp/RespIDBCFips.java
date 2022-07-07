package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.RespID;

public class RespIDBCFips implements IRespID {
    private final RespID respID;

    public RespIDBCFips(RespID respID) {
        this.respID = respID;
    }

    public RespIDBCFips(IX500Name x500Name) {
        this(new RespID(((X500NameBCFips) x500Name).getX500Name()));
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
        RespIDBCFips that = (RespIDBCFips) o;
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
