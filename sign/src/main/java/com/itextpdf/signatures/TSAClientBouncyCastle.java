/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.kernel.PdfException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * Time Stamp Authority Client interface implementation using Bouncy Castle
 * org.bouncycastle.tsp package.
 * <p>
 * Created by Aiken Sam, 2006-11-15, refactored by Martin Brunecky, 07/15/2007
 * for ease of subclassing.
 * </p>
 */
public class TSAClientBouncyCastle implements ITSAClient {

    /**
     * The default value for the hash algorithm
     */
    public static final String DEFAULTHASHALGORITHM = "SHA-256";
    /**
     * The default value for the hash algorithm
     */
    public static final int DEFAULTTOKENSIZE = 4096;
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
    protected int tokenSizeEstimate;
    /**
     * Hash algorithm
     */
    protected String digestAlgorithm;

    /**
     * TSA request policy
     */
    private String tsaReqPolicy;

    /**
     * Creates an instance of a TSAClient that will use BouncyCastle.
     *
     * @param url String - Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     */
    public TSAClientBouncyCastle(String url) {
        this(url, null, null, DEFAULTTOKENSIZE, DEFAULTHASHALGORITHM);
    }

    /**
     * Creates an instance of a TSAClient that will use BouncyCastle.
     *
     * @param url      String - Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     * @param username String - user(account) name
     * @param password String - password
     */
    public TSAClientBouncyCastle(String url, String username, String password) {
        this(url, username, password, 4096, DEFAULTHASHALGORITHM);
    }

    /**
     * Constructor.
     * Note the token size estimate is updated by each call, as the token
     * size is not likely to change (as long as we call the same TSA using
     * the same imprint length).
     *
     * @param url           String - Time Stamp Authority URL (i.e. "http://tsatest1.digistamp.com/TSA")
     * @param username      String - user(account) name
     * @param password      String - password
     * @param tokSzEstimate int - estimated size of received time stamp token (DER encoded)
     */
    public TSAClientBouncyCastle(String url, String username, String password, int tokSzEstimate, String digestAlgorithm) {
        this.tsaURL = url;
        this.tsaUsername = username;
        this.tsaPassword = password;
        this.tokenSizeEstimate = tokSzEstimate;
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
        return tokenSizeEstimate;
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
     * @return encoded, TSA signed data of the timeStampToken
     * @throws IOException
     * @throws TSPException
     */
    @Override
    public byte[] getTimeStampToken(byte[] imprint) throws IOException, TSPException {
        byte[] respBytes = null;
        // Setup the time stamp request
        TimeStampRequestGenerator tsqGenerator = new TimeStampRequestGenerator();
        tsqGenerator.setCertReq(true);
        if (tsaReqPolicy != null && tsaReqPolicy.length() > 0) {
            tsqGenerator.setReqPolicy(tsaReqPolicy);
        }
        // tsqGenerator.setReqPolicy("1.3.6.1.4.1.601.10.3.1");
        BigInteger nonce = BigInteger.valueOf(SystemUtil.getTimeBasedSeed());
        TimeStampRequest request = tsqGenerator.generate(new ASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest(digestAlgorithm)), imprint, nonce);
        byte[] requestBytes = request.getEncoded();

        // Call the communications layer
        respBytes = getTSAResponse(requestBytes);

        // Handle the TSA response
        TimeStampResponse response = new TimeStampResponse(respBytes);

        // validate communication level attributes (RFC 3161 PKIStatus)
        response.validate(request);
        PKIFailureInfo failure = response.getFailInfo();
        int value = (failure == null) ? 0 : failure.intValue();
        if (value != 0) {
            // @todo: Translate value of 15 error codes defined by PKIFailureInfo to string
            throw new PdfException(PdfException.InvalidTsa1ResponseCode2).setMessageParams(tsaURL, String.valueOf(value));
        }
        // @todo: validate the time stap certificate chain (if we want
        //        assure we do not sign using an invalid timestamp).

        // extract just the time stamp token (removes communication status info)
        TimeStampToken tsToken = response.getTimeStampToken();
        if (tsToken == null) {
            throw new PdfException(PdfException.Tsa1FailedToReturnTimeStampToken2).setMessageParams(tsaURL, response.getStatusString());
        }
        TimeStampTokenInfo tsTokenInfo = tsToken.getTimeStampInfo(); // to view details
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
     * @return - byte[] - TSA response, raw bytes (RFC 3161 encoded)
     * @throws IOException
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
