package com.itextpdf.core.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Creates an AES Cipher with CBC and no padding.
 *
 * @author Paulo Soares
 */
public class AESCipherCBCnoPad {

    private BlockCipher cbc;

    /**
     * Creates a new instance of AESCipher
     */
    public AESCipherCBCnoPad(boolean forEncryption, byte[] key) {
        BlockCipher aes = new AESFastEngine();
        cbc = new CBCBlockCipher(aes);
        KeyParameter kp = new KeyParameter(key);
        cbc.init(forEncryption, kp);
    }

    public byte[] processBlock(byte[] inp, int inpOff, int inpLen) {
        if ((inpLen % cbc.getBlockSize()) != 0)
            throw new IllegalArgumentException("Not multiple of block: " + inpLen);
        byte[] outp = new byte[inpLen];
        int baseOffset = 0;
        while (inpLen > 0) {
            cbc.processBlock(inp, inpOff, outp, baseOffset);
            inpLen -= cbc.getBlockSize();
            baseOffset += cbc.getBlockSize();
            inpOff += cbc.getBlockSize();
        }
        return outp;
    }
}
