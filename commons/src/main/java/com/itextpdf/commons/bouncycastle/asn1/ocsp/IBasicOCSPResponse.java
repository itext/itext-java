package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

import java.text.ParseException;
import java.util.Date;

/**
 * This interface represents the wrapper for BasicOCSPResponse that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IBasicOCSPResponse extends IASN1Encodable {
    /**
     * Gets TbsResponseData for the wrapped BasicOCSPResponse object
     * and calls actual {@code getProducedAt} method, then gets Date.
     *
     * @return produced at date.
     */
    Date getProducedAtDate() throws ParseException;
}
