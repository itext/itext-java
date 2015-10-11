package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.jcajce.provider.digest.MD2;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.RIPEMD128;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.RIPEMD256;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA224;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA384;
import org.bouncycastle.jcajce.provider.digest.SHA512;

/**
 * Implementation for digests accessed directly from the BouncyCastle library bypassing
 * any provider definition.
 */
public class BouncyCastleDigest implements ExternalDigest {

    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        String oid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);
        if (oid == null)
            throw new NoSuchAlgorithmException(hashAlgorithm);
        if (oid.equals("1.2.840.113549.2.2")) { //MD2
            return new MD2.Digest();
        }
        else if (oid.equals("1.2.840.113549.2.5")) { //MD5
            return new MD5.Digest();
        }
        else if (oid.equals("1.3.14.3.2.26")) { //SHA1
            return new SHA1.Digest();
        }
        else if (oid.equals("2.16.840.1.101.3.4.2.4")) { //SHA224
            return new SHA224.Digest();
        }
        else if (oid.equals("2.16.840.1.101.3.4.2.1")) { //SHA256
            return new SHA256.Digest();
        }
        else if (oid.equals("2.16.840.1.101.3.4.2.2")) { //SHA384
            return new SHA384.Digest();
        }
        else if (oid.equals("2.16.840.1.101.3.4.2.3")) { //SHA512
            return new SHA512.Digest();
        }
        else if (oid.equals("1.3.36.3.2.2")) { //RIPEMD128
            return new RIPEMD128.Digest();
        }
        else if (oid.equals("1.3.36.3.2.1")) { //RIPEMD160
            return new RIPEMD160.Digest();
        }
        else if (oid.equals("1.3.36.3.2.3")) { //RIPEMD256
            return new RIPEMD256.Digest();
        }
        else if (oid.equals("1.2.643.2.2.9")) { //GOST3411
            return new GOST3411.Digest();
        }

        throw new NoSuchAlgorithmException(hashAlgorithm); //shouldn't get here
    }
}
