/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;
import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time Stamp Authority Client interface implementation using Bouncy Castle
 * org.bouncycastle.tsp package.
 * <p>
 * Created by Aiken Sam, 2006-11-15, refactored by Martin Brunecky, 07/15/2007
 * for ease of subclassing.
 */
public class TSAClientBouncyCastle implements ITSAClient {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    /**
     * The default value for the hash algorithm
     */
    public static final String DEFAULTHASHALGORITHM = "SHA-256";
    /**
     * The default value for token size estimation.
     */
    public static final int DEFAULTTOKENSIZE = 10240;
    /**
     * The Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TSAClientBouncyCastle.class);
    /**
     * URL of the Time Stamp Authority
     */
    protected String tsaURL;
    /**
     * TSA Username
     */
    protected String tsaUsername;
    /**
     * TSA password
     */
    protected String tsaPassword;
    /**
     * An interface that allows you to inspect the timestamp info.
     */
    protected ITSAInfoBouncyCastle tsaInfo;
    /**
     * Estimate of the received time stamp token
     */
    protected int tokenSizeEstimate = DEFAULTTOKENSIZE;
    /**
     * Hash algorithm
     */
    protected String digestAlgorithm = DEFAULTHASHALGORITHM;

    /**
     * TSA request policy
     */
    private String tsaReqPolicy;
    
    private int customTokenSizeEstimate = -1;

    /**
     * Creates an instance of a TSAClient that will use BouncyCastle.
     *
     * @param url String - Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     */
    public TSAClientBouncyCastle(String url) {
        this(url, null, null);
    }

    /**
     * Creates an instance of a TSAClient that will use BouncyCastle.
     *
     * @param url      String - Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     * @param username String - user(account) name
     * @param password String - password
     */
    public TSAClientBouncyCastle(String url, String username, String password) {
        this.tsaURL = url;
        this.tsaUsername = username;
        this.tsaPassword = password;
    }

    /**
     * Constructor.
     * Note the token size estimate is updated by each call, as the token
     * size is not likely to change (as long as we call the same TSA using
     * the same imprint length).
     *
     * @param url             Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     * @param username        user(account) name, optional
     * @param password        password, optional if used in combination with username, the credentials will be used in
     *                        basic authentication. Use only in combination with a https url to ensure encryption
     * @param tokSzEstimate   estimated size of received time stamp token (DER encoded)
     * @param digestAlgorithm is a hash algorithm
     */
    public TSAClientBouncyCastle(String url, String username, String password, int tokSzEstimate, String digestAlgorithm) {
        this.tsaURL = url;
        this.tsaUsername = username;
        this.tsaPassword = password;
        this.customTokenSizeEstimate = tokSzEstimate;
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * @param tsaInfo the tsaInfo to set
     */
    public void setTSAInfo(ITSAInfoBouncyCastle tsaInfo) {
        this.tsaInfo = tsaInfo;
    }

    /**
     * Get the token size estimate.
     * Returned value reflects the result of the last succesfull call, padded
     *
     * @return an estimate of the token size
     */
    @Override
    public int getTokenSizeEstimate() {
        return customTokenSizeEstimate == -1 ? tokenSizeEstimate : customTokenSizeEstimate;
    }

    /**
     * Gets the TSA request policy that will be used when retrieving timestamp token.
     *
     * @return policy id, or <code>null</code> if not set
     */
    public String getTSAReqPolicy() {
        return tsaReqPolicy;
    }

    /**
     * Sets the TSA request policy that will be used when retrieving timestamp token.
     *
     * @param tsaReqPolicy policy id
     */
    public void setTSAReqPolicy(String tsaReqPolicy) {
        this.tsaReqPolicy = tsaReqPolicy;
    }

    /**
     * Gets the MessageDigest to digest the data imprint
     *
     * @return the digest algorithm name
     *
     * @throws GeneralSecurityException if digestAlgorithm doesn't match any known hash algorithm
     */
    @Override
    public MessageDigest getMessageDigest() throws GeneralSecurityException {
        return SignUtils.getMessageDigest(digestAlgorithm);
    }

    /**
     * Get RFC 3161 timeStampToken.
     * Method may return null indicating that timestamp should be skipped.
     *
     * @param imprint data imprint to be time-stamped
     *
     * @return encoded, TSA signed data of the timeStampToken
     * @throws IOException if I/O error occurs
     * @throws AbstractTSPException if the TSA response is malformed
     */
    @Override
    public byte[] getTimeStampToken(byte[] imprint)
            throws IOException, AbstractTSPException {
        byte[] respBytes = null;
        // Setup the time stamp request
        ITimeStampRequestGenerator tsqGenerator = BOUNCY_CASTLE_FACTORY.createTimeStampRequestGenerator();
        tsqGenerator.setCertReq(true);
        if (tsaReqPolicy != null && tsaReqPolicy.length() > 0) {
            tsqGenerator.setReqPolicy(tsaReqPolicy);
        }
        // tsqGenerator.setReqPolicy("1.3.6.1.4.1.601.10.3.1");
        BigInteger nonce = BigInteger.valueOf(SystemUtil.getTimeBasedSeed());
        ITimeStampRequest request = tsqGenerator.generate(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                DigestAlgorithms.getAllowedDigest(digestAlgorithm)), imprint, nonce);
        byte[] requestBytes = request.getEncoded();

        // Call the communications layer
        respBytes = getTSAResponse(requestBytes);

        // Handle the TSA response
        ITimeStampResponse response = BOUNCY_CASTLE_FACTORY.createTimeStampResponse(respBytes);

        // validate communication level attributes (RFC 3161 PKIStatus)
        response.validate(request);
        IPKIFailureInfo failure = response.getFailInfo();
        int value = failure.isNull() ? 0 : failure.intValue();
        if (value != 0) {
            throw new PdfException(SignExceptionMessageConstant.INVALID_TSA_RESPONSE)
                    .setMessageParams(tsaURL, value + ": " + response.getStatusString());
        }

        // extract just the time stamp token (removes communication status info)
        ITimeStampToken tsToken = response.getTimeStampToken();
        if (tsToken == null) {
            throw new PdfException(
                    SignExceptionMessageConstant.THIS_TSA_FAILED_TO_RETURN_TIME_STAMP_TOKEN
            ).setMessageParams(tsaURL, response.getStatusString());
        }
        ITimeStampTokenInfo tsTokenInfo = tsToken.getTimeStampInfo(); // to view details
        byte[] encoded = tsToken.getEncoded();

        LOGGER.info("Timestamp generated: " + tsTokenInfo.getGenTime());
        if (tsaInfo != null) {
            tsaInfo.inspectTimeStampTokenInfo(tsTokenInfo);
        }
        // Update our token size estimate for the next call (padded to be safe)
        this.tokenSizeEstimate = encoded.length + 32;
        return encoded;
    }

    /**
     * Get timestamp token - communications layer
     *
     * @param requestBytes is a byte representation of TSA request
     *
     * @return - byte[] - TSA response, raw bytes (RFC 3161 encoded)
     * @throws IOException if I/O issue occurs
     */
    protected byte[] getTSAResponse(byte[] requestBytes) throws IOException {
        // Setup the TSA connection
        SignUtils.TsaResponse response = SignUtils.getTsaResponseForUserRequest(tsaURL, requestBytes, tsaUsername, tsaPassword);
        // Get TSA response as a byte array
        InputStream inp = response.tsaResponseStream;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inp.read(buffer, 0, buffer.length)) >= 0) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] respBytes = baos.toByteArray();

        if (response.encoding != null && response.encoding.toLowerCase().equals("base64".toLowerCase())) {
            respBytes = Base64.decode(new String(respBytes, "US-ASCII"));
        }
        return respBytes;
    }
}
