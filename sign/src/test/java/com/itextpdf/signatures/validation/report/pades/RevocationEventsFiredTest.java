package com.itextpdf.signatures.validation.report.pades;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.OCSPValidator;
import com.itextpdf.signatures.validation.RevocationDataValidator;
import com.itextpdf.signatures.validation.RevocationResponseOrigin;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidationOcspClient;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.events.DssNotTimestampedEvent;
import com.itextpdf.signatures.validation.events.RevocationNotFromDssEvent;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag("BouncyCastleUnitTest")
public class RevocationEventsFiredTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final ValidationContext VALIDATION_CONTEXT = new ValidationContext(
            ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private static final Date CURRENT_DATE = DateTimeUtil.getCurrentTimeDate();
    private CustomReportGenerator customReportGenerator;
    private ValidatorChainBuilder builder;
    private X509Certificate dummyCertificate;
    private X509Certificate parentCert;
    private PrivateKey privateKey;

    @BeforeEach
    public void setUp() throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        builder = new ValidatorChainBuilder();
        customReportGenerator = new CustomReportGenerator();
        builder.withPAdESLevelReportGenerator(customReportGenerator);
        dummyCertificate = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem")[0];
        parentCert = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem")[1];
        privateKey = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
    }

    @Test
    public void zeroApplicableResponsesFireTwoEventsTest() {
        RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
        revocationDataValidator.validate(new ValidationReport(), VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(2, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof DssNotTimestampedEvent);
        Assertions.assertTrue(customReportGenerator.firedEvents.get(1) instanceof RevocationNotFromDssEvent);
    }

    @Test
    public void notTimestampedResponsesFireOneEventTest() throws CertificateEncodingException {
        setUpOcspClient(RevocationResponseOrigin.LATEST_DSS, TimeBasedContext.PRESENT);
        RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
        revocationDataValidator.validate(new ValidationReport(), VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(1, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof DssNotTimestampedEvent);
    }

    @Test
    public void responsesNotFromLatestDssFireTwoEventsTest() throws CertificateEncodingException {
        setUpOcspClient(RevocationResponseOrigin.HISTORICAL_DSS, TimeBasedContext.HISTORICAL);
        RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
        revocationDataValidator.validate(new ValidationReport(), VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(2, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof DssNotTimestampedEvent);
        Assertions.assertTrue(customReportGenerator.firedEvents.get(1) instanceof RevocationNotFromDssEvent);
    }

    @Test
    public void responsesFromSignatureFireTwoEventsTest() throws CertificateEncodingException {
        setUpOcspClient(RevocationResponseOrigin.SIGNATURE, TimeBasedContext.HISTORICAL);
        RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
        revocationDataValidator.validate(new ValidationReport(), VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(2, customReportGenerator.firedEvents.size());
        Assertions.assertTrue(customReportGenerator.firedEvents.get(0) instanceof DssNotTimestampedEvent);
        Assertions.assertTrue(customReportGenerator.firedEvents.get(1) instanceof RevocationNotFromDssEvent);
    }

    @Test
    public void responsesFromTimestampedDssDontFireEventsTest() throws CertificateEncodingException {
        setUpOcspClient(RevocationResponseOrigin.LATEST_DSS, TimeBasedContext.HISTORICAL);
        RevocationDataValidator revocationDataValidator = builder.buildRevocationDataValidator();
        revocationDataValidator.validate(new ValidationReport(), VALIDATION_CONTEXT, dummyCertificate, CURRENT_DATE);

        Assertions.assertEquals(0, customReportGenerator.firedEvents.size());
    }

    private void setUpOcspClient(RevocationResponseOrigin responseOrigin, TimeBasedContext timeBasedContext) throws CertificateEncodingException {
        TestOcspClient testOcspClient = new TestOcspClient().addBuilderForCertIssuer(parentCert, privateKey);
        SignatureValidationProperties validationProperties = new SignatureValidationProperties();
        validationProperties.addOcspClient(new ValidationOcspClient() {
            @Override
            public Map<IBasicOCSPResp, RevocationDataValidator.OcspResponseValidationInfo> getResponses() {
                IBasicOCSPResp basicOCSPResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(
                        testOcspClient.getEncoded(dummyCertificate, parentCert, null)));
                Map<IBasicOCSPResp, RevocationDataValidator.OcspResponseValidationInfo> dummyResponses = new HashMap<>();
                dummyResponses.put(basicOCSPResp,
                        new RevocationDataValidator.OcspResponseValidationInfo(basicOCSPResp.getResponses()[0],
                                basicOCSPResp, CURRENT_DATE, timeBasedContext, responseOrigin));
                return dummyResponses;
            }
        });
        builder.withSignatureValidationProperties(validationProperties);
        builder.withOCSPValidatorFactory(() -> new OCSPValidator(builder) {
            @Override
            public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                                 ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate, Date responseGenerationDate) {
            }
        });
    }

    public static class CustomReportGenerator extends PAdESLevelReportGenerator {
        List<IEvent> firedEvents = new ArrayList<>();

        @Override
        public void onEvent(IEvent rawEvent) {
            firedEvents.add(rawEvent);
        }
    }
}
