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
package com.itextpdf.signatures.validation.lotl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class which stores possible values for service type identifiers in LOTL files, which are supported by iText.
 */
public final class ServiceTypeIdentifiersConstants {
    public static final String CA_QC = "http://uri.etsi.org/TrstSvc/Svctype/CA/QC";
    public static final String CA_PKC = "http://uri.etsi.org/TrstSvc/Svctype/CA/PKC";
    public static final String OCSP_QC = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC";
    public static final String CRL_QC = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC";
    public static final String TSA_QTST = "http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST";
    public static final String EDS_Q = "http://uri.etsi.org/TrstSvc/Svctype/EDS/Q";
    public static final String REM_Q = "http://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q";
    public static final String PSES_Q = "http://uri.etsi.org/TrstSvc/Svctype/PSES/Q";
    public static final String QES_VALIDATION_Q = "http://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q";
    public static final String REMOTE_Q_SIG_CD_MANAGEMENT_Q =
            "http://uri.etsi.org/TrstSvc/Svctype/RemoteQSigCDManagement/Q";
    public static final String REMOTE_Q_SEAL_CD_MANAGEMENT_Q =
            "http://uri.etsi.org/TrstSvc/Svctype/RemoteQSealCDManagement/Q";
    public static final String EAA_Q = "http://uri.etsi.org/TrstSvc/Svctype/EAA/Q";
    public static final String ELECTRONIC_ARCHIVING_Q = "http://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving/Q";
    public static final String LEDGERS_Q = "http://uri.etsi.org/TrstSvc/Svctype/Ledgers/Q";
    public static final String OCSP = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP";
    public static final String CRL = "http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL";
    public static final String TS = "http://uri.etsi.org/TrstSvc/Svctype/TS/";
    public static final String TSA_TSS_QC = "http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC";
    public static final String TSA_TSS_ADES_Q_CAND_QES = "http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES";
    public static final String PSES = "http://uri.etsi.org/TrstSvc/Svctype/PSES";
    public static final String ADES_VALIDATION = "http://uri.etsi.org/TrstSvc/Svctype/AdESValidation";
    public static final String ADES_GENERATION = "http://uri.etsi.org/TrstSvc/Svctype/AdESGeneration";
    public static final String REMOTE_SIG_CD_MANAGEMENT = "http://uri.etsi.org/TrstSvc/Svctype/RemoteSigCDManagemen";
    public static final String REMOTE_SEAL_CD_MANAGEMENT = "http://uri.etsi.org/TrstSvc/Svctype/RemoteSealCDManagement";
    public static final String EAA = "http://uri.etsi.org/TrstSvc/Svctype/EAA";
    public static final String ELECTRONIC_ARCHIVING = "http://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving";
    public static final String LEDGERS = "http://uri.etsi.org/TrstSvc/Svctype/Ledgers";
    public static final String PKC_VALIDATION = "http://uri.etsi.org/TrstSvc/Svctype/PKCValidation";
    public static final String PKC_PRESERVATION = "http://uri.etsi.org/TrstSvc/Svctype/PKCPreservation";
    public static final String EAA_VALIDATION = "http://uri.etsi.org/TrstSvc/Svctype/EAAValidation";
    public static final String TST_VALIDATION = "http://uri.etsi.org/TrstSvc/Svctype/TSTValidation";
    public static final String EDS_VALIDATION = "http://uri.etsi.org/TrstSvc/Svctype/EDSValidation";
    public static final String EAA_PUB_EAA = "http://uri.etsi.org/TrstSvc/Svctype/EAA/Pub-EAA";
    public static final String CERTS_FOR_OTHER_TYPES_OF_TS =
            "http://uri.etsi.org/TrstSvc/Svctype/PKCValidation/CertsforOtherTypesOfTS";
    public static final String RA = "http://uri.etsi.org/TrstSvc/Svctype/RA";
    public static final String RA_NOT_HAVING_PKI_ID = "http://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid";
    public static final String SIGNATURE_POLICY_AUTHORITY =
            "http://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority";
    public static final String ARCHIV = "http://uri.etsi.org/TrstSvc/Svctype/Archiv";
    public static final String ARCHIV_NOT_HAVING_PKI_ID = "http://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid";
    public static final String ID_V = "http://uri.etsi.org/TrstSvc/Svctype/IdV";
    public static final String K_ESCROW = "http://uri.etsi.org/TrstSvc/Svctype/KEscrow";
    public static final String K_ESCROW_NOT_HAVING_PKI_ID =
            "http://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid";
    public static final String PP_WD = "http://uri.etsi.org/TrstSvc/Svctype/PPwd";
    public static final String PP_WD_NOT_HAVING_PKI_ID = "http://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavinPKIid";
    public static final String TL_ISSUER = "http://uri.etsi.org/TrstSvc/Svctype/TLIssuer";

    private ServiceTypeIdentifiersConstants() {
        // Private constructor to prevent class initialization.
    }

    /**
     * Gets all the constant values of service type identifiers.
     *
     * @return set of all service type identifiers defined in this class.
     */
    public static Set<String> getAllValues() {
        HashSet<String> values = new HashSet<>();
        values.add(CA_QC);
        values.add(CA_PKC);
        values.add(OCSP_QC);
        values.add(CRL_QC);
        values.add(TSA_QTST);
        values.add(EDS_Q);
        values.add(REM_Q);
        values.add(PSES_Q);
        values.add(QES_VALIDATION_Q);
        values.add(REMOTE_Q_SIG_CD_MANAGEMENT_Q);
        values.add(REMOTE_Q_SEAL_CD_MANAGEMENT_Q);
        values.add(EAA_Q);
        values.add(ELECTRONIC_ARCHIVING_Q);
        values.add(LEDGERS_Q);
        values.add(OCSP);
        values.add(CRL);
        values.add(TS);
        values.add(TSA_TSS_QC);
        values.add(TSA_TSS_ADES_Q_CAND_QES);
        values.add(PSES);
        values.add(ADES_VALIDATION);
        values.add(ADES_GENERATION);
        values.add(REMOTE_SIG_CD_MANAGEMENT);
        values.add(REMOTE_SEAL_CD_MANAGEMENT);
        values.add(EAA);
        values.add(ELECTRONIC_ARCHIVING);
        values.add(LEDGERS);
        values.add(PKC_VALIDATION);
        values.add(PKC_PRESERVATION);
        values.add(EAA_VALIDATION);
        values.add(TST_VALIDATION);
        values.add(EDS_VALIDATION);
        values.add(EAA_PUB_EAA);
        values.add(CERTS_FOR_OTHER_TYPES_OF_TS);
        values.add(RA);
        values.add(RA_NOT_HAVING_PKI_ID);
        values.add(SIGNATURE_POLICY_AUTHORITY);
        values.add(ARCHIV);
        values.add(ARCHIV_NOT_HAVING_PKI_ID);
        values.add(ID_V);
        values.add(K_ESCROW);
        values.add(K_ESCROW_NOT_HAVING_PKI_ID);
        values.add(PP_WD);
        values.add(PP_WD_NOT_HAVING_PKI_ID);
        values.add(TL_ISSUER);
        return Collections.<String>unmodifiableSet(values);
    }
}
