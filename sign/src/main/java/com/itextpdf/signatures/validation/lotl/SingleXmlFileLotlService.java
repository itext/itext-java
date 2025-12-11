package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.datastructures.ConcurrentHashSet;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.validation.lotl.CountrySpecificLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SingleXmlFileLotlService extends LotlService {


    private final CountrySpecificLotlWithCerts countrySpecificLotl;
    private final ConcurrentHashSet<IServiceContext> contexts = new ConcurrentHashSet<>();
    private ValidationReport report;


    /**
     * Creates a new instance of {@link LotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     */
    public SingleXmlFileLotlService(LotlFetchingProperties lotlFetchingProperties,
            CountrySpecificLotlWithCerts countrySpecificLotl) {
        super(lotlFetchingProperties);
        this.countrySpecificLotl = countrySpecificLotl;
    }

    @Override
    public void loadFromCache(InputStream in) {
        String jsonString;
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        try {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading from cache input stream", e);
        }
        jsonString = sb.toString();
        JsonValue json = JsonValue.fromJson(jsonString);
        if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;
            this.report = converJsonToReport((JsonObject) jsonObject.getFields().get("report"));
            List<IServiceContext> contexts = convertJsonToServiceContexts(
                    (JsonObject) jsonObject.getFields().get("serviceContexts"));
            this.contexts.clear();
            this.contexts.addAll(contexts);
        } else {
            throw new IllegalArgumentException("Invalid JSON format in cache input stream");
        }
    }

    @Override
    public void serializeCache(OutputStream outputStream) throws IOException {


    }



    @Override
    public ValidationReport getValidationResult() {
        return this.report;
    }

    @Override
    public List<IServiceContext> getNationalTrustedCertificates() {
        return new ArrayList<>(contexts);
    }

    @Override
    protected void loadFromNetwork() {
        CountrySpecificLotlFetcher lotlr = new CountrySpecificLotlFetcher(this);
        ValidationReport report = new ValidationReport();
        Map<String, Result> f = lotlr.getAndValidateCountrySpecificFiles(
                countrySpecificLotl.getGenerallyTrustedCertificates(),
                Collections.singletonList(this.countrySpecificLotl.countrySpecificLotl), this);
        contexts.clear();
        for (Result value : f.values()) {
            report.merge(value.getLocalReport());
            contexts.addAll(value.getContexts());
        }
        this.report = report;
    }

    private ValidationReport converJsonToReport(JsonObject jsonObject) {
        ValidationReport report = new ValidationReport();
        // Implement the logic to convert JsonObject to ValidationReport
        return report;
    }

    private List<IServiceContext> convertJsonToServiceContexts(JsonObject jsonObject) {
        List<IServiceContext> contexts = new ArrayList<>();
        // Implement the logic to convert JsonObject to List<IServiceContext>
        return contexts;
    }

    static class CountrySpecificLotlWithCerts {

        private final CountrySpecificLotl countrySpecificLotl;
        private final List<String> certificates;

        CountrySpecificLotlWithCerts(CountrySpecificLotl countrySpecificLotl, List<String> certificates) {
            this.countrySpecificLotl = countrySpecificLotl;
            this.certificates = certificates;
        }


        public List<Certificate> getGenerallyTrustedCertificates() {
            final ArrayList<Certificate> result = new ArrayList<>();
            for (String certificateString : certificates) {
                Certificate certificate = CertificateUtil.readCertificatesFromPem(
                        new ByteArrayInputStream(certificateString.getBytes(
                                StandardCharsets.UTF_8)))[0];
                result.add(certificate);
            }
            return result;
        }

    }
}
